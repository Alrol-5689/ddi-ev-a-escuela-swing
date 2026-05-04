package com.primertrimestre.ui.controllers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.primertrimestre.auth.SessionContext;
import com.primertrimestre.model.Administrator;
import com.primertrimestre.model.Enrollment;
import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;
import com.primertrimestre.model.Teacher;
import com.primertrimestre.service.EnrollmentService;
import com.primertrimestre.service.ModuleService;
import com.primertrimestre.service.TeacherService;
import com.primertrimestre.ui.view.AdminMainFrame;

public class AdminController implements ActionListener {

    private AdminMainFrame view;
    private final SessionContext session;
    private final TeacherService teacherService;
    private final ModuleService moduleService;
	private final EnrollmentService enrollmentService;
    private List<Module> allModules = List.of();

    public AdminController(SessionContext session,
                           TeacherService teacherService,
                           ModuleService moduleService,
						   EnrollmentService enrollmentService) {
        this.session = session;
        this.teacherService = teacherService;
        this.moduleService = moduleService;
		this.enrollmentService = enrollmentService;
    }

	@Override
	public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
        	// HEADER
            case AdminMainFrame.CMD_LOGOUT -> logout();
            case AdminMainFrame.CARD_MODULES -> toModuleCard();
            case AdminMainFrame.CARD_TEACHERS -> toTeacherCard();
            // CARD -> create / delete modules
            case AdminMainFrame.CMD_CREATE_MODULE -> createModule();
            case AdminMainFrame.CMD_DELETE_MODULE -> deleteModule();
            // CARD -> assingn module to teacher
            case AdminMainFrame.CMD_ASSING_MODULE_TO_TEACHER -> assingModuleToTeacher();
            case AdminMainFrame.CMD_REMOVE_TEACHER_FROM_MODULE -> removeTeacherFromModule();
            // FOOTER
            case AdminMainFrame.CMD_REFRESH -> refresh();
            case AdminMainFrame.CMD_SAVE -> save();
        }
		
	}
	
    public void showAdminMainFrame() {
        Administrator admin = resolveCurrentAdministrator();
        view = new AdminMainFrame(admin);
        registerListeners();
        refresh();
        view.setVisible(true);
    }

    private void registerListeners() {
        view.getBtnLogout().addActionListener(this);
        view.getBtnManageModules().addActionListener(this);
        view.getBtnAssignTeachers().addActionListener(this);
        view.getBtnDeleteModule().addActionListener(this);
        view.getBtnCreateModule().addActionListener(this);
        view.getBtnAssignModuleToTeacher().addActionListener(this);
		view.getBtnAssignModuleToTeacher().setEnabled(false);
        view.getBtnRemoveTeacherFromModule().addActionListener(this);
		view.getBtnRemoveTeacherFromModule().setEnabled(false);
        view.getBtnRefresh().addActionListener(this);
        view.getBtnSave().addActionListener(this);
		/*
		view.addTeacherSelectionListener(e -> refreshModuleListsForSelectedTeacher()); */ 
		view.addTeacherSelectionListener( // combobox de selección de profesor
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshModuleListsForSelectedTeacher();
				}
			}
		);

		// Solo para el setEnabled de los botones de asignar / eliminar profesor-módulo:

		view.addSelectedUnassignedModuleListener(
			new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					boolean hasSelection = !view.getListUnassignedModules().isSelectionEmpty();
					//boolean hasSelection = view.getSelectedUnassignedModule() != null;
					view.getBtnAssignModuleToTeacher().setEnabled(hasSelection);
				}
			}
		);
		view.addSelectedAssignedModuleListener(
			new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					boolean hasSelection = !view.getListAssignedModules().isSelectionEmpty();
					//boolean hasSelection = view.getSelectedAssignedModule() != null;
					view.getBtnRemoveTeacherFromModule().setEnabled(hasSelection);
				}
			}
		);
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
        /* 
		Las acciones (crear, asignar, eliminar) invocan directamente a los servicios, 
		así que aquí sólo refrescamos la vista
		*/
		refresh();
		view.showInfo("No hay cambios pendientes: todo se guarda automáticamente al ejecutar cada acción.");
	}

	private void refresh() {
		loadTeachers();
		loadModules();
		refreshModuleListsForSelectedTeacher();
	}

	private void loadTeachers() {
		List<Teacher> teachers = teacherService.listTeachers();
		view.setTeachers(teachers);
	}

	private void loadModules() {
		allModules = moduleService.listAll();
		view.setAllModules(allModules);
	}

	private void refreshModuleListsForSelectedTeacher() {
		if (allModules == null) {
			allModules = List.of();
		}
		Teacher selectedTeacher = view.getSelectedTeacher();
		List<Module> unassigned = new ArrayList<>();
		List<Module> assignedToTeacher = new ArrayList<>();
		for (Module module : allModules) {
			if (module == null) continue;
			if (module.getTeacher() == null) {
				unassigned.add(module);
			} else if (selectedTeacher != null &&
					module.getTeacher() != null &&
					module.getTeacher().getId() != null &&
					module.getTeacher().getId().equals(selectedTeacher.getId())) {
				assignedToTeacher.add(module);
			}
		}
		view.setModulesWithoutTeacher(unassigned);
		view.setModulesForTeacher(assignedToTeacher);
	}

	private void removeTeacherFromModule() {
		Teacher selectedTeacher = (Teacher) view.getSelectedTeacher();
		if (selectedTeacher == null) {
			view.showInfo("Seleccione un profesor.");
			return;
		}
		Module selectedModule = (Module) view.getSelectedAssignedModule();
		if (selectedModule == null) {
			view.showInfo("Seleccione un módulo asignado al profesor.");
			return;
		}
		try {
			moduleService.removeTeacherFromModule(selectedModule.getId(), selectedTeacher.getId());
			loadModules();
			refreshModuleListsForSelectedTeacher();
		} catch (Exception ex) {
			view.showError(ex.getMessage());
		}
	}

	private void assingModuleToTeacher() {
		Module selectedModule = view.getSelectedUnassignedModule();
		if (selectedModule == null) {
			view.showInfo("Seleccione un módulo sin profesor.");
			return;
		}
		Teacher selectedTeacher = view.getSelectedTeacher();
		if (selectedTeacher == null) {
			view.showInfo("Seleccione un profesor.");
			return;
		}
		try {
			moduleService.assignTeacher(selectedModule.getId(), selectedTeacher.getId());
			loadModules();
			refreshModuleListsForSelectedTeacher();
		} catch (Exception ex) {
			view.showError(ex.getMessage());
		}
	}

	private void deleteModule() {
        Module selectedModule = view.getSelectedAllModulesModule();
        if (selectedModule == null) {
            view.showInfo("Seleccione un módulo de la lista general para eliminarlo.");
            return;
        }
        try {
            Teacher teacher = selectedModule.getTeacher();
            if (teacher != null && teacher.getId() != null) {
                moduleService.removeTeacherFromModule(selectedModule.getId(), teacher.getId());
            }
            List<Enrollment> enrollments = enrollmentService.listByModule(selectedModule.getId());
            for (Enrollment enrollment : enrollments) {
                Student student = enrollment.getStudent();
                if (student != null && student.getId() != null) {
                    enrollmentService.unenrollStudent(student.getId(), selectedModule.getId());
                }
            }
            moduleService.removeModule(selectedModule.getId());
            view.showInfo("Módulo eliminado correctamente.");
            loadModules();
            refreshModuleListsForSelectedTeacher();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
	}

	private void createModule() {
        String name = view.getModuleNameInput();
        String code = view.getModuleCodeInput();
        String creditsInput = view.getModuleCreditsInput();
		if(name == null || name.isBlank()) {
			view.showError("El nombre del módulo no puede estar vacío.");
			return;
		}
        if(code == null || code.isBlank()) {
			view.showError("El código del módulo no puede estar vacío.");
			return;
		}
		if(creditsInput == null || creditsInput.isBlank()) {
			view.showError("Los créditos del módulo no pueden estar vacíos.");
			return;
		}
        int credits;
        try {
            credits = Integer.parseInt(creditsInput);
        } catch (NumberFormatException ex) {
            view.showError("Los créditos deben ser un número entero.");
            return;
        }
        if (credits < 0) {
            view.showError("Los créditos del módulo no pueden ser negativos.");
            return;
        }
        try {
            moduleService.createModule(name, code, credits);
            view.showInfo("Módulo creado correctamente.");
            view.clearModuleForm();
            loadModules();
            refreshModuleListsForSelectedTeacher();
        } catch (Exception ex) {
            view.showError(ex.getMessage());
        }
	}

	private void toTeacherCard() {
		view.showTeachersCard();
	}

	private void toModuleCard() {
		view.showModulesCard();
	}

	private Administrator resolveCurrentAdministrator() {
		Object current = session.getCurrentUser();
		if (current instanceof Administrator admin) {
			return admin;
		}
		throw new IllegalStateException("La sesión debe contener un administrador autenticado.");
	}

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
