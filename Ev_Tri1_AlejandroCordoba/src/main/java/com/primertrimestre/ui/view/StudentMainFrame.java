package com.primertrimestre.ui.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import com.primertrimestre.model.Module;
import com.primertrimestre.model.Student;

import java.awt.FlowLayout;

public class StudentMainFrame extends JFrame {

	private static final long serialVersionUID = 1L;

    public static final String CMD_LOGOUT = "CMD_LOGOUT";
    public static final String CMD_ENROLL_IN_MODULE = "CMD_ENROLL_IN_MODULE";
    public static final String CMD_UNSUBSCRIBE_FROM_MODULE = "CMD_UNSUBSCRIBE_FROM_MODULE";
    public static final String CMD_REFRESH = "CMD_REFRESH";
    public static final String CMD_SAVE = "CMD_SAVE";
	
	private JPanel contentPane;
    private Student student;
    private final DefaultTableModel enrolledTableModel = 
        new DefaultTableModel(new Object[] {"ID", "CRÉDITOS", "NOMBRE", "CÓDIGO", "NOTA"}, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Todas las celdas no editables
            }
        };
    private final JTable enrolledTable = new JTable(enrolledTableModel);
	private final String[] enrolledRow = new String[5];
    private final DefaultListModel<Module> availableListModel = new DefaultListModel<>();
    private final JList<Module> availableList = new JList<>(availableListModel);
    private JButton btnSave;
    private JButton btnRefresh;
    private JButton btnLogout;
    private JButton btnEnrollInModule;
    private JButton btnUnsubscribeFromModule;
    private JLabel lblStudentName;
    
    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================

    public StudentMainFrame(Student student) {
    	if(student == null) {
    		throw new IllegalArgumentException(
                "La vista StudenMainFrame requiere un alumno válido");
    	}
    	setStudent(student);
    	
    	setTitle("Ventana de alumnos - " 
            + (student.getFullName() != null ? student.getFullName() : student.getUsername()));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(850, 500));
		setBounds(100, 100, 0, 0); //==>> sabiendo que se abrirá al tamaño mínimo de setMinimumSize

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

        buildHeader();
        buildMainPanels();
        buildFooter();

    }

    // ========================================================================
    // BUILD UI
    // ========================================================================

    private void buildHeader() {
		JPanel header = new JPanel();
		header.setBorder(new EmptyBorder(10, 15, 0, 15));
		contentPane.add(header, BorderLayout.NORTH);
		header.setLayout(new BorderLayout(0, 0));
		
		lblStudentName = new JLabel(resolveStudentDisplayName(student));
		header.add(lblStudentName, BorderLayout.WEST);
		
		btnLogout = new JButton("Cerrar sesion");
		btnLogout.setActionCommand(CMD_LOGOUT);
		header.add(btnLogout, BorderLayout.EAST);
    }
    
    private void buildMainPanels() {
		JPanel mainCenterPanel = new JPanel();
		contentPane.add(mainCenterPanel, BorderLayout.CENTER);
		mainCenterPanel.setLayout(new GridLayout(0, 2, 20, 20));  
        mainCenterPanel.add(buildAvailableModulesPanel()); 
		mainCenterPanel.add(buildEnrolledModulesPanel());	
    }
    
    private JPanel buildAvailableModulesPanel() {
		JPanel availableModulesPanel = new JPanel();
		availableModulesPanel.setBorder(new EmptyBorder(100, 5, 100, 5));
		availableModulesPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel availablesPanelNorth = new JPanel();
		availableModulesPanel.add(availablesPanelNorth, BorderLayout.NORTH);
		
		JLabel lblAvailableTitle = new JLabel("MÓDULOS DISPONIBLES");
		availablesPanelNorth.add(lblAvailableTitle);
		
		JScrollPane scrollPaneAvailableModules = new JScrollPane();
		availableModulesPanel.add(scrollPaneAvailableModules, BorderLayout.CENTER);
		
		availableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneAvailableModules.setViewportView(availableList);
		
		JPanel availablePanelSouth = new JPanel();
		FlowLayout flowLayout = (FlowLayout) availablePanelSouth.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		availableModulesPanel.add(availablePanelSouth, BorderLayout.SOUTH);
		
		btnEnrollInModule = new JButton("Inscribirse en el módulo seleccionado  -->");
		btnEnrollInModule.setActionCommand(CMD_ENROLL_IN_MODULE);
		availablePanelSouth.add(btnEnrollInModule);

        return availableModulesPanel;
    }
    
    private JPanel buildEnrolledModulesPanel() {
		JPanel enrolledModulesPanel = new JPanel();
		enrolledModulesPanel.setBorder(new EmptyBorder(100, 5, 100, 5));

		enrolledModulesPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel enrolledNorthPanel = new JPanel();
		enrolledModulesPanel.add(enrolledNorthPanel, BorderLayout.NORTH);
		
		JLabel lblEnrolledTitle = new JLabel("MÓDULOS EN LOS QUE ESTÁS INSCRITO");
		enrolledNorthPanel.add(lblEnrolledTitle);
		
		JScrollPane scrollPaneEnrolledModules = new JScrollPane();
		enrolledModulesPanel.add(scrollPaneEnrolledModules, BorderLayout.CENTER);
		
		enrolledTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollPaneEnrolledModules.setViewportView(enrolledTable);
		
		JPanel enrolledPanelSouth = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) enrolledPanelSouth.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		enrolledModulesPanel.add(enrolledPanelSouth, BorderLayout.SOUTH);
		
		btnUnsubscribeFromModule = new JButton("<-- Abandonar el módulo seleccionado");
		btnUnsubscribeFromModule.setActionCommand(CMD_UNSUBSCRIBE_FROM_MODULE);
		enrolledPanelSouth.add(btnUnsubscribeFromModule);

        return enrolledModulesPanel;
    }

    private void buildFooter() {
		JPanel footer = new JPanel();
		contentPane.add(footer, BorderLayout.SOUTH);
		footer.setLayout(new GridLayout(0, 2, 20, 0));
		
		btnRefresh = new JButton("Refrescar");
		btnRefresh.setActionCommand(CMD_REFRESH);
		footer.add(btnRefresh);
		
		btnSave = new JButton("Guardar");
		btnSave.setActionCommand(CMD_SAVE);
		footer.add(btnSave);
    }
    
    // ========================================================================
    // METHODS (STUDENTS AND MODULES)
    // ========================================================================

	public Module getSelectedAvailableModule() {
		return availableList.getSelectedValue();
	}

    private String resolveStudentDisplayName(Student student) {
        if (student == null) return "Profesor desconocido";
        return student.getFullName() != null && !student.getFullName().isBlank()
                ? student.getFullName()
                : student.getUsername();
    }

	public void addAvailableModuleSelectionListener(ListSelectionListener listener) {
		// availableList recibe un ListSelectionListener del controller 
        // Permite al controller enterarse cuando el alumno selecciona un módulo en la lista.
		availableList.addListSelectionListener(listener);
	}

	public void addEnrolledModuleSelectionListener(ListSelectionListener listener) {
		// El controller se suscribe aquí para saber cuándo el alumno selecciona
		// un módulo de la tabla de la derecha.
		enrolledTable.getSelectionModel().addListSelectionListener(listener);
	}

	public void removeModuleFromEnrolledModules(Long moduleId) {
		for (int i = 0; i < enrolledTableModel.getRowCount(); i++) {
			String idStr = (String) enrolledTableModel.getValueAt(i, 0);
			if (idStr != null && !idStr.isBlank()) {
				Long id = Long.parseLong(idStr);
				if (id.equals(moduleId)) {
					enrolledTableModel.removeRow(i);
					return;
				}
			}
		}
	}

	public void clearEnrolledModules() {
		enrolledTableModel.setRowCount(0);
	}

	public void setAvailableModules(List<Module> modules) {
    	availableListModel.clear();
    	if (modules == null) return;
    	modules.forEach(m -> availableListModel.addElement(m));
	}

	public void addEnrolledModuleRow(Long id, Integer credits, String name, 
			String code, Double grade) {
		enrolledRow[0] = id != null ? id.toString() : "";
		enrolledRow[1] = credits != null ? credits.toString() : "";
		enrolledRow[2] = name != null ? name : "";
		enrolledRow[3] = code != null ? code : "";
		enrolledRow[4] = grade != null ? grade.toString() : "";
		enrolledTableModel.addRow(enrolledRow.clone());
	}
	// public void addEnrolledModuleRow(Object[] rowData) {
	// 	enrolledTableModel.addRow(rowData);
	// }
    
    // ========================================================================
    // getters & setters
    // ========================================================================

	public Student getStudent() {return student;}
	public JTable getEnrolledTable() {return enrolledTable;}
	public JList<Module> getAvailableList() {return availableList;}
	public JButton getBtnSave() {return btnSave;}
	public JButton getBtnRefresh() {return btnRefresh;}
	public JButton getBtnLogout() {return btnLogout;}
	public JButton getBtnEnrollInModule() {return btnEnrollInModule;}
	public JButton getBtnUnsubscribeFromModule() {return btnUnsubscribeFromModule;}
	public JLabel getLblStudentName() {return lblStudentName;}
	
	public void setStudent(Student student) {
		this.student = student;
		if(lblStudentName != null) {
			lblStudentName.setText(resolveStudentDisplayName(student));
		}
	}

    // ========================================================================
    // HELPERS DIALOG
    // ========================================================================

    public boolean finishApp() {
        // showConfirmDialog: bloquea hasta que el usuario responde, muestra un modal con botones predefinidos
        int option = JOptionPane.showConfirmDialog(
                this, // componente padre para centrar el diálogo
                "¿Deseas cerrar la aplicación?", // mensaje a mostrar
                "Cancelar registro", // título de la ventana del diálogo
                JOptionPane.YES_NO_OPTION, // botones Sí y No
                JOptionPane.QUESTION_MESSAGE); // icono de pregunta
        // Devuelve true solo si el usuario pulsa Sí; en otro caso false
        return option == JOptionPane.YES_OPTION;
    }
    
    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
	
}
