
import java.io.*;

public class ChatLog {
    public static void add(Message message) throws IOException{
        //create an appendable text file named after the user
        File log = new File(message.Sender+".txt");
        FileWriter fw = new FileWriter(log, true);
        PrintWriter output = new PrintWriter(fw);

        //appends messages
        output.println(message.toString());

        output.close();
        fw.close();
    }

}
