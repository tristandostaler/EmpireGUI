package pse_gui;
import java.util.HashMap;
import java.util.Map;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class UIObjectCreator {
	
	private SharedCentralisedClass sharedClass;
	
	public UIObjectCreator(SharedCentralisedClass sharedClass) {
		this.sharedClass = sharedClass;
	}
	
	public VBox generateVBox(UserRequest ur, HashMap<String, Object> map) {
		
		VBox vBox = new VBox();
		HBox.setHgrow(vBox, Priority.NEVER);
		
		for(String item : map.keySet()){
			if(item.equals("options")){
				vBox.getChildren().add(new Label(item));
				Map<String, Object> fieldMap = (Map<String, Object>)map.get(item);
				for(String item2 : fieldMap.keySet()){
					if(fieldMap.get(item2) instanceof Field){
						Field f = (Field)fieldMap.get(item2);
						Label name = new Label("\t" + item2);
						name.setMinWidth(150);
						HBox.setHgrow(name, Priority.NEVER);
						if(f.isRequired())
							name.setText(name.getText() + "*");
						TextField t = new TextField();
						t.setMinWidth(70);
						t.textProperty().addListener(new ChangeListener<String>() {
				            @Override
				            public void changed(ObservableValue<? extends String> ob, String o, String n) {
				                //int size = t.getText().length() * 7;
				                //if(size < 70)
				                	//size = 70;
				                //t.setPrefWidth(size);
				            	//t.setPrefColumnCount(size);
				            	double size = TextUtils.computeTextWidth(t.getFont(),
				                        t.getText(), 0.0D) + 30;
				            	if(size < 150)
				            		size = 150;
				            	t.setPrefWidth(size);
				            }
				        });
						t.setText(f.getValue());
						HBox.setHgrow(t, Priority.NEVER);
						t.setOnKeyReleased(new EventHandler<KeyEvent>()
					    {
					        @Override
					        public void handle(KeyEvent ke)
					        {
					            ur.getFieldFromName(f.getName()).setValue(t.getText());
					        }
					    });
						HBox hb = new HBox();
						HBox.setHgrow(hb, Priority.NEVER);
						Label lDescription = new Label(f.getDescription());
						HBox.setHgrow(lDescription, Priority.NEVER);
						hb.getChildren().addAll(name, t, lDescription);
						hb.setSpacing(10);
						vBox.getChildren().add(hb);
					}
					else{
						HBox hb = new HBox();
						HBox.setHgrow(hb, Priority.NEVER);
						TextField t = generateTextField(fieldMap.get(item2).toString());
						HBox.setHgrow(t, Priority.NEVER);
						Label lItem = new Label("\t" + item2);
						lItem.setMinWidth(150);
						HBox.setHgrow(lItem, Priority.NEVER);
						hb.getChildren().addAll(lItem, t);
						hb.setSpacing(10);
						vBox.getChildren().add(hb);
					}
				}
			}
			else{
				if(map.get(item) instanceof Map<?, ?>){
					vBox.getChildren().add(new Label(item));
					Map<String, Object> fieldMap = (Map<String, Object>)map.get(item);
					for(String item2 : fieldMap.keySet()){
						HBox hb = new HBox();
						HBox.setHgrow(hb, Priority.NEVER);
						TextField t = generateTextField(fieldMap.get(item2).toString());
						HBox.setHgrow(t, Priority.NEVER);
						Label lItem = new Label(item2);
						lItem.setMinWidth(150);
						HBox.setHgrow(lItem, Priority.NEVER);
						hb.getChildren().addAll(lItem, t);
						hb.setSpacing(10);
						vBox.getChildren().add(hb);
					}
				}
				else{
					HBox hb = new HBox();
					HBox.setHgrow(hb, Priority.NEVER);
					TextField t = generateTextField(map.get(item).toString());
					HBox.setHgrow(t, Priority.NEVER);
					Label lItem = new Label(item);
					lItem.setMinWidth(150);
					HBox.setHgrow(lItem, Priority.NEVER);
					hb.getChildren().addAll(lItem, t);
					hb.setSpacing(10);
					vBox.getChildren().add(hb);
				}
			}
		}
		
		/*Label label = new Label(map.get("Description").toString());
		
		vBox.getChildren().add(label);*/

		return vBox;
	}

	private TextField generateTextField(String textToWrite){
		TextField t = new TextField();
		t.setEditable(false);
		t.setStyle("-fx-background-color: lightgray;");
		
		t.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> ob, String o, String n) {
            	double size = TextUtils.computeTextWidth(t.getFont(),
                        t.getText(), 0.0D) + 30;
            	if(size < 150)
            		size = 150;
            	t.setPrefWidth(size);
            }
        });
		
		t.setText(textToWrite);
		
		return t;
	}
}
