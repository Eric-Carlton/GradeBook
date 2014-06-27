package gradebookdata;

import java.util.ArrayList;

public class CategoryList {
	
	private ArrayList<Category> types;

	/**
	 * Creates a TypeList
	 */
	public CategoryList() {
		types = new ArrayList<Category>();
	}

	/**
	 * Gets number of items in this TypeList
	 * 
	 * @return number of items in type list
	 */
	public int getSize() {
		return types.size();
	}

	/**
	 * Gets names of types in this list
	 * 
	 * @return ArrayList of type names
	 */
	public ArrayList<String> getTypeNames() {
		// if size is 0, list of names is null
		if (getSize() <= 0)
			return null;

		ArrayList<String> names = new ArrayList<String>();
		for (int i = 0; i < types.size(); i++)
			names.add(types.get(i).getName());

		return names;
	}

	/**
	 * Gets IDs of all types in this list
	 * 
	 * @return ArrayList of type IDs
	 */
	public ArrayList<Integer> getTypeIDs() {
		ArrayList<Integer> Ids = new ArrayList<Integer>();

		if (getSize() <= 0)
			return Ids;

		for (int i = 0; i < types.size(); i++)
			Ids.add(types.get(i).getID());

		return Ids;
	}

	/**
	 * Gets type at specified position in this list
	 * 
	 * @param pos
	 *            position of desired type in list
	 * @return type at specified position
	 */
	public Category getType(int pos) {
		if (pos > getSize() - 1)
			return null;

		return types.get(pos);
	}

	/**
	 * Add a type to this TypeList
	 * 
	 * @param toAdd
	 *            the Type to add to this list
	 */
	public void add(Category toAdd) {
		types.add(toAdd);
	}
	
	/**
	 * Returns a String formatted as:
	 * type1_name
	 * Weight: type1_weight%
	 * 
	 * type2_name
	 * Weight: type2_weight%
	 * 
	 * etc.
	 */
	@Override
	public String toString()
	{
		String result = "";
		for(int i = 0; i < types.size(); i++)
			result = result + types.get(i).toString()+"\n\n";
		
		return result;
	}

}
