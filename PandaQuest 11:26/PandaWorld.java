import greenfoot.*;
/**
 * PandaWorld - The main game world for PandaQuest.
 *
 * A Greenfoot adaptation of the PandaQuest puzzle game.
 * Players control a panda navigating through a grid-based world,
 * collecting items and avoiding bamboo obstacles.
 *
 * @author PandaQuest Team
 * @version 1.0
 */
public class PandaWorld extends World
{
    private static final int CELL_SIZE = 50;

    // Playable grid size (kept as original 8x8)
    private static int PLAY_WIDTH = Levels.currentLevel * 2 + 6;
    private static int PLAY_HEIGHT = Levels.currentLevel * 2 + 6;

    // Frame width (in cells) around the playable board where status is shown
    private static int FRAME_SIZE = 1;

    // Actual world size includes frame on all sides
    private static int WORLD_WIDTH = PLAY_WIDTH + 2;
    private static int WORLD_HEIGHT = PLAY_WIDTH + 2;

    private int level = Levels.currentLevel;
    private int lives;
    private int score;
    // Game state arrays use PLAY_* sizes (indices are in playable coordinates 0..PLAY_WIDTH-1)
    private boolean[][] bambooGrid;
    private boolean[][] revealedGrid;

    /**
     * Constructor for PandaWorld.
     * Creates a new world with the specified grid dimensions.
     */
    public PandaWorld()
    {
        // Create world larger than the playable grid so we can draw a frame around it
        super(Levels.currentLevel *2 + 8, Levels.currentLevel *2 + 8, CELL_SIZE);
        level = Levels.currentLevel;
        lives = 3;
        score = 0;
        PLAY_WIDTH = Levels.currentLevel * 2 + 6;
        PLAY_HEIGHT = Levels.currentLevel * 2 + 6;
        WORLD_WIDTH = PLAY_WIDTH + 2;
        WORLD_HEIGHT = PLAY_WIDTH + 2;
        // In your World subclass constructor or setup method
        initializeGame();
        prepare();
        drawFrame();
        setPaintOrder(Panda.class, Bamboo.class, TileMarker.class);
    }
    // In your World subclass constructor or setup method
    /**
     * Initialize the game state.
     */
    private void initializeGame()
    {
        bambooGrid = new boolean[PLAY_WIDTH][PLAY_HEIGHT];
        revealedGrid = new boolean[PLAY_WIDTH][PLAY_HEIGHT];

        // Place bamboo randomly (uses playable dimensions)
        int bambooCount = 5 + (level * 3);
        placeBambooRandomly(bambooCount);
    }

    /**
     * Place bamboo randomly on the playable grid.
     */
    private void placeBambooRandomly(int count)
    {
        int placed = 0;
        while (placed < count) {
            int x = Greenfoot.getRandomNumber(PLAY_WIDTH);
            int y = Greenfoot.getRandomNumber(PLAY_HEIGHT);

            if (!bambooGrid[x][y]) {
                bambooGrid[x][y] = true;
                placed++;
            }
        }
    }

    /**
     * Prepare the world for the start of the program.
     * Add initial actors to the world.
     */
    private void prepare()
    {
        // Add panda at starting position inside the frame (playable origin is offset by FRAME_SIZE)
        Panda panda = new Panda();
        addObject(panda, FRAME_SIZE, FRAME_SIZE);
        // Display game info inside the top frame row (centered)
        showText("Level: " + level + " Lives: " + lives + " Score: " + score,
                 WORLD_WIDTH / 2, 0);
    }

    /**
     * Draw a visible frame around the playable board (for status text).
     */
    private void drawFrame()
    {
        GreenfootImage bg = getBackground();
        if (bg == null) return;

        bg.setColor(Color.BLACK);
        int left = FRAME_SIZE * CELL_SIZE;
        int top = FRAME_SIZE * CELL_SIZE;
        int width = PLAY_WIDTH * CELL_SIZE;
        int height = PLAY_HEIGHT * CELL_SIZE;

        // Draw outer rectangle around playable area
        bg.drawRect(left, top, width - 1, height - 1);

        // Optionally draw a thin inner rectangle for a clearer frame (subtle)
        bg.setColor(Color.BLACK);
        bg.drawRect(left + 2, top + 2, width - 5, height - 5);
    }

    /**
     * World act method - ensure Panda cannot move into the frame cells.
     */
    public void act()
    {
        // Clamp any Panda instances to stay within playable area
        for (Object obj : getObjects(Panda.class)) {
            Panda p = (Panda)obj;
            int px = p.getX();
            int py = p.getY();

            int minX = FRAME_SIZE;
            int minY = FRAME_SIZE;
            int maxX = FRAME_SIZE + PLAY_WIDTH - 1;
            int maxY = FRAME_SIZE + PLAY_HEIGHT - 1;

            boolean moved = false;
            int newX = px;
            int newY = py;
            if (px < minX) { newX = minX; moved = true; }
            if (py < minY) { newY = minY; moved = true; }
            if (px > maxX) { newX = maxX; moved = true; }
            if (py > maxY) { newY = maxY; moved = true; }

            if (moved) {
                p.setLocation(newX, newY);
            }
        }
    }

