package pse_gui;

public class Communication {

	public enum METHODS{
		GET,
		POST,
		DELETE
	}
	
	private METHODS method;
	private String endPoint;
	private ResponseHandler responseHandlerCallback;
	private JSON json;
	
	public Communication(METHODS method, String endPoint, ResponseHandler responseHandlerCallback, JSON json){
		this.method = method;
		this.endPoint = endPoint;
		this.endPoint = this.endPoint.replace("\\", "/");
		if(this.endPoint.toCharArray()[0] == '/')
			this.endPoint = this.endPoint.substring(1);
		if(this.endPoint.startsWith("api/"))
			this.endPoint = this.endPoint.replace("api/", "");
		this.responseHandlerCallback = responseHandlerCallback;
		this.json = json;
	}
	
	public METHODS getMethod(){
		return this.method;
	}
	
	public String getEndPoint(){
		return this.endPoint;
	}
	
	public ResponseHandler getCallBack(){
		return this.responseHandlerCallback;
	}
	
	public JSON getJson(){
		return this.json;
	}
}
