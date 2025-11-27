import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and so forth)

public class StartScreen extends World
{
    // Constructor for objects of class StartScreen.
    public StartScreen()
    {    
        // Create a new world with 600x400 cells and a cell size of 1x1 pixels.
        super(500, 500, 1); 
        // The size should match the size of your PandaWorld for a smooth transition.

        prepare();
    }

    /**
     * Prepare the world for the start of the program.
     */
    private void prepare()
    {
        // Add the Start Button to the center of the screen
        StartButton startButton = new StartButton();
        addObject(startButton, getWidth()/2, 300); 

        // Optional: Add a title or instructions
        // (You'd usually use a custom Actor with a text image for a title)
    }
}