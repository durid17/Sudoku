package assign3;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.text.Document;

import java.awt.*;
import java.awt.event.*;


 public class SudokuFrame extends JFrame {
	
	private JTextArea source;
	private JTextArea result;
	private JCheckBox auto;
	
	private void check() {
		Sudoku sudoku = null;
		try {
			sudoku = new Sudoku(parse(source.getText()));					
		} catch (Exception e) {
			result.setText("Parsing problem");
		}
		if(sudoku == null) return;
		int sol = sudoku.solve();
		String res = "";
		if(sol > 0) {
			res = sudoku.getSolutionText() + "\n";
			res = res + "Solutions: " + sol + "\n";
			res = res + "elapsed: " + sudoku.getElapsed() +"ms";
		}
		result.setText(res);
	}
	
	private String parse(String text) {
		String s = "";
		for(int i = 0 ; i < text.length() ; i++) {
			if(text.charAt(i) >= '0' && text.charAt(i) <= '9') s = s + text.charAt(i) + " ";
		}
		return s;
	}

	public SudokuFrame() {
		super("Sudoku Solver");
		
		setLayout(new BorderLayout(4 , 4));
		
		source = new JTextArea(15, 20);
		add(source, BorderLayout.CENTER);
		Document doc = source.getDocument();
		doc.addDocumentListener(new DocumentListener() {	
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if(auto.isSelected())check();
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if(auto.isSelected())check();			
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0) {
				if(auto.isSelected())check();				
			}
		});
		source.setBorder(new TitledBorder("Puzzle"));
	
		result = new JTextArea(15, 20);
		add(result, BorderLayout.EAST);
		result.setBorder(new TitledBorder("Solution"));
		
		Box box = Box.createHorizontalBox();
		JButton check = new JButton("Check");
		box.add(check);
		check.addActionListener(new ActionListener() {	
			@Override
			public void actionPerformed(ActionEvent arg0) {
				check();				
			}
		});
		auto = new JCheckBox("Auto Check");
		auto.setSelected(true);
		auto.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Object source = arg0.getSource();
				JCheckBox box = (JCheckBox) source;
				if(box.isSelected()) check();
			}
		});
		box.add(auto);
		add(box, BorderLayout.SOUTH);
		
		
		// Could do this:
		setLocationByPlatform(true);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	
	
	public static void main(String[] args) {
		// GUI Look And Feel
		// Do this incantation at the start of main() to tell Swing
		// to use the GUI LookAndFeel of the native platform. It's ok
		// to ignore the exception.
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ignored) { }
		
		SudokuFrame frame = new SudokuFrame();
	}

}
