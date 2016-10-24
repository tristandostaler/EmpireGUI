package pse_gui;

public enum ItemType {
	MODULE,
	AGENT,
	STAGER, 
	LISTENER,
	CREATE_LISTENER,
	REPORTING;
	

    public String getStringValue() {
    	switch (this) {
	      case MODULE:
	          return PSEConstants.MODULE_LIST_ENDPOINT;
	      case AGENT:
	          return PSEConstants.AGENT_LIST_ENDPOINT;
	      case STAGER:
	    	  return PSEConstants.STAGER_LIST_ENDPOINT;
	      case LISTENER:
	    	  return PSEConstants.LISTENER_LIST_ENDPOINT;
	      default:
	          throw new RuntimeException("Unknown value in ItemType.");
    	}
    }
}
