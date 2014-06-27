package gradebookdata;

import java.util.ArrayList;

/**
 * A list of Assignment Objects
 * 
 * @author Eric Carlton
 */
public class AssignmentList {

	private ArrayList<Assignment> assignments;

	/**
	 * Creates an AssignmentList
	 */
	public AssignmentList() {
		assignments = new ArrayList<Assignment>();
	}

	/**
	 * Gets number of items in this AssignmentList
	 * 
	 * @return number of items in assignment list
	 */
	public int getSize() {
		return assignments.size();
	}

	/**
	 * Gets names of Assignments in this list
	 * 
	 * @return ArrayList of assignment names
	 */
	public ArrayList<String> getAssignmentNames() {
		// if size is 0, list of names is null
		if (getSize() <= 0)
			return null;

		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < assignments.size(); i++)
			names.add(assignments.get(i).getName());

		return names;
	}

	/**
	 * Gets IDs of all Assignments in this list
	 * 
	 * @return ArrayList of assignment IDs
	 */
	public ArrayList<Integer> getAssignmentIDs() {
		ArrayList<Integer> Ids = new ArrayList<Integer>();

		if (getSize() <= 0)
			return Ids;

		for (int i = 0; i < assignments.size(); i++)
			Ids.add(assignments.get(i).getID());

		return Ids;
	}

	/**
	 * Gets Assignment at specified position in this list
	 * 
	 * @param pos
	 *            position of desired Assignment in list
	 * @return Assignment at specified position
	 */
	public Assignment getAssignment(int pos) {
		if (pos > getSize() - 1)
			return null;

		return assignments.get(pos);
	}

	/**
	 * Add an assignment to this AssignmentList
	 * 
	 * @param toAdd
	 *            the Assignment to add to this list
	 */
	public void add(Assignment toAdd) {
		assignments.add(toAdd);
	}
	
	/**
	 * Returns a String formatted as:
	 * assignment1_name
	 * Score: assignment1_weight
	 * 
	 * assignment2_name
	 * Score: assignment2_weight
	 * 
	 * etc.
	 */
	@Override
	public String toString()
	{
		String result = "";
		for(int i = 0; i < assignments.size(); i++)
			result = result + assignments.get(i).toString()+"\n\n";
		
		return result;
	}

}
