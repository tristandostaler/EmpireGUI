package pse_gui.Handlers;

import pse_gui.Models.Model;
import pse_gui.Models.ServerResponse;

/**
 * Created by r1p on 6/17/17.
 */
public class ListenerRequestResponseHandler extends ResponseHandler {
    RequestHandler handler;
    Model model;
    public ListenerRequestResponseHandler(RequestHandler handler, Model model) {
        super(false); //boolean: don't display error window if there was an error
        this.handler = handler;
        this.model = model;
    }
    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        String s = serverResponse.getValue().toString();
        System.out.println(s);
        handler.getListeners();
    }
}
