package pse_gui;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.control.BreadCrumbBar.BreadCrumbActionEvent;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
	@FXML TreeView<File> leftFileTree;
	@FXML TreeView<File> rightFileTree;
	@FXML BreadCrumbBar<File> leftFileBreadCrumb;
	@FXML BreadCrumbBar<File> rightFileBreadCrumb;
	
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
	private final String CREATE_LISTENER_STRING = "create new listener";
	private final String MODULE_STRING = "modules";
	private final String LISTENER_OPTIONS_STRING = "listeneroptions";

	private UIObjectCreator uiObjectCreator;
	
	private RequestHandler handler;
	private Model model;
	
	private Service<Void> backgroundThread;
	
	private String logTextAreaBuffer = "";
	
	private boolean HasRunLater = false;
	
	private String actualItemToDelete = "";
	
	private ItemType deleteType;
	
	public MainView() {
		uiObjectCreator = new UIObjectCreator();
	}
	
	private MapTreeItem addTreeItem(String title, HashMap<String, Object> value, MapTreeItem parent) {
		MapTreeItem item = null;
		MapTreeItem existingItem = getItemOfName(title, parent);
		if(existingItem == null) {
			item = new MapTreeItem(title);
			item.setMap(value);
		    parent.getChildren().add(item);
		}
		else
			item = existingItem;

		return item;
	}
	
	private MapTreeItem addTreeItemAtPosition(String title, HashMap<String, Object> value, MapTreeItem parent, int position) {
		MapTreeItem item = null;
		MapTreeItem existingItem = getItemOfName(title, parent);
		if(existingItem == null) {
			item = new MapTreeItem(title);
			item.setMap(value);
		    parent.getChildren().add(position, item);
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
		
		leftFileBreadCrumb.setAutoNavigationEnabled(false);
		leftFileBreadCrumb.selectedCrumbProperty().bind(leftFileTree.getSelectionModel().selectedItemProperty());
		leftFileBreadCrumb.setOnCrumbAction(new EventHandler<BreadCrumbActionEvent<File>>() {

			@Override
			public void handle(BreadCrumbActionEvent<File> event) {
				TreeItem<File> selectedCrumb = event.getSelectedCrumb();
				if(selectedCrumb == leftFileTree.getRoot()) {
					leftFileTree.getSelectionModel().select(leftFileTree.getRoot());
				}
				else {
					leftFileTree.getSelectionModel().select(event.getSelectedCrumb());
				}
			}
		});
		rightFileBreadCrumb.setAutoNavigationEnabled(false);
		rightFileBreadCrumb.selectedCrumbProperty().bind(rightFileTree.getSelectionModel().selectedItemProperty());
		rightFileBreadCrumb.setOnCrumbAction(new EventHandler<BreadCrumbActionEvent<File>>() {

			@Override
			public void handle(BreadCrumbActionEvent<File> event) {
				TreeItem<File> selectedCrumb = event.getSelectedCrumb();
				if(selectedCrumb == rightFileTree.getRoot()) {
					rightFileTree.getSelectionModel().select(rightFileTree.getRoot());
				}
				else {
					rightFileTree.getSelectionModel().select(event.getSelectedCrumb());
				}
			}
		});
		
		initialiseLocalFileBrowser();
    }
	
	private void initializeTreeView() {
		if(treeRoot == null) {
			treeRoot = new MapTreeItem("/");
			tree.setShowRoot(false);
			tree.setRoot(treeRoot);
			
			synchronized (treeRoot) {
				stagersItem = new MapTreeItem(STAGER_STRING);
				agentsItem = new MapTreeItem(AGENT_STRING);
				listenersItem = new MapTreeItem(LISTENER_STRING);
				modulesItem = new MapTreeItem(MODULE_STRING);
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
	
	public void notifyListenerOptionsUpdated() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				try {
					initializeTreeView();
					refreshTreeBranch(listenersItem, model.getListenerOptionsList().getValue());
					//addTreeItem(CREATE_LISTENER_STRING, (HashMap<String, Object>)model.getListenerOptionsList().getValue(), listenersItem);
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

	public void initialiseLocalFileBrowser() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//http://www.java2s.com/Code/Java/JavaFX/Createthetreeitemonthefly.htm
				TreeItem<File> leftFileTreeRoot;
				try {
					leftFileTreeRoot = createNode(new File("/"), null);
					leftFileTree.setShowRoot(false);
					leftFileTree.setRoot(leftFileTreeRoot);
				} catch (SftpException e) {
					SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
				}
			}
		});
		
	}

	public void initialiseRemoteFileBrowser() {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				//http://www.java2s.com/Code/Java/JavaFX/Createthetreeitemonthefly.htm
				TreeItem<File> rightFileTreeRoot;
				rightFileTree.setShowRoot(false);
				if(model.getPowershellEmpireConnection().isSSHConnected()) {
					ChannelSftp sftpChann = model.getPowershellEmpireConnection().getSFTPChannel();
					try {
						rightFileTreeRoot = createNode(new File("/"), sftpChann);
						rightFileTree.setRoot(rightFileTreeRoot);
					} catch (SftpException e) {
						SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
					}
				}
				else {
					try {
						rightFileTreeRoot = createNode(new File("/"), null);
						rightFileTree.setRoot(rightFileTreeRoot);
					} catch (SftpException e) {
						SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
					}
				}
				
			}
		});
		
	}
	
	private TreeItem<File> createNode(final File f, ChannelSftp ifIsRemoteSftpChannel) throws SftpException {
	    return new TreeItem<File>(f) {
	        private boolean isLeaf;
	        private boolean isFirstTimeChildren = true;
	        private boolean isFirstTimeLeaf = true;
	        private ChannelSftp IsRemoteSftpChannel = ifIsRemoteSftpChannel;
	         
	        @Override public ObservableList<TreeItem<File>> getChildren() {
	            if (isFirstTimeChildren) {
	                isFirstTimeChildren = false;
	                super.getChildren().setAll(buildChildren(this));
	            }
	            return super.getChildren();
	        }

	        @Override public boolean isLeaf() {
	            if (isFirstTimeLeaf) {
	                isFirstTimeLeaf = false;
	                File f = (File) getValue();
	                isLeaf = f.isFile();
	            }

	            return isLeaf;
	        }

	        private ObservableList<TreeItem<File>> buildChildren(TreeItem<File> TreeItem) {
	            if (IsRemoteSftpChannel == null) {
		        	File f = TreeItem.getValue();
		            if (f != null && f.isDirectory()) {
		                File[] files = f.listFiles();
		                if (files != null) {
		                    ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
	
		                    for (File childFile : files) {
		                        try {
									children.add(createNode(childFile, IsRemoteSftpChannel));
								} catch (SftpException e) {
									SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
								}
		                    }
	
		                    return children;
		                }
		            }
	
		            return FXCollections.emptyObservableList();
	            }
	            else {
	            	try {
	            		String toLS = TreeItem.getValue().getAbsolutePath();
	            		IsRemoteSftpChannel.cd(toLS);
		            	Vector<Object> v = IsRemoteSftpChannel.ls(toLS);
		            	LsEntry f = (LsEntry) v.get(0);
			            if (f != null && f.getAttrs().isDir()) {
			                //File[] files = f.listFiles();
			                if (v.size() > 1) {
			                    ObservableList<TreeItem<File>> children = FXCollections.observableArrayList();
		
			                    for (int i = 0; i < v.size(); i++) {
			                        	String fileName = ((LsEntry)v.get(i)).getFilename();
			                        	String completeFileName = IsRemoteSftpChannel.realpath(fileName);
			                        	if (!fileName.equals(".") && !fileName.equals(".."))
			                        		children.add(createNode(new File(completeFileName), IsRemoteSftpChannel));
									
			                    }
		
			                    return children;
			                }
			            }
	            	} catch (SftpException e) {
						SharedCentralisedClass.getInstance().showStackTraceInAlertWindow(e.getMessage(), e);
					}
	
		            return FXCollections.emptyObservableList();
	            }
	        }
	    };
	}
	
	public void onBtnConnectClick() {
		try {
			if(loginStage == null) {
			    loginStage = new Stage();
				Parent root;
				
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
				root = fxmlLoader.load();
				
				loginController = fxmlLoader.getController();
				
				loginStage.setTitle("PowerShell Empire Login");
				loginStage.initModality(Modality.APPLICATION_MODAL);
				loginStage.setResizable(false);
				
				Scene scene = new Scene(root, 400, 430);
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
										sshInfos = new SSHInformations();
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
											handler.getListenerOptions();
											//handler.initialiseFileHandler();
											initialiseRemoteFileBrowser();
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
			handler.makeUserRequest(new UserRequestResponseHandler());
		else if(this.model.getUserRequest().getType() == ItemType.STAGER)
			handler.makeUserRequest(new StagerRequestResponseHandler());
		else if(this.model.getUserRequest().getType() == ItemType.MODULE)
			handler.makeUserRequest(new ModuleRequestResponseHandler());
		else if(this.model.getUserRequest().getType() == ItemType.LISTENER) {
			handler.makeUserRequest(new ListenerRequestResponseHandler());
		}
		else
			handler.makeUserRequest(new UserRequestResponseHandler());
	}
	
	public class ListenerRequestResponseHandler extends ResponseHandler{

		public ListenerRequestResponseHandler() {
			super(false);
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			String s = serverResponse.getValue().toString();
			System.out.println(s);
			handler.getListeners();
		}

	}
	
	public void onBtnResetClick() { //TODO
		logTextArea.appendText("> Reset current page command executed..\n" );
	}
	
	public void onBtnDeleteClick(){
		if(deleteType == ItemType.AGENT) {
			ItemType type = ItemType.AGENT;
			this.model.setUserRequest(new UserRequest(Communication.METHODS.GET, null, type, "agents/" + actualItemToDelete + "/kill"));
			handler.makeUserRequest(new KillAgentResponseHandler());
		}
		else if(deleteType == ItemType.LISTENER){
			ItemType type = ItemType.LISTENER;
			this.model.setUserRequest(new UserRequest(Communication.METHODS.DELETE, null, type, "listeners/" + actualItemToDelete));
			handler.makeUserRequest(new DeleteListenerResponseHandler());
		}
	}
	
	public class KillAgentResponseHandler extends ResponseHandler{

		public KillAgentResponseHandler() {
			super(false);
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			ItemType type = ItemType.AGENT;
			model.setUserRequest(new UserRequest(Communication.METHODS.DELETE, null, type, "agents/" + actualItemToDelete));
			handler.makeUserRequest(new DeleteAgentResponseHandler());
		}

	}
	
	public class DeleteAgentResponseHandler extends ResponseHandler{

		public DeleteAgentResponseHandler() {
			super(false);
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			handler.getAgents();
		}

	}
	
	public class DeleteListenerResponseHandler extends ResponseHandler{

		public DeleteListenerResponseHandler() {
			super(false);
		}
		@Override
		public void baseHandleResponse(ServerResponse serverResponse) {
			handler.getListeners();
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
		actualItemToDelete = "";
		
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
				deleteType = ItemType.LISTENER;
				if(item.getValue().equals(CREATE_LISTENER_STRING)) {
					this.model.setUserRequest(new UserRequest(Communication.METHODS.POST, item.getFieldList(), type, "listeners"));
					btnReset.setDisable(false);
					btnSend.setDisable(false);
				}
				else {
					actualItemToDelete = item.getValue();
					this.model.setUserRequest(null);
					this.btnDelete.setDisable(false);
				}
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
			}
			else if(parent.getValue().equals(AGENT_STRING)){
				ItemType type = ItemType.AGENT;
				deleteType = ItemType.AGENT;
				this.model.setUserRequest(null);
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				actualItemToDelete = item.getValue();
				this.btnDelete.setDisable(false);
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
			else if(parent.getValue().equals(STAGER_STRING)){
				ItemType type = ItemType.STAGER;
				ArrayList<Field> copy = (ArrayList<Field>) item.getFieldList().clone();
				copy.add(new Field("StagerName", "The stager name", ((MapTreeItem)item).getValue(), true));
				this.model.setUserRequest(new UserRequest(Communication.METHODS.POST, copy, type));
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
			else if(parent.getValue().equals(MODULE_STRING)){
				ItemType type = ItemType.MODULE;
				this.model.setUserRequest(new UserRequest(Communication.METHODS.POST, item.getFieldList(), type, item.getMap()));
				content.getChildren().add(uiObjectCreator.generateVBox(this.model.getUserRequest(), item.getMap()));
				btnReset.setDisable(false);
				btnSend.setDisable(false);
			}
		}
		
		if(item != null && item.getValue().equals(AGENT_STRING)){
			handler.getAgents();
		}
		else if(item != null && item.getValue().equals(LISTENER_STRING)){
			handler.getListeners();
			handler.getListenerOptions();
		}
	}

	private void refreshTreeBranch(MapTreeItem branch, Map<String, Object> map) {
		
		if(map.get(LISTENER_OPTIONS_STRING) != null){
			if(branch.getChildren().size() > 0 && branch.getChildren().get(0).getValue().equals(CREATE_LISTENER_STRING)){
				branch.getChildren().remove(0);
			}
			/* 
			 * Ok, the next part is really complicated for nothing but...hey, it works!
			 * So the logic is this:
			 * Normally, we have an array of listeners which are then an array of values. Here, we receive directly the array of values
			 * so we need to access it as such (so parse everything until we have an array of values)
			 * We then know that these values are all options for the listeners so we create a hashmap with the key "options"
			 * (like normally we would have) and give the array with the options for the listener.
			 * We then create a treeitem at position 0 of the branch "listeners" with the name "create a new listener".
			 * That treeitem contains our array of "options".
			*/
			//We know we only have 1 element: the listener options
			Map.Entry<String, Object> elem0 = (Entry<String, Object>)map.entrySet().iterator().next();
			//We know it's a ArrayList of 1 hashmap of fields
			ArrayList<HashMap<String, Object>> alElem0 = (ArrayList<HashMap<String, Object>>) elem0.getValue();
			HashMap<String, Object> hmElem0 = (HashMap<String, Object>) alElem0.get(0);
			HashMap<String, Object> hmElem0AsOptions = new HashMap<String, Object>();
			hmElem0AsOptions.put("options", hmElem0);
			addTreeItemAtPosition(CREATE_LISTENER_STRING, hmElem0AsOptions, branch,0);
		}
		else{
			if(branch.getChildren() != null) {
				if(map.get(LISTENER_STRING) != null){
					if(branch.getChildren().size() > 0 && branch.getChildren().get(0).getValue().equals(CREATE_LISTENER_STRING)){
						TreeItem<String> temp = branch.getChildren().get(0);
						branch.getChildren().clear();
						branch.getChildren().add(temp);
					}
					else
						branch.getChildren().clear();
				}
				else
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