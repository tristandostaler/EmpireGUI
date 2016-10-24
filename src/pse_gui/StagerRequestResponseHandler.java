package pse_gui;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.control.Alert.AlertType;

@SuppressWarnings("restriction")
public class StagerRequestResponseHandler extends ResponseHandler{

	public StagerRequestResponseHandler() {
		super(false);
	}
	@SuppressWarnings("unchecked")
	@Override
	public void baseHandleResponse(ServerResponse serverResponse) {
				
		Map<String, Object> map = serverResponse.getValue();
		for(String item : map.keySet()){
			if(map.get(item) instanceof Map<?,?>){
				Map<String, Object> map2 = (Map<String, Object>) map.get(item);
				if(map2.containsKey("Output"))
					showOutut(map2);
				else{
					String s = serverResponse.getValue().toString();
					System.out.println(s);
					SharedCentralisedClass.getInstance().writeTextToLogArea(s);
				}
			}
			else{
				if(map.containsKey("Output"))
					showOutut(map);
				else{
					String s = serverResponse.getValue().toString();
					System.out.println(s);
					SharedCentralisedClass.getInstance().writeTextToLogArea(s);
				}
			}
		}
		
	}
	
	public void showOutut(Map<String, Object> map){
		System.out.println(map.get("Output").toString());
		SharedCentralisedClass.getInstance().writeTextToLogArea(map.get("Output").toString());
		
		//Alert alert = new Alert(AlertType.INFORMATION, map.get("Output").toString(), ButtonType.OK);
		//alert.showAndWait();
		
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle("Stager Output");
		alert.setHeaderText("Stager Output");
		alert.setContentText("This is the generated Output: ");
		
		Label label = new Label("Output:");

		TextArea textArea = new TextArea(map.get("Output").toString());
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

}