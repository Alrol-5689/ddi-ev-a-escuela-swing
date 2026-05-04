package com.primertrimestre.ui.view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionListener;

import java.awt.event.ActionListener;

import com.primertrimestre.model.Administrator;
import com.primertrimestre.model.Module;
import com.primertrimestre.model.Teacher;
import java.awt.Color;
import java.awt.Dimension;

public class AdminMainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Commands --> Necesito que sean public para usarlos en el controller porque el switch no me deja usar get... y no quiro usar cadenas if
    
    // HEADER
    public static final String CARD_MODULES  = "CARD_MODULES";
    public static final String CARD_TEACHERS = "CARD_TEACHERS";
	public static final String CMD_LOGOUT = "LOGOUT";
	// CARD -> create / delete modules
	public static final String CMD_CREATE_MODULE = "CREATE_MODULE";
    public static final String CMD_DELETE_MODULE = "DELETE_MODULE";
    // CARD -> assingn module to teacher
    public static final String CMD_ASSING_MODULE_TO_TEACHER = "CMD_ASSING_MODULE_TO_TEACHER";
    public static final String CMD_REMOVE_TEACHER_FROM_MODULE = "CMD_REMOVE_TEACHER_FROM_MODULE";
    // FOOTER
	public static final String CMD_REFRESH = "REFRESH";
    public static final String CMD_SAVE = "SAVE";

    // ==== ROOT ====
    private JPanel contentPane;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    // ==== HEADER ====
    private JLabel lblAdminName;
    private JButton btnLogout;

    // ==== NAV ====
    private JButton btnManageModules;
    private JButton btnAssignTeachers;

    // ==== CARD 1: GESTIÓN DE MÓDULOS ====
    private DefaultListModel<Module> allModulesListModel = new DefaultListModel<>();
    private JList<Module> listAllModules;
    private JButton btnDeleteModule;

    private JTextField txtModuleName;
    private JTextField txtModuleCode;
    private JTextField txtModuleCredits;
    private JButton btnCreateModule;

    // ==== CARD 2: ASIGNAR PROFESORES ====
    private JComboBox<Teacher> comboBoxTeachers;
    private DefaultListModel<Module> modulesWithoutTeacherModel = new DefaultListModel<>();
    private DefaultListModel<Module> modulesForTeacherModel    = new DefaultListModel<>();
    
    private JList<Module> listUnassignedModules;
    private JList<Module> listAssignedModules;

    private JButton btnAssignModuleToTeacher;
    private JButton btnRemoveTeacherFromModule;

    // ==== FOOTER ====
    private JButton btnRefresh;
    private JButton btnSave;

    // ========================================================================
    // CONSTRUCTOR
    // ========================================================================

    public AdminMainFrame(Administrator admin) {
        if (admin == null) {
            throw new IllegalArgumentException("La vista AdminMainFrame requiere un administrador válido");
        }
        setTitle("Panel administrador - " + (admin.getFullName() != null ? admin.getFullName() : admin.getUsername()));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setMinimumSize(new Dimension(720, 700));
        //setSize(900, 500);
        setBounds(100, 100, 850, 0); //==>> setMinimumSize
        setLocationRelativeTo(null);

        buildRoot();
        buildHeader(admin);
        buildCenter();
        buildFooter();
    }

    // ========================================================================
    // BUILD UI
    // ========================================================================

    private void buildRoot() {
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);
    }

    private void buildHeader(Administrator admin) {
        JPanel header = new JPanel(new BorderLayout(0, 0));
        header.setBorder(new EmptyBorder(0, 12, 60, 12));
        contentPane.add(header, BorderLayout.NORTH);

        lblAdminName = new JLabel(
                admin.getFullName() != null && !admin.getFullName().isBlank()
                        ? admin.getFullName()
                        : admin.getUsername()
        );
        lblAdminName.setHorizontalAlignment(SwingConstants.LEFT);
        lblAdminName.setBorder(new EmptyBorder(0, 10, 0, 10));
        header.add(lblAdminName, BorderLayout.WEST);

        btnLogout = new JButton("Cerrar sesión");
        btnLogout.setActionCommand(CMD_LOGOUT);
        header.add(btnLogout, BorderLayout.EAST);
    }

    private void buildCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 0));
        contentPane.add(center, BorderLayout.CENTER);

        // Panel de botones para cambiar de card
        JPanel cardChangePanel = new JPanel();
        cardChangePanel.setBorder(new EmptyBorder(0, 0, 35, 0));
        FlowLayout fl_cardChangePanel = (FlowLayout) cardChangePanel.getLayout();
        fl_cardChangePanel.setAlignment(FlowLayout.LEFT);
        center.add(cardChangePanel, BorderLayout.NORTH);

        btnManageModules = new JButton("Gestionar módulos");
        btnManageModules.setActionCommand(CARD_MODULES);
        cardChangePanel.add(btnManageModules);

        btnAssignTeachers = new JButton("Asignar profesores");
        btnAssignTeachers.setActionCommand(CARD_TEACHERS);
        cardChangePanel.add(btnAssignTeachers);

        // Panel con CardLayout
        cardLayout = new CardLayout(0, 0);
        cardPanel = new JPanel(cardLayout);
        center.add(cardPanel, BorderLayout.CENTER);

        cardPanel.add(buildModulesCard(), CARD_MODULES);
        cardPanel.add(buildTeachersCard(), CARD_TEACHERS);

        cardLayout.show(cardPanel, CARD_MODULES);
    }

    private JPanel buildModulesCard() {
        JPanel modulesCardPanel = new JPanel(new GridLayout(1, 2, 10, 0));

        // --- Lado izquierdo: lista de módulos + eliminar ---
        JPanel modulesListPanel = new JPanel(new BorderLayout(0, 0));
        modulesListPanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        modulesCardPanel.add(modulesListPanel);

        JPanel modulesListHeaderPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        modulesListHeaderPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        modulesListPanel.add(modulesListHeaderPanel, BorderLayout.NORTH);

        JLabel lblModulesList = new JLabel("MÓDULOS EXISTENTES");
        modulesListHeaderPanel.add(lblModulesList);

        listAllModules = new JList<>(allModulesListModel);
        listAllModules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane modulesScrollPane = new JScrollPane(listAllModules);
        modulesListPanel.add(modulesScrollPane, BorderLayout.CENTER);

        JPanel modulesListFooterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnDeleteModule = new JButton("Eliminar módulo");
        btnDeleteModule.setActionCommand(CMD_DELETE_MODULE);
        btnDeleteModule.setBackground(new Color(255, 162, 155));
        modulesListFooterPanel.add(btnDeleteModule);
        modulesListPanel.add(modulesListFooterPanel, BorderLayout.SOUTH);

        // --- Lado derecho: crear nuevo módulo ---
        JPanel createModulePanel = new JPanel(new BorderLayout(0, 0));
        createModulePanel.setBorder(new EmptyBorder(0, 15, 0, 15));
        modulesCardPanel.add(createModulePanel);

        JPanel createModuleHeader = new JPanel(new GridLayout(2, 1, 0, 0));
        createModulePanel.add(createModuleHeader, BorderLayout.NORTH);

        JPanel createModuleTitlePanel = new JPanel();
        createModuleHeader.add(createModuleTitlePanel);

        JLabel lblCreateModuleTitle = new JLabel("CREAR NUEVO MÓDULO");
        lblCreateModuleTitle.setBorder(new EmptyBorder(100, 0, 0, 0));
        createModuleTitlePanel.add(lblCreateModuleTitle);

        JPanel createModuleFormPanel = new JPanel(new BorderLayout(0, 0));
        createModuleHeader.add(createModuleFormPanel);

        JPanel createModuleLabelsPanel = new JPanel(new GridLayout(3, 1, 50, 0));
        createModuleLabelsPanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        createModuleFormPanel.add(createModuleLabelsPanel, BorderLayout.WEST);

        JLabel lblModuleNameLabel = new JLabel("Nombre:   ");
        lblModuleNameLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblModuleNameLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        createModuleLabelsPanel.add(lblModuleNameLabel);

        JLabel lblModuleCodeLabel = new JLabel("Código:   ");
        lblModuleCodeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblModuleCodeLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        createModuleLabelsPanel.add(lblModuleCodeLabel);

        JLabel lblModuleCreditsLabel = new JLabel("Créditos ETCS:   ");
        lblModuleCreditsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        lblModuleCreditsLabel.setBorder(new EmptyBorder(20, 0, 20, 0));
        createModuleLabelsPanel.add(lblModuleCreditsLabel);

        JPanel createModuleFieldsPanel = new JPanel(new GridLayout(3, 1, 0, 0));
        createModuleFormPanel.add(createModuleFieldsPanel, BorderLayout.CENTER);

        txtModuleName = new JTextField();
        txtModuleName.setColumns(10);
        createModuleFieldsPanel.add(txtModuleName);

        txtModuleCode = new JTextField();
        txtModuleCode.setColumns(10);
        createModuleFieldsPanel.add(txtModuleCode);

        txtModuleCredits = new JTextField();
        txtModuleCredits.setColumns(10);
        createModuleFieldsPanel.add(txtModuleCredits);

        // Panel center vacío
        JPanel createModuleCenterPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        createModulePanel.add(createModuleCenterPanel, BorderLayout.CENTER);

        JPanel createModuleFooterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createModulePanel.add(createModuleFooterPanel, BorderLayout.SOUTH);

        btnCreateModule = new JButton("Crear módulo");
        btnCreateModule.setActionCommand(CMD_CREATE_MODULE);
        btnCreateModule.setBackground(new Color(132, 255, 140));
        createModuleFooterPanel.add(btnCreateModule);

        return modulesCardPanel;
    }

    private JPanel buildTeachersCard() {
        JPanel teacherAssignmentCardPanel = new JPanel(new BorderLayout(0, 0));

        JPanel teacherHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        teacherHeaderPanel.setBorder(new EmptyBorder(0, 12, 0, 0));
        teacherAssignmentCardPanel.add(teacherHeaderPanel, BorderLayout.NORTH);

        JLabel lblTeacher = new JLabel("Profesor: ");
        teacherHeaderPanel.add(lblTeacher);

        comboBoxTeachers = new JComboBox<>();
        teacherHeaderPanel.add(comboBoxTeachers);

        JPanel teacherCenterPanel = new JPanel(new GridLayout(0, 2, 10, 0));
        teacherAssignmentCardPanel.add(teacherCenterPanel, BorderLayout.CENTER);

        // --- Columna izquierda: módulos sin profesor ---
        JPanel unassignedModulesPanel = new JPanel(new BorderLayout(0, 0));
        teacherCenterPanel.add(unassignedModulesPanel);

        JPanel unassignedHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblUnassignedModules = new JLabel("Módulos sin profesor asignado:");
        unassignedHeaderPanel.add(lblUnassignedModules);
        unassignedModulesPanel.add(unassignedHeaderPanel, BorderLayout.NORTH);

        listUnassignedModules = new JList<>(modulesWithoutTeacherModel);
        listUnassignedModules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane unassignedScrollPane = new JScrollPane(listUnassignedModules);
        unassignedModulesPanel.add(unassignedScrollPane, BorderLayout.CENTER);

        JPanel unassignedFooterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnAssignModuleToTeacher = new JButton("Asignar este módulo a este profesor  -->");
        btnAssignModuleToTeacher.setActionCommand(CMD_ASSING_MODULE_TO_TEACHER);
        unassignedFooterPanel.add(btnAssignModuleToTeacher);
        unassignedModulesPanel.add(unassignedFooterPanel, BorderLayout.SOUTH);

        // --- Columna derecha: módulos asignados al profesor ---
        JPanel assignedModulesPanel = new JPanel(new BorderLayout(0, 0));
        teacherCenterPanel.add(assignedModulesPanel);

        JPanel assignedHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel lblAssignedModules = new JLabel("Módulos asignados a este profesor:");
        assignedHeaderPanel.add(lblAssignedModules);
        assignedModulesPanel.add(assignedHeaderPanel, BorderLayout.NORTH);

        listAssignedModules = new JList<>(modulesForTeacherModel);
        listAssignedModules.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane assignedScrollPane = new JScrollPane(listAssignedModules);
        assignedModulesPanel.add(assignedScrollPane, BorderLayout.CENTER);

        JPanel assignedFooterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRemoveTeacherFromModule = new JButton("<--  Eliminar profesor del módulo seleccionado");
        btnRemoveTeacherFromModule.setActionCommand(CMD_REMOVE_TEACHER_FROM_MODULE);
        assignedFooterPanel.add(btnRemoveTeacherFromModule);
        assignedModulesPanel.add(assignedFooterPanel, BorderLayout.SOUTH);

        return teacherAssignmentCardPanel;
    }

    private void buildFooter() {
        JPanel south = new JPanel(new GridLayout(0, 2, 0, 0));
        south.setBorder(new EmptyBorder(75, 12, 0, 12));
        contentPane.add(south, BorderLayout.SOUTH);

        btnRefresh = new JButton("Refrescar");
        btnRefresh.setActionCommand(CMD_REFRESH);
        south.add(btnRefresh);

        btnSave = new JButton("Guardar");
        btnSave.setActionCommand(CMD_SAVE);
        south.add(btnSave);
    }

    // ========================================================================
    // PUBLIC API FOR CONTROLLER
    // ========================================================================



    public void showModulesCard() {
        cardLayout.show(cardPanel, CARD_MODULES);
    }

    public void showTeachersCard() {
        cardLayout.show(cardPanel, CARD_TEACHERS);
    }

    public void setAdminDisplayName(String displayName) {
        lblAdminName.setText(displayName != null && !displayName.isBlank() ? displayName : "Administrador");
    }

    public void setAllModules(List<Module> modules) {
        allModulesListModel.clear();
        if (modules == null) return;
        modules.forEach(allModulesListModel::addElement);
    }

    public void setModulesWithoutTeacher(List<Module> modules) {
        modulesWithoutTeacherModel.clear();
        if (modules == null) return;
        modules.forEach(modulesWithoutTeacherModel::addElement);
    }

    public void setModulesForTeacher(List<Module> modules) {
        modulesForTeacherModel.clear();
        if (modules == null) return;
        modules.forEach(modulesForTeacherModel::addElement);
    }

    public void setTeachers(List<Teacher> teachers) {
        comboBoxTeachers.removeAllItems();
        if (teachers == null) return;
        for (Teacher teacher : teachers) {
            comboBoxTeachers.addItem(teacher);
        }
        if (comboBoxTeachers.getItemCount() > 0) {
            comboBoxTeachers.setSelectedIndex(0);
        }
    }
    // // NO SE ESTÁ USANDO --> PENSAR SI QUITARLO O DARLE USO
    // public void setSelectedTeacher(Teacher teacher) {
    //     // Limpia la selección si no hay profesor nuevo para evitar mostrar datos antiguos
    //     if (teacher == null) {
    //         // -1 --> desselecciona todo y el comboBox queda vacío
    //         comboBoxTeachers.setSelectedIndex(-1);
    //         return;
    //     }
    //     // Swing compara por equals, así que vemos esta instancia que le pasamos 
    //     comboBoxTeachers.setSelectedItem(teacher);
    // }

    public Teacher getSelectedTeacher() {
        return (Teacher) comboBoxTeachers.getSelectedItem();
    }

    public Module getSelectedAllModulesModule() {
        return listAllModules.getSelectedValue();
    }

    public Module getSelectedUnassignedModule() {
        return listUnassignedModules.getSelectedValue();
    }

    public Module getSelectedAssignedModule() {
        return listAssignedModules.getSelectedValue();
    }

    public String getModuleNameInput() {
        return txtModuleName.getText().trim();
    }

    public String getModuleCodeInput() {
        return txtModuleCode.getText().trim();
    }

    public String getModuleCreditsInput() {
        return txtModuleCredits.getText().trim();
    }

    public void clearModuleForm() {
        txtModuleName.setText("");
        txtModuleCode.setText("");
        txtModuleCredits.setText("");
    }

    public void addTeacherSelectionListener(ActionListener listener) {
        comboBoxTeachers.addActionListener(listener);
    }

    public void addSelectedUnassignedModuleListener(ListSelectionListener listener) {
        listUnassignedModules.getSelectionModel().addListSelectionListener(listener);
    }

    public void addSelectedAssignedModuleListener(ListSelectionListener listener) {
        listAssignedModules.getSelectionModel().addListSelectionListener(listener);
    }

    public JList<Module> getListUnassignedModules() {return listUnassignedModules;}
    public JList<Module> getListAssignedModules() {return listAssignedModules;}

    // ========================================================================
    // GETTERS BOTONES
    // ========================================================================

    public JButton getBtnLogout() { return btnLogout; }
    public JButton getBtnManageModules() { return btnManageModules; }
    public JButton getBtnAssignTeachers() { return btnAssignTeachers; }
    public JButton getBtnDeleteModule() { return btnDeleteModule; }
    public JButton getBtnCreateModule() { return btnCreateModule; }
    public JButton getBtnAssignModuleToTeacher() { return btnAssignModuleToTeacher; }
    public JButton getBtnRemoveTeacherFromModule() { return btnRemoveTeacherFromModule; }
    public JButton getBtnRefresh() { return btnRefresh; }
    public JButton getBtnSave() { return btnSave; }

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
