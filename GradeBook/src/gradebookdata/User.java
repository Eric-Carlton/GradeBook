package gradebookdata;

/**
 *  Represents information retrieved from GradeBook DB Server
 * @author Eric Carlton
 */
public class User {

	private String firstName;
	private String lastName;
	private int ID;

	/**
	 * Create a generic user
	 */
	public User() {
		this("none","none", -1);
	}

	/**
	 * Create a user
	 * @param firstName first name of this user
	 * @param lastName last name of this user
	 * @param ID ID of this user
	 */
	public User(String firstName, String lastName, int ID) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.ID = ID;
	}
	
	/**
	 * Get first name of this user
	 * @return first name of this user in String format
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Get last name of this user
	 * @return last name of this user in String format
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Get full name of this user
	 * @return first name of user and last name of user, separated by a space in String format
	 */
	public String getName() {
		return firstName + " " + lastName;
	}

	/**
	 * Returns ID of this user
	 * @return the ID of this user in int format
	 */
	public Integer getID() {
		return ID;
	}

	/**
	 * Return string of all of this user's fields
	 */
	public String toString() {
		return getName()+"\n"+getID();
	}

}
