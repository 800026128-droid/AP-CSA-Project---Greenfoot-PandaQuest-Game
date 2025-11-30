import greenfoot.*;

/**
 * Panda - The player character in PandaQuest.
 * 
 * The panda can move around the world and reveal tiles.
 * Use arrow keys to move and SPACE to reveal the current tile.
 * 
 * @author PandaQuest Team
 * @version 1.0
 */
public class Panda extends Actor
{
    private int moveDelay;
    private static final int MOVE_SPEED = 10;
    
    /**
     * Constructor for Panda.
     */
    public Panda()
    {
        moveDelay = 0;
    }
    
    /**
     * Act - do whatever the Panda wants to do.
     * This method is called whenever the 'Act' or 'Run' button gets pressed.
     */
    public void act()
    {
        if (moveDelay > 0) {
            moveDelay--;
            return;
        }
        
        handleMovement();
        handleAction();
    }
    
    /**
     * Handle player movement with arrow keys.
     */
    private void handleMovement()
    {
        int dx = 0;
        int dy = 0;
        
        if (Greenfoot.isKeyDown("up")) {
            dy = -1;
        }
        else if (Greenfoot.isKeyDown("down")) {
            dy = 1;
        }
        else if (Greenfoot.isKeyDown("left")) {
            dx = -1;
        }
        else if (Greenfoot.isKeyDown("right")) {
            dx = 1;
        }
        
        if (dx != 0 || dy != 0) {
            int newX = getX() + dx;
            int newY = getY() + dy;
            
            PandaWorld world = (PandaWorld) getWorld();
            if (world != null && newX >= 0 && newX < world.getWidth() && 
                newY >= 0 && newY < world.getHeight()) {
                setLocation(newX, newY);
                moveDelay = MOVE_SPEED;
            }
        }
    }
    
    /**
     * Handle player actions (revealing tiles).
     */
    private void handleAction()
    {
        if (Greenfoot.isKeyDown("space")) {
            PandaWorld world = (PandaWorld) getWorld();
            if (world != null) {
                int x = getX();
                int y = getY();
                
                if (!world.isRevealed(x, y)) {
                    boolean hitBamboo = world.revealTile(x, y);
                    
                    if (!hitBamboo) {
                        // Show tile info
                        int adjacent = world.getAdjacentBambooCount(x, y);
                        if (adjacent > 0) {
                            // Could add a visual indicator here
                        }
                    }
                }
            }
            moveDelay = MOVE_SPEED;
        }
        if (Greenfoot.isKeyDown("a")){
            PandaWorld world = (PandaWorld) getWorld();
            if (world != null) {
                int x = getX();
                int y = getY();
                
                if (!world.isRevealed(x, y)) {
                    world.addFlag(x, y);
    
                }
            }
        }
        if (Greenfoot.isKeyDown("b")){
            PandaWorld world = (PandaWorld) getWorld();
            if (world != null) {
                int x = getX();
                int y = getY();
                
                if (!world.isRevealed(x, y)) {
                    world.removeFlag(x, y);
    
                }
            }
        }
        }
    }
