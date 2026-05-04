package com.primertrimestre.aplication;

import java.awt.EventQueue;

import javax.swing.JOptionPane;

import com.primertrimestre.persistence.util.JpaUtil;
import com.primertrimestre.ui.controllers.UiLauncher;

public class App {

	public static void main(String[] args) {
        // 1) Inicializar JPA antes de lanzar la UI
        try {
            JpaUtil.getEntityManager().close(); // lo llamamos para comprobar la db y enseguida lo cerramos 
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo inicializar la base de datos:\n" + e.getMessage(),
                    "Error grave",
                    JOptionPane.ERROR_MESSAGE
            );
            //return; // no seguimos si la BD no está
        }
        // 2) Lanzar la UI
        EventQueue.invokeLater(
        	new Runnable() {
	            @Override
	            public void run() {
	            	try {
	            		UiLauncher.showLogin();                    
	            	} catch (Exception e) {
	            		e.printStackTrace();
	            	}
	            }
        	}
        );
        /*VERSIONES LAMBDA CON Y SIN TRY-CATCH
         
         EventQueue.invokeLater(() -> {
    		try {
        		UiLauncher.showLogin();
    		} catch (Exception e) {
        		e.printStackTrace();
    		}
		});
       
        EventQueue.invokeLater( () -> UiLauncher.showLogin() ); 
        
        */
	}
}
