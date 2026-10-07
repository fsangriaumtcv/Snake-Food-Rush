import java.io.*;
import javax.sound.sampled.*;

class SoundEffects {

    Clip eatSound = load("eatsfx.wav");
    Clip hitSound = load("Hitwallsfx.wav");

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

    void play(Clip clip) {
        if (clip == null) {
            return;
        }
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    void playEat() {
        play(eatSound);
    }

    void playHit() {
        play(hitSound);
    }
}
