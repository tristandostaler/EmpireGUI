package pse_gui.Utils;
import javafx.scene.control.Alert;
import pse_gui.Views.MainView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Map;

public class SharedCentralisedClass {

	private static SharedCentralisedClass instance;
	
	private MainView mainView;
	private ArrayList<OutputLogAreaObject> queuedOutputLogAreaList = new ArrayList<OutputLogAreaObject>();
	private ArrayList<StackTraceQueueObject> queuedStackTraceList = new ArrayList<StackTraceQueueObject>();
	private ArrayList<AlertQueueObject> queuedAlertList = new ArrayList<AlertQueueObject>();
	
	private SharedCentralisedClass(){
	}
	
	public static SharedCentralisedClass getInstance(){
		if(instance == null)
			instance = new SharedCentralisedClass();
		return instance;
	}
	
	public void setMainView(MainView mainView){
		this.mainView = mainView;
		synchronized(queuedOutputLogAreaList) {
			for(OutputLogAreaObject olao : queuedOutputLogAreaList){
				this.mainView.writeTextToLogArea(olao.getMessage() + "\n", olao.getVerboseLevel1to3());
			}
			queuedOutputLogAreaList.clear();
		}
		synchronized(queuedStackTraceList) {
			for(StackTraceQueueObject sto : queuedStackTraceList){
				this.mainView.showStackTraceInAlertWindow(sto.getMessage(), sto.getTrace(), sto.getVerboseLevel1to3());
			}
			queuedStackTraceList.clear();
		}
		synchronized(queuedAlertList) {
			for(AlertQueueObject ao : queuedAlertList){
				this.mainView.showAlertWindow(ao.getAlertType(), ao.getMessageType(), ao.getMap(), ao.getKey(), ao.getVerboseLevel1to3());
			}
			queuedAlertList.clear();
		}
	}
	
	public void writeTextToLogArea(String text, int verboseLevel1to3){
		if(this.mainView != null)
			this.mainView.writeTextToLogArea(text, verboseLevel1to3);
		else
			synchronized(queuedOutputLogAreaList) {
				System.out.println(text);
				queuedOutputLogAreaList.add(new OutputLogAreaObject(text, verboseLevel1to3));
			}
	}
	
	public void showStackTraceInAlertWindow(String message, String trace, int verboseLevel1to3){
		System.out.println("Exception: " + message + "\nTrace: " + trace);
		if(this.mainView != null)
			this.mainView.showStackTraceInAlertWindow(message, trace, verboseLevel1to3);
		else {
			synchronized(queuedStackTraceList) {
				queuedStackTraceList.add(new StackTraceQueueObject(message, trace, verboseLevel1to3));
			}
		}
			
	}
	
	public void showStackTraceInAlertWindow(String message, Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		e.printStackTrace();
		showStackTraceInAlertWindow(message, sw.toString(), 3);
	}

	public void showAlertWindow(Alert.AlertType alertType, String messageType, Map<String, Object> map, String key, int verboseLevel1to3){
		System.out.println("Error: " + (String) map.get(key.toString()));
		if(this.mainView != null)
			this.mainView.showAlertWindow(alertType, messageType, map, key, verboseLevel1to3);
		else {
			synchronized(queuedAlertList) {
				queuedAlertList.add(new AlertQueueObject(alertType, messageType, map, key, verboseLevel1to3));
			}
		}
	}

	private class OutputLogAreaObject{
		private String message;
		private int verboseLevel1to3;
		public OutputLogAreaObject(String message, int verboseLevel1to3){
			this.message = message;
			this.verboseLevel1to3 = verboseLevel1to3;
		}

		public String getMessage() {
			return message;
		}

		public int getVerboseLevel1to3() {
			return verboseLevel1to3;
		}
	}

	private class StackTraceQueueObject{
		private String message;
		private String trace;
		private int verboseLevel1to3;
		public StackTraceQueueObject(String message, String trace, int verboseLevel1to3){
			this.message = message;
			this.trace = trace;
			this.verboseLevel1to3 = verboseLevel1to3;
		}
		public String getMessage(){
			return message;
		}

		public String getTrace(){
			return trace;
		}

		public int getVerboseLevel1to3(){
			return verboseLevel1to3;
		}
	}

	private class AlertQueueObject{
		private Alert.AlertType alertType;
		private String messageType;
		private Map<String, Object> map;
		private String key;
		private int verboseLevel1to3;
		public AlertQueueObject(Alert.AlertType alertType, String messageType, Map<String, Object> map, String key, int verboseLevel1to3){
			this.alertType = alertType;
			this.messageType = messageType;
			this.map = map;
			this.key = key;
			this.verboseLevel1to3 = verboseLevel1to3;
		}

		public Alert.AlertType getAlertType() {
			return alertType;
		}

		public String getMessageType() {
			return messageType;
		}

		public Map<String, Object> getMap() {
			return map;
		}

		public String getKey() {
			return key;
		}

		public int getVerboseLevel1to3() {
			return verboseLevel1to3;
		}
	}
}
