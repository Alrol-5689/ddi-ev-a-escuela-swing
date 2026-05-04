package com.primertrimestre.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.primertrimestre.auth.SessionContext;
import com.primertrimestre.model.Administrator;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.service.AdministratorService;
import com.primertrimestre.service.StudentService;
import com.primertrimestre.service.TeacherService;
import com.primertrimestre.ui.view.LoginWindow;

public final class LoginController implements ActionListener{
	
    private LoginWindow view;
    private final SessionContext session;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AdministratorService administratorService;
    private final LoginNavigator navigator;

    public LoginController(StudentService studentService, TeacherService teacherService, 
                           AdministratorService administratorService, SessionContext session, 
                           LoginNavigator navigator) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.administratorService = administratorService;
        this.session = session;
        this.navigator = navigator;
    }

    @Override
    public void actionPerformed(ActionEvent e) {	
    	String command = e.getActionCommand(); // Objet obj = e.getSource(); me gusta menos. 
    	switch (command) {
	    	case LoginWindow.CMD_LOGIN  -> handleLogin();
	        case LoginWindow.CMD_CLEAR  -> view.clearForm();
	        case LoginWindow.CMD_SINGUP -> openRegistration();
    	}
    }
    
    public void showLoginFrame() {
        view = new LoginWindow();
        registerListeners();
        view.setVisible(true);
    }

    private void registerListeners() {
        view.getBtnLogin().addActionListener(this);
        view.getBtnSingUp().addActionListener(this);
        view.getBtnClear().addActionListener(this);
        // JFrame -> java.awt.Window.addWindowListener
        view.addWindowListener(
        	new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                handleFinishApp();
	            }
        	}
        );
    }
    
    private void handleLogin() {    	
        String username = view.getUserText();
        String password = view.getPasswordText();
        String userType = view.getSelectedUserType();

        if (username.isEmpty() || password.isEmpty()) {
            view.showError("Usuario y contraseña son obligatorios.");
            view.clearForm();
            return;
        }

        if (userType == null || "Seleccione".equalsIgnoreCase(userType)) {
            view.showError("Seleccione un cargo.");
            return;
        }
        
        switch (userType) {
	        case "Alumno" -> handleStudent(username, password);
	        case "Profesor" -> handleTeacher(username, password);
	        case "Administrador" -> handleAdmin(username, password);
	        default -> view.showError("Tipo no soportado.");
        }
    }

	private void handleAdmin(String username, String password) {
        Administrator administrator = administratorService.authenticate(username, password);
        if (administrator != null) {
            session.setCurrentUser(administrator);
            view.dispose();
            navigator.onAdminLogin();
        } else {
            view.showError("Credenciales incorrectas.");
            view.clearForm();
        }
	}

	private void handleTeacher(String username, String password) {
        Teacher teacher = teacherService.authenticate(username, password);
        if (teacher != null) {
            session.setCurrentUser(teacher);
            view.dispose();
            navigator.onTeacherLogin();
        } else {
            view.showError("Credenciales incorrectas.");
            view.clearForm();
        }
	}

	private void handleStudent(String username, String password) {
        Student student = studentService.authenticate(username, password);
        if (student != null) {
            session.setCurrentUser(student);
            view.dispose();
            navigator.onStudentLogin();
        } else {
            view.showError("Credenciales incorrectas.");
            view.clearForm();
        }
	}

    private void openRegistration() {
        session.clear();
        view.dispose();
        UiLauncher.showRegistration();
    }

    // Interfaz anidada: expone callbacks para que otro componente decida a qué pantalla navegar.
    // LoginController solo notifica el rol autenticado y delega la navegación al implementador.
    public interface LoginNavigator {
        void onStudentLogin();
        void onTeacherLogin();
        void onAdminLogin();
    }

    private void handleFinishApp() {
        if (view.finishApp()) {
            System.exit(0);
        }
    }
}
