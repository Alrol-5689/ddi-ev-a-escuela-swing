package com.primertrimestre.aplication;

import java.awt.EventQueue;
import java.sql.Connection;

import javax.swing.JOptionPane;

import com.primertrimestre.persistence.util.JdbcUtil;
import com.primertrimestre.ui.controllers.UiLauncher;

public class App {

	public static void main(String[] args) {
        // 1) Comprobar la conexion JDBC antes de lanzar la UI
        try (Connection connection = JdbcUtil.getConnection()) {
            // Si llegamos aqui, la base de datos existe y acepta la conexion.
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudo conectar con la base de datos:\n" + e.getMessage(),
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
