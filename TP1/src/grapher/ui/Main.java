package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class Main extends JFrame implements ActionListener {
	
	Grapher grapher;
	Menu leftMenu;
	JMenuBar menuBar;
	
	Main(String title, String[] expressions) {
		super(title);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Graph (right)
		grapher = new Grapher();		
		for(String expression : expressions) {
			grapher.add(expression);
		}
		
		//Menu (left)
		leftMenu = new Menu(expressions, grapher);
		
		//SplitPane (left/right separation)
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,leftMenu,grapher);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(120);
		this.add(splitPane);
		
	    //Menu bar (top)
	    menuBar = new JMenuBar();
	    JMenu menu = new JMenu("Expression");
	    menu.setMnemonic('t');
	    menuBar.add(menu);
	    //submenu : add
	    JMenuItem itemAdd = new JMenuItem("Add");
	    itemAdd.addActionListener(this);
	    menu.add(itemAdd);
	    //submenu : remove
	    JMenuItem itemRemove = new JMenuItem("Remove");
	    itemRemove.addActionListener(this);
	    menu.add(itemRemove);
	    this.setJMenuBar(menuBar);
	    
	    //Display
		this.pack();
		this.setLocationRelativeTo(null);//(center the frame)
		this.setVisible(true);
	}

	public static void main(String[] argv) {
		final String[] expressions = argv;
		SwingUtilities.invokeLater(new Runnable() {
			public void run() { 
				new Main("grapher", expressions).setVisible(true); 
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//these buttons are just aliases to the "+" and "-" buttons
		switch (e.getActionCommand()) {
	        case "Add":
	        	leftMenu.getButtonPlus().doClick();
	        	break;
	        case "Remove":
	        	leftMenu.getButtonMin().doClick();
	        	break;
	        default:
	        	assert(false);
		}
	}
}
