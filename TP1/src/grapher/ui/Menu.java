package grapher.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

public class Menu extends JPanel implements ActionListener {
	
	static final String PLUS = " + ";
	static final String MINUS = " -  ";

	Grapher grapher;
	JTable table;
	JToolBar toolbar;
	
	JButton buttonPlus;
	JButton buttonMin;
	
	public Menu(String[] expressions, Grapher grapher) {
		this.grapher = grapher;
		
		//1) Table
		
		// Initialization
		String[] columnNames = {"Expression", "Color"};
		Object[][] data = {};
		MyTableModel model = new MyTableModel(data, columnNames);
		table = new JTable(model);
		for(String expression : expressions) {
			model.addRow(new Object[]{expression, Color.BLACK});
		}
		// Color column
		TableColumn colorColumn = table.getColumnModel().getColumn(1);
		colorColumn.setCellEditor(new ColorEditor());
		colorColumn.setCellRenderer(new ColorRenderer(true));
		
		// Display
		table.setFillsViewportHeight(true);
		table.getColumnModel().getColumn(0).setPreferredWidth(130);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		
		// Listener : selection
	    ListSelectionModel cellSelectionModel = table.getSelectionModel();
	    cellSelectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);//TODO : REPLACE BY MULTIPLE
	    cellSelectionModel.addListSelectionListener(new MyListSelectionListener());
			    	    
		//2) Buttons
		
		this.toolbar = new JToolBar();
		// button +
	    buttonPlus = new JButton(PLUS);
	    buttonPlus.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonPlus.addActionListener(this);
	    this.toolbar.add(buttonPlus);
	    // button -
	    buttonMin = new JButton(MINUS);
	    buttonMin.setHorizontalAlignment(SwingConstants.CENTER);
	    buttonMin.addActionListener(this);
	    this.toolbar.add(buttonMin);
	    
	    //final = 1) + 2)
	       
	    this.setLayout(new BorderLayout());
	    JScrollPane scrollPane = new JScrollPane(table);
	    this.add(scrollPane, BorderLayout.CENTER);
	    this.add(toolbar, BorderLayout.SOUTH);
	}

	@Override //Button event
	public void actionPerformed(ActionEvent e) {

        switch (e.getActionCommand()) {
	        case PLUS:
	        	String newExp = JOptionPane.showInputDialog(this.getParent(),"Nouvelle expression");
	        	if(newExp == null) break;//close or cancel button has been clicked
	        	try {
	        		//update graph
		        	grapher.add(newExp);
		        	//update menu
		        	DefaultTableModel model = (DefaultTableModel) table.getModel();
		        	model.addRow(new Object[]{newExp, Color.BLACK});
	        	} catch (Exception ex) {
	        		JOptionPane.showMessageDialog(this.getParent(),"Expression invalide", "Erreur", 0);
	        	}
	        	break;
	        case MINUS:
	        	int[] selectedRows = table.getSelectedRows();
	        	//update graph
	        	grapher.remove(selectedRows);
	        	//update menu
	        	table.clearSelection();
	        	DefaultTableModel model = (DefaultTableModel) table.getModel();
	        	for(int i = 0; i < selectedRows.length; ++i) {
	        		model.removeRow(selectedRows[i] - i);
	        		//" - i" because the table gets smaller with every iteration
	        	}
	        	break;
	        default:
	        	assert(false);
        }
	}
	
	public JButton getButtonPlus() {
		return buttonPlus;
	}
	
	public JButton getButtonMin() {
		return buttonMin;
	}
	
	//Intern Class : Custom TableModel
	public class MyTableModel extends DefaultTableModel {
		
		public MyTableModel(Object rowData[][], Object columnNames[]) {
	        super(rowData, columnNames);
	    }
		
		@Override
		public void setValueAt(Object aValue, int row, int column) {
			try {
				//edit the grapher first, then the cell value
				//=> if the expression is invalid, an error is
				//   thrown and the cell value is not changed.
				grapher.edit(row, column, aValue);
				super.setValueAt(aValue, row, column);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(new JPanel(),"Modification invalide", "Erreur", 0);
			}
		}
	}
	
	//Intern Class : Listener on cell selection
	public class MyListSelectionListener implements ListSelectionListener {
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if(!e.getValueIsAdjusting()) {
				grapher.changeActiveFunctions(table.getSelectedRows());
				grapher.repaint();
			}
		}
		
	}
}
