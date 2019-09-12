package byow.TileEngine;

import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import byow.Core.Engine;
import java.util.Map;
import java.util.HashMap;


/**
 * Utility class for rendering tiles. You do not need to modify this file. You're welcome
 * to, but be careful. We strongly recommend getting everything else working before
 * messing with this renderer, unless you're trying to do something fancy like
 * allowing scrolling of the screen or tracking the avatar or something similar.
 */
public class TERenderer {
    private static final int TILE_SIZE = 16;
    private int width;
    private int height;
    private int xOffset;
    private int yOffset;
    private String biome;
    private static TETile hover;
    private static String currentTile = "Floor"; //Avatar always spawns on a floor
    private static TETile[][] currentWorld;
    private Map<String, String> flavor;

    /**
     * Same functionality as the other initialization method. The only difference is that the xOff
     * and yOff parameters will change where the renderFrame method starts drawing. For example,
     * if you select w = 60, h = 30, xOff = 3, yOff = 4 and then call renderFrame with a
     * TETile[50][25] array, the renderer will leave 3 tiles blank on the left, 7 tiles blank
     * on the right, 4 tiles blank on the bottom, and 1 tile blank on the top.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h, int xOff, int yOff) {
        this.width = w;
        this.height = h;
        this.xOffset = xOff;
        this.yOffset = yOff;
        StdDraw.setCanvasSize(width * TILE_SIZE, height * TILE_SIZE);
        Font font = new Font("Monaco", Font.BOLD, TILE_SIZE - 2);
        StdDraw.setFont(font);      
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.clear(new Color(0, 0, 0));
        biome = "";
        hover = Tileset.AVATAR_A_3;
        currentWorld = new TETile[w][h];
        flavor = new HashMap<String, String>();
        flavor.put("EmptyA", "Ah.. the blue sea. We can see that the sea is blue,"
                        + " yes, very blue.");
        flavor.put("FloorA", "This is green grass. Legends say that the grass is… green.");
        flavor.put("WallA", "Hm… the bush seems very bushy. I don’t think I can walk through "
                + "it.");
        flavor.put("AvatarA", "Hey! It’s not nice to poke me with the mouse.");
        flavor.put("TrapA", "Seems like Haunter is asleep, best not wake him up!");
        flavor.put("FlagA", "HELP IM LOST :(");

        flavor.put("EmptyB", "Brrrr! When’d it get so chilly? I wonder if my weight can break "
                + "the ice.");
        flavor.put("FloorB", "Wowa! These floors are slippery. Walking in a winter wonderland u"
                + "wu.");
        flavor.put("WallB", "Hm… the wall is not very bushy. I still don’t think I can walk thr"
                + "ough though.");
        flavor.put("AvatarB", "Hey! It’s not nice to poke me with the mouse.");
        flavor.put("TrapB", "Seems like Haunter is asleep, best not wake him up!");
        flavor.put("FlagB", "HELP IM LOST :(");

        StdDraw.enableDoubleBuffering();
        StdDraw.show();
    }

    /**
     * Initializes StdDraw parameters and launches the StdDraw window. w and h are the
     * width and height of the world in number of tiles. If the TETile[][] array that you
     * pass to renderFrame is smaller than this, then extra blank space will be left
     * on the right and top edges of the frame. For example, if you select w = 60 and
     * h = 30, this method will create a 60 tile wide by 30 tile tall window. If
     * you then subsequently call renderFrame with a TETile[50][25] array, it will
     * leave 10 tiles blank on the right side and 5 tiles blank on the top side. If
     * you want to leave extra space on the left or bottom instead, use the other
     * initializatiom method.
     * @param w width of the window in tiles
     * @param h height of the window in tiles.
     */
    public void initialize(int w, int h) {
        initialize(w, h, 0, 0);
    }

