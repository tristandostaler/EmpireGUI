package pse_gui.Comm;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Properties;

import javax.net.ssl.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import pse_gui.Models.JSON;
import pse_gui.Models.PowershellEmpireInformations;
import pse_gui.Models.SSHInformations;
import pse_gui.Utils.SharedCentralisedClass;

public class PowershellEmpireConnection {

	protected SSHInformations sshInfo;
	protected PowershellEmpireInformations pseInfo;
	
	protected String token;
	
	private SSHConnectionHandler sshConn;
	
	private Boolean isSSH = false;
	
	private final int INVALID_TOKEN_ERROR_CODE = 403;
	private final int INVALID_PSE_CREDS_ERROR_CODE = 401;
	
	public PowershellEmpireConnection(SSHInformations sshInfo, PowershellEmpireInformations pseInfo) throws KeyManagementException, NoSuchAlgorithmException, JSchException {
		this.isSSH = true;
		this.sshInfo = sshInfo;
		this.pseInfo = pseInfo;
		this.sshConn = new SSHConnectionHandler(this);
		this.sshConn.run();
		
		try {
			int counter = 30;
			while(!this.sshConn.isConnected()) { //TODO handle when can never connect?
				Thread.sleep(1000);
				System.out.print("Sleeping in PowershellEmpireConnection.ctor for 1 sec.");
			}
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		makeConnection();
	}
	
	public PowershellEmpireConnection(PowershellEmpireInformations pseInfo) throws KeyManagementException, NoSuchAlgorithmException{
		this.pseInfo = pseInfo;
		makeConnection();
	}
	
	public Boolean isConnected(){
		return token == null ? false : token.equals("None") ? false : true;
	}
	
	public Boolean isSSHConnected(){
		return this.sshConn != null && this.sshConn.isConnected();
	}
	
	public void disconnect() {
		if(this.sshConn != null ) {
			if(this.sshConn.isConnected()) { 
				this.sshConn.stop();
			}
		}
		token = null;
	}
	
	public ChannelSftp getSFTPChannel(){
		return this.sshConn.getSFTPChannel();
	}
	
	private void makeConnection() throws NoSuchAlgorithmException, KeyManagementException{
		if(isSSH && !isSSHConnected())
			return;
		SSLContext ssl = SSLContext.getInstance("SSL");
	    ssl.init(null, new TrustManager[]{new SimpleX509TrustManager()}, new java.security.SecureRandom());
	    SSLSocketFactory factory = ssl.getSocketFactory();
	    
	    HttpsURLConnection.setDefaultSSLSocketFactory(factory);
	    
	 // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
			@Override
			public boolean verify(String arg0, SSLSession arg1) {
				return true;
			}
        };
	    
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        
       refreshToken();
	}
	
	private void refreshToken(){
		if(this.pseInfo.isTokenInstead())
			this.token = this.pseInfo.getToken();
		else
		{
			token = null;
			String tokenR = "";
			tokenR = sendRequest(Communication.METHODS.POST, "admin/login", "{\"username\":\"" + this.pseInfo.getUserName() + "\", \"password\":\"" + this.pseInfo.getPassword() + "\"}");
			System.out.println(tokenR);
			JSONObject tokenO = new JSONObject(tokenR);
			try {
				this.token = tokenO.getString("token");
			} catch (JSONException e) {
				token = "None";
			}
			try{
				JSONArray exception = tokenO.getJSONArray("Exception");
				String exceptionMessage = exception.getJSONObject(0).getString("Message");
				SharedCentralisedClass.getInstance().writeTextToLogArea(exceptionMessage, 3);
				SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(exceptionMessage, exception.getJSONObject(0).getString("Trace"), 3);
			} catch (JSONException e){
				return;
			}
		}
	}
	
	public void send(Communication comm){
		new RequestRunner(comm, this).run();
	}
	
