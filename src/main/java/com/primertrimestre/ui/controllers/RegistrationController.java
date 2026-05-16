package com.primertrimestre.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.SwingUtilities;

import com.primertrimestre.model.Administrator;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.service.AdministratorService;
import com.primertrimestre.service.StudentService;
import com.primertrimestre.service.TeacherService;
import com.primertrimestre.ui.view.RegistrationWindow;

public final class RegistrationController implements ActionListener {

    private RegistrationWindow view;
    private final StudentService studentService;
    private final TeacherService teacherService;
    private final AdministratorService administratorService;
    private final Runnable returnToLogin;

    public RegistrationController(StudentService studentService,
                                  TeacherService teacherService,
                                  AdministratorService administratorService,
                                  Runnable returnToLogin) {
        this.studentService = studentService;
        this.teacherService = teacherService;
        this.administratorService = administratorService;
        this.returnToLogin = returnToLogin != null ? returnToLogin : () -> {};
        // Si no nos pasan callback de navegación, usamos uno vacío para evitar NPE
        // () -> {} = new Runnable() { public void run() {}
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case RegistrationWindow.CMD_REGISTER -> handleRegister();
            case RegistrationWindow.CMD_CANCEL -> handleCancel();
        }
    }

    public void showRegistrationFrame() {
        view = new RegistrationWindow();
        registerListeners();
        view.setVisible(true);
    }

    private void registerListeners() {
        view.getBtnRegister().addActionListener(this);
        view.getBtnCancel().addActionListener(this);
        // JFrame -> java.awt.Window.addWindowListener
        view.addWindowListener(
        	new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent e) {
	                handleCancel();
	            }
        	}
        );
    }

    private void handleRegister() {
        String username = view.getUsername();
        String fullName = view.getFullName();
        String password = view.getPassword();
        String confirmPassword = view.getConfirmPassword();
        String role = view.getSelectedRole();

        if (username.isEmpty() || fullName.isEmpty() || password.isEmpty()) {
            view.showError("Todos los campos son obligatorios.");
            return;
        }
        if (!password.equals(confirmPassword)) {
            view.showError("Las contraseñas no coinciden.");
            view.clearForm();
            return;
        }

        try {
            switch (role) {
                case "Profesor" -> registerTeacher(username, fullName, password);
                case "Administrador" -> registerAdministrator(username, fullName, password);
                default -> registerStudent(username, fullName, password);
            }
            view.showInfo("Registro completado. Ahora puede iniciar sesión.");
            closeAndReturn();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
    }

    private void registerStudent(String username, String fullName, String password) {
        Student student = new Student();
        student.setUsername(username);
        student.setFullName(fullName);
        student.setPassword(password);
        studentService.registerStudent(student);
    }

    private void registerTeacher(String username, String fullName, String password) {
        Teacher teacher = new Teacher();
        teacher.setUsername(username);
        teacher.setFullName(fullName);
        teacher.setPassword(password);
        teacherService.registerTeacher(teacher);
    }

    private void registerAdministrator(String username, String fullName, String password) {
        Administrator administrator = new Administrator();
        administrator.setUsername(username);
        administrator.setFullName(fullName);
        administrator.setPassword(password);
        administratorService.registerAdministrator(administrator);
    }

    private void handleCancel() {
        if (view.confirmCancel()) {
            closeAndReturn();
        }
    }

    // Callback de navegación: el controller no decide destino: 
    // solo ejecuta el Runnable que le pasaron (p.ej. UiLauncher::showLogin)
    private void closeAndReturn() {
        view.dispose();
        SwingUtilities.invokeLater(returnToLogin);
        /*
        - SwingUtilities -> clase utilitaria para operaciones con Swing en el hilo de despacho de eventos (EDT)
            EDT -> Event Dispatch Thread, hilo donde se ejecutan eventos y actualizaciones de la UI en Swing
        - invokeLater(Runnable) -> método estático que recibe un Runnable y lo ejecuta en el EDT lo más pronto posible
        - returnToLogin -> UiLauncher::showLogin

        returnToLogin = () -> UiLauncher.showLogin();

        returnToLogin = () -> {
            UiLauncher.showLogin();
        };
        
        returnToLogin = new Runnable() {
            @Override
            public void run() {
                UiLauncher.showLogin();
            }
        };

        */

    }
}
