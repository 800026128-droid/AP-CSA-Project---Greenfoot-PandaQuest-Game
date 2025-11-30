import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class Levels here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class Levels extends Actor
{
    /**
     * Act - do whatever the Levels wants to do. This method is called whenever
     * the 'Act' or 'Run' button gets pressed in the environment.
     */
    public static int[] gridSizes = {8,10,12,14,16}; 
    public static int currentLevel = 1;
    public static int PlayWidth = gridSizes[currentLevel-1];
    public static int PlayHeight = PlayWidth + 2;
    public Levels()
    {
        
    }
    
    public void act()
    {
        // Add your action code here.
    }
    public static int getPLAY_WIDTH()
    {
        int PlayWidth = gridSizes[currentLevel-1];
        return PlayWidth;
    
    }
    public static int getWORLD_WIDTH()
    {
        int PlayHeight = PlayWidth + 2;
        return PlayHeight;
    }
    
}
