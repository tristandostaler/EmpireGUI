package pse_gui;

public class JSON {
	private String jsonData;
	
	private SharedCentralisedClass sharedClass;
	
	public JSON(SharedCentralisedClass sharedClass, String jsonData){
		this.sharedClass = sharedClass;
		this.jsonData = jsonData;
	}
	
	public String getJSONFormatedData(){
		return this.jsonData;
	}
	
	@Override
	public String toString(){
		return this.jsonData;
	}
}
