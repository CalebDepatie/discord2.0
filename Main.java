package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;

import java.io.File;


public class Main extends Application {

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
                System.out.println(userName[0]);
                login.close();
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

            // bottom respectively "button area"
            HBox message = new HBox();
            Button send = new Button("Send");
            send.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent event) {
                    console.clear();
                }
            });
            console.setPromptText("Write a message...");
            console.setPrefWidth(570);

            message.getChildren().addAll(console, send);
            message.setAlignment(Pos.BOTTOM_LEFT);

            // root
            BorderPane root = new BorderPane();
            root.setPadding(new Insets(20)); // space between elements and window border
            root.setBottom(message);
            root.getChildren().add(pplBox);

            Scene scene = new Scene(root, 900, 500);
            File f = new File("style.css");
            scene.getStylesheets().clear();
            scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));
            stage.setTitle("Discord 2.0");
            stage.setScene(scene);
            stage.show();
            login();
        }

    public static void main(String[] args) {
        launch(args);
    }
}
