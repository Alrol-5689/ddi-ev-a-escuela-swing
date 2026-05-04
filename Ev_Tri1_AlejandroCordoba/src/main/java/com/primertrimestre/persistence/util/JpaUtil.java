package com.primertrimestre.persistence.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class JpaUtil {

    //===>> FIELDS <<===//

    private static volatile EntityManagerFactory emf;
    /*             --------
     				 |||
     				 VVV
 	--> volatile <--    	
 	:> Las lecturas/escrituras van directamente a memoria principal, sin cacheos intermedios en los hilos
 	:> Cuendo un hilo actualiza emf, cualquier otro hilo ve el cambio inmediatamente 
 	:> Evita que el valor quede desfasado
 	:> Útil cuando varios hilos comparten un recurso (emf singleton) y quieres visibilidad sin sincronización pesada
 	:> No aporta atomicidad por sí mismo, para operaciones compuestas (leer y escribir) --> complementar con sincronización
     */

    //===>> METHODS <<===//

    public static EntityManagerFactory getEntityManagerFactory() {
        EntityManagerFactory result = emf; //--> copia local; si ya existe evitamos bloquear
        if (result == null) {
            synchronized (JpaUtil.class) { //--> double-check: solo un hilo puede crear la factoría (por ' synchronized ' solo pasa UNO)
                result = emf;
                if (result == null) {
                    result = Persistence.createEntityManagerFactory("EvPrimerTrimestrePU"); // inicialización costosa => una sola vez
                    emf = result; // publicar la instancia compartida; volatile asegura visibilidad
                }
            }
        }
        return result;
    }

    public static EntityManager getEntityManager() { //--> Cada em que se saca del único emf debe cerrarse tras ser usado. 
        return getEntityManagerFactory().createEntityManager();
    }

    public static void close() {
        EntityManagerFactory local = emf;
        if (local != null) {
            synchronized (JpaUtil.class) {
                local = emf; // re-lee dentro del lock
                if (local != null) {
                    try {
                        if (local.isOpen()) local.close();
                    } finally {
                        emf = null; // MUY IMPORTANTE: permitir recreación en siguiente init
                    }
                }
            }
        }
    }
}
