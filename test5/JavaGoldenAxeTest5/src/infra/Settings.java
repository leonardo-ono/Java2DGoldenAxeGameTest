package infra;

import javax.sound.sampled.AudioFormat;
import static javax.sound.sampled.AudioFormat.Encoding.PCM_UNSIGNED;

/**
 * (Project) Settings class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Settings {
    
    public static final long REFRESH_PERIOD = 1000000000 / 60;

    // --- display ---
    
    public static final int CANVAS_WIDTH = 276;
    public static final int CANVAS_HEIGHT = 207;

    public static final int PREFERRED_VIEWPORT_WIDTH = (int) (276 * 2.5);
    public static final int PREFERRED_VIEWPORT_HEIGHT = (int) (207 * 2.5);
    
    public static final double ASPECT_RATIO = CANVAS_WIDTH / CANVAS_HEIGHT;
    public static final boolean KEEP_ASPECT_RATIO = false;
    
    // --- resources ---
    
    public static final AudioFormat SOUND_AUDIO_FORMAT 
            = new AudioFormat(PCM_UNSIGNED, 11025, 8, 1, 1, 11025, true);
    
    public static final String RES_IMAGE_FILE_EXT = ".png";
    public static final String RES_SOUND_FILE_EXT = ".wav"; 
    public static final String RES_FONT_FILE_EXT = ".ttf"; 
    public static final String RES_LEVEL_FILE_EXT = ".txt"; 
    public static final String RES_TITLE_ANIMATION_FILE_EXT = ".txt"; 
    
    public static final String RES_IMAGE_PATH = "/res/image/";
    public static final String RES_SOUND_PATH = "/res/sound/";
    public static final String RES_FONT_PATH = "/res/font/";
    public static final String RES_LEVEL_PATH = "/res/level/";
    public static final String RES_TITLE_ANIMATION_PATH = "/res/title_animation/";
    
    // DEBUG
    
    public static boolean SHOW_COLLIDERS = true;
}
