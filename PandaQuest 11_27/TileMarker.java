import greenfoot.*;

/**
 * TileMarker - Visual indicator for revealed tiles.
 * 
 * Displays the number of adjacent bamboo shoots for a revealed tile.
 * Used to give players information about nearby hazards.
 * 
 * @author PandaQuest Team
 * @version 1.0
 */
public class TileMarker extends Actor
{
    private int adjacentCount;
    
    /**
     * Constructor for TileMarker.
     * @param count Number of adjacent bamboo shoots
     */
    public TileMarker(int count)
    {
        this.adjacentCount = count;
        updateImage();
        getFlag();
    }
    
    /**
     * Update the visual appearance based on adjacent count.
     */
    private void updateImage()
    {
        GreenfootImage img = new GreenfootImage(50, 50);
        
        if (adjacentCount > 0) {
            img.setColor(Color.WHITE);
            img.fillRect(0, 0, 50, 50);
            img.setColor(Color.BLACK);
            img.drawRect(0, 0, 49, 49);
            
            Font font = new Font("Arial", true, false, 24);
            img.setFont(font);
            img.drawString(String.valueOf(adjacentCount), 18, 35);
        }
        else {
            // Empty tile
            img.setColor(new Color(200, 200, 200));
            img.fillRect(0, 0, 50, 50);
            img.setColor(Color.GRAY);
            img.drawRect(0, 0, 49, 49);
        }
        
        setImage(img);
    }
    
    /**
     * Act - do whatever the TileMarker wants to do.
     */
    public void act()
    {
        // TileMarker is static
    }
    public void getFlag()
    {
        if (Greenfoot.isKeyDown("enter"))
        {
            GreenfootImage img = new GreenfootImage(50,50);
            img.setColor(new Color(219,101,101));
            img.fillRect(0, 0, 50, 50);
            img.setColor(Color.GRAY);
            img.drawRect(0, 0, 49, 49);
            setImage(img);

        }
    }
}

