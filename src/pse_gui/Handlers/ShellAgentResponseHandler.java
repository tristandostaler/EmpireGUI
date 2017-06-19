package pse_gui.Handlers;

import javafx.scene.control.TextArea;
import pse_gui.Comm.Communication;
import pse_gui.Models.Model;
import pse_gui.Models.ServerResponse;
import pse_gui.Models.UserRequest;
import pse_gui.Utils.Constants.ItemType;

/**
 * Created by r1p on 6/17/17.
 */
public class ShellAgentResponseHandler extends ResponseHandler {
    RequestHandler handler;
    Model model;
    String agentName;
    TextArea shellTextArea;
    String shellCommand;
    public ShellAgentResponseHandler(RequestHandler handler, Model model, String agentName, TextArea shellTextArea, String shellCommand) {
        super(false); //boolean: don't display error window if there was an error
        this.handler = handler;
        this.model = model;
        this.agentName = agentName;
        this.shellTextArea = shellTextArea;
        this.shellCommand = shellCommand;
    }
    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        if(serverResponse.getValue().get("success") != null && ((Boolean)serverResponse.getValue().get("success"))){
            synchronized(model) {
                model.setUserRequest(new UserRequest(Communication.METHODS.GET, null, ItemType.SHELL, "agents/" + agentName + "/results"));
                handler.makeUserRequest(new SHellResultAgentResponseHandler(handler, model, shellTextArea, shellCommand));
            }
        }
    }

}
