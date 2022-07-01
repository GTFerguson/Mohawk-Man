/*
 *	Encapsulates the loading of images only.
 *	Access from other classes: 
 *     Image myImg = GameImage.loadImage("C://MyImage.gif");
 */
import java.awt.*;              // Graphics stuff from the AWT library, here: Image
import java.io.File;            // File I/O functionality (for loading an image)
import javax.swing.ImageIcon;   // All images are used as "icons"

public class GameImage
{
    public static Image loadImage(String imagePathName)
    {   
        // All images are loaded as "icons"
        ImageIcon i = null;
        // Try to load the image
        File f = new File(imagePathName);
        // If file exists assign the image to the "icon"
        if(f.exists())
        {
            i = new ImageIcon(imagePathName);
        }
        else
        {
            System.out.println("\nCould not find this image: "+imagePathName+"\nAre file name and/or path to the file correct?");            
            System.exit(0);
        }
        // Done. Either return the image or "null"
        return i.getImage();
    }// End of loadImages method
}
