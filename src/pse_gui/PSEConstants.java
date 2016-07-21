package pse_gui;

public class PSEConstants {
	
	/*
	 * All the endpoints
	 */
	public static final String MODULE_LIST_ENDPOINT = "modules";
	public static final String AGENT_LIST_ENDPOINT = "agents";
	public static final String STAGER_LIST_ENDPOINT = "stagers";
	public static final String LISTENER_LIST_ENDPOINT = "listeners";
	
	/*
	 * Constants needed for the SyntaxAnalyzer. Represents all the keys that the values are needed in the Field object.
	 */
	public static final String FIELD_DESCRIPTION_KEY = "Description";
	public static final String FIELD_REQUIRED_KEY = "Required";
	public static final String FIELD_VALUE_KEY = "Value";
	
	// Constant necessary to get the endpoint value from a Map<String, Object> containing the description of a module
	public static final String ENDPOINT_NAME_KEY = "Name";
	
	public static final String MESSAGE_KEY = "msg";
	public static final String ERROR_KEY = "error";
	public static final String EXCEPTION_KEY = "Exception";

}
