package infra;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

/**
 * Audio class.
 * 
 * @author Leonardo Ono (ono.leo@gmail.com)
 */
public class Audio { 
    
    private Clip clip;
    private Sequencer sequencer;
    private final Map<Integer, AudioInputStream> sounds = new HashMap<>();
    private final Map<Integer, Sequence> musics = new HashMap<>();
    
    public void start() {
        try {
            clip = AudioSystem.getClip();
        } catch (LineUnavailableException ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadSound(int soundId, String resource) {
        try {
            InputStream is = getClass().getResourceAsStream(resource);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int b;
            while ((b = is.read()) >= 0) {
                baos.write(b);
            }
            is.close();
            byte[] buf = baos.toByteArray();
            baos.close();
            ByteArrayInputStream bais = new ByteArrayInputStream(buf);
            AudioInputStream ais = AudioSystem.getAudioInputStream(bais);
            ais.mark(0);
            sounds.put(soundId, ais);
        }
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void loadMusic(int musicId, String resource) {
        try {
            InputStream is = getClass().getResourceAsStream(resource);
            musics.put(musicId, MidiSystem.getSequence(is));
        } 
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void playSound(int soundId) {
        try {
            if (clip.isOpen()) {
                clip.close();
            }
            AudioInputStream ais = sounds.get(soundId);
            ais.reset();
            clip.open(ais);
            clip.setLoopPoints(0, clip.getFrameLength() / 2);
            clip.loop(1);
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void playMusic(int musicId) {
        try {
            sequencer.setSequence(musics.get(musicId));
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } 
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
