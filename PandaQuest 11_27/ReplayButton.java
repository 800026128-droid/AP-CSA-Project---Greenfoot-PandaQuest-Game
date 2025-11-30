import greenfoot.*;

public class ReplayButton extends Actor
{
    private static final GreenfootImage NORMAL_IMAGE = new GreenfootImage("replayButton.png");
    private static final GreenfootImage HOVER_IMAGE = new GreenfootImage("replayButton.png");
    
    public ReplayButton()
    {
        // Set the initial image
        setImage(NORMAL_IMAGE);
    }

    public void act()
    {
        // 1. Handle the click action (same as before)
        if (Greenfoot.mouseClicked(this))
        {
            Levels.currentLevel -= 1;
            Greenfoot.setWorld(new PandaWorld());
            return; 
        }

        // 2. Handle the hover action: Switch images
        if (Greenfoot.mouseMoved(this))
        {
            // If the mouse is over the button, set the pre-faded image
            if (getImage() != HOVER_IMAGE) {
                setImage(HOVER_IMAGE);
            }
        }
        else
        {
            // If the mouse is NOT over the button, set the normal image
            if (getImage() != NORMAL_IMAGE) {
                setImage(NORMAL_IMAGE);
            }
        }
    }    
}