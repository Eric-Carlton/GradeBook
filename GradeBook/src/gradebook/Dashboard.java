package gradebook;

import gradebookclient.Client;
import gradebookdata.Course;
import gradebookdata.User;
import gradebookdata.Packet;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Dashboard extends JPanel implements ActionListener {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6824683684567741313L;
	CoursesView courses;
	AssignmentsView assignments;
	JLabel topMargin, leftMargin, rightMargin, center, leftBottom, leftBottomSeparator1,exitLeftMargin;
	private static TableLayout manager;
	BufferedImage bgImage;
	User user;
	
	JButton exit, addCourse, deleteCourse, addAssignment, deleteAssignment;
	
	public Dashboard(GradeBookGUI gui){
		super(manager);
		manager = new TableLayout();
		setLayout(manager);
		
		user = null;
		
		bgImage = null;
		try{
			bgImage = ImageIO.read(new File("resources/loginbg.jpg"));
		}
		catch (IOException e) {
			System.out.println("IOException");
		}

		repaint();
	
		courses = new CoursesView(this);
		
		exit = new JButton("Exit");
		exit.addActionListener(this);
		
		exitLeftMargin = new JLabel(" ");
		exitLeftMargin.setPreferredSize(new Dimension(1,1));
		
		center = new JLabel(" ");
		center.setPreferredSize(new Dimension(1, 1));
		
		leftMargin = new JLabel(" ");
		leftMargin.setPreferredSize(new Dimension(1, 1));
		
		rightMargin = new JLabel(" ");
		rightMargin.setPreferredSize(new Dimension(1, 100));
		
		topMargin = new JLabel(" ");
		topMargin.setPreferredSize(new Dimension(getWidth(), 1));
		
		addCourse = new JButton("Add Course");
		addCourse.addActionListener(this);
		
		deleteCourse = new JButton("Delete Course");
		deleteCourse.addActionListener(this);
		
		addAssignment = new JButton("Add Assignment");
		addAssignment.addActionListener(this);
		
		deleteAssignment = new JButton("Delete Assignment");
		deleteAssignment.addActionListener(this);
		
		leftBottom = new JLabel(" ");
		leftBottom.setPreferredSize(new Dimension(getWidth(), 1));
		
		leftBottomSeparator1 = new JLabel(" ");
		leftBottomSeparator1.setPreferredSize(new Dimension(getWidth(), 1));
		
		//courses.setMinimumSize(new Dimension(gui.getWidth()/3, gui.getHeight()-exit.getHeight()));
		
		assignments = new AssignmentsView(this);
		assignments.setBackground(Color.white);
		//assignments.setPreferredSize(new Dimension(gui.getWidth()/3, gui.getHeight()-exit.getHeight()));
		
		add(topMargin, ".");
		add(leftMargin, "LTB");
		add(courses, "LR2W");
		add(center, "L3W");
		add(assignments, "LR2W");
		add(rightMargin, "R.");
		add(leftBottom, "L");
		add(addCourse,"LR");
		add(deleteCourse,"LR");
		add(leftBottomSeparator1, "L3W");
		add(addAssignment, "LR");
		add(deleteAssignment, "LR.");
		add(exitLeftMargin,"LR5W");
		add(exit, "LR2W");
		
		
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if(bgImage != null)
			g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
	}

	@Override
	public void actionPerformed(ActionEvent ev) {
		if(ev.getSource() == exit)
			System.exit(0);
		if(ev.getSource() == addCourse)
			addCourseBtnClicked();
		if(ev.getSource() == deleteCourse)
			courses.deleteCourseBtnClicked();
			
		
	}
	
	public void setUser(User curUser){
		user = curUser;
		courses.setUser(curUser);
		assignments.loggedIn();
	}
	
	public void showAssignmentsforCourse(Course course){
		assignments.courseSelected(course);
	}
	
	private void addCourseBtnClicked(){
		
		JLabel courseNameLbl = new JLabel("Course Name:");
		JTextField courseNameTxt = new JTextField();
		
		JLabel creditHoursLbl = new JLabel("Credit Hours:");
		JTextField creditHoursTxt = new JTextField();
		
		Object[] components = new Object[4];
		components[0] = courseNameLbl;
		components[1] = courseNameTxt;
		components[2] = creditHoursLbl;
		components[3] = creditHoursTxt;
		
		int result = JOptionPane.showConfirmDialog(null, components, "Add Course", JOptionPane.OK_CANCEL_OPTION);
		
		if(result == JOptionPane.OK_OPTION){
			if(courseNameTxt.getText().trim().length() != 0 && creditHoursTxt.getText().trim().length() != 0)
				createCourse(courseNameTxt.getText().trim(), creditHoursTxt.getText().trim());
			else
				JOptionPane.showMessageDialog(this, "All information required to add a course","Add Course Error", JOptionPane.ERROR_MESSAGE);
		}
			
	}
	
	private void createCourse(String courseName, String creditHours){
		try {
			Client client = new Client(new Packet(100, user.getID().toString()
			+ ";" + courseName + ";" + creditHours));
			
			if(client.succeeded())
				courses.setUser(user);
			else
				JOptionPane.showMessageDialog(this, client.getResponse().getData(), "Couldn't Add Course", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Oops! An error occurred! Please try again.", "Couldn't Add Course", JOptionPane.ERROR_MESSAGE);
		}
	}

}
