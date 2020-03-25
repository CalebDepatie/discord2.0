import javafx.application.Platform;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ComsHandler {
    protected Main parent;
    protected ExecutorService pool;
    protected Vector<Connection> connections;

    public ComsHandler(Main parent) {
        this.parent = parent;
        connections = new Vector<Connection>();
        pool = Executors.newFixedThreadPool(10);
    }

    public void addConnection(String ip, int port) {
        connections.add(new Connection(ip, port));
        pool.submit(new Thread(new SendThread(ip, port, parent.SenderName)));
        pool.submit(new Thread(new ReceieveThread(ip, port)));
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
        private final Lock Sendlock;
        private Socket connection;
        private ServerSocket backup;
        public boolean isServer = false;
        private String message;

        public SendThread(String ip, int port, String message) {
            Sendlock = new ReentrantLock();
            //Sendlock.lock();
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
                } finally {
                    //Sendlock.unlock();
                }
            }
        }

        @Override
        public void run() {
            //Sendlock.lock();
            try {
                this.connection.getOutputStream().write(this.message.getBytes());
                this.connection.close();
                System.out.println("sent");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                //Sendlock.unlock();
            }
        }
    }

    private class ReceieveThread implements Runnable {
        private Socket connection;
        private ServerSocket backup;
        public User partner;
        public boolean isServer = false;
        private final Lock Recievelock;

        public ReceieveThread(String ip, int port) {
            Recievelock = new ReentrantLock();
            //Recievelock.lock();
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
                } finally {
                    //Recievelock.unlock();
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
                    Platform.runLater(() -> sendMessage(parent.SenderName));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            System.out.println("0");
            //Recievelock.lock();
            try {
                System.out.println("1");
                //Catches the initial message sent by the new connection and uses that as their name
                this.partner = new User(this.getString(), this.connection.getInetAddress());
                Platform.runLater(() -> parent.Users.add(this.partner));
            } finally {
                //Recievelock.unlock();
            }

                while (this.connection.isConnected()) {
                    System.out.println("2");
                    Message temp;
                    temp = new Message(this.getString(), this.partner.name);
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
                String output;
                //Recievelock.lock();
                try{
                    output = String.valueOf((char)in.read());
                } finally {
                    //Recievelock.unlock();
                }

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
