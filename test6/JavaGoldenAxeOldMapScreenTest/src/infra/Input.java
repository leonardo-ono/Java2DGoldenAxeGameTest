package infra;



import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Input class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class Input extends KeyAdapter {
    
    public static boolean[] keyPressed = new boolean[256];
    public static boolean[] keyPressedConsumed = new boolean[256];

    public static boolean isKeyPressed(int keyCode) {
        return keyPressed[keyCode];
    }
    
    public static boolean isKeyPressedOnce(int keyCode) {
        if (keyPressed[keyCode] && !keyPressedConsumed[keyCode]) {
            keyPressedConsumed[keyCode] = true;
            return true;
        }
        return false;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyPressed[e.getKeyCode()] = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyPressed[e.getKeyCode()] = false;
        keyPressedConsumed[e.getKeyCode()] = false;
    }
    
}
