package pse_gui;
import java.io.PrintWriter;
import java.io.StringWriter;

public class SharedCentralisedClass {

	private MainView mainView;
	
	public SharedCentralisedClass(MainView mainView){
		this.mainView = mainView;
	}
	
	public void writeTextToLogArea(String text){
		if(this.mainView != null)
			this.mainView.writeTextToLogArea(text);
		else
			System.out.println(text);
	}
	
	public void showStackTraceInAlertWindow(String message, String trace){
		if(this.mainView != null)
			this.mainView.showStackTraceInAlertWindow(message, trace);
		else
			System.out.println("Exception: " + message + "\nTrace: " + trace);
	}
	
	public void showStackTraceInAlertWindow(String message, Exception e){
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		showStackTraceInAlertWindow(message, sw.toString());
	}
}
