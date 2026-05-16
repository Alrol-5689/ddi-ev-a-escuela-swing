package com.primertrimestre.ui.view;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.JButton;

import java.awt.Dimension;

import javax.swing.ImageIcon;

public class LoginWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	
    // Commands --> Necesito que sean public para usarlos en el controller porque el switch no me deja usar get... y no quiro usar cadenas if
	public static final String CMD_LOGIN = "LOGIN";
	public static final String CMD_CLEAR = "CLEAR";
	public static final String CMD_SINGUP = "SINGUP";
    
	private JPanel contentPane;
	private JPasswordField pass;
	private JTextField user;
	private JButton btnLogin;
	private JButton btnClear;
	private JButton btnSingUp;
	private JComboBox<String> userType;

	public LoginWindow() {
		setResizable(false);
		
		setTitle("Ventana de inicio");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setMinimumSize(new Dimension(612, 392));
		setBounds(100, 100, 612, 392);
		setLocationRelativeTo(null); // centrar 
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblUser = new JLabel("Usuario: ");
		lblUser.setBounds(45, 66, 61, 16);
		contentPane.add(lblUser);
		
		JLabel lblPass = new JLabel("Contraseña: ");
		lblPass.setBounds(45, 112, 79, 16);
		contentPane.add(lblPass);
		
		pass = new JPasswordField();
		pass.setBounds(136, 107, 147, 26);
		contentPane.add(pass);
		
		user = new JTextField();
		user.setBounds(136, 62, 147, 26);
		contentPane.add(user);
		user.setColumns(10);
		
		JLabel lblCargo = new JLabel("Cargo: ");
		lblCargo.setBounds(45, 158, 61, 16);
		contentPane.add(lblCargo);
		
		userType = new JComboBox<>();
		userType.setBounds(136, 154, 147, 27);
		userType.addItem("Seleccione");
		userType.addItem("Alumno");
		userType.addItem("Profesor");
		userType.addItem("Administrador");
		contentPane.add(userType);
		
		btnClear = new JButton("Limpiar");
		btnClear.setActionCommand(CMD_CLEAR);
		btnClear.setBounds(108, 305, 111, 29);
		contentPane.add(btnClear);
		
		btnLogin = new JButton("Enviar");
		btnLogin.setActionCommand(CMD_LOGIN);
		btnLogin.setBounds(259, 305, 117, 29);
		contentPane.add(btnLogin);
		
		btnSingUp = new JButton("Inscribirse");
        btnSingUp.setActionCommand(CMD_SINGUP);
        btnSingUp.setBounds(413, 305, 112, 29);
        contentPane.add(btnSingUp);
        
        JLabel lblNewLabel = new JLabel("New label");
        lblNewLabel.setIcon(new ImageIcon(LoginWindow.class.getResource("/img/256x256bb.jpg")));
        lblNewLabel.setBounds(324, 24, 258, 256);
        contentPane.add(lblNewLabel);

	}

	public JButton getBtnLogin() {return btnLogin;}
	public JButton getBtnSingUp() {return btnSingUp;}
	public JButton getBtnClear() {return btnClear;}

	public void clearForm() {
        user.setText("");
        pass.setText("");
        userType.setSelectedIndex(0);
        user.requestFocus();
	}

	public String getUserText() {
		return user.getText().trim();
	}

	public String getPasswordText() {
		return new String(pass.getPassword());
	}

	public String getSelectedUserType() {
		Object selected = userType.getSelectedItem();
		return selected != null ? selected.toString() : null;
	}

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

	public void showError(String message) {
		JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
