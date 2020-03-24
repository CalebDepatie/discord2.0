import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;


public class Main extends Application {

    public String SenderName;

    boolean login(){
        Stage login = new Stage();

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        final String[] userName = new String[1];
        TextField user = new TextField();
        user.setPromptText("Enter UserName");
        grid.add(user, 1, 0);

        Text label = new Text("UserName:");
        grid.add(label,0, 0);

        Button confirm = new Button("Confirm");
        grid.add(confirm, 1, 1);

        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                userName[0] = user.getText();
                SenderName = userName[0].toString(); //define the senders name internally
                login.close();
            }
        });
        grid.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                confirm.fire();
                ev.consume();
            }
        });

        Scene start = new Scene(grid, 300, 80);
        login.setScene(start);
        login.show();

        if(userName[0]!= null){
            return true;
        }else{
            return false;
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

            Rectangle pplBox = new Rectangle(650, 0, 250, 500);
            pplBox.setFill(Color.rgb(52, 55, 61));

            TextField console = new TextField();
            console.setId("console");

            //List of current messages
            ObservableList<Message> messages = FXCollections.observableArrayList();

            // bottom respectively "button area"
            HBox message = new HBox();
            Button send = new Button("Send");
            send.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    Message temp = new Message(console.getText(), SenderName);
                    System.out.println(temp.toDebugString());
                    messages.add(temp);
                    try {
                        ChatLog.add(temp);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    console.clear();
                }
            });
            console.setPromptText("Write a message...");
            console.setPrefWidth(570);

            message.getChildren().addAll(console, send);
            message.setAlignment(Pos.BOTTOM_LEFT);

            //Object to display messages
            ListView<Message> list = new ListView<Message>();
            list.setItems(messages);

            // root
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20)); // space between elements and window border
            root.setBottom(message);
            root.setCenter(list);
            root.setMargin(list, new Insets(0,260,20,0));
            root.getChildren().add(pplBox);

            //Press enter activates the 'send' button
            root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
                if (ev.getCode() == KeyCode.ENTER) {
                    send.fire();
                    ev.consume();
                }
            });

            Scene scene = new Scene(root, 900, 500);
            scene.getStylesheets().add("style.css");
            stage.setTitle("Discord 2.0");
            stage.setScene(scene);
            stage.show();
            login();
        }

    public static void main(String[] args) {
        launch(args);
    }
}
