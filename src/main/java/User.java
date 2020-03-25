import java.net.InetAddress;

//Class to hold identifying info for the User
public class User {
    public String name;
    public InetAddress ip;

    public User(String name, InetAddress ip) {
        this.name = name;
        this.ip = ip;
    }

    public String toString() {
        return this.name;
    }
}