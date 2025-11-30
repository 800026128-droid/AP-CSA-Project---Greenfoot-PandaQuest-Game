import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and so forth)

public class StartScreen extends World
{
    // Background music for the start screen
    private GreenfootSound startMusic = 
        new GreenfootSound("Map (Night) - Tomodachi Life OST.mp3");

    // Constructor for objects of class StartScreen.
    public StartScreen()
    {    
        // Create a new world with 500x500 cells and a cell size of 1x1 pixels.
        super(500, 500, 1); 
        // The size should match the size of your PandaWorld for a smooth transition.

        startMusic.setVolume(70); // 0–100, adjust if too loud/quiet
        prepare();
    }

    /**
     * Called automatically when this world becomes the active world
     * (e.g. when you press Run, or when you Greenfoot.setWorld(new StartScreen()))
     */
    public void started()
    {
        startMusic.playLoop();
    }

    /**
     * Called when this world is no longer the active world
     * (e.g. scenario stopped or you switch to PandaWorld)
     */
    public void stopped()
    {
        startMusic.stop();
    }

    /**
     * Prepare the world for the start of the program.
     */
    private void prepare()
    {
        // Add the Start Button to the center of the screen
        StartButton startButton = new StartButton();
        addObject(startButton, getWidth()/2, 300); 

        // Optional: title / instructions via another Actor
    }
}
