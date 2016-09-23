package pse_gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SyntaxAnalyzer {
	
	public SyntaxAnalyzer(){
	}
	
	public JSON userRequestToJson(UserRequest userRequest){
		if (userRequest.getFieldList() == null || userRequest.getFieldList().size() == 0)
			return null;
		Iterator<Field> fieldIterator = userRequest.getFieldList().iterator();
		Map<String, Object> requestAsMap = new HashMap<String, Object>();
		while(fieldIterator.hasNext()){
			Field currentField = fieldIterator.next();
			if (currentField.getValue() != null && !currentField.getValue().isEmpty())
				requestAsMap.put(currentField.getName(), currentField.getValue());
		}
		return new JSON(new JSONObject(requestAsMap).toString());
	}
	
	public ServerResponse jsonToServerResponse(JSON json){
		try{
			JSONObject referenceNode = new JSONObject(json.getJSONFormatedData());
			Map<String, Object> parsedReferenceNode = parseNode(referenceNode, "");
			return new ServerResponse(parsedReferenceNode);
		}catch(Exception e){
			Map<String, Object> errorMap = new HashMap<String, Object>();
			if (e instanceof JSONException){
				errorMap.put(PSEConstants.ERROR_KEY, json.getJSONFormatedData());
			}
			else
				errorMap.put(PSEConstants.ERROR_KEY, "An unknown error occured when parsing JSON. Here is the error:\n\n" + e.toString());
			
			return new ServerResponse(errorMap);
		}
		
	}
	
	private Map<String, Object> parseNode(JSONObject node, String parentKey){
		Map<String, Object> keyValueMap = new HashMap<String, Object>();
		Iterator<String> keyIterator = node.keys();
		
		//Necessary attributes for detecting if we're talking about a Field or not.
		String fieldDescription = null;
		Boolean fieldRequired = null;
		String fieldValue = null;
		Boolean looksLikeAFieldObject = true;
		
		while(keyIterator.hasNext()){
			String key = keyIterator.next();
			Object value = node.get(key);
			if(value instanceof Double)
				value = Float.parseFloat(value.toString());
			if (value instanceof JSONObject){
				// It's another node. Calling recursively.
				Map<String, Object> childMap = parseNode((JSONObject)value, key);
				if (childMap.size() == 1 && childMap.keySet().toArray()[0].equals(key)){
					// Could be a single Field object, because the child map has a length of 1, and the single child key is the same as this key. Testing...
					Object childValue = childMap.get(childMap.keySet().toArray()[0]);
					if(childValue instanceof Field){
						keyValueMap.put(key, (Field)childValue);
					}
					else {
						keyValueMap.put(key, childMap);
					}
				}
				else{
					keyValueMap.put(key, childMap);
				}
				looksLikeAFieldObject = false;
			}
			else if (value instanceof JSONArray){
				keyValueMap.put(key, parseList((JSONArray)value, key));
				looksLikeAFieldObject = false;
			}
			else if (
					value instanceof String || 
					value instanceof Integer || 
					value instanceof Float ||
					value instanceof Boolean){
				// It's a leaf. Must detect if we're talking about a Field object or something else.
				if (key.equalsIgnoreCase(PSEConstants.FIELD_DESCRIPTION_KEY) && looksLikeAFieldObject == true)
					fieldDescription = (String)value;
				else if (key.equalsIgnoreCase(PSEConstants.FIELD_VALUE_KEY)  && looksLikeAFieldObject == true)
					fieldValue = (String) value;
				else if (key.equalsIgnoreCase(PSEConstants.FIELD_REQUIRED_KEY) && looksLikeAFieldObject == true)
					fieldRequired = (Boolean)value;
				else{
					// Any other field means it's not a Field object,
					looksLikeAFieldObject = false;
				}
					
					
				if(looksLikeAFieldObject && fieldDescription != null && fieldRequired != null && fieldValue != null){
					if (keyIterator.hasNext()){
						//Checking if this node in completed. If not, (meaning there are other key-values), it is not a Field object,
						// and we've been tricked all along.
						looksLikeAFieldObject = false;
					}
					else {
						//It really is a field, and all the values are filled up. Adding a new Field object.
						Field field = new Field(parentKey, fieldDescription, fieldValue, fieldRequired);
						keyValueMap.put(parentKey, field);
					}
				}
				
				if (looksLikeAFieldObject == false){
					//It's not a field object. Adding the current value, and all values stored in fieldDescription, fieldRequired and fieldValue 
					// that were temporarily stored there.
					keyValueMap.put(key, value);
					
					
					if (fieldDescription != null){
						keyValueMap.put(PSEConstants.FIELD_DESCRIPTION_KEY, fieldDescription);
						fieldDescription = null;
					}
					if (fieldValue != null){
						keyValueMap.put(PSEConstants.FIELD_VALUE_KEY, fieldValue);
						fieldValue = null;
					}
					if (fieldRequired != null){
						keyValueMap.put(PSEConstants.FIELD_REQUIRED_KEY, fieldRequired);
						fieldValue = null;
					}
				}
			}
			else{
				// It's something else we don't know. Throwing an exception.
				throw new RuntimeException("The value \"" + value.toString() + 
						"\" (key \"" + key + "\", parent key \"" + parentKey + 
						"\", Class " + value.getClass().toString() + ")is of an unknown type.");
			}
		}
		return keyValueMap;
	}
	
	private List<Object>parseList(JSONArray array, String parentKey){
		List<Object> output = new ArrayList<Object>();
		Iterator<Object> iterator = array.toList().iterator();
		while(iterator.hasNext()){
			Object value = iterator.next();
			if(value instanceof Double)
				value = Float.parseFloat(value.toString());
			if (value instanceof Map){
				output.add(parseNode(new JSONObject((Map)value), parentKey));
			}
			else if (value instanceof List){
				output.add(parseList(new JSONArray((List)value), parentKey));
			}
			else if (
					value instanceof String || 
					value instanceof Integer || 
					value instanceof Float ||
					value instanceof Boolean){
				output.add(value);
			}
			else{
				// It's something else we don't know. Throwing an exception.
				throw new RuntimeException("The value \"" + value.toString() + 
						"\" (parent key \"" + parentKey + 
						"\", Class " + value.getClass().toString() + ")is of an unknown type.");
			}
		}
		return output;
	}
}
