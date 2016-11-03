package pse_gui;

public class PowershellEmpireInformations {
	
	private String userName;
	private String password;
	private String token;
	private boolean isTokenInstead;
	private String host;
	private int port;
	private boolean infoHasError = false;
	
	public PowershellEmpireInformations(){
		setInfoHasError(false);
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isTokenInstead(){
		return isTokenInstead;
	}
	public void setIsTokenInstead(boolean isTokenInstead){
		this.isTokenInstead = isTokenInstead;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	public boolean isInfoHasError() {
		return infoHasError;
	}

	public void setInfoHasError(boolean infoHasError) {
		this.infoHasError = infoHasError;
	}
	
}
