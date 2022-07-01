/* 
 * 
 * This example program shows how to do timer-driven animation.
 * The program avoids OO features as much as possible.
 * 
 * It demonstrates:
 *   - how to initialise the keyboard
 *   - how to read in normal keys
 *   - how to read in special keys (arrow keys, 'f' keys, ESc, etc.)
 *   - how to move graphic objects around using the keyboard
 */

// First import all the functionality wee need
import javax.swing.JFrame;      // window functionality

import java.awt.Color;          // RGB color stuff
import java.awt.Image;

import javax.swing.Timer;       // import timer functionality

import java.awt.event.*;        // functionality for the event fired by the timer
import java.awt.*;              // Graphics stuff from the AWT library, e.g. Graphics

import javax.swing.*;           // Graphics stuff from the Swing library, e.g. JLabel

import java.awt.image.BufferedImage; // Graphics drawing canvass

import javax.sound.sampled.*;       // required for using the sound system

public class GameMain
{
    // The window in which everything happens
    private static JFrame window;
    // A "handle" for graphics capabilities
    private static Graphics gr;
    // IMAGES
    private static Image GI_IS1;			//Intro stills
    private static Image GI_IS2;
    private static Image GI_IS3;
    private static Image GI_IS4;
    private static Image GI_TitleScreen;
    private static Image GI_TitleScreen2;
    private static Image GI_Background; 	// Planet Xantharrs surface
    private static Image GI_MohawkMan;		// Player Sprite
    private static Image GI_Bang;       	// Animated GIF of an explosion
    private static Image GI_Flame;
    private static Image GI_Hairspray;
    private static Image GI_LandingPad;
    private static Image GI_ScoreBox;
    private static Image GI_ScoreBox2;
    // SOUNDS
    private static Clip GS_MohawkJam;
    private static Clip GS_GameOver;
    private static Clip GS_Rocket;
    private static Clip GS_GZShout;
    // GAME VARIABLES
    private static double landerX=200, landerY=200; // starting position of the lunar lander
    private static boolean gameOver=false;
    // Velocity
    private static double velocityY=0;
    private static double velocityX=0;
    private static double mass=10;
    private static double gravity=0.02;
    // Fuel
    private static double fuel=500;
    private static double fuelMax=2000;
    // Time & Score variables
    private static double time=0;
    private static double introTimer=0;
    private static double speechTimer=0;
    private static int score=0, hsX = 0, hsY = 0, lpX= 0, lpY=0, mpCounter = 0;
    private static int shoutCount = 0;
    // Various Bools
    private static boolean pickupHS=false, landingPLoaded=false, startPressed = false, introPlaying = true;
    // Jetpack flames
    private static boolean flameLeft = false, flameRight = false;
  
