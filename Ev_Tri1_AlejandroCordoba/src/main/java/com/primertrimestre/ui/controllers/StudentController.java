package com.primertrimestre.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.primertrimestre.auth.SessionContext;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Module;
import com.primertrimestre.service.EnrollmentService;
import com.primertrimestre.service.ModuleService;
import com.primertrimestre.service.StudentService;
import com.primertrimestre.ui.view.StudentMainFrame;

public class StudentController implements ActionListener {

    private StudentMainFrame view;
    private final SessionContext session;
    private final StudentService studentService;
    private final EnrollmentService enrollmentService;
    private final ModuleService moduleService;

    public StudentController(SessionContext session,
                             StudentService studentService,
                             EnrollmentService enrollmentService,
                             ModuleService moduleService) {
        this.session = session;
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
        this.moduleService = moduleService;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case StudentMainFrame.CMD_LOGOUT -> logout();
            case StudentMainFrame.CMD_ENROLL_IN_MODULE -> enrollInModule();
            case StudentMainFrame.CMD_UNSUBSCRIBE_FROM_MODULE -> unsubscribeFromModule();
            case StudentMainFrame.CMD_REFRESH -> refresh();
            case StudentMainFrame.CMD_SAVE -> save();
        }
    }

    public void showStudentMainFrame() {
        Student currentStudent = (Student) session.getCurrentUser();
        view = new StudentMainFrame(currentStudent);
        registerListeners();
        refresh();
        view.setVisible(true);
    }

    public void registerListeners() {
        view.getBtnLogout().addActionListener(this);
        view.getBtnEnrollInModule().addActionListener(this);
        view.getBtnEnrollInModule().setEnabled(false);
        view.getBtnUnsubscribeFromModule().addActionListener(this);
        view.getBtnRefresh().addActionListener(this);
        view.getBtnSave().addActionListener(this);
        view.getBtnUnsubscribeFromModule().setEnabled(false);

        view.addAvailableModuleSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    boolean isSelection = !view.getAvailableList().isSelectionEmpty();
                    // isSelectionEmpty() de JList devuelve true si NO hay selección
                    // el botón se activa cuando hay un módulo seleccionado
                    view.getBtnEnrollInModule().setEnabled(isSelection);
                }
            }
        );

        view.addEnrolledModuleSelectionListener(
            new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (e.getValueIsAdjusting()) {
                        return;
                    }
                    // La primera fila es la 0; si no hay selección, devuelve -1
                    boolean hasSelection = view.getEnrolledTable().getSelectedRow() >= 0;
                    view.getBtnUnsubscribeFromModule().setEnabled(hasSelection);
                }
            }
        );
        // VERSIÓN SIN view.addEnrolledModuleSelectionListener:

        // view.getEnrolledTable()
        //     .getSelectionModel()
        //     .addListSelectionListener(
        //         new ListSelectionListener() {
        //             @Override
        //             public void valueChanged(ListSelectionEvent e) {
        //                 if (e.getValueIsAdjusting()) {
        //                     return;
        //                 }
        //                 boolean hasSelection = view.getEnrolledTable().getSelectedRow() >= 0;
        //                 view.getBtnUnsubscribeFromModule().setEnabled(hasSelection);
        //             }
        //         }
        //     );
        
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

    private void save() {
        refresh();
        view.showInfo("Datos guardados correctamente.");
    }

    private void refresh() {
        loadAvailableModules();
        loadEnrolledModules();
    }

    private void unsubscribeFromModule() {
        int selectedRow = view.getEnrolledTable().getSelectedRow();
        if (selectedRow < 0) {
            view.showError("Por favor, selecciona un módulo para darte de baja.");
            return;
        } // Object porque la tabla usa Object[] para las filas
        Object moduleIdCell = view.getEnrolledTable().getValueAt(selectedRow, 0);
        if (moduleIdCell == null || moduleIdCell.toString().isBlank()) {
            view.showError("No se ha podido identificar el módulo seleccionado.");
            return;
        }
        Long moduleId = Long.valueOf(moduleIdCell.toString());
        Student currentStudent = (Student) session.getCurrentUser();
        enrollmentService.unenrollStudent(currentStudent.getId(), moduleId);
        view.removeModuleFromEnrolledModules(moduleId); // loadEnrolledModules(); gasta más recursos
        loadAvailableModules();
    }

    private void enrollInModule() {
        Module selectedModule = view.getSelectedAvailableModule();
        if (selectedModule != null) {
            Student currentStudent = (Student) session.getCurrentUser();
            enrollmentService.enrollStudent(currentStudent.getId(), selectedModule.getId());
            refresh();
        }
    }

    private void loadAvailableModules() {
        view.getAvailableList().clearSelection();
        Student currentStudent = (Student) session.getCurrentUser();
        List<Module> enrolledModules = studentService.getEnrolledModules(currentStudent.getId());
        List<Module> availableModules = moduleService.listAvailableForStudent(enrolledModules);
        view.setAvailableModules(availableModules);
    }

    // private void loadAvailableModules() {
    //     view.getAvailableList().clearSelection();
    //     Student currentStudent = (Student) session.getCurrentUser();
    //     List<Module> availableModules = 
    //         enrollmentService.availableModules(currentStudent.getId());
    //     view.setAvailableModules(availableModules);
    // } NO ME GUSTA QUE EN enrollmentService HAYA UN MÉTODO QUE DEVUELVA MÓDULOS (NO ES SU RESPONSABILIDAD)
    // ELIMINAR EN EnrollmentService.availableModules()

    // private void loadAvailableModules() {
    //     view.getAvailableList().clearSelection();
    //     Student currentStudent = (Student) session.getCurrentUser();
    //     List<Module> enrolledModules = studentService.getEnrolledModules(currentStudent.getId());
    //     List<Module> availableModules = moduleService.listAvailableForStudent(enrolledModules);
    //     view.setAvailableModules(availableModules);
    // } NO ME GUSTA PORQUE USA DOS SERVICIOS
    
    private void loadEnrolledModules() {
        view.clearEnrolledModules();
        Student currentStudent = (Student) session.getCurrentUser();
        var enrollments = enrollmentService.listByStudent(currentStudent.getId());
        for (var e : enrollments) {
            Module m = e.getModule();
            Long id = m.getId();
            Integer credits = m.getCreditsECTS();
            String name = m.getName();
            String code = m.getCode();
            Double grade = e.getGrade();
            view.addEnrolledModuleRow(id, credits, name, code, grade);
            
        }
    }
    // private void loadEnrolledModules() {
        //     view.clearEnrolledModules();
        //     Student currentStudent = (Student) session.getCurrentUser();
        //     var enrollments = enrollmentService.listByStudent(currentStudent.getId());
        //     for (var e : enrollments) {
            //         Object[] rowData = new Object[] {
                //             e.getModule().getId(),
                //             e.getModule().getName(),
                //             e.getModule().getCode(),
                //             e.getModule().getCreditsECTS(),
                //             e.getGrade()
                //         };
                //         view.addEnrolledModuleRow(rowData); 
                //     }
                // }
                
    private void logout() {
        session.clear();
        view.dispose();
        SwingUtilities.invokeLater(UiLauncher::showLogin);
    }

    private void handleFinishApp() {
        if (view.finishApp()) {
            System.exit(0);
        }
    }
}
