package pse_gui;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LoginView {

	public static EventType<ConnectEvent> CONNECT = new EventType<ConnectEvent>("CONNECT");
	
	private boolean isRemote;
	private boolean isTokenInstead;
	
	@FXML VBox root;
	@FXML TextField username;
	@FXML Label usernameLbl;
	@FXML PasswordField password;
	@FXML Label passwordLbl;
	@FXML TextField address;
	@FXML TextField port;
	@FXML CheckBox checkBox;
	@FXML TextField sshUsername;
	@FXML PasswordField sshPassword;
	@FXML TextField sshAddress;
	@FXML TextField sshPort;
	@FXML Label txtSshUsername;
	@FXML Label txtSshPassword;
	@FXML Label txtSshServerAddress;
	@FXML Label txtSshPort;
	@FXML Button btnCancel;
	@FXML Button btnLogin;
	@FXML CheckBox checkBoxToken;
	@FXML TextField tokenTxtField;
	@FXML Label tokenLbl;
	
	private final String CONNECTION_INFORMATION_FILES = System.getProperty("user.home") + "/PSEConnectionInformations.psegui";
	private final String POWERSHELLEMPIRE_USERNAME_KEY = "POWERSHELLEMPIRE_USERNAME";
	private final String POWERSHELLEMPIRE_PASSWORD_KEY = "POWERSHELLEMPIRE_PASSWORD";
	private final String POWERSHELLEPMIRE_ADDRESS_KEY = "POWERSHELLEPMIRE_ADDRESS";
	private final String POWERSHELLEMPIRE_PORT_KEY = "POWERSHELLEMPIRE_PORT";
	private final String SSH_USERNAME_KEY = "SSH_USERNAME";
	private final String SSH_PASSWORD_KEY = "PASSWORD_SSH";
	private final String SSH_PORT_KEY = "SSH_PORT";
	private final String SERVER_ADDRESS_KEY = "SERVER_ADDRESS";
	private final String TOKEN = "TOKEN";
	private final String SEPARATOR = "=";
	private final String NEW_LINE = System.getProperty("line.separator");
	

	public LoginView() {
		
	}
	
	public boolean isRemote() {
		return isRemote;
	}
	public boolean isTokenInstead() {
		return isTokenInstead;
	}
	
	public PowershellEmpireInformations getInformations() {
		PowershellEmpireInformations infos = new PowershellEmpireInformations();
		try
		{
			infos.setUserName(username.getText());
			infos.setPassword(password.getText());
			infos.setToken(tokenTxtField.getText());
			infos.setIsTokenInstead(isTokenInstead);
			infos.setHost(address.getText());		
			infos.setPort(Integer.parseInt(port.getText()));
		}
		catch (NumberFormatException e) {
			SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("One or more information in the Powershell Empire informations is invalid", e);
		}
		return infos;
	}
	
	public SSHInformations getSSHInformations() {
		SSHInformations infos = new SSHInformations();
		try
		{
			infos.setUserName(sshUsername.getText());
			infos.setPassword(sshPassword.getText());
			infos.setHost(sshAddress.getText());
			infos.setPort(Integer.parseInt(sshPort.getText()));
		}
		catch (NumberFormatException e) {
			SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("One or more information in the SS informations is invalid", e);
		}
		return infos;
	}
	
	public void setIsLoading(boolean isLoading) {
		btnCancel.setDisable(isLoading);
		btnLogin.setDisable(isLoading);
		btnLogin.setText(isLoading ? "Connecting..." : "Login");
		root.getScene().setCursor(isLoading ? Cursor.WAIT : Cursor.DEFAULT);
	}
	
	private void hide() {
		if(root != null) {
			root.getScene().getWindow().hide();
		}
	}
	
	private void fireConnect() {
		writeToFile();
		if(root != null) {
			root.getScene().getWindow().fireEvent(new ConnectEvent());
		}
	}
	
	@FXML
    public void initialize() {
		readFile();
		setRemoteValuesDisabled(true);
		setUseTokenInsteadDisabled(true);
		
		checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				isRemote = arg2;
				setRemoteValuesDisabled(!arg2);
			}
			
		});
		
		checkBoxToken.selectedProperty().addListener(new ChangeListener<Boolean>() {
			
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) {
				isTokenInstead = arg2;
				setUseTokenInsteadDisabled(!arg2);
			}
		});
		
	}
	
	public void onBtnCancelClick() {
		hide();
	}
	
	public void onBtnLoginClick() {
		fireConnect();
	}
	
	private void setUseTokenInsteadDisabled(boolean disabled) {
		tokenTxtField.setDisable(disabled);
		tokenLbl.setDisable(disabled);
		
		usernameLbl.setDisable(!disabled);
		username.setDisable(!disabled);
		passwordLbl.setDisable(!disabled);
		password.setDisable(!disabled);
	}
	
	private void setRemoteValuesDisabled(boolean disabled) {
		txtSshUsername.setDisable(disabled);
		txtSshPassword.setDisable(disabled);
		txtSshServerAddress.setDisable(disabled);
		txtSshPort.setDisable(disabled);
		
		sshUsername.setDisable(disabled);
		sshPassword.setDisable(disabled);
		sshAddress.setDisable(disabled);
		sshPort.setDisable(disabled);
	}
	
	private void readFile(){
		try{
			if (Files.exists(Paths.get(CONNECTION_INFORMATION_FILES))){
				for (String line : Files.readAllLines(Paths.get(CONNECTION_INFORMATION_FILES))) {
				    String[] keyValue = line.split(SEPARATOR, 2);
				    if (keyValue[0].equals(POWERSHELLEMPIRE_USERNAME_KEY))
				    	username.setText(keyValue[1]);
				    else if (keyValue[0].equals(POWERSHELLEMPIRE_PASSWORD_KEY))
				    	password.setText(keyValue[1]);
				    else if (keyValue[0].equals(POWERSHELLEPMIRE_ADDRESS_KEY))
				    	address.setText(keyValue[1]);
				    else if (keyValue[0].equals(POWERSHELLEMPIRE_PORT_KEY))
				    	port.setText(keyValue[1]);
				    else if (keyValue[0].equals(SSH_USERNAME_KEY))
				    	sshUsername.setText(keyValue[1]);
				    else if (keyValue[0].equals(SSH_PASSWORD_KEY))
				    	sshPassword.setText(keyValue[1]);
				    else if (keyValue[0].equals(SSH_PORT_KEY))
				    	sshPort.setText(keyValue[1]);
				    else if (keyValue[0].equals(SERVER_ADDRESS_KEY))
				    	sshAddress.setText(keyValue[1]);
				    else if (keyValue[0].equals(TOKEN))
				    	tokenTxtField.setText(keyValue[1]);
				}
			}
		}
		catch(Exception e){	}
	}
	
	private void writeToFile(){
		try {
			FileOutputStream steam = new FileOutputStream(new File(CONNECTION_INFORMATION_FILES), false);
			String text = "";
			text += POWERSHELLEMPIRE_USERNAME_KEY + SEPARATOR + username.getText() + NEW_LINE;
			text += POWERSHELLEMPIRE_PASSWORD_KEY + SEPARATOR + password.getText() + NEW_LINE;
			text += POWERSHELLEPMIRE_ADDRESS_KEY + SEPARATOR + address.getText() + NEW_LINE;
			text += POWERSHELLEMPIRE_PORT_KEY + SEPARATOR + port.getText() + NEW_LINE;
			text += SSH_USERNAME_KEY + SEPARATOR + sshUsername.getText() + NEW_LINE;
			text += SSH_PASSWORD_KEY + SEPARATOR + sshPassword.getText() + NEW_LINE;
			text += SSH_PORT_KEY + SEPARATOR + sshPort.getText() + NEW_LINE;
			text += SERVER_ADDRESS_KEY + SEPARATOR + sshAddress.getText() + NEW_LINE;
			text += TOKEN + SEPARATOR + tokenTxtField.getText() + NEW_LINE;
			byte[] myBytes = text.getBytes();
			steam.write(myBytes);
			steam.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
