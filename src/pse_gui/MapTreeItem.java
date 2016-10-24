package pse_gui;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.control.TreeItem;

@SuppressWarnings("restriction")
public class MapTreeItem extends TreeItem<String>{

	private HashMap<String, Object> map;
	
	public MapTreeItem(String name) {
		super(name);
	}
	
	public HashMap<String, Object> getMap() {
		return map;
	}
	
	public void setMap(HashMap<String, Object> map) {
		this.map = map;
	}
	
	@SuppressWarnings("unchecked")
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
