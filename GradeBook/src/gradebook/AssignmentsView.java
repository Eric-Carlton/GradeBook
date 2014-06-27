package gradebook;


import gradebookclient.Client;
import gradebookdata.Assignment;
import gradebookdata.AssignmentList;
import gradebookdata.Category;
import gradebookdata.Course;
import gradebookdata.Packet;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.TableColumn;

public class AssignmentsView extends JPanel {

	JLabel lbl;
	AssignmentList assigns;
	JTable assignmentsTable;
	String[] columnNames;
	Object[][] data;
	BorderLayout manager;
	Dashboard dash;
	Course course;
	JScrollPane scrollPane;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8670519973058262643L;

	public AssignmentsView(Dashboard dash){
		manager = new BorderLayout();
		setLayout(manager);
		this.dash = dash;
		assigns = null;
		columnNames = new String[3];
		columnNames[0] = "Name";
		columnNames[1] = "Score";
		columnNames[2] = "Category";
		data = null;
		assignmentsTable = null;
	}

	public void loggedIn(){
		lbl = new JLabel("Select a course to view assignments", SwingConstants.CENTER);
		lbl.setPreferredSize(new Dimension(dash.getWidth()/3, dash.getHeight()-dash.exit.getHeight()));
		add(lbl);
	}

	public void courseSelected(Course course){
		this.course = course;
		if(lbl != null)
		{
			remove(lbl);
			lbl = null;
		}
		if(scrollPane != null){
			remove(scrollPane);
			scrollPane = null;
		}
		getAssigns();
		if(assigns == null)
			JOptionPane.showMessageDialog(this, "Unable to retrieve assignments", "Assignment List Error", JOptionPane.ERROR_MESSAGE);
		else{

			data = new Object[assigns.getSize()][3];
			for(int i = 0; i<assigns.getSize(); i++){
				Assignment curAssign = assigns.getAssignment(i);
				data[i][0] = curAssign.getName();
				data[i][1] = curAssign.getScore();
				try {
					data[i][2] = getCategoryNameForAssignment(curAssign);
				} catch (Exception e) {
					data[i][2] = "Unable to calculate";
					e.printStackTrace();
				}
			}
			assignmentsTable = new JTable(data,columnNames);
			scrollPane = new JScrollPane(assignmentsTable);
			assignmentsTable.setFillsViewportHeight(true);
			scrollPane.setPreferredSize(new Dimension(dash.getWidth()/3, dash.getHeight()-dash.exit.getHeight()));
			setColumnWidths();
			add(scrollPane);
			validate();
			repaint();
		}
	}

	private void getAssigns(){
		Client client;
		try {
			client = new Client(new Packet(201, course.getID().toString()));
			String getData = client.getResponse().getData();		

			assigns = new AssignmentList();

			if (!client.succeeded()) {
				return;
			}

			String[] temp = getData.split(";");

			if (temp.length % 6 != 0) {
				System.out.println("Error in Packet sent from client!");
				return;
			}

			for (int i = 0; i < temp.length; i += 6) {
				Assignment cur = new Assignment(temp[i], Integer.parseInt(temp[i+1]), Integer.parseInt(temp[i+2]), Integer.parseInt(temp[i+3]), Integer.parseInt(temp[i+4]), Integer.parseInt(temp[i+5]));
				assigns.add(cur);
			}

		} catch (Exception e) {
			lbl.setText("Unable to retrieve assignments for " + course.getName() );
		} 
	}

	private void setColumnWidths(){
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
			column = assignmentsTable.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth((int) (this.getWidth() * .5));
			}
			else if(i == 1){
				column.setPreferredWidth((int) (this.getWidth() * .13));
			}else {
				column.setPreferredWidth((int) (this.getWidth() * .37));
			}
		}
	}
	
	private String getCategoryNameForAssignment(Assignment assign) throws  Exception{
		Client category = new Client(new Packet(301, assign.getCategoryID().toString()));
		
		if (!category.succeeded()) {
			return "Unable to retrieve";
		}
		
		String getData = category.getResponse().getData();

		String[] temp = getData.split(";");

		if (temp.length % 4 != 0) {
			System.out.println("Error in Packet sent from client!");
			return "Unable to retrieve";
		}
		
		Category cur = new Category(temp[0],
				Integer.parseInt(temp[1]),
				Integer.parseInt(temp[2]),
				Integer.parseInt(temp[3]));
		
		return cur.getName();
	}

}
