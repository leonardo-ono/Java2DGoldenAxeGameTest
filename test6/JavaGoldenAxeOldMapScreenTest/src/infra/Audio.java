package infra;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Track;
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
    private static Sequencer sequencer;
    private static final Map<Integer, Clip> sounds = new HashMap<>();
    private static final Map<Integer, Sequence> musics = new HashMap<>();
    
    public static void start() {
        
        try {
        Synthesizer synthesizer = MidiSystem.getSynthesizer();
        System.out.println(synthesizer.getDefaultSoundbank().getName()); // on windows, it shows "GS sound set (16 bit)"
        synthesizer.open();
        //Soundbank soundBank = MidiSystem.getSoundbank(new BufferedInputStream(Audio.class.getClass().getResourceAsStream("/res/music/opl4.sf2")));
        //synthesizer.unloadAllInstruments(synthesizer.getDefaultSoundbank());
        //synthesizer.loadAllInstruments(soundBank);
        Sequencer localSequencer = MidiSystem.getSequencer(false);
        Sequence sequence4 = MidiSystem.getSequence(Audio.class.getClass().getResourceAsStream("/res/music/old-map.mid"));
        
//        for (Track track : sequence4.getTracks()) {
//            int volume = (int) (127 * 0.2);
//            for (int channel = 0; channel < 16; channel++) {
//                MidiMessage midiMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 7, volume);
//                MidiEvent midiEvent = new MidiEvent(midiMessage, 0);
//                track.add(midiEvent);
//            }
//        }
         
        // change volume
        // https://stackoverflow.com/questions/18992815/whats-the-method-to-control-volume-in-an-midi-sequencer
        localSequencer.open();
        localSequencer.getTransmitter().setReceiver(synthesizer.getReceiver());
        localSequencer.setSequence(sequence4);
        localSequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
        localSequencer.start();
        
        
//        sequencer = MidiSystem.getSequencer(true);
//        sequencer.open();
            
            // https://stackoverflow.com/questions/10965377/java-midi-volume-control-wont-work
              
//            Receiver receiver = sequencer.getReceiver();
//            ShortMessage volumeMessage = new ShortMessage();
//            for ( int i = 0; i < 16; i++ ) {
//               volumeMessage.setMessage( ShortMessage.CONTROL_CHANGE, i, 7, (int)(vol*127) );
//               receiver.send( volumeMessage, -1 );
//            }            
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void loadSound(int soundId, String resource) {
        try {
            InputStream is = Audio.class.getResourceAsStream(resource);
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
            try {
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                sounds.put(soundId, clip);
            } catch (LineUnavailableException ex) {
                Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void loadMusic(int musicId, String resource) {
        try {
            InputStream is = Audio.class.getResourceAsStream(resource);
            Sequence sequence = null;
            musics.put(musicId, sequence = MidiSystem.getSequence(is));
            
            // change volume
            // https://stackoverflow.com/questions/18992815/whats-the-method-to-control-volume-in-an-midi-sequencer
            for (Track track : sequence.getTracks()) {
                int volume = (int) (127 * 0.2);
                for (int channel = 0; channel < 16; channel++) {
                    MidiMessage midiMessage = new ShortMessage(ShortMessage.CONTROL_CHANGE, channel, 7, volume);
                    MidiEvent midiEvent = new MidiEvent(midiMessage, 0);
                    track.add(midiEvent);
                }
            }
        } 
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void playSound(int soundId) {
        try {
            Clip clip = sounds.get(soundId);
//            if (clip.isOpen()) {
//                clip.close();
//            }
//            AudioInputStream ais = sounds.get(soundId);
//            ais.reset();
//            clip.open(ais);
            //clip.flush();
            //clip.stop();
            //clip.start();
            //clip.setLoopPoints(0, clip.getFrameLength() / 2);
            //clip.loop(0);
            //clip.stop();
            if (!clip.isRunning()) {
                clip.setFramePosition(0);
                clip.start();
            }
        } catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void playMusic(int musicId) {
        try {

            // volume
//            double vol = 0.5;
//            Synthesizer synth = MidiSystem.getSynthesizer();
//            MidiChannel[] channels = synth.getChannels();
//
//            for( int c = 0; c < channels.length; c++ ) {
//               if(channels[c] != null) {
//                   channels[c].controlChange( 7, (int)( vol*127) );
//               }
//            }
           
                    
            sequencer.setSequence(musics.get(musicId));
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
                         
        } 
        catch (Exception ex) {
            Logger.getLogger(Audio.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
