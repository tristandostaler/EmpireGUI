package pse_gui;
public class ModuleRequestResponseHandler extends ResponseHandler{

	private SharedCentralisedClass sharedClass;
	public ModuleRequestResponseHandler(SharedCentralisedClass sharedClass) {
		super(sharedClass, false);
		this.sharedClass = sharedClass;
	}
	@Override
	public void baseHandleResponse(ServerResponse serverResponse) {
		String s = serverResponse.getValue().toString();
		System.out.println(s);
		sharedClass.writeTextToLogArea(s);
	}

}