    /**
     * Reveal a tile at the specified world position.
     * Note: x,y parameters are expected to be world coordinates. They are mapped to playable coordinates internally.
     */
    public boolean revealTile(int worldX, int worldY)
    {
        int x = worldToPlayX(worldX);
        int y = worldToPlayY(worldY);

        if (x < 0 || x >= PLAY_WIDTH || y < 0 || y >= PLAY_HEIGHT) {
            return false;
        }

        if (revealedGrid[x][y]) {
            return false;
        }

        revealedGrid[x][y] = true;

        if (bambooGrid[x][y]) {
            // Hit bamboo - lose a life
            lives--;
            addObject(new Bamboo(), worldX, worldY); // show bamboo visually at world coords
            updateDisplay();

            if (lives <= 0) {
                gameOver();
            }
            return true;
        }

        // Count adjacent bamboo (playable coords)
        int adjacentBamboo = countAdjacentBamboo(x, y);

        // Add visual marker for this tile at world coords
        TileMarker marker = new TileMarker(adjacentBamboo);
        addObject(marker, worldX, worldY);
        
    

    

        // Auto-reveal if no adjacent bamboo
        if (adjacentBamboo == 0) {
            autoRevealAdjacent(x, y);
        }

        score += 10;
        updateDisplay();

        // Check if level is complete
        if (isLevelComplete()) {
            levelComplete();
        }

        return false;
    }
    public void addFlag(int worldX, int worldY)
    {
        FlagMarker flag = new FlagMarker();
        addObject(flag, worldX, worldY);
        updateDisplay();
    }
    public void removeFlag(int worldX, int worldY)
    {
        removeObjects(getObjectsAt(worldX, worldY, FlagMarker.class));
        updateDisplay();
    }
    /**
     * Count bamboo in adjacent playable tiles (x,y are playable coords).
     */
    private int countAdjacentBamboo(int x, int y)
    {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < bambooGrid.length && newY >= 0 && newY < bambooGrid[0].length)
                    if (bambooGrid[newX][newY]) {
                        count++;
                }
            }
        }
        return count;
    }

    /**
     * Auto-reveal adjacent tiles recursively. Input coordinates are playable coords.
     */
    private void autoRevealAdjacent(int x, int y)
    {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) continue;
                int newX = x + i;
                int newY = y + j;
                if (newX >= 0 && newX < PLAY_WIDTH &&
                    newY >= 0 && newY < PLAY_HEIGHT &&
                    !revealedGrid[newX][newY] && !bambooGrid[newX][newY]) {

                    revealedGrid[newX][newY] = true;

                    // Add visual marker at corresponding world coords
                    int adjacentBamboo = countAdjacentBamboo(newX, newY);
                    TileMarker marker = new TileMarker(adjacentBamboo);
                    addObject(marker, playToWorldX(newX), playToWorldY(newY));

                    // Recurse if still zero
                    if (adjacentBamboo == 0) {
                        autoRevealAdjacent(newX, newY);
                    }
                }
            }
        }
    }

    /**
     * Check if the current level is complete.
     */
    private boolean isLevelComplete()
    {
        for (int x = 0; x < PLAY_WIDTH; x++) {
            for (int y = 0; y < PLAY_HEIGHT; y++) {
                if (!bambooGrid[x][y] && !revealedGrid[x][y]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Handle level completion.
     */
    private void levelComplete()
    {
        level++;
        showText("Level " + (level - 1) + " Complete! Press SPACE for next level",
                 WORLD_WIDTH / 2, 0);
        Levels.currentLevel += 1;
        Greenfoot.setWorld(new PandaWorld());
    }

    /**
     * Handle game over.
     */
    private void gameOver()
    {
        showText("Game Over! Final Score: " + score,
                 WORLD_WIDTH / 2, WORLD_HEIGHT / 2);
        Greenfoot.setWorld(new PandaWorld());
    }

    /**
     * Update the display with current game state (placed in top frame row).
     */
    private void updateDisplay()
    {
        showText("Level: " + level + " Lives: " + lives + " Score: " + score,
                 WORLD_WIDTH / 2, 0);
    }

    /**
     * Check if a world position (world coords) is revealed.
     */
    public boolean isRevealed(int worldX, int worldY)
    {
        int x = worldToPlayX(worldX);
        int y = worldToPlayY(worldY);

        if (x < 0 || x >= PLAY_WIDTH || y < 0 || y >= PLAY_HEIGHT) {
            return false;
        }
        return revealedGrid[x][y];
    }

    /**
     * Get the number of adjacent bamboo for a world-position.
     */
    public int getAdjacentBambooCount(int worldX, int worldY)
    {
        int x = worldToPlayX(worldX);
        int y = worldToPlayY(worldY);
        if (x < 0 || x >= PLAY_WIDTH || y < 0 || y >= PLAY_HEIGHT) {
            return 0;
        }
        return countAdjacentBamboo(x, y);
    }

    /**
     * Helper: convert world X coordinate to playable grid X coordinate.
     */
    private int worldToPlayX(int worldX)
    {
        return worldX - FRAME_SIZE;
    }

    /**
     * Helper: convert world Y coordinate to playable grid Y coordinate.
     */
    private int worldToPlayY(int worldY)
    {
        return worldY - FRAME_SIZE;
    }

    /**
     * Helper: convert playable X to world X.
     */
    private int playToWorldX(int playX)
    {
        return playX + FRAME_SIZE;
    }

    /**
     * Helper: convert playable Y to world Y.
     */
    private int playToWorldY(int playY)
    {
        return playY + FRAME_SIZE;
    }
}
