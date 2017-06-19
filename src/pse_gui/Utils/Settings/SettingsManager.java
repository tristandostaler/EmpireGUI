package pse_gui.Utils.Settings;

import pse_gui.Utils.SharedCentralisedClass;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by r1p on 6/19/17.
 */
public class SettingsManager {
    private static SettingsManager instance;

    private final String CONNECTION_INFORMATION_FILES = System.getProperty("user.home") + "/PSEGUISettings.psegui";
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

    private String loginUsername = "";
    private String loginPassword = "";
    private String loginAddress = "";
    private String loginPort = "";
    private String loginSshUsername = "";
    private String loginSshPassword = "";
    private String loginSshPort = "";
    private String loginSshAddress = "";
    private String loginTokenTxtField = "";

    private SettingsManager() {
        loadSettingsFromFile();
    }

    public static SettingsManager getInstance(){
        if(instance == null){
            instance = new SettingsManager();
        }
        return instance;
    }

    public void loadSettingsFromFile(){
        try{
            if (Files.exists(Paths.get(CONNECTION_INFORMATION_FILES))){
                for (String line : Files.readAllLines(Paths.get(CONNECTION_INFORMATION_FILES))) {
                    String[] keyValue = line.split(SEPARATOR, 2);
                    if (keyValue[0].equals(POWERSHELLEMPIRE_USERNAME_KEY))
                        loginUsername = keyValue[1];
                    else if (keyValue[0].equals(POWERSHELLEMPIRE_PASSWORD_KEY))
                        loginPassword = keyValue[1];
                    else if (keyValue[0].equals(POWERSHELLEPMIRE_ADDRESS_KEY))
                        loginAddress = keyValue[1];
                    else if (keyValue[0].equals(POWERSHELLEMPIRE_PORT_KEY))
                        loginPort = keyValue[1];
                    else if (keyValue[0].equals(SSH_USERNAME_KEY))
                        loginSshUsername = keyValue[1];
                    else if (keyValue[0].equals(SSH_PASSWORD_KEY))
                        loginSshPassword = keyValue[1];
                    else if (keyValue[0].equals(SSH_PORT_KEY))
                        loginSshPort = keyValue[1];
                    else if (keyValue[0].equals(SERVER_ADDRESS_KEY))
                        loginSshAddress = keyValue[1];
                    else if (keyValue[0].equals(TOKEN))
                        loginTokenTxtField = keyValue[1];
                }
            }
        }
        catch(Exception e){
            SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("An error occurred while loading the settings file.", e);
            e.printStackTrace();
        }
    }

    public void saveSettingsToFile(){
        try {
            FileOutputStream steam = new FileOutputStream(new File(CONNECTION_INFORMATION_FILES), false);
            String text = "";
            text += POWERSHELLEMPIRE_USERNAME_KEY + SEPARATOR + loginUsername + NEW_LINE;
            text += POWERSHELLEMPIRE_PASSWORD_KEY + SEPARATOR + loginPassword + NEW_LINE;
            text += POWERSHELLEPMIRE_ADDRESS_KEY + SEPARATOR + loginAddress + NEW_LINE;
            text += POWERSHELLEMPIRE_PORT_KEY + SEPARATOR + loginPort + NEW_LINE;
            text += SSH_USERNAME_KEY + SEPARATOR + loginSshUsername + NEW_LINE;
            text += SSH_PASSWORD_KEY + SEPARATOR + loginSshPassword + NEW_LINE;
            text += SSH_PORT_KEY + SEPARATOR + loginSshPort + NEW_LINE;
            text += SERVER_ADDRESS_KEY + SEPARATOR + loginSshAddress + NEW_LINE;
            text += TOKEN + SEPARATOR + loginTokenTxtField + NEW_LINE;
            byte[] myBytes = text.getBytes();
            steam.write(myBytes);
            steam.close();
        } catch (IOException e) {
            SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("An error occurred while saving the settings file.", e);
            e.printStackTrace();
        }
    }

    public String getLoginUsername() {
        return loginUsername;
    }

    public void setLoginUsername(String loginUsername) {
        this.loginUsername = loginUsername;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getLoginAddress() {
        return loginAddress;
    }

    public void setLoginAddress(String loginAddress) {
        this.loginAddress = loginAddress;
    }

    public String getLoginPort() {
        return loginPort;
    }

    public void setLoginPort(String loginPort) {
        this.loginPort = loginPort;
    }

    public String getLoginSshUsername() {
        return loginSshUsername;
    }

    public void setLoginSshUsername(String loginSshUsername) {
        this.loginSshUsername = loginSshUsername;
    }

    public String getLoginSshPassword() {
        return loginSshPassword;
    }

    public void setLoginSshPassword(String loginSshPassword) {
        this.loginSshPassword = loginSshPassword;
    }

    public String getLoginSshPort() {
        return loginSshPort;
    }

    public void setLoginSshPort(String loginSshPort) {
        this.loginSshPort = loginSshPort;
    }

    public String getLoginSshAddress() {
        return loginSshAddress;
    }

    public void setLoginSshAddress(String loginSshAddress) {
        this.loginSshAddress = loginSshAddress;
    }

    public String getLoginTokenTxtField() {
        return loginTokenTxtField;
    }

    public void setLoginTokenTxtField(String loginTokenTxtField) {
        this.loginTokenTxtField = loginTokenTxtField;
    }
}
