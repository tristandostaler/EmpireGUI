package pse_gui;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import com.jcraft.jsch.JSchException;


public class RequestHandler {
	private Model model;
	private MainView ui;
	private SyntaxAnalyzer syntaxAnalyzer;
	
	public RequestHandler(){
		Initialise();
	}
	
	public void connect(PowershellEmpireInformations infos) {
		try {
			this.model.setPowershellEmpireConnection(new PowershellEmpireConnection(infos));
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public void connect(PowershellEmpireInformations infos, SSHInformations sshInfos) {
		try {
			this.model.setPowershellEmpireConnection(new PowershellEmpireConnection(sshInfos, infos));
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (JSchException e) {
			SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
		}
	}
	
	public void disconnect() {
		this.model.getPowershellEmpireConnection().disconnect();
	}

	private void Initialise(){
		this.syntaxAnalyzer = new SyntaxAnalyzer();
	}
	
	public void setModel(Model model) {
		this.model = model;
	}
	
	public void setMainController(MainView controller) {
		this.ui = controller;
	}

	// The delegate for a module response
	private class ModuleResponseHandler extends ResponseHandler{
		
		public ModuleResponseHandler(Boolean bypassIfError){
			super(bypassIfError);
		}

		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			model.setModuleList(serverResponse);
			ui.notifyModulesUpdated();
		}

	}
	// The delegate for an agent response
	private class AgentResponseHandler extends ResponseHandler{
		
		public AgentResponseHandler(Boolean bypassIfError){
			super(bypassIfError);
		}

		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			model.setAgentList(serverResponse);
			ui.notifyAgentsUpdated();
		}

	}
	// The delegate for a stager response
	private class StagerResponseHandler extends ResponseHandler{

		public StagerResponseHandler(Boolean bypassIfError){
			super(bypassIfError);
		}
		
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			model.setStagerList(serverResponse);
			ui.notifyStagersUpdated();
		}

	}
	
	private class ListenerResponseHandler extends ResponseHandler{

		public ListenerResponseHandler(Boolean bypassIfError){
			super(bypassIfError);
		}
		
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			model.setListenerList(serverResponse);
			ui.notifyListenersUpdated();
		}

	}
	
	private class ListenerOptionsResponseHandler extends ResponseHandler{

		public ListenerOptionsResponseHandler(Boolean bypassIfError){
			super(bypassIfError);
		}
		
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			model.setListenerOptionsList(serverResponse);
			ui.notifyListenerOptionsUpdated();
		}

	}
	
	public void getModules() {
		if(model.getPowershellEmpireConnection() != null) {
			Communication comm = new Communication(Communication.METHODS.GET, "modules", new ModuleResponseHandler(true), null);
			model.getPowershellEmpireConnection().send(comm);
		}
	}
	
	public void getAgents() {
		if(model.getPowershellEmpireConnection() != null) {
			Communication comm = new Communication(Communication.METHODS.GET, "agents", new AgentResponseHandler(true), null);
			model.getPowershellEmpireConnection().send(comm);
		}
	}

	public void getStagers() {
		if(model.getPowershellEmpireConnection() != null) {
			Communication comm = new Communication(Communication.METHODS.GET, "stagers", new StagerResponseHandler(false), null);
			model.getPowershellEmpireConnection().send(comm);
		}
	}
	
	public void getListeners() {
		if(model.getPowershellEmpireConnection() != null) {
			Communication comm = new Communication(Communication.METHODS.GET, "listeners", new ListenerResponseHandler(true), null);
			model.getPowershellEmpireConnection().send(comm);
		}
	}
	
	public void getListenerOptions() {
		if(model.getPowershellEmpireConnection() != null) {
			Communication comm = new Communication(Communication.METHODS.GET, "listeners/options", new ListenerOptionsResponseHandler(false), null);
			model.getPowershellEmpireConnection().send(comm);
		}
	}

	public void makeUserRequest(ResponseHandler handler) {
		JSON json = syntaxAnalyzer.userRequestToJson(this.model.getUserRequest());
		Communication comm = new Communication(this.model.getUserRequest().getMethod(), this.model.getUserRequest().getEndpoint(), handler, json);
		this.model.getPowershellEmpireConnection().send(comm);
	}
}
