package pse_gui;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class UIMain extends Application {
	
	private MainView mainController;
	
	public MainView getMainController() {
		return mainController;
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		initializeView(primaryStage);
		
		// Disconnect from PowershellEmpireConnection if necessary.
		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				if(mainController != null) {
					mainController.disconnectDoAction();
				}	
			}
		});
		
		SharedCentralisedClass.getInstance().setMainView(mainController);
		
		Model model = new Model();
		RequestHandler handler = new RequestHandler();
		
		handler.setMainController(mainController);
		handler.setModel(model);
		
		mainController.setRequestHandler(handler);
		mainController.setModel(model);
		
	} 
	
	private void initializeView(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MainView.fxml"));
		Parent root = fxmlLoader.load();
		mainController  = fxmlLoader.getController();
		primaryStage.setTitle("PowerShell GUI");
		Scene scene = new Scene(root, 1024, 768);
		scene.getStylesheets().add("pse_gui/MainView.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}

}
