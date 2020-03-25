import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;


public class Main extends Application {

    //TODO: implement a blockingQueue and privatize messages again (don't forget to change Communication so that it passes it into the new BlockingQueue)
    public ObservableList<Message> messages;

    public String SenderName;
    public ObservableList<User> Users;
    private Communication partner;

    boolean login(){
        Stage login = new Stage();

        //create login window
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(5);
        grid.setHgap(5);

        //initialize login window appearance
        final String[] userName = new String[1];
        TextField user = new TextField();
        user.setPromptText("Enter UserName");
        grid.add(user, 1, 0);

        Text label = new Text("UserName:");
        grid.add(label,0, 0);

        TextField ipInput = new TextField();
        ipInput.setPromptText("Enter Target IP");
        grid.add(ipInput, 1, 1);

        Text ipLabel = new Text("IP:");
        grid.add(ipLabel, 0, 1);

        TextField portInput = new TextField();
        portInput.setPromptText("Enter Target Port");
        grid.add(portInput, 1, 2);

        Text portLabel = new Text("Port:");
        grid.add(portLabel, 0, 2);

        Button confirm = new Button("Confirm");
        grid.add(confirm, 1, 3);

        //action event when user inputs the username
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                userName[0] = user.getText();
                SenderName = userName[0].toString(); //define the senders name internally

                try {
                    Users.add(new User(SenderName, InetAddress.getLocalHost())); //Add self to user list
                } catch (UnknownHostException e) {
                    Users.add(new User(SenderName, null));
                }

                //Boot up a Communication with the input IP/Port
                partner = new Communication(ipInput.getText(), Integer.valueOf(portInput.getText()), Main.this);
                Thread primary = new Thread(partner);
                primary.start();

                login.close();
            }
        });
        grid.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                confirm.fire();
                ev.consume();
            }
        });

        //display the login window
        Scene start = new Scene(grid, 250, 150);
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
        Music music = new Music();
        music.SoundClipTest();

        //part of window that displays users
        ListView<User> pplBox = new ListView<User>();
        pplBox.setPrefSize(220, 480);
        pplBox.setId("pplBox");
        Users = FXCollections.observableArrayList();
        pplBox.setItems(Users);
        //I wanted to set the CSS for these cells in particular, so I had to do this i think
        pplBox.setCellFactory(cell -> new ListCell<User>() {
            @Override
            protected void updateItem(User user, boolean empty) {
                super.updateItem(user, empty);
                super.setId("pplBox");
                if(user == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(user.toString());
                    //setTooltip(new Tooltip(user.ip.toString()));
                }
            }
        });

        TextField console = new TextField();
        console.setId("console");

        //List of current messages
        messages = FXCollections.observableArrayList();

        // bottom respectively "button area"
        HBox message = new HBox();
        Button send = new Button("Send");
        send.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                Message temp = new Message(console.getText(), SenderName);
                System.out.println(temp.toDebugString());
                //add the message to observable list
                messages.add(temp);
                //Send the message to the other connected
                partner.sendMessage(temp.Content);
                //add message to the user's respective chat log
                try {
                    ChatLog.add(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                console.clear();
            }
        });

        //update the console field
        console.setPromptText("Write a message...");
        console.setPrefWidth(570);

        message.getChildren().addAll(console, send);
        message.setAlignment(Pos.BOTTOM_LEFT);

        //Object to display messages
        ListView<Message> list = new ListView<Message>();
        list.setItems(messages);
        //Add cell tooltip once cursor hovers over a sent message
        list.setCellFactory(cell -> new ListCell<Message>() {
            @Override
            protected void updateItem(Message message, boolean empty) {
                super.updateItem(message, empty);
                if(message == null || empty) {
                    setText(null);
                    setTooltip(null);
                } else {
                    SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    setText(message.toString());
                    setTooltip(new Tooltip(fmt.format(message.Time.getTime())));
                }
            }
        });

        // root to setup chat window
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20)); // space between elements and window border
        root.setBottom(message);
        root.setCenter(list);
        root.setMargin(list, new Insets(0,20,20,0));
        root.setRight(pplBox);

        //Press enter activates the 'send' button
        root.addEventHandler(KeyEvent.KEY_PRESSED, ev -> {
            if (ev.getCode() == KeyCode.ENTER) {
                send.fire();
                ev.consume();
            }
        });

        //display the chat window
        Scene scene = new Scene(root, 900, 500);
        scene.getStylesheets().add("style.css");
        stage.setTitle("Discord 2.0");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("icon.png")));
        stage.setScene(scene);
        stage.show();
        login();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
