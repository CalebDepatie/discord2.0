import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ComsHandler {
    protected Main parent;
    protected ExecutorService pool;
    protected Vector<Connection> connections;

    public ComsHandler() {
        connections = new Vector<Connection>();
        pool = Executors.newCachedThreadPool();
    }

    public void addConnection(String ip, int port) {
        connections.add(new Connection(ip, port));
        pool.submit(new Thread(new ReceieveThread(ip, port)));
        pool.submit(new Thread(new SendThread(ip, port, parent.SenderName)));
    }

    public void sendMessage(String message) {
        for(Connection c : this.connections) {
            pool.submit(new Thread(new SendThread(c.ip, c.port, message)));
        }
    }

    protected class Connection {
        protected String ip;
        protected int port;
        public Connection(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    private class SendThread implements Runnable {
        private Socket connection;
        private ServerSocket backup;
        public boolean isServer = false;
        private String message;

        public SendThread(String ip, int port, String message) {
            try {
                parent = parent;
                this.connection = new Socket(ip, port);
                this.message = message;
            } catch (Exception e) {
                try {
                    isServer = true;
                    //In the future this could be more secure by checking to make sure the incoming connection is from the specified IP
                    this.backup = new ServerSocket(port);
                    //runLater to avoid errors caused by UI updates forced by non-FX application thread
                    Platform.runLater(() -> parent.messages.add(new Message("Connection Failed. Waiting for response on local port...", "ERROR")));
                } catch (Exception f) {
                    e.printStackTrace();
                    f.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            try {
                this.connection.getOutputStream().write(this.message.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ReceieveThread implements Runnable {
        private Socket connection;
        private ServerSocket backup;
        public User partner;
        public boolean isServer = false;

        public ReceieveThread(String ip, int port) {
            try {
                parent = parent;
                this.connection = new Socket(ip, port);
            } catch (Exception e) {
                try {
                    isServer = true;
                    //In the future this could be more secure by checking to make sure the incoming connection is from the specified IP
                    this.backup = new ServerSocket(port);
                    //runLater to avoid errors caused by UI updates forced by non-FX application thread
                    Platform.runLater(() -> parent.messages.add(new Message("Connection Failed. Waiting for response on local port...", "ERROR")));
                } catch (Exception f) {
                    e.printStackTrace();
                    f.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            //Catches the initial message sent by the new connection and uses that as their name
            this.partner = new User(this.getString(), this.connection.getInetAddress());
            Platform.runLater(() -> parent.Users.add(this.partner));

            while (this.connection.isConnected()) {
                Message temp = new Message(this.getString(), this.partner.name);
                System.out.println(temp.toDebugString());
                Platform.runLater(() -> parent.messages.add(temp));
            }
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

                return output;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
