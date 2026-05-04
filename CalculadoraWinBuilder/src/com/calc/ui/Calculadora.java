package com.calc.ui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import javax.swing.JTextField;
import java.awt.GridLayout;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

public class Calculadora extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField textField;
	private JButton btnAC;
	private JButton btnC;
	private JButton btnMasMenos;
	private JButton btnPercent;
	private JButton btn_7;
	private JButton btn_8;
	private JButton btn_9;
	private JButton btnDivision;
	private JButton btn_4;
	private JButton btn_5;
	private JButton btn_6;
	private JButton btnMultiplication;
	private JButton btn_1;
	private JButton btn_2;
	private JButton btn_3;
	private JButton btnSubtraction;
	private JButton btn_0;
	private JButton btnPoint;
	private JButton btnEqual;
	private JButton btnSum;
	
	// --- Estado de la calculadora ---
	private double acumulado = 0.0;
	private String operador = null; // "+", "-", "*", "/"
	private boolean nuevaEntrada = true;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Calculadora frame = new Calculadora();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public Calculadora() {

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 290, 444);
		
		// contentPane
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		// textField
		
		textField = new JTextField();
		contentPane.add(textField, BorderLayout.NORTH);
		textField.setColumns(10);
		textField.setHorizontalAlignment(JTextField.RIGHT);
		textField.setFont(new Font("SansSerif", Font.BOLD, 24));
		textField.setText("0"); // estado inicial
		
		// panel
	
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new GridLayout(5, 4, 5, 5));

		// --- Crear botones  ---
		
		btnAC = new JButton("AC");
		btnAC.setBackground(new Color(255, 119, 113));
		btnAC.setOpaque(true); 
		btnAC.setContentAreaFilled(true); 
		panel.add(btnAC);
		
		btnC = new JButton("C");
		btnC.setBackground(new Color(255, 171, 167));
		btnC.setOpaque(true); 
		btnC.setContentAreaFilled(true); 
		panel.add(btnC);
		
		btnMasMenos = new JButton("±");
		btnMasMenos.setBackground(new Color(187, 239, 255));
		btnMasMenos.setOpaque(true); 
		btnMasMenos.setContentAreaFilled(true); 
		panel.add(btnMasMenos);
		
		btnPercent = new JButton("%");
		btnPercent.setBackground(new Color(187, 239, 255));
		btnPercent.setOpaque(true); 
		btnPercent.setContentAreaFilled(true); 
		panel.add(btnPercent);
		
		btn_7 = new JButton("7"); panel.add(btn_7);
		btn_8 = new JButton("8"); panel.add(btn_8);
		btn_9 = new JButton("9"); panel.add(btn_9);
		
		btnDivision = new JButton("/");
		btnDivision.setBackground(new Color(187, 239, 255));
		btnDivision.setOpaque(true); 
		btnDivision.setContentAreaFilled(true); 
		panel.add(btnDivision);
		
		btn_4 = new JButton("4");  panel.add(btn_4);
		btn_5 = new JButton("5"); panel.add(btn_5);
		btn_6 = new JButton("6"); panel.add(btn_6);
		
		btnMultiplication = new JButton("*");
		btnMultiplication.setBackground(new Color(187, 239, 255));
		btnMultiplication.setOpaque(true); 
		btnMultiplication.setContentAreaFilled(true);
		panel.add(btnMultiplication);
		
		btn_1 = new JButton("1"); panel.add(btn_1);
		btn_2 = new JButton("2"); panel.add(btn_2);
		btn_3 = new JButton("3"); panel.add(btn_3);
		
		btnSubtraction = new JButton("-");
		btnSubtraction.setBackground(new Color(187, 239, 255));
		btnSubtraction.setOpaque(true); 
		btnSubtraction.setContentAreaFilled(true); 
		panel.add(btnSubtraction);
		
		btn_0 = new JButton("0"); panel.add(btn_0);
		btnPoint = new JButton("."); panel.add(btnPoint);
		
		btnEqual = new JButton("=");
		btnEqual.setBackground(new Color(170, 196, 255));
		btnEqual.setOpaque(true); 
		btnEqual.setContentAreaFilled(true);
		panel.add(btnEqual);
		
		btnSum = new JButton("+");
		btnSum.setBackground(new Color(187, 239, 255));
		btnSum.setOpaque(true); 
		btnSum.setContentAreaFilled(true); 
		panel.add(btnSum);
		
		// --- Asignar listeners a los botones ---
		// lambda ("e" es un objeto/evento ActionEvent que implementa la interfaz ActionListener)

        ActionListener numberListener = e -> {
            JButton src = (JButton) e.getSource(); // e.getSource() --> devuelve el botón "src" que generó el evento
            appendDigit(src.getText());            // getText() --> devuelve el texto del botón devuelto por e.getSource()
        };

        ActionListener opListener = e -> {
            JButton src = (JButton) e.getSource();
            aplicarOperador(src.getText()); // "+", "-", "*", "/"
        };
        
        // Números
        btn_0.addActionListener(numberListener);
        btn_1.addActionListener(numberListener);
        btn_2.addActionListener(numberListener);
        btn_3.addActionListener(numberListener);
        btn_4.addActionListener(numberListener);
        btn_5.addActionListener(numberListener);
        btn_6.addActionListener(numberListener);
        btn_7.addActionListener(numberListener);
        btn_8.addActionListener(numberListener);
        btn_9.addActionListener(numberListener);

        // Punto decimal
        btnPoint.addActionListener(e -> appendPoint());
        
        // Operadores
        btnSum.addActionListener(opListener);
        btnSubtraction.addActionListener(opListener);
        btnMultiplication.addActionListener(opListener);
        btnDivision.addActionListener(opListener);

        // Igual
        btnEqual.addActionListener(e -> igual());

        // AC, C, ±, %
        btnAC.addActionListener(e -> resetAll());
        btnC.addActionListener(e -> clearEntry());
        btnMasMenos.addActionListener(e -> toggleSign());
        btnPercent.addActionListener(e -> percent());       

	}

	// --- Helpers de display ---
	
	private String getDisplay() {
	    String s = textField.getText();
	    return (s == null || s.isEmpty()) ? "0" : s;
	}

	private void setDisplay(String s) {
	    textField.setText(s);
	}

	private void appendDigit(String t) {
	    if (nuevaEntrada || getDisplay().equals("0")) {
	        setDisplay(t);
	    } else {
	        setDisplay(getDisplay() + t);
	    }
	    nuevaEntrada = false;
	}

	private void appendPoint() {
	    if (nuevaEntrada) {
	        setDisplay("0.");
	        nuevaEntrada = false;
	    } else if (!getDisplay().contains(".")) {
	        setDisplay(getDisplay() + ".");
	    }
	}

	private void aplicarOperador(String op) {
	    double valor = Double.parseDouble(getDisplay());
	    if (operador == null) {
	        acumulado = valor;
	    } else {
	        acumulado = calcular(acumulado, valor, operador);
	        setDisplay(trimDouble(acumulado));
	    }
	    operador = op;
	    nuevaEntrada = true;
	}

	private double calcular(double a, double b, String op) {
	    return switch (op) {
	        case "+" -> a + b;
	        case "-" -> a - b;
	        case "*" -> a * b;
	        case "/" -> (b == 0) ? Double.NaN : a / b;
	        default -> b;
	    };
	}

	private String trimDouble(double d) {
	    if (Double.isNaN(d) || Double.isInfinite(d)) return "Error";
	    String s = Double.toString(d);
	    if (s.endsWith(".0")) s = s.substring(0, s.length() - 2);
	    return s;
	}

	private void igual() {
	    if (operador != null) {
	        double valor = Double.parseDouble(getDisplay());
	        double res = calcular(acumulado, valor, operador);
	        setDisplay(trimDouble(res));
	        acumulado = res;
	        operador = null;
	        nuevaEntrada = true;
	    }
	}

	private void resetAll() { // AC
	    acumulado = 0.0;
	    operador = null;
	    nuevaEntrada = true;
	    setDisplay("0");
	}

	private void clearEntry() { // C
	    setDisplay("0");
	    nuevaEntrada = true;
	}

	private void toggleSign() { // ±
	    String s = getDisplay();
	    if (s.equals("0") || s.equals("0.")) return;
	    if (s.startsWith("-")) setDisplay(s.substring(1));
	    else setDisplay("-" + s);
	}

	private void percent() { // %
	    double v = Double.parseDouble(getDisplay());
	    double res = (operador == null) ? v / 100.0 : (acumulado * v / 100.0); // % tipo calculadora
	    setDisplay(trimDouble(res));
	    nuevaEntrada = true;
	}

}
