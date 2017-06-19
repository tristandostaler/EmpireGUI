package pse_gui.Handlers;

import javafx.application.Platform;
import javafx.scene.control.TextArea;
import pse_gui.Models.Model;
import pse_gui.Models.ServerResponse;
import pse_gui.Utils.SharedCentralisedClass;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by r1p on 6/17/17.
 */
public class SHellResultAgentResponseHandler extends ResponseHandler {
    RequestHandler handler;
    Model model;
    String shellCommand;
    TextArea shellTextArea;

    public SHellResultAgentResponseHandler(RequestHandler handler, Model model, TextArea shellTextArea, String shellCommand) {
        super(false); //boolean: don't display error window if there was an error
        this.handler = handler;
        this.model = model;
        this.shellTextArea = shellTextArea;
        this.shellCommand = shellCommand;
    }

    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        if(serverResponse.getValue().get("results") != null){
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try{
                        ArrayList<Map<String, Object>> results = (ArrayList<Map<String, Object>>)serverResponse.getValue().get("results");
                        Map<String, Object> result = results.get(results.size() - 1);
                        shellTextArea.appendText((String)result.get("agentname") + "> " + shellCommand + "\r\n"
                                + ((String)result.get("results")).replace(",", "\r\n") + "\r\n\r\n");
                    }catch (Exception ex){
                        SharedCentralisedClass.getInstance().showStackTraceInAlertWindow("An exception occured in SHellResultAgentResponseHandler", ex);
                    }
                }
            });
        }
    }

}
