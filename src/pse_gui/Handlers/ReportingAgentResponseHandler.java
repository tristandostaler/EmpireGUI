package pse_gui.Handlers;

import pse_gui.Interfaces.IReportingAgentView;
import pse_gui.Models.ServerResponse;

import java.util.HashMap;

/**
 * Created by r1p on 6/17/17.
 */
public class ReportingAgentResponseHandler extends ResponseHandler {

    String actualSelectedItemAgentOrListenerToHandle;
    HashMap<String, ServerResponse> agentsReportingMap;
    IReportingAgentView reportingAgentView;
    public ReportingAgentResponseHandler(HashMap<String, ServerResponse> agentsReportingMap, IReportingAgentView reportingAgentView, String actualSelectedItemAgentOrListenerToHandle) {
        super(false); //boolean: don't display error window if there was an error
        this.actualSelectedItemAgentOrListenerToHandle = actualSelectedItemAgentOrListenerToHandle;
        this.reportingAgentView = reportingAgentView;
    }
    @Override
    public void baseHandleResponse(ServerResponse serverResponse) {
        agentsReportingMap.put(actualSelectedItemAgentOrListenerToHandle, serverResponse);
        reportingAgentView.notifyReportingAgentView(actualSelectedItemAgentOrListenerToHandle);
    }

}