	protected String sendRequest(Communication.METHODS method, String endPoint, String postData) {
		try {
			if(isSSH && !isSSHConnected())
				throw new Exception("Not connected to ssh server");
			
			URL url;
		
			if(this.isSSH){
				if(token != null && !token.equals("None"))
					url = new URL("https://127.0.0.1:8080/api/" + endPoint + "?token=" + token);
				else
					url = new URL("https://127.0.0.1:8080/api/" + endPoint);
			}
			else {
				if(token != null && !token.equals("None"))
					url = new URL("https://" + this.pseInfo.getHost() + ":" + this.pseInfo.getPort() + "/api/" + endPoint + "?token=" + token);
				else
					url = new URL("https://" + this.pseInfo.getHost() + ":" + this.pseInfo.getPort() + "/api/" + endPoint);
			}
			
			System.out.println(url.toString());
			SharedCentralisedClass.getInstance().writeTextToLogArea(url.toString(), 2);
			
		    HttpsURLConnection uc = (HttpsURLConnection)url.openConnection();
		    
		    if(method == Communication.METHODS.GET)
		    	uc.setRequestMethod("GET");
		    else if(method == Communication.METHODS.POST)
		    	uc.setRequestMethod("POST");
		    else if(method == Communication.METHODS.DELETE)
		    	uc.setRequestMethod("DELETE");
		    else
		    	uc.setRequestMethod("GET");
		    
		    System.out.println(uc.getRequestMethod());
		    SharedCentralisedClass.getInstance().writeTextToLogArea(uc.getRequestMethod(), 2);
		    
		    if(method == Communication.METHODS.POST){
		    	uc.setRequestProperty("Content-Type", "application/json");
		    	uc.setDoOutput(true);
		    	DataOutputStream wr = new DataOutputStream(uc.getOutputStream());
		    	wr.writeBytes(postData);
				wr.flush();
				wr.close();

			    System.out.println(postData);
			    SharedCentralisedClass.getInstance().writeTextToLogArea(postData, 2);
		    }
		    
		    
		 // Hack to force HttpURLConnection to run the request
		    // Otherwise getErrorStream always returns null
		    int responseCode = uc.getResponseCode();
		    if(responseCode == INVALID_TOKEN_ERROR_CODE){
		    	//The token changed. Refreshing
		    	if(token == null || token.equals("None"))
		    		throw new Exception("Invalid PSE connection informations");
		    	refreshToken();
		    	return sendRequest(method, endPoint, postData);
		    }
		    else if(responseCode == INVALID_PSE_CREDS_ERROR_CODE){
		    	throw new Exception("Invalid PSE connection informations");
		    }
		    else {
		    	InputStream stream = uc.getErrorStream();
			    if (stream == null) {
			        stream = uc.getInputStream();
			    }
			    
			    InputStreamReader inputStreamReader = new InputStreamReader(stream);
				String line = convertStreamToString(inputStreamReader);
				
				System.out.println("Raw return: " + line);
				SharedCentralisedClass.getInstance().writeTextToLogArea("Raw return: " + line, 2);
				
				return line;
		    }

		}
		catch (SocketException | SSLHandshakeException e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			//e.printStackTrace();
			//e.printStackTrace();
			String st = sw.toString().replace("\n", "\\n"); //Replace so JSON can handle it!
			//SharedCentralisedClass.getInstance().writeTextToLogArea(st);
			//SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
			//System.out.println(st);
			return "{ \"Exception\": [{ \"Message\": \"Bad Empire Address or Port? --> Exception in sendRequest: " + e.getMessage().replace("\n", "\\n") + "\", \"Trace\": \"" + st + "\" }] }";
		}
		catch (Exception e) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			//e.printStackTrace();
			//e.printStackTrace();
			String st = sw.toString().replace("\n", "\\n"); //Replace so JSON can handle it!
			//SharedCentralisedClass.getInstance().writeTextToLogArea(st);
			//SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
			//System.out.println(st);
			return "{ \"Exception\": [{ \"Message\": \"Exception in sendRequest: " + e.getMessage().replace("\n", "\\n") + "\", \"Trace\": \"" + st + "\" }] }";
		}
	}
	
	private String convertStreamToString(InputStreamReader is) throws IOException {
		BufferedReader b = new BufferedReader(is);
		String line = "";
		String finalLine = "";
		while((line = b.readLine()) != null){
			finalLine += line.toString();
		}
		b.close();
		return finalLine.replace("\\r\\n", ",");
	}
}


class SimpleX509TrustManager implements X509TrustManager {
    public void checkClientTrusted(
            X509Certificate[] cert, String s)
            throws CertificateException {
    }

    public void checkServerTrusted(
            X509Certificate[] cert, String s)
            throws CertificateException {
      }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        // TODO Auto-generated method stub
        return null;
    }

}

class RequestRunner extends Thread {
	private Communication comm;
	private PowershellEmpireConnection pseConn;
	
	public RequestRunner(Communication comm, PowershellEmpireConnection pseConn){
		this.comm = comm;
		this.pseConn = pseConn;
	}
	
    public void run() {
        String returnedString = this.pseConn.sendRequest(this.comm.getMethod(), this.comm.getEndPoint(), this.comm.getJson() != null ? this.comm.getJson().getJSONFormatedData() : "");
        comm.getCallBack().handleResponse(new JSON(returnedString));
    }
}

class SSHConnectionHandler {
	
	private PowershellEmpireConnection pseConn;
	private Integer LOCAL_SERVICE_PORT = 8080;
	private final Integer MAXIMUM_TIMEOUT = 3600; //30000;
	private Session session;
	private ChannelSftp sftpChannel;
	
	public SSHConnectionHandler(PowershellEmpireConnection pseConn){
		this.pseConn = pseConn;
	}
	
	public Boolean isConnected(){
		return session == null ? false : session.isConnected();
	}
	
	public ChannelSftp getSFTPChannel(){
		return sftpChannel;
	}
	
	public void run() throws JSchException {
		JSch jsch=new JSch();
		try {
			session = jsch.getSession(this.pseConn.sshInfo.getUserName(), this.pseConn.sshInfo.getHost(), this.pseConn.sshInfo.getPort());
			session.setPassword(this.pseConn.sshInfo.getPassword());
			
			Properties config = new Properties(); 
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect(MAXIMUM_TIMEOUT);
			session.setPortForwardingL(LOCAL_SERVICE_PORT, "localhost", this.pseConn.pseInfo.getPort());
			
			Channel channel = session.openChannel("sftp");
            channel.connect();
            sftpChannel = (ChannelSftp) channel;
			
            //sftpChannel.get("remotefile.txt", "localfile.txt");
            
			//http://stackoverflow.com/questions/2405885/any-good-jsch-examples/37067557#37067557
			//http://stackoverflow.com/questions/17473398/java-sftp-upload-using-jsch-but-how-to-overwrite-the-current-file
            //http://stackoverflow.com/questions/14617/java-what-is-the-best-way-to-sftp-a-file-from-a-server
		} catch (JSchException e) {
			//e.printStackTrace();
			throw e;
		}
	}
	
	public void stop(){
		sftpChannel.exit();
		this.session.disconnect();
	}
}
