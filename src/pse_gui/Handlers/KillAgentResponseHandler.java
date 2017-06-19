package pse_gui.Handlers;

import pse_gui.Comm.Communication;
import pse_gui.Models.Model;
import pse_gui.Models.ServerResponse;
import pse_gui.Models.UserRequest;
import pse_gui.Utils.Constants.ItemType;
import pse_gui.Utils.SharedCentralisedClass;

/**
 * Created by r1p on 6/17/17.
 */
public class KillAgentResponseHandler extends ResponseHandler {

    RequestHandler handler;
    Model model;
    String actualSelectedItemAgentOrListener;
    public KillAgentResponseHandler(RequestHandler handler, Model model, String actualSelectedItemAgentOrListener) {
        super(false); //boolean: don't display error window if there was an error
        this.handler = handler;
        this.model = model;
        this.actualSelectedItemAgentOrListener = actualSelectedItemAgentOrListener;
    }
    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        try {
            Thread.sleep(3000); //TODO handle exception instead of deleting?
        } catch (InterruptedException e) {
            SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
            e.printStackTrace();
        }
        ItemType type = ItemType.AGENT;
        synchronized(model) {
            model.setUserRequest(new UserRequest(Communication.METHODS.DELETE, null, type, "agents/" + actualSelectedItemAgentOrListener));
            handler.makeUserRequest(new DeleteAgentResponseHandler(handler));
        }
    }

}
