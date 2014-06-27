package gradebookdata;

public class Category {
	
	private String name;
	private int ID;
	private int course_ID;
	private int weight;
	
	public Category(String name, int ID, int course_ID, int weight)
	{
		this.name = name;
		this.ID = ID;
		this.course_ID = course_ID;
		this.weight = weight;
	}

	public String getName()
	{
		return this.name;
	}
	
	public Integer getID()
	{
		return this.ID;
	}
	
	public Integer getCourseID()
	{
		return this.course_ID;
	}
	
	public Integer getWeight()
	{
		return this.weight;
	}
	
	@Override
	public String toString()
	{
		return this.name+"\nWeight: " + this.weight + "%";
	}
}
