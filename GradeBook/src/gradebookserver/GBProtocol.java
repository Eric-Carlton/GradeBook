package gradebookserver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;

import gradebookdata.Assignment;
import gradebookdata.AssignmentList;
import gradebookdata.Course;
import gradebookdata.Packet;
import gradebookdata.User;

/**
 * Describes how Server and Client should communicate in GradeBook System. Also
 * includes DB connection and database manipulation methods
 * 
 * @author Eric Carlton
 */
public class GBProtocol {

	private Connection connection;

	public GBProtocol() {
		getDBConnection();
	}

	/**
	 * Processes the content of the Packet sent, performs database operations (
	 * if applicable ) and returns information to the client
	 * 
	 * @param in
	 *            the Packet received from the client
	 * @return a Packet to send to the client containing relevant information
	 *         about operations performed
	 */
	public Packet processInput(Packet in) {

		// first contact by Client
		if (in == null) {
			return new Packet(0, "Whatcha need boss?");
		}

		// find out what the client wants to do
		int intent = in.getIntent();

		// Packet to send
		Packet out = null;

		// String array of data
		String[] temp;

		// a Packet's data will frequently contain multiple pieces of
		// information.
		// each piece of information is separated by a ";" in this protocol
		temp = in.getData().split(";");

		// Decide what to do based on Packet's intent field
		switch (intent) {
		// add a user
		// requires a string in the format:
		// first_name;last_name;user_name;password
		case 0:
			// need 4 pieces of info to add a user. If all fields are not
			// present, or
			// too many are present, the user cannot be added
			if (temp.length != 4) {
				out = new Packet(905,
						"Not enough infomation given to add User!");
				break;
			}

			// Make sure the desired username isn't already taken
			boolean exists = searchUser(temp[2]);
			if (exists) {
				out = new Packet(904, "User name already exists!");
				break;
			}

			// try to add the user to the database
			boolean uCreated = addUser(temp[0], temp[1], temp[2], temp[3]);

			if (uCreated)
				out = new Packet(999, "We got a new User!");
			else
				out = new Packet(901,
						"Something happened, your User wasn't created!");
			break;

		// login
		// need a string in the format : user_name;password
		case 1:

			// make sure the right amount of information was given
			if (temp.length != 2)
				return null;

			// attempt to get the validate the user's information
			// against database contents
			User user = getUser(temp[0], temp[1]);

			// if no user is found, getUser() will return null
			if (user == null)
				out = new Packet(902, "No user found!");

			// otherwise, provide all information needed to reconstruct User
			// object
			// on client side, with each relevant piece of information separated
			// by a ";"
			else
				out = new Packet(999, user.getFirstName() + ";"
						+ user.getLastName() + ";" + user.getID());
			break;

		// add a course
		// need a string in the format: user_ID;course_name;credit_hours
		case 100:

			// make sure the right number of fields were provided
			if (temp.length != 3) {
				out = new Packet(906,
						"Not enough information given to add Course");
				break;
			}

			boolean cCreated = false;

			// try to parse the information into the correct format
			try {
				// try to add the course to the database
				cCreated = addCourse(Integer.parseInt(temp[0]), temp[1],
						Integer.parseInt(temp[2]));
			} catch (NumberFormatException e) {
				out = new Packet(912,
						"Information provided is in the wrong format!");
				break;
			}

			if (cCreated)
				out = new Packet(999, "Course added successfully!");
			else
				out = new Packet(907,
						"Something happened, your Course couldn't be added!");
			break;

		// get all courses for a user
		// data segment of Packet should only contain the user_ID of the user
		// to retrieve a course list for
		case 101:

			// make sure that the right amount of information was given
			if (temp.length != 1) {
				out = new Packet(908,
						"Not correct information to get User's courses!");
				break;
			}

			String courseInfo = null;

			// try to parse information into correct format
			try {
				courseInfo = getUserCourses(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(913,
						"Information provided is not in the correct format!");
				break;
			}

			// if no courses exist for the user, getUserCourses will return null
			if (courseInfo == null)
				out = new Packet(909, "User has no courses!");
			else
				out = new Packet(999, courseInfo);
			break;
		// delete a course
		// data segment of Packet should only contain the course_ID of
		// the course to delete
		case 102:
			if (temp.length != 1)
				out = new Packet(910,
						"Not correct information to delete a course!");

			boolean delete = false;
			try {
				delete = deleteCourse(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(914,
						"Information is not in the correct format!");
				break;
			}

			if (delete)
				out = new Packet(999, "Course deleted!");
			else
				out = new Packet(911, "Course couldn't be deleted!");
			break;
		// add an assignment
		// requires data segment of packet to be:
		// assign_name;score;category_ID;course_ID
		case 200:
			if (temp.length != 4) {
				out = new Packet(915,
						"Not correct information to add an assignment!");
				break;
			}

			String category = null;

			try {
				category = getCategory(Integer.parseInt(temp[2]));
			} catch (NumberFormatException e) {
				out = new Packet(922,
						"Information provided is in the wrong format!");
				break;
			}

			if (category == null) {
				out = new Packet(923,
						"Category doesn't exist! Try to create it first!");
				break;
			}

			boolean aAdded;

			try {
				aAdded = addAssignment(temp[0], Integer.parseInt(temp[1]),
						Integer.parseInt(temp[2]), Integer.parseInt(temp[3]));
			} catch (NumberFormatException e) {
				out = new Packet(924,
						"Information provided is in the wrong format!");
				break;
			}

			if (aAdded)
				out = new Packet(999, "Assignment added!");
			else
				out = new Packet(925,
						"Something happened! Assignment couldn't be added!");

			break;
		// retrieve all assignments associated with a given course
		// data segment of packet should only contain the course_ID
		// of the course to get an assignment list for
		case 201:

			if (temp.length != 1) {
				out = new Packet(926,
						"Not correct information to get an assignment list!");
				break;
			}

			String assignInfo;

			try {
				assignInfo = getCourseAssigns(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(927,
						"Information provided is in the wrong format!");
				break;
			}

			if (assignInfo == null)
				out = new Packet(928, "Course has no assignments!");
			else
				out = new Packet(999, assignInfo);
			break;
		//delete an assignment
		//data segment of Packet should only contain
		//assign_ID of assignment to delete
		case 202:
			if(temp.length != 1)
			{
				out = new Packet(932, "Not correct information to delete an assignment!");
				break;
			}
			
			boolean assignDel = false;			
			try{
				assignDel = deleteAssign(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(933, "Information provided is not in the correct format!");
				break;
			}
			
			if(assignDel)
				out = new Packet(999,"Assignment deleted!");
			else
				out = new Packet(934, "Something happened! Assignment couldn't be deleted!");	
			break;
		// add a category
		// requires data segment of packet to be:
		// category_name;weight;course_ID		
		case 300:
			if (temp.length != 3) {
				out = new Packet(916, "Not correct information to add a category!");
				break;
			}

			boolean categoryAdded = false;

			try {
				categoryAdded = addCategory(temp[0], Integer.parseInt(temp[1]),
						Integer.parseInt(temp[2]));
			} catch (NumberFormatException e) {
				out = new Packet(917,
						"Information provided is in the wrong format!");
				break;
			}

			if (categoryAdded)
				out = new Packet(999, "Category added successfully!");
			else
				out = new Packet(918, "Something happened! Category not added!");

			break;
		// get information about a category
		// data segment of packet should only contain the category_ID of
		// the category to be retrieved
		case 301:
			if (temp.length != 1) {
				out = new Packet(919,
						"Not correct information to retrieve category!");
				break;
			}

			String categoryInfo = null;

			try {
				categoryInfo = getCategory(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(920,
						"Information provided is not in the correct format!");
				break;
			}

			if (categoryInfo == null)
				out = new Packet(921, "Category not found in the database!");
			else
				out = new Packet(999, categoryInfo);

			break;
		// get all categories for a given course
		// data segment of packet should only contain
		// the course_ID of the course to get categories for
		case 302:
			if (temp.length != 1) {
				out = new Packet(929,
						"Not correct information to get a category list!");
				break;
			}

			String categories;

			try {
				categories = getCourseCategories(Integer.parseInt(temp[0]));
			} catch (NumberFormatException e) {
				out = new Packet(930,
						"Information provided is in the wrong format!");
				break;
			}

			if (categories == null)
				out = new Packet(931, "Course has no categories!");
			else
				out = new Packet(999, categories);
			break;
			
			//delete a category
			//data segment of Packet should only contain
			//category_ID of category to delete
			case 303:
				if(temp.length != 1)
				{
					out = new Packet(935, "Not correct information to delete a category!");
					break;
				}
				
				boolean categoryDel = false;			
				try{
					categoryDel = deleteCategory(Integer.parseInt(temp[0]));
				} catch (NumberFormatException e) {
					out = new Packet(936, "Information provided is not in the correct format!");
					break;
				}
				
				if(categoryDel)
					out = new Packet(999,"Category deleted!");
				else
					out = new Packet(937, "Something happened! Category couldn't be deleted!");
				break;
			//get weighted average of all assignments for a course
			//data segment of Packet should only contain course_ID
			//for course to average
			case 400:
				if(temp.length != 1)
				{
					out = new Packet(938, "Not correct information to average a course!");
					break;
				}
				String average;
				try
				{
					average = getCourseAvg(Integer.parseInt(temp[0]));
				} catch (NumberFormatException e){
					out = new Packet(939,"Information provided is not in the correct format!");
					break;
				}
				
				if(average == null)
					out = new Packet(940,"Course could not be averaged! Are there any assignments for it?");
				else
					out = new Packet(999, average);
				break;
		// given a code that there is no protocol for
		default:
			out = new Packet(999, "Are you sure that's what you meant, friend?");
			break;
		}

		// try to close database connection
		try {
			connection.close();
		} catch (SQLException e) {
			System.out.println("Error closing connection!");
			return out;
		}
		return out;
	}

	/**
	 * Connects to database
	 */
	private void getDBConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println("JDBC missing!");
			e.printStackTrace();
			return;
		}
		connection = null;

		try {
			connection = DriverManager
					.getConnection("jdbc:mysql://localhost:3306/grade_book",
							"root", "password");

		} catch (SQLException e) {
			System.out.println("Connection to DB Failed!");
			e.printStackTrace();
			return;
		}

		if (connection == null) {
			System.out.println("Failed to make connection to DB!");
		}
	}

	/**
	 * Adds a user to the GradeBook DB
	 * 
	 * @param data
	 *            String of all user info to be added, formatted as follows:
	 *            first_name;last_name;user_name;password
	 * @return true if operation succeeds, false otherwise
	 */
	public boolean addUser(String first, String last, String user, String pass) {

		PreparedStatement stmt;
		String insert = "INSERT INTO USERS (first_name, last_name, user_name, password) VALUES (?,?,?,?)";

		// DB operations
		try {
			stmt = connection.prepareStatement(insert);
		} catch (SQLException e) {
			System.out.println("Error with PreparedStatement!");
			return false;
		}

		try {
			stmt.setString(1, first);
			stmt.setString(2, last);
			stmt.setString(3, user);
			stmt.setString(4, pass);
		} catch (SQLException e) {
			System.out.println("Error converting columns in User!");
			return false;
		}

		try {
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error executing update!");
			return false;
		}

		return true;
	}

	/**
	 * Get information about a user from the GradeBook DB
	 * 
	 * @param data
	 *            String of information about user to find, formatted as
	 *            follows: user_name;password
	 * @return a User object if the user exists, null otherwise
	 */
	public User getUser(String name, String pass) {

		Statement stmt = null;
		ResultSet rs = null;

		User user;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from users where user_name = \""
					+ name + "\" and password = \"" + pass + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				user = new User(rs.getString("first_name"),
						rs.getString("last_name"), rs.getInt("user_ID"));
			}

			else {
				System.out.println("No user found!");
				user = null;
			}

		} catch (SQLException e) {
			System.out.println("SQLException");
			user = null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error closing SQL objects!");
			user = null;
		}

		return user;

	}

	/**
	 * Search GradeBook DB for a user by username
	 * 
	 * @param data
	 *            String that contains only the username to search for
	 * @return true if the user exists in the database, false otherwise
	 */
	public boolean searchUser(String data) {

		Statement stmt = null;
		ResultSet rs = null;

		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return true;
		}

		try {
			rs = stmt.executeQuery("Select * from users where user_name = \""
					+ data + "\"");
		} catch (SQLException e) {
			System.out.println("Error with search statement!");
			return true;
		}

		try {
			if (rs.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			return false;
		}

	}

	/**
	 * Adds a course that is associated with a user to the GradeBook database
	 * 
	 * @param user
	 *            user_ID of user to associate course with
	 * @param name
	 *            course name
	 * @param weight
	 *            credit hours ( or weight of User's overall GPA ) for Course
	 * @return true if course was added to database, false otherwise
	 */
	private boolean addCourse(int user, String name, int weight) {
		PreparedStatement coursestmt;
		String insertCourse = "INSERT INTO COURSES (course_name, credit_hours, user_ID) VALUES (?,?,?)";

		// DB operations
		try {
			coursestmt = connection.prepareStatement(insertCourse);
		} catch (SQLException e) {
			System.out.println("Error with PreparedStatement!");
			return false;
		}

		try {
			coursestmt.setString(1, name);
			coursestmt.setInt(2, weight);
			coursestmt.setInt(3, user);
		} catch (SQLException e) {
			System.out.println("Error converting columns in Course!");
			return false;
		}

		try {
			coursestmt.executeUpdate();
			coursestmt.close();
		} catch (SQLException e) {
			System.out.println("Error executing update!");
			return false;
		}

		return true;
	}

	/**
	 * Get a courses' information in the database based on its name
	 * 
	 * @param cname
	 *            name of course to retrieve
	 * @param user
	 *            userID of user that the course belongs to
	 * @return the requested Course if it exists, null otherwise
	 */
	private Course getCourse(String cname, int user) {

		Statement stmt = null;
		ResultSet rs = null;

		Course course;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt
					.executeQuery("Select * from courses where course_name = \""
							+ cname + "\" and user_ID = \"" + user + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				course = new Course(rs.getString("course_name"),
						rs.getInt("credit_hours"), rs.getInt("course_ID"));
			}

			else {
				System.out.println("No course found!");
				course = null;
			}

		} catch (SQLException e) {
			System.out.println("SQLException");
			course = null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error closing SQL objects!");
			course = null;
		}

		return course;
	}

