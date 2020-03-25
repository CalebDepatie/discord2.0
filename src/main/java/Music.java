import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;


public class Music{

    public void SoundClipTest() {
        try {
            URL soundStream = getClass().getResource("Music.wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundStream);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        clip.stop();
    }
    public void play() {
        clip.start();
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
