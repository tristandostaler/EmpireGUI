package pse_gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.control.TreeItem;

public class MapTreeItem extends TreeItem<String>{

	private HashMap<String, Object> map;
	private SharedCentralisedClass sharedClass;
	
	public MapTreeItem(SharedCentralisedClass sharedClass, String name) {
		super(name);
		this.sharedClass = sharedClass;
	}
	
	public HashMap<String, Object> getMap() {
		return map;
	}
	
	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}
	
	public ArrayList<Field> getFieldList(){
		ArrayList<Field> fieldList = new ArrayList<Field>();
		Map<String, Object> fieldMap = (Map<String, Object>)map.get("options");
		if(fieldMap == null)
			return null;
		for(String item2 : fieldMap.keySet()){
			if(fieldMap.get(item2) instanceof Field){
				fieldList.add((Field)fieldMap.get(item2));
			}
		}
		return fieldList;
	}
}
