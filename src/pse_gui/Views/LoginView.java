package pse_gui.Views;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import pse_gui.Comm.ConnectEvent;
import pse_gui.Models.PowershellEmpireInformations;
import pse_gui.Models.SSHInformations;
import pse_gui.Utils.Settings.SettingsManager;
import pse_gui.Utils.SharedCentralisedClass;

@SuppressWarnings("restriction")
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
	@FXML Label txtPortLbl;
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

	public LoginView() {

	}

	@FXML
	public void initialize() {
		readSettings();
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
			infos.setInfoHasError(true);
			SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("The port number in the Powershell Empire informations is invalid", e);
			//SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("One or more information in the Powershell Empire informations is invalid", e);
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
		saveSettings();
		if(root != null) {
			root.getScene().getWindow().fireEvent(new ConnectEvent());
		}
	}
	
	public void onBtnCancelClick() {
		hide();
	}
	
	public void onBtnLoginClick() {
		boolean cantParseInt = false;
		try {
			Integer.parseInt(port.getText());
		} catch(Exception ex) {
			cantParseInt = true;
		}
		if(cantParseInt)
			port.setStyle("-fx-border-color: red");
		if((!address.isDisabled() && address.getText().trim().equals("")))
			address.setStyle("-fx-border-color: red");

		if(!((!address.isDisabled() && address.getText().trim().equals("")) || port.getText().trim().equals("") || cantParseInt)) {
			port.setStyle("-fx-border-color: white");
			address.setStyle("-fx-border-color: white");
			fireConnect();
		}
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
		address.setDisable(!disabled);

		txtSshUsername.setDisable(disabled);
		txtSshPassword.setDisable(disabled);
		txtSshServerAddress.setDisable(disabled);
		txtSshPort.setDisable(disabled);
		
		sshUsername.setDisable(disabled);
		sshPassword.setDisable(disabled);
		sshAddress.setDisable(disabled);
		sshPort.setDisable(disabled);
	}

	private void readSettings(){
		username.setText(SettingsManager.getInstance().getLoginUsername());
		password.setText(SettingsManager.getInstance().getLoginPassword());
		address.setText(SettingsManager.getInstance().getLoginAddress());
		port.setText(SettingsManager.getInstance().getLoginPort());
		sshUsername.setText(SettingsManager.getInstance().getLoginSshUsername());
		sshPassword.setText(SettingsManager.getInstance().getLoginSshPassword());
		sshPort.setText(SettingsManager.getInstance().getLoginSshPort());
		sshAddress.setText(SettingsManager.getInstance().getLoginSshAddress());
		tokenTxtField.setText(SettingsManager.getInstance().getLoginTokenTxtField());
	}
	
	private void saveSettings(){
		SettingsManager.getInstance().setLoginUsername(username.getText());
		SettingsManager.getInstance().setLoginPassword(password.getText());
		SettingsManager.getInstance().setLoginAddress(address.getText());
		SettingsManager.getInstance().setLoginPort(port.getText());
		SettingsManager.getInstance().setLoginSshUsername(sshUsername.getText());
		SettingsManager.getInstance().setLoginSshPassword(sshPassword.getText());
		SettingsManager.getInstance().setLoginSshPort(sshPort.getText());
		SettingsManager.getInstance().setLoginSshAddress(sshAddress.getText());
		SettingsManager.getInstance().setLoginTokenTxtField(tokenTxtField.getText());
		SettingsManager.getInstance().saveSettingsToFile();
	}

}