	/**
	 * Get courses from Database that are associated with a specific user_ID
	 * 
	 * @param userID
	 *            the user_ID that the desired courses are associated with
	 * @return server response string, formatted as follows :
	 *         course_name;course_weight;course_ID; for each course that is
	 *         retrieved. The last semicolon is truncated to make the string a
	 *         valid server response for the GradeBook System. Null is returned
	 *         if no courses are found.
	 */
	private String getUserCourses(int userID) {

		Statement stmt = null;
		ResultSet rs = null;

		String list = "";
		Course temp;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from courses where user_ID = \""
					+ userID + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				temp = getCourse(rs.getString("course_name"), userID);
				list = list + temp.getName() + ";" + temp.getWeight() + ";"
						+ temp.getID() + ";";
				while (rs.next()) {
					temp = getCourse(rs.getString("course_name"), userID);
					list = list + temp.getName() + ";" + temp.getWeight() + ";"
							+ temp.getID() + ";";
				}
			} else
				return null;
		} catch (SQLException e) {
			System.out.println("No courses found!");
			return null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error closing SQL objects!");
			return null;
		}

		if (list.charAt(list.length() - 1) == ';')
			list = list.substring(0, list.length() - 1);

		return list;

	}

	/**
	 * Deletes a course from the GradeBook database
	 * 
	 * @param ID
	 *            course_ID of the course to be deleted
	 * @return true if the operation succeeded, false otherwise
	 */
	private boolean deleteCourse(int ID) {
		PreparedStatement stmt;
		String delete = "DELETE FROM COURSES WHERE course_ID = ?";

		try {
			stmt = connection.prepareStatement(delete);

			stmt.setInt(1, ID);

			stmt.executeUpdate();

			stmt.close();
		} catch (SQLException e) {
			System.out.println("Couldn't delete course!");
			return false;
		}

		return true;

	}

	/**
	 * Add a row to the categories table in the GradeBook database
	 * 
	 * @param name
	 *            name of category
	 * @param weight
	 *            weight that should be given to all assignments referencing the
	 *            category
	 * @return true if the operation succeeds, false otherwise
	 */
	private boolean addCategory(String name, int weight, int course_ID) {
		PreparedStatement catStmt;
		String insert = "INSERT INTO Categories (category_name, weight, course_ID) VALUES (?,?,?)";

		// DB operations
		try {
			catStmt = connection.prepareStatement(insert);
		} catch (SQLException e) {
			System.out.println("Error with PreparedStatement!");
			return false;
		}

		try {
			catStmt.setString(1, name);
			catStmt.setInt(2, weight);
			catStmt.setInt(3, course_ID);
		} catch (SQLException e) {
			System.out.println("Error converting columns in Categories!");
			return false;
		}

		try {
			catStmt.executeUpdate();
			catStmt.close();
		} catch (SQLException e) {
			System.out.println("Error executing update!");
			return false;
		}
		return true;
	}

	/**
	 * Retrieve information from a row in the categories table of the GradeBook
	 * database
	 * 
	 * @param category
	 *            the category_ID of to get information about
	 * @return a string formatted as follows : category_name;weight;course_ID if one
	 *         exists, null otherwise
	 */
	private String getCategory(int category) {
		Statement stmt = null;
		ResultSet rs = null;

		int weight;
		String name;
		int course_ID;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from categories where category_ID = \""
					+ category + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				weight = rs.getInt("weight");
				name = rs.getString("category_name");
				course_ID = rs.getInt("course_ID");
			}

			else {
				System.out.println("No category found!");
				return null;
			}

		} catch (SQLException e) {
			System.out.println("SQLException");
			return null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error closing SQL objects!");
			return null;
		}

		return name + ";" + category + ";" + course_ID + ";" + weight;
	}

	/**
	 * Add a row to the assigns table of the GradeBook database
	 * 
	 * @param name
	 *            name of assignment to add
	 * @param score
	 *            score of assignment to add
	 * @param category_ID
	 *            category_ID of assignment to add
	 * @param course_ID
	 *            course_ID of assignment to add
	 * @return true if the operation succeeded, false otherwise
	 */
	private boolean addAssignment(String name, int score, int category_ID,
			int course_ID) {

		PreparedStatement stmt;
		String insert = "INSERT INTO ASSIGNS (assign_name, score, category_ID, course_ID) VALUES (?,?,?,?)";

		// DB operations
		try {
			stmt = connection.prepareStatement(insert);
		} catch (SQLException e) {
			System.out.println("Error with PreparedStatement!");
			return false;
		}

		try {
			stmt.setString(1, name);
			stmt.setInt(2, score);
			stmt.setInt(3, category_ID);
			stmt.setInt(4, course_ID);
		} catch (SQLException e) {
			System.out.println("Error converting columns in assign!");
			return false;
		}

		try {
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error executing update!");
			return false;
		}
		return true;
	}

	/**
	 * Gets information about each assignment associated with a certain course
	 * in the GradeBook system database
	 * 
	 * @param course_ID
	 *            course_ID of course to get assignment list for
	 * @return server response string, formatted as follows :
	 *         assignment_name;assignment_weight
	 *         ;assignment_ID;assignment_score;course_ID; for each assignment
	 *         that is retrieved. The last semicolon is truncated to make the
	 *         string a valid server response for the GradeBook System. Null is
	 *         returned if no assignments are found.
	 */
	private String getCourseAssigns(int course_ID) {
		Statement stmt = null;
		ResultSet rs = null;

		String list = "";
		Assignment temp;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from assigns where course_ID = \""
					+ course_ID + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				temp = getAssignment(rs.getString("assign_name"), course_ID);
				list = list + temp.getName() + ";" + temp.getWeight() + ";"
						+ temp.getID() + ";" + temp.getScore() + ";"
						+ course_ID + ";" + temp.getCategoryID() + ";";
				while (rs.next()) {
					temp = getAssignment(rs.getString("assign_name"), course_ID);
					list = list + temp.getName() + ";" + temp.getWeight() + ";"
							+ temp.getID() + ";" + temp.getScore() + ";"
							+ course_ID + ";" + temp.getCategoryID() + ";";
				}
			} else
				return null;
		} catch (SQLException e) {
			System.out.println("No Assignments found!");
			return null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error closing SQL objects!");
			return null;
		}

		if (list.charAt(list.length() - 1) == ';')
			list = list.substring(0, list.length() - 1);

		return list;
	}

	/**
	 * Retrieve information from a row in the assignment table that is
	 * associated with both a name and course_ID
	 * 
	 * @param name
	 *            name of assignment to search for
	 * @param course_ID
	 *            course_ID of assignment to search for
	 * @return a filled Assignment object if information is found, null
	 *         otherwise
	 */
	private Assignment getAssignment(String name, int course_ID) {
		Statement stmt = null;
		ResultSet rs = null;

		Assignment assignment;

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt
					.executeQuery("Select * from assigns where assign_name = \""
							+ name
							+ "\""
							+ "and course_ID = \""
							+ course_ID
							+ "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				int category = rs.getInt("category_ID");
				String categoryInfoRaw = getCategory(category);
				String[] categoryInfo = categoryInfoRaw.split(";");
				assignment = new Assignment(rs.getString("assign_name"),
						Integer.parseInt(categoryInfo[1]), rs.getInt("assign_ID"),
						rs.getInt("score"), course_ID, category);
			}

			else {
				System.out.println("No assignment found!");
				assignment = null;
			}

		} catch (SQLException e) {
			System.out.println("SQLException");
			assignment = null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("Error closing SQL objects!");
			assignment = null;
		}

		return assignment;
	}

	/**
	 * Gets information about each category associated with a certain course in the
	 * GradeBook system database
	 * 
	 * @param course_ID
	 *            course_ID of course to get category list for
	 * @return server response string, formatted as follows :
	 *         category_name;category_ID;course_ID;weight for each category that is
	 *         retrieved. The last semicolon is truncated to make the string a
	 *         valid server response for the GradeBook System. Null is returned
	 *         if no categories are found.
	 */
	private String getCourseCategories(int course_ID) {
		Statement stmt = null;
		ResultSet rs = null;

		String list = "";

		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from CATEGORIES where course_ID = \""
					+ course_ID + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				list = list + rs.getString("category_name") + ";"
						+ rs.getInt("category_ID") + ";" + course_ID + ";"
						+ rs.getInt("weight") + ";";
				while (rs.next()) {
					list = list + rs.getString("category_name") + ";"
							+ rs.getInt("category_ID") + ";" + course_ID + ";"
							+ rs.getInt("weight") + ";";
				}
			} else
				return null;
		} catch (SQLException e) {
			System.out.println("No Categories found!");
			return null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error closing SQL objects!");
			return null;
		}

		if (list.charAt(list.length() - 1) == ';')
			list = list.substring(0, list.length() - 1);

		return list;
	}
	
	/**
	 * Deletes and assignment from the GradeBook database
	 * @param assign_ID the assign_ID of the assignment to delete
	 * @return true if the operation succeeds, false otherwise
	 */
	private boolean deleteAssign(int assign_ID)
	{
		PreparedStatement stmt;
		String delete = "DELETE FROM ASSIGNS WHERE assign_ID = ?";

		try {
			stmt = connection.prepareStatement(delete);

			stmt.setInt(1, assign_ID);

			stmt.executeUpdate();

			stmt.close();
		} catch (SQLException e) {
			System.out.println("Couldn't delete course!");
			return false;
		}

		return true;
	}
	
	/**
	 * Deletes a category from the GradeBook database
	 * @param category_ID the category_ID of the category to delete
	 * @return true if the operation succeeds, false otherwise
	 */
	private boolean deleteCategory(int category_ID)
	{
		PreparedStatement stmt;
		String delete = "DELETE FROM CATEGORIES WHERE category_ID = ?";

		try {
			stmt = connection.prepareStatement(delete);

			stmt.setInt(1, category_ID);

			stmt.executeUpdate();

			stmt.close();
		} catch (SQLException e) {
			System.out.println("Couldn't delete category!");
			return false;
		}

		return true;
	}
	
	/**
	 * Gets the weighted average for a course.  This assumes that
	 * all assignments entered make up the full weight of the course, such that
	 * an 85 at 60% weight averaged with a 90 of 30% weight will give 86.67 even though
	 * the weights do not add to 100%.
	 * @param course_ID the course to get averages for
	 * @return a string that represents the average for the course rounded to 2 decimal places.
	 */
	private String getCourseAvg(int course_ID)
	{
		Statement stmt = null;
		ResultSet rs = null;
		
		Assignment temp;
		
		AssignmentList list = new AssignmentList();	
		
		double avg = 0;
		
		double score;
		double weight;
		
		double totalWeight = 0;
		
		// DB operations
		try {
			stmt = connection.createStatement();
		} catch (SQLException e) {
			System.out.println("Error creating statement!");
			return null;
		}
		try {
			rs = stmt.executeQuery("Select * from assigns where course_ID = \""
					+ course_ID + "\"");
		} catch (SQLException e) {
			System.out.println("Invalid Query!");
			return null;
		}

		try {
			if (rs.next()) {
				temp = getAssignment(rs.getString("assign_name"), course_ID);
				list.add(temp);
				while (rs.next()) {
					temp = getAssignment(rs.getString("assign_name"), course_ID);
					list.add(temp);
				}
			} else
				return null;
		} catch (SQLException e) {
			System.out.println("No Assignments found!");
			return null;
		}

		try {
			rs.close();
			stmt.close();
		} catch (Exception e) {
			System.out.println("Error closing SQL objects!");
			return null;
		}
		
		for(int i = 0; i<list.getSize(); i++)
		{
			temp=list.getAssignment(i);
			score = temp.getScore();
			weight = temp.getWeight();
			totalWeight += weight;
			avg += score*weight;
		}
		
		avg = avg/totalWeight;
		
		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return twoDForm.format(avg);
	}
}