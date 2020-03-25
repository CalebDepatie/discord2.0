import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;


public class Communication implements Runnable{
    private Socket connection;
    private ServerSocket backup;
    private Main parent;
    public User partner;
    public boolean isServer = false;

    public Communication(String ip, int port, Main parent) {
        try {
            this.parent = parent;
            this.connection = new Socket(ip, port);
            this.parent.sending.add(new Message(this.parent.SenderName, this.parent.SenderName));
            this.sendMessage();
        } catch (Exception e) {
            try {
                isServer = true;
                //In the future this could be more secure by checking to make sure the incoming connection is from the specified IP
                this.backup = new ServerSocket(port);
                //runLater to avoid errors caused by UI updates forced by non-FX application thread
                Platform.runLater(() -> this.parent.messages.add(new Message("Connection Failed. Waiting for response on local port...", "ERROR")));
            } catch (Exception f) {
                e.printStackTrace();
                f.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        if (isServer) {
            try {
                //TODO: This IP display is a TEMPORARY MEASURE, implement a more elegant display for the host (that is hidden until you click show so we don't get anyone interrupting the demo)
                //Also, if anyone knows a better, more streamlined way to get the external ip PLEASE write it in
                URL ip = new URL("http://checkip.amazonaws.com/");
                System.out.println(this.getString(ip.openStream()));

                this.connection = this.backup.accept();
                this.parent.sending.add(new Message(this.parent.SenderName, this.parent.SenderName));
                Platform.runLater(() -> this.sendMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //Catches the initial message sent by the new connection and uses that as their name
        this.partner = new User(this.getString(), this.connection.getInetAddress());
        Platform.runLater(() -> this.parent.Users.add(this.partner));
        while (this.connection.isConnected()) {
            try {
                if(this.connection.getInputStream().available() != 0) {
                    Message temp = new Message(this.getString(), this.partner.name);
                    System.out.println(temp.toDebugString());
                    Platform.runLater(() -> this.parent.messages.add(temp));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(!this.parent.sending.isEmpty()) {
                this.sendMessage();
            }
        }
        //TODO: Put actions to run on disconnect here
    }


    public String getString() {
        try {
            return getString(this.connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getString(InputStream in) {
        try {
            String output = String.valueOf((char)in.read());
            byte[] buffer = new byte[in.available()];
            in.read(buffer);
            output = output.concat(new String(buffer));

            System.out.println(output);

            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void sendMessage() {
        try {
            this.connection.getOutputStream().write(parent.sending.take().Content.getBytes());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