    /**
     * Takes in a 2d array of TETile objects and renders the 2d array to the screen, starting from
     * xOffset and yOffset.
     *
     * If the array is an NxM array, then the element displayed at positions would be as follows,
     * given in units of tiles.
     *
     *              positions   xOffset |xOffset+1|xOffset+2| .... |xOffset+world.length
     *                     
     * startY+world[0].length   [0][M-1] | [1][M-1] | [2][M-1] | .... | [N-1][M-1]
     *                    ...    ......  |  ......  |  ......  | .... | ......
     *               startY+2    [0][2]  |  [1][2]  |  [2][2]  | .... | [N-1][2]
     *               startY+1    [0][1]  |  [1][1]  |  [2][1]  | .... | [N-1][1]
     *                 startY    [0][0]  |  [1][0]  |  [2][0]  | .... | [N-1][0]
     *
     * By varying xOffset, yOffset, and the size of the screen when initialized, you can leave
     * empty space in different places to leave room for other information, such as a GUI.
     * This method assumes that the xScale and yScale have been set such that the max x
     * value is the width of the screen in tiles, and the max y value is the height of
     * the screen in tiles.
     * @param world the 2D TETile[][] array to render
     */
    public void renderFrame(TETile[][] world) {
        currentWorld = world;
        int numXTiles = world.length;
        int numYTiles = world[0].length;
        StdDraw.clear(new Color(0, 0, 0));
        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x + xOffset, y + yOffset);
            }
        }
        String[] biomeArray = world[0][0].filepath().split("_");
        if (biomeArray[1].equals("A")) {
            biome = "Tropical Islands";
        } else if (biomeArray[1].equals("B")) {
            biome = "Icy Tundra";
        } else {
            biome = "???";
        }
        showInventory();
        StdDraw.show();
    }

    public void showInventory() {
        //Background
        StdDraw.picture(75.0, 20.0, Tileset.PREFIX_PATH + "INVENTORY_A.png");
        //Player Name
        StdDraw.setFont(new Font("Arial", Font.BOLD, 21));
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(75.0, 37.5, "Pikachu");
        //Biome
        StdDraw.setFont(new Font("Arial", Font.BOLD, 11));
        StdDraw.text(75.0, 36.0, "Biome: " + biome);
        //Health
        String[] avatarDataArray = Engine.avatarData().split("_");
        int health = Integer.parseInt(avatarDataArray[2]);
        for (int i = 0; i < health; i += 1) {
            StdDraw.picture(71.5 + i, 34.5, Tileset.PREFIX_PATH + "HEALTH_A.png");
        }
        //Display Current Tile

        StdDraw.setFont(new Font("Arial", Font.BOLD, 15));
        StdDraw.text(75.0, 30.0, "Current Tile: " + hover.description());

        StdDraw.setFont(new Font("Arial", Font.BOLD, 15));
        String message = flavorText(hover);
        int length = message.length();
        for (int i = 0; i <= (length / 16); i += 1) {
            int endLength = 16;
            if (message.length() < endLength) {
                endLength = message.length();
            }
            StdDraw.text(75.0, 28.0 - (i), message.substring(0, endLength));
            message = message.substring(endLength);
        }
    }

    public String flavorText(TETile tile) {
        String key = tile.description() + Engine.biome();
        return flavor.get(key);
    }

    public void updateHover(String mouseXY) {
        String[] mouseDataArray = mouseXY.split("_");
        int mouseX = Integer.parseInt(mouseDataArray[0]);
        int mouseY = Integer.parseInt(mouseDataArray[1]);
        try {
            hover = currentWorld[mouseX][mouseY];
        } catch (Exception e) {
            System.out.println(e);
        }
        this.renderFrame(currentWorld);
    }

    public void updateCurrentTile(String predictedLocation) {
        String[] predictedLocationArray = predictedLocation.split("_");
        int avatarX = Integer.parseInt(predictedLocationArray[1]);
        int avatarY = Integer.parseInt(predictedLocationArray[0]);
        currentTile = currentWorld[avatarX][avatarY].description();
        this.showInventory();
    }

    public void gameOver() {
        StdDraw.picture(40, 20, Tileset.PREFIX_PATH + "GAMEOVER.png");
        StdDraw.show();
        try {
            Thread.sleep((long) 360000000);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }

    public void winGame() {
        StdDraw.picture(40, 20, Tileset.PREFIX_PATH + "WIN.png");
        StdDraw.show();
        try {
            Thread.sleep((long) 360000000);
        } catch (NullPointerException e) {
            System.out.println(e);
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
