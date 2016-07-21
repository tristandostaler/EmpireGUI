package pse_gui;
import java.util.ArrayList;
import java.util.Map;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

// The interface that is used to handle responses from the JSON thing
public abstract class ResponseHandler {
	
	private SyntaxAnalyzer syntaxAnalyzer;
	private SharedCentralisedClass sharecClass;
	private Boolean bypassIfError;
	public ResponseHandler(SharedCentralisedClass sharecClass, Boolean bypassIfError){
		syntaxAnalyzer = new SyntaxAnalyzer(sharecClass);
		this.sharecClass = sharecClass;
		this.bypassIfError = bypassIfError;
	}
	
	public void handleResponse(JSON json){
		ServerResponse serverResponse = syntaxAnalyzer.jsonToServerResponse(json);
		baseHandleResponse(serverResponse);
		
		if(bypassIfError)
			return;
		
		Map<String, Object> map = serverResponse.getValue();
		for(String item : map.keySet()){
			if(map.get(item) instanceof Map<?,?>){
				Map<String, Object> map2 = (Map<String, Object>) map.get(item);
				if(map2.containsKey(PSEConstants.MESSAGE_KEY) || map2.containsKey(PSEConstants.ERROR_KEY)){
					showOutput(map2);
				}
				else if(map2.containsKey(PSEConstants.EXCEPTION_KEY)){
					showException(map2);
				}
			}
			else{
				if(item.equals(PSEConstants.MESSAGE_KEY) || item.equals(PSEConstants.ERROR_KEY)){
					showOutput(map);
				}
				else if(item.equals(PSEConstants.EXCEPTION_KEY)){
					showException(map);
				}
			}
		}
		
	}
	
	private void showOutput(Map<String, Object> map){
		String key = "";
		AlertType alertType = null;
		String messageType = "";
		if (map.containsKey(PSEConstants.MESSAGE_KEY)){
			key = PSEConstants.MESSAGE_KEY;
			alertType = AlertType.INFORMATION;
			messageType = "Message";
		}
		else if (map.containsKey(PSEConstants.ERROR_KEY)){
			key = PSEConstants.ERROR_KEY;
			alertType = AlertType.ERROR;
			messageType = "Error";
		}
		
		Alert alert = new Alert(alertType);
		alert.setTitle("PowerShell Empire GUI");
		alert.setHeaderText(messageType);
		WebView browser = new WebView();
		WebEngine engine = browser.getEngine();

		ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(browser);
        engine.loadContent((String) map.get(key.toString()));

		GridPane.setVgrow(scrollPane, Priority.ALWAYS);
		GridPane.setHgrow(scrollPane, Priority.ALWAYS);

		GridPane content = new GridPane();
		content.setMaxWidth(Double.MAX_VALUE);
		content.add(scrollPane, 0, 0);
		
		alert.getDialogPane().setContent(content);

		alert.showAndWait();
	}
	
	private void showException(Map<String, Object> map){
		ArrayList<Map<String, Object>> ex = (ArrayList<Map<String, Object>>) map.get("Exception");
		this.sharecClass.showStackTraceInAlertWindow((String)ex.get(0).get("Message"), (String)ex.get(0).get("Trace"));
	}
	
	protected abstract void baseHandleResponse(ServerResponse serverResponse);
}
