package pse_gui;
import java.util.Map;

public class ServerResponse {
	
	/**
	 * Possible values for the Object:
	 * - String
	 * - Int
	 * - Float
	 * - Boolean
	 * - A Field object (As defined in the project)
	 * - List<Object> (Where the object can be any of these object types)
	 * - Map<String, Object> (Where the object can be any of these object types)
	 */
	private Map<String, Object> value;
	
	private SharedCentralisedClass sharedClass;
	
	public ServerResponse(SharedCentralisedClass sharedClass, Map<String, Object> value){
		this.sharedClass = sharedClass;
		this.value = value;
	}
	
	public Map<String, Object> getValue(){
		return value;
	}	
}
