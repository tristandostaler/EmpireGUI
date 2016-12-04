package pse_gui;
import javafx.event.Event;

@SuppressWarnings({ "restriction", "serial" })
public class ConnectEvent extends Event {
	
	public ConnectEvent() {
		super(LoginView.CONNECT);
	}
	
}