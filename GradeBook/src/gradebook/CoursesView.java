package gradebook;

import java.awt.BorderLayout;
import java.awt.Dimension;
import gradebookclient.Client;
import gradebookdata.Course;
import gradebookdata.CourseList;
import gradebookdata.User;
import gradebookdata.Packet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

public class CoursesView extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5583355898948589862L;

	private User curUser;
	CourseList courses;
	Object[][] data;
	String[] columnNames;
	JTable coursesTable;
	BorderLayout manager;
	Dashboard dash;
	JScrollPane scrollPane;

	public CoursesView(Dashboard dash){
		manager = new BorderLayout();
		setLayout(manager);
		curUser = null;
		courses = null;
		data = null;
		columnNames = new String[3];
		columnNames[0] = "Course";
		columnNames[1] = "Credit Hours";
		columnNames[2] = "Average";
		scrollPane = null;
		coursesTable = null;
		this.dash = dash;
	}
	
	public void setUser(User curUser){
		this.curUser = curUser;
		courses = getCoursesForUser();
		if(scrollPane != null){
			remove(scrollPane);
			scrollPane = null;
		}
		if(courses!=null){
			data = new Object[courses.getSize()][3];
			for(int i = 0; i<courses.getSize(); i++){
					Course curCourse = courses.getCourse(i);
					data[i][0] = curCourse.getName();
					data[i][1] = curCourse.getWeight();
					Client average;
					try {
						average = new Client(new Packet(400, curCourse.getID().toString()));
						data[i][2] = average.getResponse().getData();
					} catch (Exception e) {
						data[i][2] = "Unable to calculate";
						e.printStackTrace();
					}
					

			}
			coursesTable = new JTable(data,columnNames);
		}
		else coursesTable = new JTable(new Object[0][0], columnNames);
		
		scrollPane = new JScrollPane(coursesTable);
		coursesTable.setFillsViewportHeight(true);
		coursesTable.getSelectionModel().addListSelectionListener(new RowListener());
		scrollPane.setPreferredSize(new Dimension(dash.getWidth()/3, dash.getHeight()-dash.exit.getHeight()));
		setColumnWidths();
		add(scrollPane);
		validate();
		repaint();
	}
	
	public void deleteCourseBtnClicked(){
		if(coursesTable.getSelectedRow() < 0)
			JOptionPane.showMessageDialog(this, "You must select a course to delete it", "Delete Course", JOptionPane.PLAIN_MESSAGE);
		
		else{
			Course toDelete = courses.getCourse(coursesTable.getSelectedRow());
			
			String res = deleteCourse(toDelete);
			
			if(!res.equals("Course was deleted"))
				JOptionPane.showMessageDialog(this, res, "Delete Course", JOptionPane.PLAIN_MESSAGE);
			
			setUser(curUser);
		}
		
	}

	private CourseList getCoursesForUser(){
		try{
			Client get = new Client(new Packet(101, curUser.getID().toString()));

			String getData = get.getResponse().getData();

			CourseList courses = new CourseList();

			if (!get.succeeded()) {
				System.out.println(getData);
				return null;
			}


			String[] temp = getData.split(";");

			if (temp.length % 3 != 0) {
				System.out.println("Error in Packet sent from client!");
				return null;
			}

			for (int i = 0; i < temp.length; i += 3) {
				Course curCourse = new Course(temp[i],
						Integer.parseInt(temp[i + 1]),
						Integer.parseInt(temp[i + 2]));
				courses.add(curCourse);
			}

			if(courses.getSize() <= 0){
				return null;
			}
			else return courses;
		}
		catch(Exception e){
			return null;
		}


	}
	
	private void setColumnWidths(){
		TableColumn column = null;
		for (int i = 0; i < 3; i++) {
		    column = coursesTable.getColumnModel().getColumn(i);
		    if (i == 0) {
		        column.setPreferredWidth((int) (this.getWidth() * .5)); //third column is bigger
		    } else {
		        column.setPreferredWidth((int) (this.getWidth() * .25));
		    }
		}
	}
	
	private class RowListener implements ListSelectionListener {
		@Override
		public void valueChanged(ListSelectionEvent arg0) {
			dash.showAssignmentsforCourse(courses.getCourse(coursesTable.getSelectedRow()));
		}
    }
	
	private String deleteCourse(Course toDelete){
		if(toDelete == null)
			return "No Course Selected";
		try{
			Client delete = new Client(new Packet(102, toDelete.getID().toString()));
			
			if(delete.succeeded())
				return "Course was deleted";
		}catch(Exception e){
		}
		
		return "An error occurred! Please try again";
	}

}
