package pse_gui;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.BreadCrumbBar.BreadCrumbActionEvent;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pse_gui.MainView.KillAgentResponseHandler;

public class MainView implements ChangeListener<Object> {

	@FXML BreadCrumbBar<String> breadCrumb;
	@FXML VBox leftContainer;
	@FXML HBox bottomContainer;
	@FXML SplitPane horizontalSplitPane;
	@FXML SplitPane verticalSplitPane;
	@FXML TreeView<String> tree;
	@FXML VBox content;
	@FXML TextArea logTextArea;
	@FXML Button btnConnect;
	@FXML Label username;
	@FXML Button btnDelete;
	@FXML Button btnReset;
	@FXML Button btnSend;
	LoginView loginController;
	private Stage loginStage = null;
	
	MapTreeItem treeRoot;
	MapTreeItem modulesItem;
	MapTreeItem agentsItem;
	MapTreeItem listenersItem;
	MapTreeItem stagersItem;
	
	private final String STAGER_STRING = "stagers";
	private final String AGENT_STRING = "agents";
	private final String LISTENER_STRING = "listeners";
	private final String MODULE_STRING = "modules";

	private UIObjectCreator uiObjectCreator;
	
	private RequestHandler handler;
	private Model model;
	
	private Service<Void> backgroundThread;
	
	private SharedCentralisedClass sharedClass;
	
	private String logTextAreaBuffer = "";
	
	private boolean HasRunLater = false;
	
	private String actualAgent = "";
	
	public MainView() {
		
	}
	
	public void setSharedClass(SharedCentralisedClass sharedClass){
		this.sharedClass = sharedClass;
		uiObjectCreator = new UIObjectCreator(this.sharedClass);
	}
	
	private MapTreeItem addTreeItem(String title, HashMap<String, Object> value, MapTreeItem parent) {
		MapTreeItem item = null;
		MapTreeItem existingItem = getItemOfName(title, parent);
		if(existingItem == null) {
			item = new MapTreeItem(this.sharedClass, title);
			item.setMap(value);
		    parent.getChildren().add(item);
		}
		else
			item = existingItem;

		return item;
	}
	
	@Override
	public void changed(ObservableValue<?> observable, Object oldValue, Object newValue) {
		refreshMainContent((MapTreeItem)newValue);
	}
	
	public void disconnect() {
		if(model.getPowershellEmpireConnection() != null && model.getPowershellEmpireConnection().isConnected()) {
			model.getPowershellEmpireConnection().disconnect();
			resetTreeView();
			resetMainContent();
		}
	}

	private MapTreeItem getItemOfName(String name, MapTreeItem parent) {
		MapTreeItem treeItem = null;
		for(TreeItem<String> item : parent.getChildren()) {
			MapTreeItem mapItem = (MapTreeItem) item;
			if(item.getValue().equals(name)) {
				treeItem = mapItem;
				break;
			}
		}
		return treeItem;
	}
	
	@FXML
    public void initialize() {
		// tree initialization
		tree.getSelectionModel().selectedItemProperty().addListener(this);
		tree.getSelectionModel().select(tree.getRoot());
		
		// Left pane initialization
		horizontalSplitPane.setDividerPositions(0.3);
		leftContainer.setMinWidth(175);
		leftContainer.maxWidthProperty().bind(horizontalSplitPane.widthProperty().multiply(0.3));
		
		// bottom pane initialization
		verticalSplitPane.setDividerPositions(0.6);
		bottomContainer.setMinHeight(100);
		//bottomContainer.maxHeightProperty().bind(verticalSplitPane.heightProperty().multiply(0.3));
		
		breadCrumb.setAutoNavigationEnabled(false);
		breadCrumb.selectedCrumbProperty().bind(tree.getSelectionModel().selectedItemProperty());
		breadCrumb.setOnCrumbAction(new EventHandler<BreadCrumbActionEvent<String>>() {

			@Override
			public void handle(BreadCrumbActionEvent<String> event) {
				TreeItem<String> selectedCrumb = event.getSelectedCrumb();
				if(selectedCrumb == tree.getRoot()) {
					tree.getSelectionModel().select(tree.getRoot());
				}
				else {
					tree.getSelectionModel().select(event.getSelectedCrumb());
				}
			}
			
		});
		btnDelete.setDisable(true);
    }
	
