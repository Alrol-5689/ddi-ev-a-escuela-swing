package com.primertrimestre.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.primertrimestre.auth.SessionContext;
import com.primertrimestre.model.Enrollment;
import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.service.EnrollmentService;
import com.primertrimestre.service.ModuleService;
import com.primertrimestre.ui.view.TeacherMainFrame;

public class TeacherController implements ActionListener {
	
    private TeacherMainFrame view;
	private final SessionContext session;
	private final ModuleService moduleService;
	private final EnrollmentService enrollmentService;

    public TeacherController(SessionContext session, 
							 ModuleService moduleService, 
							 EnrollmentService enrollmentService) {
		this.session = session;
		this.moduleService = moduleService;
		this.enrollmentService = enrollmentService;
        // El combo "Mostrar" vive en la vista; todavía no tenemos la instancia aquí.
        // Cuando showTeacherMainFrame() cree la ventana, usaremos addViewTypeListener
        // para conectar el action listener que decide qué módulos ver.
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		switch (command) {
			case TeacherMainFrame.CMD_LOGOUT -> logout();
			case TeacherMainFrame.CMD_SAVE -> saveAllNotes();
			case TeacherMainFrame.CMD_REFRESH -> refresh();
		}
	}
	
	public void showTeacherMainFrame() {
        Teacher currentTeacher = resolveCurrentTeacher();
        view = new TeacherMainFrame(currentTeacher);
        registerListeners();
        refreshModules(); 
        view.setVisible(true);
	}

    private void registerListeners() {
        view.getBtnLogout().addActionListener(this);    // Botón superior de cerrar sesión
        view.getBtnRefresh().addActionListener(this);   // Botón inferior de refrescar datos
        view.getBtnSaveNotes().addActionListener(this); // Botón inferior para guardar la nota editada    
        view.getBtnSaveNotes().setEnabled(false);       // setEnabled(false) desactiva el botón hasta que haya un módulo del profesor

        //=== [ DIFÍCIL DE ENTENDER ] ======================================================

        /*
        La vista no sabe qué hacer cuando cambia el módulo o el filtro,
        así que nos expone métodos para "enchufar" nuestros listeners:

           1. addModuleSelectionListener -> nos avisa cuando el usuario
              hace clic en otro módulo de la lista de la izquierda.
              El evento que llega aquí es ListSelectionEvent (propio de JList),
              y lo tratamos delegando en onModuleSelected(...), que a su vez
              carga los alumnos del nuevo módulo.

           2. addViewTypeListener -> nos avisa cuando cambia el combo "Mostrar".
              Ese combo lanza eventos ActionEvent; en respuesta solo necesitamos
              recalcular la lista de módulos visibles, así que llamamos a refreshModules().

        No usamos "this" como listener porque TeacherController ya está ocupando
        su actionPerformed para los botones y no implementa ListSelectionListener.
        */

        view.addModuleSelectionListener(
            new ListSelectionListener() { // Reaccionar cuando el profe cambia el módulo seleccionado
                @Override
                public void valueChanged(ListSelectionEvent event) { // JList -> ListSelectionEvent
                    onModuleSelected(event);
                }
            }
        );  // ListSelectionListener para la lista de módulos     

        view.addViewTypeListener(
            // le pasamos al método un ActionListener anónimo que refresca la lista de módulos
            new ActionListener() { // Cambiar la lista de módulos según el filtro del combo
                @Override
                public void actionPerformed(ActionEvent e) { // cuando se selecciona una opción del combo
                    refreshModules(); // cada vez que se cambia el tipo de vista, recargamos los módulos
                }
            }
        );  

        /* ActionListener -> actionPerformed(ActionEvent e)
             ActionEvent -> tiene método getActionCommand(), getSource()...
                 getSource() -> botón que lanzó el evento
                 getActionCommand() -> TeacherMainFrame.CMD_LOGOUT = "LOGOUT"... */

        /* ListSelectionListener -> valueChanged(ListSelectionEvent event)
             ListSelectionEvent -> tiene método getValueIsAdjusting(), etc.
                 getValueIsAdjusting() -> true si el usuario todavía está arrastrando el ratón */
        
        //==================================================================================
        
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

    private void onModuleSelected(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) { // Ignorar eventos intermedios mientras el usuario todavía está arrastrando/clickando
            return;
        }
        loadStudentsForSelectedModule(); // Cuando confirma la selección, pedimos los alumnos del módulo
    }

