package pse_gui;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;

public class SharedCentralisedClass {

	private static SharedCentralisedClass instance;
	
	private MainView mainView;
	private String queuedOutputLogArea = "";
	private ArrayList<StackTraceQueueObject> queuedStackTraceList = new ArrayList<StackTraceQueueObject>();
	
	private SharedCentralisedClass(){
	}
	
	public static SharedCentralisedClass getInstance(){
		if(instance == null)
			instance = new SharedCentralisedClass();
		return instance;
	}
	
	public void setMainView(MainView mainView){
		this.mainView = mainView;
		synchronized(queuedOutputLogArea) {
			this.mainView.writeTextToLogArea(queuedOutputLogArea);
			queuedOutputLogArea = "";
		}
		synchronized(queuedStackTraceList) {
			for(StackTraceQueueObject sto : queuedStackTraceList){
				this.mainView.showStackTraceInAlertWindow(sto.getMessage(), sto.getTrace());
			}
			queuedStackTraceList.clear();
		}
	}
	
	public void writeTextToLogArea(String text){
		if(this.mainView != null)
			this.mainView.writeTextToLogArea(text);
		else
			synchronized(queuedOutputLogArea) {
				System.out.println(text);
				queuedOutputLogArea += text + "\n";
			}
	}
	
	public void showStackTraceInAlertWindow(String message, String trace){
		if(this.mainView != null)
			this.mainView.showStackTraceInAlertWindow(message, trace);
		else {
			System.out.println("Exception: " + message + "\nTrace: " + trace);
			synchronized(queuedStackTraceList) {
				queuedStackTraceList.add(new StackTraceQueueObject(message, trace));
			}
		}
			
	}
	
	public void showStackTraceInAlertWindow(String message, Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		showStackTraceInAlertWindow(message, sw.toString());
	}
	
	private class StackTraceQueueObject{
		private String message;
		private String trace;
		public StackTraceQueueObject(String message, String trace){
			this.message = message;
			this.trace = trace;
		}
		public String getMessage(){
			return message;
		}
		public String getTrace(){
			return trace;
		}
	}
}
