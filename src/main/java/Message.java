import java.util.Calendar;

public class Message {
    public String Content;
    public String Sender;
    public Calendar Time;

    public Message(String content, String username) {
        this.Content = content;
        this.Sender = username;
        this.Time = Calendar.getInstance();
    }

    //debug console function
    public String toString() {
        return "Content: " + this.Content + "\nSender: " + this.Sender + "\nTime: " + this.Time.toString();
    }
}
