/* 
 * This class implements a simple keyboard event dispatcher. Normally a Java application attaches so
 * called 'KeyListeners' to individual objects such as text input fields. In a game, however, we want
 * all parts of the game window to respond to keyboard inputs without having to click on them first.
 * We also don't want the repetitive task of attaching a 'KeyListenr' to each and every object that
 * may appear in the game.
 * The drawback of this apprach is that it is now tricky to use a standard text input field - alas,
 * that is probably not really what we want anyway.
 * 
 * USAGE:
 * ======
 * In the game class just add this line:
 *         GameKeyboard.initialise();
 *         
 * You can then interrogate two variables: 'key' and 'specialKey' like this:
 *         if(GameKeyboard.getKey() == 'q') {  ...
 *         if(GameKeyboard.getSpecialKey() == 38) { // 'up' arrow key is 38, down is 40, ...
 */
import java.awt.*;              // Graphics stuff from the AWT library, her for the KeyEventDispatcher
import java.awt.event.*;        // Event handler, here for keyboard events

public class GameKeyboard implements KeyEventDispatcher
{
    // The variable 'key' and 'specialKey' are private but can be accessed via the public
    // 'get' methods 'getKey()' and 'getSpecialKey()'. These are coded below.
    // These can be interrogated from the main application
    private static char key=' ';
    private static int  specialKey=0;

    // This constant is public so that other classes can check if a key has been pressed.
    // e.g.  if(kbd.getKey() != kbd.NONE) {...
    public final static int NONE=0;

    private static GameKeyboard kbd;

    // Attach a keyboard focus manager to the keyboard event dispatcher
    // Don't worry if you don't understand this. Just leave it as it is.
    public GameKeyboard()
    {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        manager.addKeyEventDispatcher( this );    
    }
    // This is the method that actually checks the keyboard for key strokes and sets the two
    // private variables of this class ('key' and 'specialKey') to its values accordingly.
    public boolean dispatchKeyEvent(KeyEvent e)
    {
        // To start with, let's reset both variables
        key= NONE;
        specialKey= NONE;

        // Read the special keys (e.g. arrow keys, Shift, Alt, ...)
        specialKey= e.getKeyCode();

        // Only if no special character was pressed, check for normal keys, otherwise you
        // get the scan codes of the special keys
        if(specialKey == NONE)
        {
            key = e.getKeyChar();
        }

        // If a key is going up, reset the two variables.
        if (e.getID() == KeyEvent.KEY_RELEASED)
        {
            key= NONE;
            specialKey= NONE;
        } 

        return false;
    }

    public static void initialise() {
        GameKeyboard kbd= new GameKeyboard();
    }

    public static char getKey() {
        return key;
    }

    public static int getSpecialKey() {
        return specialKey;
    }
    
}