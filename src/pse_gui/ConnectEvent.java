package pse_gui;
import javafx.event.Event;
import javafx.event.EventType;

public class ConnectEvent extends Event {

	private SharedCentralisedClass sharedClass;
	
	public ConnectEvent(SharedCentralisedClass sharedClass) {
		super(LoginView.CONNECT);
		this.sharedClass = sharedClass;
		// TODO Auto-generated constructor stub
	}
	
}