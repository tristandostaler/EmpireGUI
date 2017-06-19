package pse_gui.Models;

public class JSON {
	private String jsonData;
	
	public JSON(String jsonData){
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