    private void loadStudentsForSelectedModule() {
        view.clearStudents();
        Module selectedModule = view.getSelectionModule();
        updateSaveNotesAvailability(selectedModule);
        if (selectedModule == null) return;
        List<Enrollment> enrollments = enrollmentService.listByModule(selectedModule.getId());
        for (Enrollment e : enrollments) {
            Student student = e.getStudent();
            Long studentId = student != null ? student.getId() : null;
            String username = student != null ? student.getUsername() : "Desconocido";
            String fullName = student != null ? student.getFullName() : "";
            view.addStudentRow(studentId, username, fullName, e.getGrade());
        }
    }
	
    private void refresh() {
        refreshModules();
        loadStudentsForSelectedModule();
	}

    private void refreshModules() {
        List<Module> modules = resolveModulesForCurrentFilter(); // obtenemos los módulos según el filtro actual
        view.setModules(modules); // le metemos los modulos que mostrar
        updateSaveNotesAvailability(view.getSelectionModule());
    }

    private List<Module> resolveModulesForCurrentFilter() {
        Teacher teacher = getCurrentTeacherOrNull();
        if (teacher == null) {
            return Collections.emptyList();
        }
        String selectedView = view.getSelectedViewType();
        boolean showAll = TeacherMainFrame.VIEW_TYPE_ALL_MODULES.equalsIgnoreCase(selectedView);
        return showAll ? moduleService.listAll() : moduleService.listByTeacher(teacher.getId());
    }

    private void updateSaveNotesAvailability(Module selectedModule) {
        boolean canEdit = isModuleOwnedByCurrentTeacher(selectedModule);
        view.getBtnSaveNotes().setEnabled(canEdit); // setEnabled(canEdit) habilita o bloquea el botón según si puede editar
    }

    private boolean isModuleOwnedByCurrentTeacher(Module module) {
        Teacher teacher = getCurrentTeacherOrNull();
        if (teacher == null || module == null) {
            return false;
        }
        Teacher moduleTeacher = module.getTeacher();
        return moduleTeacher != null
                && moduleTeacher.getId() != null
                && moduleTeacher.getId().equals(teacher.getId());
    }

    private Teacher getCurrentTeacherOrNull() {
        Object current = session.getCurrentUser();
        return current instanceof Teacher t ? t : null;
    }

	private String saveNote(Module selectedModule, int row) { 
        Long studentId = view.getStudentIdAtRow(row);
        if (studentId == null) {
            return "Fila " + (row + 1) + ": no se pudo identificar al alumno.";
        }
        Double grade = null;
        String gradeText = view.getGradeTextAtRow(row);
        if (gradeText != null && !gradeText.isBlank()) {
            try {
                grade = Double.valueOf(gradeText.replace(",", "."));
            } catch (NumberFormatException ex) {
                return "Fila " + (row + 1) + ": la nota debe ser un número válido.";
            }
        }
        try {
            enrollmentService.updateGrade(studentId, selectedModule.getId(), grade);
            return null;
        } catch (Exception ex) {
            return "Fila " + (row + 1) + ": " + ex.getMessage();
        }
	}

    private void saveAllNotes() {
        Module selectedModule = view.getSelectionModule();
        if (selectedModule == null) {
            view.showInfo("Seleccione un módulo antes de guardar notas.");
            return;
        }
        if (!isModuleOwnedByCurrentTeacher(selectedModule)) {
            // Esto no pasa porque solo ve sus módulos, pero por si acaso...
            view.showError("Solo puede modificar las notas de tus alumnos.");
            return;
        }
        if(view.getStudentTableModel().getRowCount() == 0) {
            view.showInfo("No hay alumnos para guardar notas.");
            return;
        }
        List<String> errors = new ArrayList<>();
        for(int row = 0; row < view.getStudentTableModel().getRowCount(); row++) {
            String error = saveNote(selectedModule, row);
            if (error != null) {
                errors.add(error);
            }
        }
        if(errors.isEmpty()) {
            view.showInfo("Todas las notas se han guardado correctamente.");
        } else {
            // Mostrar todos los errores juntos con saltos de línea .join("\n", errors)
            view.showError("Hubo errores al guardar:\n" + String.join("\n", errors));
        }   
        loadStudentsForSelectedModule();
    }

    private void logout() {
        session.clear();
        view.dispose();
        SwingUtilities.invokeLater(UiLauncher::showLogin); 
        // qué es SwingUtilities y por qué no simplemente UiLauncher.showLogin()?
    }

    private void handleFinishApp() {
        if (view.finishApp()) {
            System.exit(0);
        }
    }

    private Teacher resolveCurrentTeacher() {
        Object current = session.getCurrentUser();
        if (current instanceof Teacher teacher) {
            return teacher;
        }
        throw new IllegalStateException(
            "La sesión debe contener un profesor para mostrar la vista de docente.");
    }
}
