package gradebookdata;

/**
 * Represents a row in the assigns table of the GradeBook database
 * 
 * @author Eric Carlton
 * 
 */
public class Assignment {

	private int ID;
	private String name;
	private int score;
	private int weight;
	private int course_ID;
	private int categoryID;

	/**
	 * Create a new Assignment with all fields filled
	 * 
	 * @param ID
	 *            ID of this assignment
	 * @param name
	 *            name of this assignment
	 * @param score
	 *            score for this assignment
	 * @param type
	 *            type_ID of this assignment
	 */
	public Assignment(String name, int weight,  int ID, int score, int course_ID, int categoryID) {
		this.ID = ID;
		this.name = name;
		this.score = score;
		this.weight = weight;
		this.course_ID = course_ID;
		this.categoryID = categoryID;
	}

	/**
	 * Create a generic assignment
	 */
	public Assignment() {
		this("none", -1, -1, 0, -1, -1);
	}

	public String getName() {
		return name;
	}

	public Integer getScore() {
		return score;
	}

	public Integer getID() {
		return ID;
	}

	public Integer getWeight() {
		return weight;
	}
	
	public Integer getCourseID()
	{
		return this.course_ID;
	}
	
	public Integer getCategoryID(){
		return this.categoryID;
	}
	/**
	 * Returns a string formatted as:
	 * assignment_name
	 * Score : assignment_score
	 * Weight: assignment_weight
	 */
	@Override
	public String toString()
	{
		return (name+"\nScore: "+score + "\nWeight: " + weight);
	}

}
