package pse_gui.Handlers;

import pse_gui.Handlers.ResponseHandler;
import pse_gui.Models.ServerResponse;

public class UserRequestResponseHandler extends ResponseHandler {

	public UserRequestResponseHandler() {
		super(false);
	}
	@Override
	public void baseHandleResponse(ServerResponse serverResponse) {
		String s = serverResponse.getValue().toString();
		System.out.println(s);
	}

}