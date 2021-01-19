package infra;

import java.awt.event.KeyEvent;

/**
 * PlayerControl class.
 * 
 * @author Leonardo Ono (ono.leo80@gmail.com)
 */
public class PlayerControl {
    
    public int up;
    public int down;
    public int left;
    public int right;
    public int attack;
    public int jump;
    public int kick;
    public int hit;
    public int knockDown;
    public int runLeft;
    public int runRight;

    public PlayerControl(int d) {
        if (d == 1) {
            setDefault1();
        }
        else if (d == 2) {
            setDefault2();
        }
        else if (d == 3) {
            setDefault3();
        }
    }

    public void setDefault1() {
        up = KeyEvent.VK_UP;
        down = KeyEvent.VK_DOWN;
        left = KeyEvent.VK_LEFT;
        right = KeyEvent.VK_RIGHT;
        attack = KeyEvent.VK_O;
        jump = KeyEvent.VK_P;
        kick = KeyEvent.VK_0;
        hit = KeyEvent.VK_0;
        knockDown = KeyEvent.VK_0;
        runLeft = KeyEvent.VK_Q;
        runRight = KeyEvent.VK_W;
    }

    public void setDefault2() {
        up = KeyEvent.VK_R;
        down = KeyEvent.VK_F;
        left = KeyEvent.VK_D;
        right = KeyEvent.VK_G;
        attack = KeyEvent.VK_A;
        jump = KeyEvent.VK_S;
        kick = KeyEvent.VK_0;
        hit = KeyEvent.VK_0;
        knockDown = KeyEvent.VK_0;
    }

    public void setDefault3() {
        up = KeyEvent.VK_U;
        down = KeyEvent.VK_J;
        left = KeyEvent.VK_H;
        right = KeyEvent.VK_K;
        attack = KeyEvent.VK_T;
        jump = KeyEvent.VK_Y;
        kick = KeyEvent.VK_0;
        hit = KeyEvent.VK_0;
        knockDown = KeyEvent.VK_0;
    }
    
}
