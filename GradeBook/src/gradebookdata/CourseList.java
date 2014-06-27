package gradebookdata;

import java.util.ArrayList;

/**
 * A list of Course Objects
 * 
 * @author Eric Carlton
 */
public class CourseList {

	public static ArrayList<Course> courses;
	/**
	 * Create a CourseList	
	 */
	public CourseList() {		
		courses = new ArrayList<Course>();

	}

	/**
	 * Gets number of items in this CourseList
	 * 
	 * @return number of items in course list
	 */
	public int getSize() {
		return courses.size();
	}
	
	/**
	 * Add a course to this CourseList
	 * @param toAdd the Course to add
	 */
	public void add(Course toAdd)
	{
		courses.add(toAdd);
	}

	/**
	 * Gets names of courses in this list
	 * 
	 * @return ArrayList of course names
	 */
	public ArrayList<String> getCourseNames() {
		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < courses.size(); i++)
			names.add(courses.get(i).getName());

		return names;
	}

	/**
	 * Gets IDs of all courses in this list
	 * 
	 * @return ArrayList of course IDs
	 */
	public ArrayList<Integer> getCourseIDs() {
		ArrayList<Integer> Ids = new ArrayList<Integer>();
		for (int i = 0; i < courses.size(); i++)
			Ids.add(courses.get(i).getID());

		return Ids;
	}

	/**
	 * Gets Course at specified position in this list
	 * 
	 * @param pos
	 *            position of desired Course in list
	 * @return Course at specified position
	 */
	public Course getCourse(int pos) {
		if (pos > getSize() - 1)
			return null;

		return courses.get(pos);
	}
	
	/**
	 * Returns a string of formatted as such:
	 * course1_name
	 * course1_weight hour(s)
	 * 
	 * course2_name
	 * course2_weight hour(s)
	 */
	@Override
	public String toString()
	{
		String result = "";
		for(int i = 0; i < courses.size(); i++)
			result = result + courses.get(i).toString()+"\n\n";
		
		return result;
	}

}