    public static void main(String[] args)
    {
        window = new JFrame();
        
        // Create a window on screen
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set position of the window on screen
        window.setLocation(450,10);
        // Add caption
        window.setTitle("Mohawk Man!");
        // Set window size
        window.setSize(1280, 720);
        // User is not allowed to resize this window
        window.setResizable(false);

        // In the window create a canvas of size 400x400 on which to draw in R,G,B colour (with alpha transparency channel)
        BufferedImage canvas=new BufferedImage(1280,720,BufferedImage.TYPE_INT_ARGB);
        // Create a "handle" for the canvas which we can use for drawing. 
        gr=canvas.getGraphics();
        // Drawing actually takes place on a "label", so create it
        JLabel label=new JLabel(new ImageIcon(canvas));
        window.add(label);
        // Show it
        window.setVisible(true);

        // Load the images we need
        // Call the 'loadImage' method implemented in the 'GameImage' class
        GI_IS1 = GameImage.loadImage("Images//IS_Stills//IS_Still1.png");
        GI_IS2 = GameImage.loadImage("Images//IS_Stills//IS_Still2.png");
        GI_IS3 = GameImage.loadImage("Images//IS_Stills//IS_Still3.png");
        GI_IS4 = GameImage.loadImage("Images//IS_Stills//IS_Still4.png");
        GI_Background = GameImage.loadImage("Images//background.jpg");
        GI_TitleScreen = GameImage.loadImage("Images//TitleScreen.png");
        GI_TitleScreen2 = GameImage.loadImage("Images//TitleScreen2.png");
        GI_MohawkMan = GameImage.loadImage("Images//mohawkman.png");
        GI_Bang   = GameImage.loadImage("Images//explosion.gif");
        GI_Flame = GameImage.loadImage("Images//animated-flame.gif");
        GI_Hairspray = GameImage.loadImage("Images//hairspray.png");
        GI_LandingPad = GameImage.loadImage("Images//landingpad.jpg");
        GI_ScoreBox = GameImage.loadImage("Images//GUI//ScoreBox.png");
        GI_ScoreBox2 = GameImage.loadImage("Images//GUI//ScoreBox2.png");
        
        // Load the Sounds
        GS_MohawkJam = GameSound.loadSound("Sounds//Chronox_-_04_-_Juno.wav");
        GS_GameOver  = GameSound.loadSound("Sounds//gameover.wav");
        GS_Rocket  = GameSound.loadSound("Sounds//csharpharmonic.wav"); 
        GS_GZShout = GameSound.loadSound("Sounds//262292__darkzanite__monster-roar.wav");

        // Start playing background music
        GameSound.setVolume(GS_MohawkJam, 8); //volume=2 (goes from 0 to 10)
        GameSound.loopSound(GS_MohawkJam);
        
        // Setup the keyboard
        GameKeyboard.initialise();

        // Setup the Timer that will trigger a screen update
        // Create a Timer. Every 25 ms the timer will call the 'actionPerformed()' method 
        // implemented just below the main method. 
        ActionListener taskPerformer = new ActionListener()      
            {
                public void actionPerformed(ActionEvent evt)
                {
                    doTimerAction();
                }
            };
        Timer t = new Timer(25, taskPerformer);
        t.start();
    }
    
    // This method is called by the timer repeatedly. Note that all drawing actions are performed
    // to a memory buffer. Only when all drawing actions are complete is the buffer committed to the screen
    // by calling the 'repaint' method at the end. This avoids update flicker on screen.
    private static void doTimerAction ()
    {
       
        // Set the text colour, font and size
        gr.setColor (Color.white); 
        gr.setFont (new Font("Arial Black", Font.PLAIN, 24));
        
        // Get keys pressed from our home-brew GameKeyboard class
        getInput ();
        
        //MENUS
        if (introPlaying)
        {
        	playIntro();	
        }
        else if (!startPressed)
        {	
            startScreen ();
        }
        else
        {    
        	 // Overwrite everything that was on screen before with the background image
            gr.drawImage (GI_Background, 0, 0, null);
        	playGame ();
        }
        // Now that everything has been "painted" to memory, blast it onto the screen
        window.repaint();       
    } // end of doTimerAction method
    
    //Company Intro
    static void playIntro()
    {
    	introTimer=introTimer+(0.25*0.25);
    	if (introTimer < 2)
    	{
    		gr.drawImage(GI_IS1,0, 0, null);
    	}
    	else if (introTimer < 4)
    	{
    		gr.drawImage(GI_IS2,0, 0, null);	
    	}
    	else if (introTimer < 6)
    	{
    		gr.drawImage(GI_IS3,0, 0, null);
    	}
   		else if (introTimer < 8)
   		{
   			gr.drawImage(GI_IS4,0, 0, null);
   		}
   		else if (introTimer < 15)
   		{
   			gr.drawImage(GI_TitleScreen,0, 0, null);
   		}
   		else if (introTimer >= 20)
   		{
   			introPlaying = false;
   		}
    }
    
    static void startScreen ()
    {
        if (!startPressed)
        {
            gr.drawImage (GI_TitleScreen2, 0, 0, null);
            getInput ();
        }
        else
        {
             
        }
    }
    
