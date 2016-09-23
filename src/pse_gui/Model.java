package pse_gui;
import java.util.ArrayList;
import java.util.Map;

public class Model {
	private ServerResponse stagerList;
	private ServerResponse agentList;
	private ServerResponse listenerList;
	private ServerResponse moduleList;
	private UserRequest userRequest;
	private ServerResponse listenerOptionsList;
	
	private SSHInformations SSHInformations;
	private PowershellEmpireInformations powershellEmpireInformations;
	private PowershellEmpireConnection powershellEmpireConnection;
	
	public Model(){
	}

	public ServerResponse getStagerList() {
		return stagerList;
	}

	public void setStagerList(ServerResponse stagerList) {
		this.stagerList = stagerList;
	}

	public ServerResponse getAgentList() {
		return agentList;
	}

	public void setAgentList(ServerResponse agentList) {
		this.agentList = agentList;
	}

	public ServerResponse getModuleList() {
		return moduleList;
	}

	public void setModuleList(ServerResponse moduleList) {
		this.moduleList = moduleList;
	}

	public UserRequest getUserRequest() {
		return userRequest;
	}

	public void setUserRequest(UserRequest userRequest) {
		this.userRequest = userRequest;
	}

	public SSHInformations getSSHInformations() {
		return SSHInformations;
	}

	public void setSSHInformations(SSHInformations sSHInformations) {
		SSHInformations = sSHInformations;
	}

	public PowershellEmpireInformations getPowershellEmpireInformations() {
		return powershellEmpireInformations;
	}

	public void setPowershellEmpireInformations(PowershellEmpireInformations powershellEmpireInformations) {
		this.powershellEmpireInformations = powershellEmpireInformations;
	}

	public PowershellEmpireConnection getPowershellEmpireConnection() {
		return powershellEmpireConnection;
	}

	public void setPowershellEmpireConnection(PowershellEmpireConnection powershellEmpireConnection) {
		this.powershellEmpireConnection = powershellEmpireConnection;
	}

	public void setListenerList(ServerResponse serverResponse) {
		this.listenerList = serverResponse;
	}
	
	public ServerResponse getListenerList(){
		return this.listenerList;
	}
	
	public void setListenerOptionsList(ServerResponse serverResponse){
		this.listenerOptionsList = serverResponse;
	}
	
	public ServerResponse getListenerOptionsList(){
		return this.listenerOptionsList;
	}
}
