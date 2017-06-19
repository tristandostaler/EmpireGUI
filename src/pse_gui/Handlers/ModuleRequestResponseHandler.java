package pse_gui.Handlers;

import pse_gui.Models.ServerResponse;
import pse_gui.Utils.SharedCentralisedClass;

public class ModuleRequestResponseHandler extends ResponseHandler {

	public ModuleRequestResponseHandler() {
		super(false);
	}
	@Override
	public void baseHandleResponse(ServerResponse serverResponse) {
		String s = serverResponse.getValue().toString();
		System.out.println(s);
		SharedCentralisedClass.getInstance().writeTextToLogArea(s, 2);
	}

}