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

    //debug console function
    public String toString() {
        SimpleDateFormat fmt = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return "Content: " + this.Content + "\nSender: " + this.Sender + "\nTime: " + fmt.format(this.Time.getTime());
    }
}
