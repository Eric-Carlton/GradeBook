package gradebook;

import gradebookclient.Client;
import gradebookdata.Category;
import gradebookdata.CategoryList;
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
import javax.swing.JComboBox;
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
	JLabel topMargin, leftMargin, rightMargin, center, leftBottom, leftBottomSeparator1, leftBottomSeparator2, exitLeftMargin;
	private static TableLayout manager;
	BufferedImage bgImage;
	User user;

	JButton exit, addCourse, deleteCourse, addAssignment, deleteAssignment, addCategory, deleteCategory;

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
		addAssignment.setEnabled(false);
		addAssignment.addActionListener(this);

		deleteAssignment = new JButton("Delete Assignment");
		deleteAssignment.setEnabled(false);
		deleteAssignment.addActionListener(this);
		
		addCategory = new JButton("Add Category");
		addCategory.setEnabled(false);
		addCategory.addActionListener(this);

		deleteCategory = new JButton("Delete Category");
		deleteCategory.setEnabled(false);
		deleteCategory.addActionListener(this);
		
		leftBottom = new JLabel(" ");
		leftBottom.setPreferredSize(new Dimension(1, 1));

		leftBottomSeparator1 = new JLabel(" ");
		leftBottomSeparator1.setPreferredSize(new Dimension(1, 1));
		
		leftBottomSeparator2 = new JLabel(" ");
		leftBottomSeparator2.setPreferredSize(new Dimension(1, 1));

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
		add(leftBottomSeparator2, "L6W");
		add(addCategory, "LR");
		add(deleteCategory,"LR.");
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
		else if(ev.getSource() == addCourse)
			addCourseBtnClicked();
		else if(ev.getSource() == deleteCourse)
			courses.deleteCourseBtnClicked();
		else if(ev.getSource() == deleteAssignment)
			assignments.deleteAssignmentButtonClicked();
		else if(ev.getSource() == addAssignment)
			addAssignmentBtnClicked();
		else if(ev.getSource() == addCategory)
			addCategoryBtnClicked();
	}

	public void setUser(User curUser){
		user = curUser;
		courses.setUser(curUser);
		assignments.loggedIn();
	}

	public void showAssignmentsforCourse(Course course){
		assignments.courseSelected(course);
		addAssignment.setEnabled(true);
		deleteAssignment.setEnabled(true);
		addCategory.setEnabled(true);
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
			if(courseNameTxt.getText().trim().length() > 0 && creditHoursTxt.getText().trim().length() > 0)
				createCourse(courseNameTxt.getText().trim(), creditHoursTxt.getText().trim());
			else
				JOptionPane.showMessageDialog(this, "All information required to add a course","Add Course Error", JOptionPane.ERROR_MESSAGE);
		}

	}

	private void addAssignmentBtnClicked(){
		CategoryList catList = getCatListForCourse(assignments.course);
		String[] catNames = new String[catList.getSize()];
		if(catList.getSize() > 0){
			for(int i = 0; i<catList.getSize(); i++)
				catNames[i] = catList.getType(i).getName();
		}

		JLabel assignmentNameLbl = new JLabel("Assignment Name: ");
		JLabel assignmentScoreLbl = new JLabel("Assignment Score: ");
		JLabel assignmentCategoryLbl = new JLabel("Assignment Category: ");

		JTextField assignmentNameTxt = new JTextField();
		JTextField assignmentScoreTxt = new JTextField();
		JComboBox<String> catsBox = new JComboBox<String>(catNames);

		Object[] components = new Object[6];
		components[0] = assignmentNameLbl;
		components[1] = assignmentNameTxt;
		components[2] = assignmentScoreLbl;
		components[3] = assignmentScoreTxt;
		components[4] = assignmentCategoryLbl;
		components[5] = catsBox;

		int result = JOptionPane.showConfirmDialog(this, components, "Add Assignment", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION){
			if(assignmentNameTxt.getText().trim().length() > 0 && assignmentScoreTxt.getText().trim().length() > 0){
				Double score;
				int scoreInt = 0;
				
				Category selectedCat = null;
				
				for(int i = 0; i<catList.getSize(); i++ ){
					Category cur = catList.getType(i);
					if(cur.getName().equals(catsBox.getSelectedItem())){
						selectedCat = cur;
						break;
					}
				}
					
				try{
					score = Double.parseDouble(assignmentScoreTxt.getText().trim());
					scoreInt = (int) Math.round(score);
					createAssignment(assignmentNameTxt.getText().trim(), scoreInt, selectedCat);
				}catch(Exception e){
					JOptionPane.showMessageDialog(null, "Score must be all numeric and roundable to two decimal places", "Add Assignment Eror", JOptionPane.ERROR_MESSAGE);
				}
			}
			else
				JOptionPane.showMessageDialog(this, "All information required to add an assignment","Add Assignment Error", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private CategoryList getCatListForCourse(Course course){
		try{
			Client get = new Client(new Packet(302, course.getID().toString()));

			String getData = get.getResponse().getData();

			CategoryList types = new CategoryList();

			if (!get.succeeded()) {
				System.out.println(getData);
				return null;
			}

			String[] temp = getData.split(";");

			if (temp.length % 4 != 0) {
				System.out.println("Error in Packet sent from client!");
				return null;
			}

			for (int i = 0; i < temp.length; i += 4) {
				Category cur = new Category(temp[i],
						Integer.parseInt(temp[i + 1]),
						Integer.parseInt(temp[i + 2]),
						Integer.parseInt(temp[i+3]));
				types.add(cur);
			}

			return types;
		}catch(Exception e){
			return null;
		}
	}

	private void createAssignment(String assignName, int score, Category cat){
		try {
			Client client = new Client(new Packet(200,assignName+";"+score+";"+cat.getID().toString()+";"+assignments.course.getID().toString()));
			if(client.succeeded()){
				assignments.courseSelected(assignments.course);
				courses.setUser(user);
			}
			else
				JOptionPane.showMessageDialog(this, client.getResponse().getData(), "Couldn't Add Assignment", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Oops! An error occurred! Please try again.", "Couldn't Add Assignment", JOptionPane.ERROR_MESSAGE);
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
	
	private void addCategoryBtnClicked(){
		JLabel categoryNameLbl = new JLabel("Category Name: ");
		JLabel categoryWeightLbl = new JLabel("Category Weight (% of final grade) : ");
		
		JTextField categoryNameTxt = new JTextField();
		JTextField categoryWeightTxt = new JTextField();
		
		Object[] components = new Object[4];
		components[0] = categoryNameLbl;
		components[1] = categoryNameTxt;
		components[2] = categoryWeightLbl;
		components[3] = categoryWeightTxt;
		
		int result = JOptionPane.showConfirmDialog(this, components, "Add Assignment", JOptionPane.OK_CANCEL_OPTION);
		if(result == JOptionPane.OK_OPTION){
			if(categoryNameTxt.getText().trim().length() > 0 && categoryWeightTxt.getText().trim().length() > 0){
				int weight = 0;
				try{
					weight = Integer.parseInt(categoryWeightTxt.getText().trim());
					if(weight < 0 || weight > 100)
						JOptionPane.showMessageDialog(this, "Weight must be between 0 and 100", "Add Category Error", JOptionPane.ERROR_MESSAGE);
					else
						createCategory(categoryNameTxt.getText().trim(), weight, assignments.course);
				}catch(Exception e){
					JOptionPane.showMessageDialog(this, "Weight must be a whole number", "Add Category Error", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	private void createCategory(String name, int weight, Course course){
		try {
			Client client = new Client(new Packet(300, name+";"+weight+";"+course.getID().toString()));
			if(!client.succeeded())
				JOptionPane.showMessageDialog(this, client.getResponse().getData(), "Couldn't Add Category", JOptionPane.ERROR_MESSAGE);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Oops! An error occurred! Please try again.", "Couldn't Add Category", JOptionPane.ERROR_MESSAGE);
		} 
	}
}
