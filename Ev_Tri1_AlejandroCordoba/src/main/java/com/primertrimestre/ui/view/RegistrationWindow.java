package com.primertrimestre.ui.view;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

public class RegistrationWindow extends JFrame {

    private static final long serialVersionUID = 1L;
    public static final String CMD_REGISTER = "REGISTER";
    public static final String CMD_CANCEL = "CANCEL";

    private final JComboBox<String> roleCombo = new JComboBox<>(new String[] { "Alumno", "Profesor", "Administrador" });
    private final JTextField usernameField = new JTextField(20);
    private final JTextField fullNameField = new JTextField(20);
    private final JPasswordField passwordField = new JPasswordField(20);
    private final JPasswordField confirmPasswordField = new JPasswordField(20);
    private JButton registerButton;
    private JButton cancelButton;

    public RegistrationWindow() {
        setTitle("Registro de usuario");
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        initComponents();
        pack(); //  hace que el JFrame calcule su tamaño mínimo adecuado según el layout 
        setLocationRelativeTo(null); // null --> En el centor del monitor principal
    }

    private void initComponents() {
        JPanel content = new JPanel(new BorderLayout(10, 10));
        content.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setContentPane(content);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.add(new JLabel("Rol:"));
        form.add(roleCombo);

        form.add(new JLabel("Usuario:"));
        form.add(usernameField);

        form.add(new JLabel("Nombre completo:"));
        form.add(fullNameField);

        form.add(new JLabel("Contraseña:"));
        form.add(passwordField);

        form.add(new JLabel("Repetir contraseña:"));
        form.add(confirmPasswordField);

        content.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        registerButton = new JButton("Registrar");
        registerButton.setActionCommand(CMD_REGISTER);
        cancelButton = new JButton("Cancelar");
        cancelButton.setActionCommand(CMD_CANCEL);
        buttons.add(registerButton);
        buttons.add(cancelButton);

        content.add(buttons, BorderLayout.SOUTH);
    }

    public JButton getBtnRegister() { return registerButton; }
    public JButton getBtnCancel() { return cancelButton; }

    public String getUsername() { return usernameField.getText().trim(); }
    public String getFullName() { return fullNameField.getText().trim(); }
    public String getPassword() { return new String(passwordField.getPassword()); }
    public String getConfirmPassword() { return new String(confirmPasswordField.getPassword()); }
    public String getSelectedRole() { return (String) roleCombo.getSelectedItem(); }

    public void clearForm() {
        usernameField.setText("");
        fullNameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        roleCombo.setSelectedIndex(0);
        usernameField.requestFocus();
    }

    public boolean confirmCancel() {
        // showConfirmDialog: bloquea hasta que el usuario responde, muestra un modal con botones predefinidos
        int option = JOptionPane.showConfirmDialog(
                this, // componente padre para centrar el diálogo
                "¿Cancelar el registro y volver al inicio de sesión?", // mensaje a mostrar
                "Cancelar registro", // título de la ventana del diálogo
                JOptionPane.YES_NO_OPTION, // botones Sí y No
                JOptionPane.QUESTION_MESSAGE); // icono de pregunta
        // Devuelve true solo si el usuario pulsa Sí; en otro caso false
        return option == JOptionPane.YES_OPTION;
    }

    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
}