    static void playGame()
    {
    	gr.drawImage (GI_ScoreBox, 0, 0, null);
        gr.drawString ("Fuel level="+fuel ,10,40);
        gr.drawImage (GI_ScoreBox2, window.getWidth()-GI_ScoreBox2.getWidth(null), 0, null);
        gr.drawString ("Time="+time ,1050,40);
        boundsCheck ();           
        loadHairspray ();
        loadLandingP ();
        hairsprayHit ();
        jpfHandling ();
        godzillaShouts ();
        if ((collided(GI_MohawkMan, GI_LandingPad, (int)landerX, (int)landerY, lpX, lpY)))
        {
        	score= (int)(time* mpCounter);
            gr.setFont(new Font("Arial Black", Font.BOLD, 60));
            gr.setColor( Color.white ); 
            gr.drawString("FINAL SCORE="+score, 350, 400);
            gameOver=true;
        }
        if(gameOver==true)
        {           
            godzillaSays("MOHAWK DOWN!");
            playGZShout();
        }
        else
        {
        	//GAME IS HERE
            mass=4+(fuel/500);  //Mass is calculated here
            velocityY=velocityY+(gravity*mass); //this represents gravity and takes into account the mass of fuel
            landerY=landerY+velocityY;
            landerX=landerX+velocityX;
            gr.drawImage(GI_MohawkMan, (int)landerX, (int)landerY, null);
            time=time+(0.25*0.25);    
        }
    }
    
    //Handles jetpack flames
    static void jpfHandling()
    {
    	if(flameLeft)
    	{
    		gr.drawImage(GI_Flame, (int)landerX+29, (int)landerY +GI_MohawkMan.getHeight(null)-60, null);    
    	}
    	
    	if(flameRight)
    	{
    		gr.drawImage(GI_Flame, (int)landerX+4, (int)landerY +GI_MohawkMan.getHeight(null)-60, null);
    	}
    }
    
	  //--------------------//
     //  Text Handling     //
    //--------------------//

    static void godzillaShouts()
    {
    	if(time>40 && shoutCount==0)
    	{
    		godzillaSays("Oh yeah!!");
    		speechTimer();

    	}
    	else if(time>60 && shoutCount==1)
    	{
    		godzillaSays("More, human! MORE!!!");
    		speechTimer();
    	}
    	else if(time>100 && shoutCount==2)
    	{
    		godzillaSays("Awesome airtime!!");
    		speechTimer();
    	}
    	else if(time>150 && shoutCount==3)
    	{
    		godzillaSays("Is it possible...");
    		speechTimer();
    	}
    	else if(time>200 && shoutCount==4)
    	{
    		godzillaSays("Such raw radicality...");
    		speechTimer();
    	}
    	else if(time>210 && shoutCount==5)
    	{
    		godzillaSays("You are the chosen one!!");
    		speechTimer();
    	}
    }
    
    static void speechTimer()
    {
		speechTimer=speechTimer+(0.25*0.25);
		if(speechTimer>5)
		{
			speechTimer = 0;
			shoutCount++;
		}
    }
    
    static void godzillaSays(String speech)
    {
	   gr.setFont(new Font("Arial", Font.BOLD, 40));
       gr.setColor( new Color(255, 255, 255) ); // a light red
	   gr.drawString(speech, 160,200);
	}
    
	 //--------------------//
    //  Sound Functions   //
   //--------------------//
   
    static void playGZShout()
    {
    	GameSound.setVolume(GS_GZShout, 8);
    	GameSound.playSound(GS_GZShout);
    	//http://www.freesound.org/people/darkzanite/sounds/262292/
    }
   
    static void playRocketSound()
    {
        GameSound.setVolume(GS_Rocket, 3);
        GameSound.playSound(GS_Rocket);
        GameSound.rewindSound(GS_Rocket);
    }

    static void playExplosionSound()
    {
	    GameSound.setVolume(GS_GameOver, 8);
        GameSound.playSound(GS_GameOver);
    }

	 //--------------------//
    //Collision Detection //
   //--------------------//
   public static void boundsCheck()
   {
        if(landerX <-59)
        {
            landerX = 1279;
        }
        if(landerX > 1280)
        {
            landerX = 0;
        }
        if((landerY < -127)||(landerY >720))
        {
            gameOver = true;
            playExplosionSound();
        }
    }
   
