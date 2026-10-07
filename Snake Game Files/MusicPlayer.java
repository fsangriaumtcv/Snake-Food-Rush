import java.io.*;
import javax.sound.sampled.*;

class MusicPlayer {

    Clip music;

    MusicPlayer(String fileName) {
        music = load(fileName);
    }

    Clip load(String fileName) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(new File(fileName))) {
            Clip clip = AudioSystem.getClip();
            clip.open(audio);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Could not load " + fileName + ": " + e);
            return null;
        }
    }

    void start() {
        if (music == null) {
            return;
        }
        music.stop();
        music.setFramePosition(0);
        music.loop(Clip.LOOP_CONTINUOUSLY);
    }

    void pause() {
        if (music != null) {
            music.stop();
        }
    }

    void resume() {
        if (music != null) {
            music.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    void stop() {
        if (music != null) {
            music.stop();
            music.setFramePosition(0);
        }
    }
}
