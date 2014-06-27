//package gradebook;
//
//import gradebookclient.Client;
//import gradebookdata.Assignment;
//import gradebookdata.AssignmentList;
//import gradebookdata.Course;
//import gradebookdata.CourseList;
//import gradebookdata.Packet;
//import gradebookdata.Category;
//import gradebookdata.CategoryList;
//import gradebookdata.User;
//
//import java.math.BigInteger;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ArrayList;
//import java.util.Scanner;
//
//public class Demo {
//
//	private static Scanner kbd = new Scanner(System.in);
//
//	public static void main(String[] args) {
//
//		displayMainMenu();
//		kbd.close();
//
//	}
//
//	private static void displayMainMenu() {
//		int choice = 0;
//		System.out.println("Hello! Please make a selection!:");
//		System.out.println("1. Login \n2. Create Account");
//		while (choice < 1) {
//			choice = kbd.nextInt();
//			kbd.nextLine();
//			switch (choice) {
//			case 1:
//				login();
//				break;
//			case 2:
//				createAccount();
//				break;
//			default:
//				System.out
//				.println("I'm sorry, that's not a valid choice! Please choose again: ");
//				choice = 0;
//				break;
//			}
//		}
//	}
//
//	private static void login() {
//
//		User curUser;
//
//		System.out.println("Enter your username: ");
//		String username = kbd.nextLine();
//		System.out.println("Enter your password: ");
//		String password = kbd.nextLine();
//		
//		Client login = new Client(new Packet(1, username + ";" + md5(password)));
//
//		if (login.succeeded()) {
//			String[] temp = login.getResponse().getData().split(";");
//
//			if (temp.length != 3) {
//				System.out.println("Error receiving information from server!");
//				System.exit(-1);
//			}
//
//			curUser = new User(temp[0], temp[1], Integer.parseInt(temp[2]));
//
//			courseOptionsMenu(curUser);
//
//		}
//
//		else
//			System.out.println(login.getResponse().getData());
//	}
//
//	private static void createAccount() {
//		System.out.println("What is your first name?:");
//		String first = kbd.nextLine();
//		System.out.println("What is your last name?:");
//		String last = kbd.nextLine();
//		System.out.println("What is your desired username?:");
//		String user = kbd.nextLine();
//		System.out.println("What would you like your password to be?: ");
//		String pass = kbd.nextLine();
//
//		Client create = new Client(new Packet(0, first + ";" + last + ";"
//				+ user + ";" + md5(pass)));
//
//		if (create.succeeded())
//			System.out.println("Account Created!");
//
//		else
//			System.out.println(create.getResponse().getData());
//	}
//
//	private static String md5(String password) {
//		String md5 = null;
//
//		if (null == password)
//			return null;
//
//		try {
//
//			// Create MessageDigest object for MD5
//			MessageDigest digest = MessageDigest.getInstance("MD5");
//
//			// Update input string in message digest
//			digest.update(password.getBytes(), 0, password.length());
//
//			// Converts message digest value in base 16 (hex)
//			md5 = new BigInteger(1, digest.digest()).toString(16);
//
//		} catch (NoSuchAlgorithmException e) {
//			System.out.println("NoSuchAlgorithmException - MD5");
//			return null;
//		}
//		return md5;
//	}
//
//	private static void courseOptionsMenu(User user) {
//		System.out.println("Hello " + user.getName() + "\nMake a selection: ");
//		System.out
//		.println("1. View Courses\n2. Add a course\n3. Delete a course");
//		int choice = 0;
//
//		while (choice < 1) {
//			choice = kbd.nextInt();
//			kbd.nextLine();
//			switch (choice) {
//			case 1:
//				viewCourses(user);
//				break;
//			case 2:
//				addCourse(user);
//				break;
//			case 3:
//				deleteCourse(user);
//				break;
//			default:
//				System.out.println("Please make a valid selection");
//				choice = 0;
//			}
//		}
//
//	}
//
//	private static void viewCourses(User user) {
//
//		System.out.println("Select a course to view more infomation: ");
//
//		Course course = courseSelectionMenu(user);
//		if(course != null)
//			courseDetailsMenu(course);
//
//	}
//
//	private static void addCourse(User user) {
//		System.out.println("What is the course name? : ");
//		String name = kbd.nextLine();
//		System.out.println("How many credit hours is the course? : ");
//		String hours = kbd.nextLine();
//
//		Client client = new Client(new Packet(100, user.getID().toString()
//				+ ";" + name + ";" + hours));
//
//		System.out.println(client.getResponse().getData());
//	}
//
//	private static void deleteCourse(User user) {
//		System.out.println("Select a course to delete: ");
//
//		Course course = courseSelectionMenu(user);
//
//		if (course == null) {
//			System.out.println("Error in courseSelectionMenu!");
//			return;
//		}
//
//		Client delete = new Client(new Packet(102, course.getID().toString()));
//
//		System.out.println(delete.getResponse().getData());
//
//	}
//
//	private static Course courseSelectionMenu(User user) {
//		Client get = new Client(new Packet(101, user.getID().toString()));
//
//		Client average;
//
//		String getData = get.getResponse().getData();
//
//		CourseList courses = new CourseList();
//
//		if (!get.succeeded()) {
//			System.out.println(getData);
//			return null;
//		}
//
//
//		String[] temp = getData.split(";");
//
//		if (temp.length % 3 != 0) {
//			System.out.println("Error in Packet sent from client!");
//			return null;
//		}
//
//		for (int i = 0; i < temp.length; i += 3) {
//			Course curCourse = new Course(temp[i],
//					Integer.parseInt(temp[i + 1]),
//					Integer.parseInt(temp[i + 2]));
//			courses.add(curCourse);
//		}
//		
//		if(courses.getSize() <= 0){
//			return null;
//		}
//		
//		else{
//			ArrayList<String> options = courses.getCourseNames();
//
//			for (int i = 0; i < options.size(); i++)
//			{
//				System.out.println((i + 1) + ". " + options.get(i));
//				average = new Client(new Packet(400, courses.getCourse(i).getID().toString()));
//				System.out.println("Average : " + average.getResponse().getData());
//			}
//
//
//			int choice = 0;
//
//			while (choice < 1) {
//				choice = kbd.nextInt();
//				kbd.nextLine();
//
//				if (choice < 1 || choice > options.size()) {
//					System.out.println("Please enter a valid choice.");
//					choice = 0;
//				}
//
//			}
//
//			choice -= 1;
//
//			return courses.getCourse(choice);
//		}
//	}
//
//	private static void courseDetailsMenu(Course course) {
//		System.out.println("What would you like to do with " + course.getName()
//				+ "?");
//		System.out
//		.println("1. View assignments\n2. Add an assignment\n3. Add an assignment type\n"
//				+ "4. Delete an assignment\n5. Delete an assignment type");
//
//		int choice = 0;
//
//		while (choice < 1) {
//			choice = kbd.nextInt();
//			kbd.nextLine();
//			switch (choice) {
//			case 1:
//				viewAssigns(course);
//				break;
//			case 2:
//				addAssign(course);
//				break;
//			case 3:
//				addType(course);
//				break;
//			case 4:
//				deleteAssign(course);
//				break;
//			case 5:
//				deleteType(course);
//				break;
//			default:
//				System.out.println("Please make a valid selection");
//				choice = 0;
//			}
//		}
//	}
//
//	private static void deleteType(Course course) {
//
//		System.out.println("Select Type to delete : ");
//
//		Category toDelete = viewTypes(course);
//
//		Client client = new Client(new Packet(303, toDelete.getID().toString()));
//
//		System.out.println(client.getResponse().getData());		
//	}
//
//
//	private static void deleteAssign(Course course) {
//
//		Client client = new Client(new Packet(201, course.getID().toString()));
//
//		String getData = client.getResponse().getData();		
//
//		AssignmentList assigns = new AssignmentList();
//
//		if (!client.succeeded()) {
//			System.out.println(getData);
//			return;
//		}
//
//		String[] temp = getData.split(";");
//
//		if (temp.length % 5 != 0) {
//			System.out.println("Error in Packet sent from client!");
//			return;
//		}
//
//		for (int i = 0; i < temp.length; i += 5) {
//			Assignment cur = new Assignment(temp[i],
//					Integer.parseInt(temp[i + 1]),
//					Integer.parseInt(temp[i + 2]), Integer.parseInt(temp[i+3]), Integer.parseInt(temp[i+4]));
//			assigns.add(cur);
//		}
//
//		ArrayList<String> options = assigns.getAssignmentNames();
//
//		System.out.println("Select a course to delete: ");
//
//		for(int i = 0; i<options.size(); i++)
//			System.out.println((i+1) + ". " + options.get(i));
//
//		int choice = 0;
//
//		while (choice < 1) {
//			choice = kbd.nextInt();
//			kbd.nextLine();
//
//			if (choice < 1 || choice > options.size()) {
//				System.out.println("Please enter a valid choice.");
//				choice = 0;
//			}
//
//		}
//
//		choice -= 1;
//
//		client = new Client(new Packet(202, assigns.getAssignment(choice).getID().toString()));
//
//		System.out.println(client.getResponse().getData());
//	}
//
//	private static void addType(Course course) {
//		System.out.println("What would you like the name of the type to be?:");
//		String name = kbd.nextLine();
//		System.out.println("What would you like the weight of this type to be?\n" +
//				"Only type the decimal form, 15% should be entered as 15");
//		String weight = kbd.nextLine();
//
//		Client client = new Client(new Packet(300, name+";"+weight+";"+course.getID().toString()));
//
//		System.out.println(client.getResponse().getData());
//	}
//
//	private static void addAssign(Course course) {
//		System.out.println("What would you like to name this assignment?");
//		String name = kbd.nextLine();
//		System.out.println("What was the score for this assignment? - All scores should be out of 100");
//		String score = kbd.nextLine();
//		System.out.println("What type of assignment is this?");
//		System.out.println("If you need to add a type, please terminate and add the appropriate type before adding this assignment.");
//		Category type = viewTypes(course);
//		
//		if(type == null)
//			System.exit(0);
//
//		Client client = new Client(new Packet(200,name+";"+score+";"+type.getID().toString()+";"+course.getID().toString()));
//
//		System.out.println(client.getResponse().getData());
//	}
//
//	private static void viewAssigns(Course course) {
//
//		Client client = new Client(new Packet(201, course.getID().toString()));
//
//		String getData = client.getResponse().getData();		
//
//		AssignmentList assigns = new AssignmentList();
//
//		if (!client.succeeded()) {
//			System.out.println(getData);
//			return;
//		}
//
//		String[] temp = getData.split(";");
//
//		if (temp.length % 5 != 0) {
//			System.out.println("Error in Packet sent from client!");
//			return;
//		}
//
//		for (int i = 0; i < temp.length; i += 5) {
//			Assignment cur = new Assignment(temp[i],
//					Integer.parseInt(temp[i + 1]),
//					Integer.parseInt(temp[i + 2]), Integer.parseInt(temp[i+3]), Integer.parseInt(temp[i+4]));
//			assigns.add(cur);
//		}
//
//		System.out.println(assigns.toString());		
//	}
//
//	private static Category viewTypes(Course course)
//	{
//		Client get = new Client(new Packet(302, course.getID().toString()));
//
//		String getData = get.getResponse().getData();
//
//		CategoryList types = new CategoryList();
//
//		if (!get.succeeded()) {
//			System.out.println(getData);
//			return null;
//		}
//
//		String[] temp = getData.split(";");
//
//		if (temp.length % 4 != 0) {
//			System.out.println("Error in Packet sent from client!");
//			return null;
//		}
//
//		for (int i = 0; i < temp.length; i += 4) {
//			Category cur = new Category(temp[i],
//					Integer.parseInt(temp[i + 1]),
//					Integer.parseInt(temp[i + 2]),
//					Integer.parseInt(temp[i+3]));
//			types.add(cur);
//		}
//
//		ArrayList<String> options = types.getTypeNames();
//
//		for (int i = 0; i < options.size(); i++)
//			System.out.println((i + 1) + ". " + options.get(i));
//
//		int choice = 0;
//
//		while (choice < 1) {
//			choice = kbd.nextInt();
//			kbd.nextLine();
//
//			if (choice < 1 || choice > options.size()) {
//				System.out.println("Please enter a valid choice.");
//				choice = 0;
//			}
//
//		}
//
//		choice -= 1;
//
//		return types.getType(choice);
//	}
//}
