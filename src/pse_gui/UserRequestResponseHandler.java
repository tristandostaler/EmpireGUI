package pse_gui;

public class UserRequestResponseHandler extends ResponseHandler{

	public UserRequestResponseHandler(SharedCentralisedClass sharedClass) {
		super(sharedClass, false);
	}
	@Override
	public void baseHandleResponse(ServerResponse serverResponse) {
		String s = serverResponse.getValue().toString();
		System.out.println(s);
	}

}