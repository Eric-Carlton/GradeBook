package gradebookdata;

/**
 * Represents one row of the course table in the GradeBook database
 * 
 * @author Eric Carlton
 * 
 */
public class Course {

	private String name;
	private int weight;
	private int ID;

	/**
	 * Create a course with all fields filled
	 * 
	 * @param name
	 *            name of course
	 * @param weight
	 *            credit hours ( or weight ) of course
	 * @param ID
	 *            course_ID in course table
	 */
	public Course(String name, int weight, int ID) {
		this.name = name;
		this.weight = weight;
		this.ID = ID;
	}

	/**
	 * Create a generic course
	 */
	public Course() {
		this("no name", 0, -1);
	}

	public String getName() {
		return name;
	}

	public Integer getWeight() {
		return weight;
	}

	public Integer getID() {
		return ID;
	}

	/**
	 * Returns a string formatted as:
	 * course_name
	 * course_weight hour(s)
	 */
	@Override
	public String toString() {
		String result = name + "\n" + weight;
		if (weight == 1)
			result = result + " hour";
		else
			result = result + " hours";
		return result;
	}

}
