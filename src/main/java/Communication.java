import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Communication implements Runnable{
    private Socket connection;
    private ServerSocket backup;
    private Main parent;
    public User partner;
    public boolean isServer = false;

    public Communication(String ip, int port, Main parent) {
        try {
            this.connection = new Socket(ip, port);
        } catch (Exception e) {
            try {
                isServer = true;
                //In the future this could be more secure by checking to make sure the incoming connection is from the specified IP
                this.backup = new ServerSocket(port);
                this.parent.messages.add(new Message("Connection Failed. Waiting for response on local port...", "ERROR"));
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
                this.connection = this.backup.accept();
                this.parent = parent;
                this.partner = new User(this.getString(), this.connection.getInetAddress());

                this.parent.Users.add(this.partner);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        while (this.connection.isConnected()) {
            //TODO: Consider maybe making a public method in Main for message addition? (maybe after blockingQueue)
            Message temp = new Message(this.getString(), this.partner.name);
            System.out.println(temp.toDebugString());
            //add the message to observable list
            this.parent.messages.add(temp);
        }
    }

    public String getString() {
        try {
            String output = Byte.toString((byte) this.connection.getInputStream().read());
            byte buffer[] = new byte[this.connection.getInputStream().available()];

            this.connection.getInputStream().read(buffer);
            output.concat(new String(buffer));

            return output;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}