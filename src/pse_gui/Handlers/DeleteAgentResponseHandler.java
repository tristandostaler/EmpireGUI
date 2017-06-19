package pse_gui.Handlers;

import pse_gui.Models.ServerResponse;

/**
 * Created by r1p on 6/17/17.
 */
public class DeleteAgentResponseHandler extends ResponseHandler {

    RequestHandler handler;
    public DeleteAgentResponseHandler(RequestHandler handler) {
        super(true); //boolean: don't display error window if there was an error
        this.handler = handler;
    }
    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        handler.getAgents();
    }

}