	private void initializeTreeView() {
		if(treeRoot == null) {
			treeRoot = new MapTreeItem(this.sharedClass, "/");
			tree.setShowRoot(false);
			tree.setRoot(treeRoot);
			
			synchronized (treeRoot) {
				stagersItem = new MapTreeItem(this.sharedClass, STAGER_STRING);
				agentsItem = new MapTreeItem(this.sharedClass, AGENT_STRING);
				listenersItem = new MapTreeItem(this.sharedClass, LISTENER_STRING);
				modulesItem = new MapTreeItem(this.sharedClass, MODULE_STRING);
				treeRoot.getChildren().add(stagersItem);
				treeRoot.getChildren().add(agentsItem);
				treeRoot.getChildren().add(listenersItem);
				treeRoot.getChildren().add(modulesItem);
			}
		}	
		else{
			while(stagersItem == null || agentsItem == null || listenersItem == null || modulesItem == null){
				synchronized (treeRoot) {
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	public void notifyAgentsUpdated() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initializeTreeView();
					refreshTreeBranch(agentsItem, model.getAgentList().getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void notifyListenersUpdated() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initializeTreeView();
					refreshTreeBranch(listenersItem, model.getListenerList().getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void notifyModulesUpdated() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initializeTreeView();
					refreshTreeBranch(modulesItem, model.getModuleList().getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}
	
	public void notifyStagersUpdated() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initializeTreeView();
					refreshTreeBranch(stagersItem, model.getStagerList().getValue());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	public void onBtnConnectClick() {
		try {
			if(loginStage == null) {
			    loginStage = new Stage();
				Parent root;
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
				root = fxmlLoader.load();
				
				loginController = fxmlLoader.getController();
				
				loginController.setSharedClass(this.sharedClass);
				
				loginStage.setTitle("PowerShell Empire Login");
				loginStage.initModality(Modality.APPLICATION_MODAL);
				loginStage.setResizable(false);
				
				Scene scene = new Scene(root, 400, 330);
				scene.getStylesheets().add("pse_gui/LoginView.css");
				
				backgroundThread = new Service<Void>() {

					@Override
					protected Task<Void> createTask() {
						return new Task<Void>() {

							@Override
							protected Void call() throws Exception {
								
								try {
									PowershellEmpireInformations infos = loginController.getInformations();
									SSHInformations sshInfos;
									if(loginController.isRemote())
										sshInfos = loginController.getSSHInformations();
									else{
										sshInfos = new SSHInformations(sharedClass);
										sshInfos.setUserName("");
										sshInfos.setPassword("");
										sshInfos.setHost("");
										sshInfos.setPort(22);
									}
									
									model.setPowershellEmpireInformations(infos);
									model.setSSHInformations(sshInfos);
									
									if(loginController.isRemote()) {
										handler.connect(infos, sshInfos);
									}
									else {
										handler.connect(infos);
									}
									
									if(model.getPowershellEmpireConnection() != null && model.getPowershellEmpireConnection().isConnected()) {
										try {
											handler.getStagers();
											handler.getAgents();
											handler.getModules();
											handler.getListeners();
										}
										catch(Exception e) {
											e.printStackTrace();
										}
									}
									else{
										makeDisconnectAction();
									}

									return null;
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
									return null;
								}
							}
						};
					};
					
					@Override
					protected void succeeded() {
						try {
							super.succeeded();
							
							refreshLabels();
							loginController.setIsLoading(false);
							loginStage.hide();
							
							PowershellEmpireInformations infos = model.getPowershellEmpireInformations();
							SSHInformations sshInfos = model.getSSHInformations();
							
							logTextArea.appendText("> Login requested..\n");
							logTextArea.appendText("> user: " + infos.getUserName() + "\n");
							logTextArea.appendText("> host: " + infos.getHost() + "\n");
							logTextArea.appendText("> port: " + infos.getPort() + "\n");
							
							if(loginController.isRemote()) {
								
								logTextArea.appendText("> SSH enabled..");
								logTextArea.appendText("> SSH user: " + sshInfos.getUserName() + "\n");
								logTextArea.appendText("> SSH host: " + sshInfos.getHost() + "\n");
								logTextArea.appendText("> SSH port: " + sshInfos.getPort() + "\n");
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					
					@Override
					protected void failed() {
						try {
							super.failed();
							refreshLabels();
							loginController.setIsLoading(false);
							
							
							Alert alert = new Alert(AlertType.ERROR, "Could not establish a connection.", ButtonType.OK);
							alert.showAndWait();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				};
				
				loginStage.setScene(scene);
				loginStage.addEventHandler(LoginView.CONNECT, new EventHandler<ConnectEvent>() {

					@Override
					public void handle(ConnectEvent e) {
						try {
							loginController.setIsLoading(true);
							backgroundThread.restart();
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
				});
			}
			
			// Disconnect
			if(model.getPowershellEmpireConnection() != null && model.getPowershellEmpireConnection().isConnected()) {
				makeDisconnectAction();
			}
			
			// Connect
			else {
				loginStage.show();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void makeDisconnectAction(){
		model.getPowershellEmpireConnection().disconnect();
		resetTreeView();
		resetMainContent();
		refreshLabels();
	}
	
	public void writeTextToLogArea(String text){
		synchronized(logTextAreaBuffer){
			logTextAreaBuffer += "> " + text + "\n";
		}
		
		if(HasRunLater == false){
			HasRunLater = true;
			Platform.runLater(new Runnable() {
		        @Override
		        public void run() {
		        	if(logTextArea != null)
		    		{
		    			synchronized(logTextAreaBuffer){
		    				if(!logTextAreaBuffer.isEmpty()){
		    					logTextArea.appendText(logTextAreaBuffer);
		    					logTextAreaBuffer = "";
		    				}
		    			}
		    		}
		        	HasRunLater = false;
		        }
		      });
		}
	}
	
	public void showStackTraceInAlertWindow(String message, String trace){
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	Alert alert = new Alert(AlertType.ERROR);
	    		alert.setTitle("Exception!");
	    		alert.setHeaderText("An Exception occured! ");
	    		alert.setContentText(message);
	    		
	    		Label label = new Label("Trace:");

	    		TextArea textArea = new TextArea(trace);
	    		textArea.setEditable(false);
	    		textArea.setWrapText(true);

	    		textArea.setMaxWidth(Double.MAX_VALUE);
	    		textArea.setMaxHeight(Double.MAX_VALUE);
	    		GridPane.setVgrow(textArea, Priority.ALWAYS);
	    		GridPane.setHgrow(textArea, Priority.ALWAYS);

	    		GridPane expContent = new GridPane();
	    		expContent.setMaxWidth(Double.MAX_VALUE);
	    		expContent.add(label, 0, 0);
	    		expContent.add(textArea, 0, 1);

	    		// Set expandable Exception into the dialog pane.
	    		alert.getDialogPane().setExpandableContent(expContent);

	    		alert.showAndWait();
	        }
	      });
	}
	
	public void onBtnSendClick() {
		logTextArea.appendText("> Send command executed..\n" );
		
		if(this.model.getUserRequest() == null)
			handler.makeUserRequest(new UserRequestResponseHandler(this.sharedClass));
		else if(this.model.getUserRequest().getType() == ItemType.STAGER)
			handler.makeUserRequest(new StagerRequestResponseHandler(this.sharedClass));
		else if(this.model.getUserRequest().getType() == ItemType.MODULE)
			handler.makeUserRequest(new ModuleRequestResponseHandler(this.sharedClass));
		else
			handler.makeUserRequest(new UserRequestResponseHandler(this.sharedClass));
	}
	
	public void onBtnResetClick() {
		logTextArea.appendText("> Reset current page command executed..\n" );
	}
	
	public void onBtnDeleteClick(){
		ItemType type = ItemType.AGENT;
		this.model.setUserRequest(new UserRequest(this.sharedClass, Communication.METHODS.GET, null, type, "agents/" + actualAgent + "/kill"));
		handler.makeUserRequest(new KillAgentResponseHandler(this.sharedClass));
	}
	
	public class KillAgentResponseHandler extends ResponseHandler{

		private SharedCentralisedClass sharedClass;
		public KillAgentResponseHandler(SharedCentralisedClass sharedClass) {
			super(sharedClass, false);
			this.sharedClass = sharedClass;
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			ItemType type = ItemType.AGENT;
			model.setUserRequest(new UserRequest(this.sharedClass, Communication.METHODS.DELETE, null, type, "agents/" + actualAgent));
			handler.makeUserRequest(new DeleteAgentResponseHandler(this.sharedClass));
		}

	}
	
	public class DeleteAgentResponseHandler extends ResponseHandler{

		private SharedCentralisedClass sharedClass;
		public DeleteAgentResponseHandler(SharedCentralisedClass sharedClass) {
			super(sharedClass, false);
			this.sharedClass = sharedClass;
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			handler.getAgents();
		}

	}
	
	private void refreshLabels() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	try {
	    			if(model.getPowershellEmpireConnection() != null && model.getPowershellEmpireConnection().isConnected()) {
	    				String name = model.getPowershellEmpireInformations().getUserName();
	    				if(loginController.isRemote()) {
	    					username.setText(name + " (remote)");
	    				}
	    				else {
	    					username.setText(name + " (local)");
	    				}
	    				btnConnect.setText("Disconnect");
	    			}
	    			else {
	    				username.setText("Disconnected");
	    				btnConnect.setText("Connect");
	    			}
	    		} catch (Exception e) {
	    			// TODO Auto-generated catch block
	    			e.printStackTrace();
	    		}
	        }
	      });
	}
	
	private void refreshMainContent(MapTreeItem item) {
		// TODO
		content.getChildren().clear();
		btnReset.setDisable(true);
		btnSend.setDisable(true);
		btnDelete.setDisable(true);
		actualAgent = "";
		
		if(item != null && item.getMap() != null) {
			//Label tempLabel = new Label(item.getMap().toString());
			//tempLabel.setWrapText(true);
			//content.getChildren().add(tempLabel);
			
			MapTreeItem parent = (MapTreeItem)((MapTreeItem)item);
			while(parent.getParent() != null && !parent.getParent().getValue().equals("/")){
				parent = (MapTreeItem) parent.getParent();
			}
			
			if(parent.getValue().equals(LISTENER_STRING)){
				ItemType type = ItemType.LISTENER;
				this.model.setUserRequest(null);
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
			else if(parent.getValue().equals(AGENT_STRING)){
				ItemType type = ItemType.AGENT;
				this.model.setUserRequest(null);
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				actualAgent = item.getValue();
				this.btnDelete.setDisable(false);
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
			else if(parent.getValue().equals(STAGER_STRING)){
				ItemType type = ItemType.STAGER;
				ArrayList<Field> copy = (ArrayList<Field>) item.getFieldList().clone();
				copy.add(new Field(this.sharedClass, "StagerName", "The stager name", ((MapTreeItem)item).getValue(), true));
				this.model.setUserRequest(new UserRequest(this.sharedClass, Communication.METHODS.POST, copy, type));
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
			else if(parent.getValue().equals(MODULE_STRING)){
				ItemType type = ItemType.MODULE;
				this.model.setUserRequest(new UserRequest(this.sharedClass, Communication.METHODS.POST, item.getFieldList(), type, item.getMap()));
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
		}
		if(item != null && item.getValue().equals(AGENT_STRING)){
			handler.getAgents();
		}
	}

	private void refreshTreeBranch(MapTreeItem branch, Map<String, Object> map) {
		
		if(branch.getChildren() != null) {
			branch.getChildren().clear();
		}
		
		for(Map.Entry<String, Object> entry : map.entrySet()) {
			Object value = entry.getValue();
			if(value instanceof ArrayList) {
				ArrayList<HashMap<String, Object>> list = (ArrayList<HashMap<String, Object>>)value;
				if(list != null) {					
					for(HashMap<String, Object> item : list) {
						if(item.containsKey("Name")) {
							String name = (String)item.get("Name"); 
						    String[] parts = name.split("/");
						    MapTreeItem curItem = branch;
						    for(String part : parts) {
					    	  curItem = addTreeItem(part, part.equals(parts[parts.length-1]) ? item : null, curItem);
						    }
						}
						else if(item.containsKey("name")) {
							String name = (String)item.get("name"); 
						    String[] parts = name.split("/");
						    MapTreeItem curItem = branch;
						    for(String part : parts) {
					    	  curItem = addTreeItem(part, part.equals(parts[parts.length-1]) ? item : null, curItem);
						    }
						}
					}
				}
			}
		}
		
		if(!treeRoot.getChildren().contains(branch)) {
			treeRoot.getChildren().add(branch);
		}
	}
	
	private void resetMainContent() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	content.getChildren().clear();
	        }
	      });
	}
	
	private void resetTreeView() {
		Platform.runLater(new Runnable() {
	        @Override
	        public void run() {
	        	if(treeRoot != null)
	    			treeRoot.getChildren().clear();
	        }
	      });
	}

	public void setModel(Model model) {
		this.model = model;
	}
	
	public void setRequestHandler(RequestHandler handler) {
		this.handler = handler;
	}
	
}