   public static void loadHairspray()
   {
       if(!pickupHS)
       {
           if(fuel < 450)
           {
               hsX = 20 + ((int) (Math.random() * window.getWidth()));
               hsY = ((int) (Math.random() * (window.getHeight() - GI_Hairspray.getHeight(null))));
               pickupHS = true;
           }
       }
       if(pickupHS)
       {
           gr.drawImage(GI_Hairspray,hsX,hsY, null);
       }
   }
   
   public static void loadLandingP()
   {
       if(!landingPLoaded)
       {
           if(time > 45)
           {
               lpX = 20 + ((int) (Math.random() * window.getWidth()));
               lpY = (window.getHeight() - GI_LandingPad.getHeight(null)) - 20;
               landingPLoaded = true;
           }
       }
       if(landingPLoaded)
       {
         gr.drawImage(GI_LandingPad,lpX,lpY, null);
       }
   }
    
   static void hairsprayHit()
   {
       if(collided(GI_MohawkMan, GI_Hairspray, (int)landerX, (int)landerY, hsX, hsY))
       {
           if(fuel<fuelMax)hsPickedUp();
       }
   }
    
   static boolean collided (Image a, Image b, int ax, int ay, int bx, int by)
   {
	   int aw = a.getWidth(null); // a width
	   int bw = b.getWidth(null); // b width
	   int ah = a.getHeight(null); // a height
	   int bh = b.getHeight(null); // b height
	   boolean colX = false, colY = false;
    
	   if((ax + aw) >= (bx) && (bx+bw) >= (ax))
	   {
		   colX = true;
	   }
	   if((ay + ah) > (by) && (by+bh) > ay)
	   {
		   colY = true;
	   }   
    
	   if(colX && colY)
	   {
		   return true;
	   }
	   else
	   {
		   return false;
	   }
	}
   
   static void hsPickedUp()
   {
	   pickupHS = false;
	   hsX = -150;
	   hsY = -150;
	   fuel += 100;
       mpCounter++;
   }
   
 	 //-------------------//
    //   User Input      //
   //-------------------//    
   static void reset()
   {
	   fuel=500; 
	   landerX=200;
	   landerY=200;
	   velocityX=0;
	   velocityY=0;
	   time = 0;          
	   hsX = 0;
	   hsY = 0;
	   lpX = 0;
	   mpCounter = 0;
	   gameOver=false;
	   pickupHS=false;
	   landingPLoaded = false;
	   startPressed = false;
	   shoutCount = 0;
   }
    
   static void getInput()
   {
	   char key= GameKeyboard.getKey();
	   int  specialKey=GameKeyboard.getSpecialKey();
	   
	   // Special keys are the arrow keys, ESC, Backspace, the 'F' keys, 
	   if(specialKey == 38)	// 38: the 'up' arrow key
	   {  
		   if(fuel>0)
		   {
			   velocityY=velocityY-0.2; 
			   playRocketSound();
			   flameRight = true;
			   flameLeft = true;
			   fuel=fuel-1;
		   }
	   }
        
	   //These will use the left and right arrows to create velocity on the X plane
	   if(specialKey == 37)
	   {
		   if(fuel>0)
		   {
			   velocityX=velocityX-0.2; 
			   playRocketSound();
			   flameLeft = true;
			   flameRight = false;
			   fuel=fuel-0.5;
		   }
	   }
	   else if(specialKey == 39)
       {
		   if(fuel>0)
		   {
			   velocityX=velocityX+0.2; 
			   playRocketSound();  
			   flameRight = true;
			   flameLeft = false;
			   fuel=fuel-0.5;
		   }
       }
	   else
	   {
		   if(specialKey != 38)
		   {
			   flameRight = false;
			   flameLeft = false;
		   }
	   }
        
       //Reset key
       if((key=='r')||(key=='R')) reset();
        
       //Quit key
       if(key == 'q') System.exit(0);
        
       //Start key
       if(key == 's')
       {
    	   //If start has not been pressed set to true
           if(!introPlaying&&!startPressed) startPressed = true;
       }
   }
} // End of class