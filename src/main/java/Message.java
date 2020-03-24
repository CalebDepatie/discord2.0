import java.util.Calendar;
import java.text.SimpleDateFormat;

public class Message {
    public String Content;
    public String Sender;
    public Calendar Time;

    public Message(String content, String username) {
        this.Content = content;
        this.Sender = username;
        this.Time = Calendar.getInstance();
    }

    //Message format
    public String toString() {
        return this.Sender + ": " + this.Content;
    }

    //debug console function
    public String toDebugString() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Content: " + this.Content + "\nSender: " + this.Sender + "\nTime: " + fmt.format(this.Time.getTime());
    }
}
