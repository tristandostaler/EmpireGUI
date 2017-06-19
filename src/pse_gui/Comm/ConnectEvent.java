package pse_gui.Comm;
import javafx.event.Event;
import pse_gui.Views.LoginView;

@SuppressWarnings({ "restriction", "serial" })
public class ConnectEvent extends Event {
	
	public ConnectEvent() {
		super(LoginView.CONNECT);
	}
	
}