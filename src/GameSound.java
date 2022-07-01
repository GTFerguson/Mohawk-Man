/*
 * This class is an example how complex stuff such as sound handling can be packaged away neatly 
 * in a a class and only simple methods such as "playClip", "stopClip", etc are exposed to the
 * user of the GameSound class.
 */
import java.io.*;                   // required for file I/O
import javax.sound.sampled.*;       // required for using the sound system
import java.io.File;
import java.io.IOException;

public class GameSound
{

    public static Clip loadSound(String s)
    {
        Clip clp=null;
        File soundFile;

        try
        {
            soundFile = new File(s);
        } catch (Exception e)
        {
            //e.printStackTrace();
            System.out.println("Could not find the sound file "+s);
            return clp;
        }
        // As always with File I/O we need 'try'-'catch' constructions just in case the file is not
        // there or has been spelled incorrectly.

        try
        {
            // Build the full path name to the sound clip
            //URL url = new URL(s); //this.getClass().getResource(s);
            // Open the audio file as an input stream (calls helper method 'loadStrea'm, coded below)
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream( soundFile );
            // Figure out which encoding format the sound file is using
            AudioFormat af = audioInputStream.getFormat();
            // Calculate the size of the clip
            int size = (int) (af.getFrameSize() * audioInputStream.getFrameLength());

            // Un-comment the line below to get some info on the clip.
            // System.out.println("\nLOADING:\nClip "+(num+1)+"= "+url+"\nSize= "+size+" bytes");

            // Provide storage space
            byte[] audio = new byte[size];
            // Now read into the storage space just provided
            audioInputStream.read(audio, 0, size);

            // Attach the audio data to a clip
            DataLine.Info info = new DataLine.Info(Clip.class, af, size);
            try
            {
                clp = (Clip) AudioSystem.getLine(info);
                clp.open(af, audio, 0, size);

            }
            catch(Exception e){
            }

        } catch(UnsupportedAudioFileException e)
        {
            System.out.println("Sorry, this file format is not supported by the GameSound class");
            return clp;
        } catch(IOException e)
        {
            System.out.println("Hmmm... can't find this file: "+s);
            return clp;
        }

        return clp;
    }

    // Helper method (called from 'loadSound' above).
    // Note that this is a 'private' method, i.e. not accessible from outside this class
    private ByteArrayInputStream loadStream(InputStream inputstream) throws IOException
    {
        ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
        byte data[] = new byte[1024];
        for(int i = inputstream.read(data); i != -1; i = inputstream.read(data))
        {
            bytearrayoutputstream.write(data, 0, i);
        }

        inputstream.close();
        bytearrayoutputstream.close();
        data = bytearrayoutputstream.toByteArray();
        return new ByteArrayInputStream(data);
    }

    // Play a sound. 
    public static void playSound( Clip clp ) 
    {
        try{
            clp.start();
        }
        catch(Exception e){
            System.out.println("GameSound class, playClip method: problem playing sound");
        }
    }

    // Rewinds a clip to the beginning
    public static void rewindSound( Clip clp ) 
    {

        try{
            clp.setFramePosition(0);  // rewind to beginning
        }
        catch(Exception e){
            System.out.println("GameSound class, rewindClip method: problem rewinding sound");

        }
    }

    // Play a clip repeatedly.
    public static void loopSound( Clip clp ) 
    {
        clp.start();
        clp.loop(Clip.LOOP_CONTINUOUSLY);
    }

    // Stop a sound. 
    public static void stopSound( Clip clp ) 
    {
        clp.stop();
    }

    // Set the volume of a clip. Range: 0 to 10
    public static void setVolume(Clip clp, int volume) 
    {
        if((volume>10)||(volume< 0)) 
        {
            System.out.println("GameSound class, setVolume method;");
            System.out.println("The volume has to be between 0 and 10.\nSetting to 5");
            volume=5;
        }

        // The "gain" in Java is really in "decibel" and defined between -80 (off) and 6 (full blast).
        // It is also logarithmic, wich is tricky to understand. Anything below -3 is too quiet to hear.
        // Therefore let's translate the volume (0 to 10) to something that is actually usable.        
        float gain;
        switch( volume )
        {
            case 0 : gain=-80.0f; break;
            case 1 : gain=-40.0f; break;
            case 2 : gain=-25.0f; break;
            case 3 : gain=-10.0f; break;
            case 4 : gain= -5.0f; break;
            case 5 : gain=  0.0f; break;
            case 6 : gain=  1.0f; break;
            case 7 : gain=  2.0f; break;
            case 8 : gain=  3.5f; break;
            case 9 : gain=  6.0f; break;
            case 10: gain=  6.0f; break;
            default: gain=  4.0f;
        }

        // Now set the gain
        FloatControl gainControl = (FloatControl) clp.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(gain);
    }
}