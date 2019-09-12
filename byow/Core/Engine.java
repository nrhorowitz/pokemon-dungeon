package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import edu.princeton.cs.algs4.StdRandom;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.awt.Color;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = Main.WIDTH;
    public static final int HEIGHT = Main.HEIGHT;
    private static final int NUM_OF_TRAPS = 10;
    Map<Integer, String> rooms;
    Set<String> pivots;
    Set<String> copy;
    int totalSectors;
    String currWorld;
    Characters[] characters; //index 0 == avatar
    private boolean SLOW = false;
    private static String biome;
    private static String avatarData;
    private boolean pastMenu;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        Inputs allCommands = new InputKey(ter);
        interactGeneral(allCommands);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, both of these calls:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.
        InputString allCommands = new InputString(input);
        //Assumes input String only in format "
        TETile[][] inputReturn = new TETile[WIDTH][HEIGHT];

        String seed = "";
        currWorld = "";

        while (allCommands.possibleNextInput()) {
            char item = allCommands.getNextKey();
            if (item == 'L') {
                currWorld = Saver.loadWorld();
                int index = 0;
                char[] loadedWorld = currWorld.toCharArray();
                for (index = 0; index < loadedWorld.length; index += 1) {
                    item = loadedWorld[index];
                    if (Character.isDigit(item)) {
                        seed += item;
                    } else if (item == 'S') {
                        break;
                    }
                }
                inputReturn = generateWorld(seed);

                for (index += 1; index < loadedWorld.length; index += 1) {
                    char c = loadedWorld[index];
                    if (c == 'W' || c == 'A' || c == 'S' || c == 'D') {
                        moveCharacter(inputReturn, c, false);
                    }
                }

            } else if (item == 'N') {
                currWorld += item;
                while (allCommands.possibleNextInput()) {
                    item = allCommands.getNextKey();
                    currWorld += item;
                    if (Character.isDigit(item)) {
                        seed += item;
                    } else if (item == 'S') {
                        break;
                    }
                }
                inputReturn = generateWorld(seed);
            } else if (item == ':') {
                Saver.saveWorld(currWorld);
                break;
            }
            while (allCommands.possibleNextInput()) {
                item = allCommands.getNextKey();
                if (item == 'W' || item == 'A' || item == 'S' || item == 'D') {
                    currWorld += item;
                    moveCharacter(inputReturn, item, false);
                } else if (item == ':') {
                    Saver.saveWorld(currWorld);
                    break;
                } else {
                    System.out.println("Please don't print");
                }
            }
        }
        return inputReturn;
    }

    private TETile[][] slowInputWithString(String input) {
        InputString allCommands = new InputString(input);
        return interactGeneral(allCommands);
    }

    private TETile[][] interactGeneral(Inputs allCommands) {
        TETile[][] activeWorld = new TETile[WIDTH][HEIGHT];
        Menu begin = new Menu();
        char menuOption = 'I';
        pastMenu = false;
        while (allCommands.possibleNextInput() && !pastMenu) {
            menuOption = allCommands.getNextKey();
            if (menuOption == 'N') {
                pastMenu = true;
                currWorld = "";
                currWorld += menuOption;
                String seed = "";
                begin.drawSeed(seed);
                boolean looking = true;
                while (allCommands.possibleNextInput() && looking) {
                    char c = allCommands.getNextKey();
                    if (Character.isDigit(c)) {
                        seed += c;
                        currWorld += c;
                        begin.drawSeed(seed);
                    } else if (c == 'S') {
                        currWorld += c;
                        looking = false;
                    } else {
                        begin.invalidSeed(c, seed);
                    }
                }
                activeWorld = generateWorld(seed);
                intitializeAndRender(ter, WIDTH + 10, HEIGHT, activeWorld);
            } else if (menuOption == 'L') {
                setSlowPastMenu();
                activeWorld = interactWithInputString(Saver.loadWorld());
                intitializeAndRender(ter, WIDTH + 10, HEIGHT, activeWorld);
            } else if (menuOption == 'R') {
                SLOW = true;
                activeWorld = slowInputWithString(Saver.loadWorld());
                intitializeAndRender(ter, WIDTH + 10, HEIGHT, activeWorld);
                setSlowPastMenu();
            } else if (menuOption == 'P') {
                begin.drawLore();
            } else if (menuOption == 'Q') {
                System.exit(0);
            } else if (menuOption == 'B') {
                begin.drawMe();
            } else {
                begin.invalidCommand(menuOption);
            }
        }
        while (allCommands.possibleNextInput()) {
            if (SLOW) {
                try {
                    Thread.sleep(150);
                } catch (NullPointerException e) {
                    System.out.println("caught" + e);
                } catch (InterruptedException e) {
                    System.out.println("caught" + e);
                }
            }
            char c = allCommands.getNextKey();
            if (c == ':') {
                while (allCommands.possibleNextInput()) {
                    c = allCommands.getNextKey();
                    if (c == 'Q') {
                        Saver.saveWorld(currWorld);
                        System.exit(0);
                        return activeWorld;
                    } else {
                        break;
                    }
                }
            } else if (c == 'W' || c == 'S' || c == 'A' || c == 'D') { //UPDATES ACTIVE WORLD
                moveCharacter(activeWorld, c, true);
                ter.renderFrame(activeWorld);
            } else {
                System.out.println("Movement option not recognized");
            }
            currWorld += c;
        }
        return activeWorld;
    }

    /**
     * Helper method for rendering.
     * @param toRender
     * @param w
     * @param h
     * @param world
     */
    private void intitializeAndRender(TERenderer toRender, int w, int h, TETile[][] world) {
        toRender.initialize(w, h);
        toRender.renderFrame(world);
    }

    /**
     * Helper method for rendering.
     */
    private void setSlowPastMenu() {
        SLOW = false;
        pastMenu = true;
    }

    /**
     * Helper method moves if valid, increments turn by one.
     * @param world of tiles
     * @param movement character
     */
    public void moveCharacter(TETile[][] world, char movement, boolean render) {
        String[] avatarDataArray = avatarData.split("_");
        int botTop = Integer.parseInt(avatarDataArray[0]);
        int leftRight = Integer.parseInt(avatarDataArray[1]);
        int destinationY = botTop;
        int destinationX = leftRight;
        TETile directionalTile = Tileset.AVATAR_A_3;
        int direction = 0;
        if (movement == 'W') {
            destinationY += 1;
            direction = 1;
        } else if (movement == 'S') {
            destinationY -= 1;
            direction = 3;
        } else if (movement == 'A') {
            destinationX -= 1;
            direction = 0;
        } else if (movement == 'D') {
            destinationX += 1;
            direction = 2;
        }
        char p1 = Tileset.AVATAR_A_3.character();
        Color p2 = Color.BLACK;
        Color p3 = p2;
        String p4 = Tileset.AVATAR_A_3.description();
        String p5 = Tileset.PREFIX_PATH + "AVATAR_" + biome + "_" + direction + ".png";
        directionalTile = new TETile(p1, p2, p3, p4, p5);
        TETile destination = world[destinationX][destinationY];
        if (destination.description().equals("Floor")) {
            avatarData = destinationY + "_" + destinationX + "_" + avatarDataArray[2];
            if (render) {
                ter.updateCurrentTile(avatarData);
            }
            world[destinationX][destinationY] = directionalTile;
            p1 = Tileset.FLOOR_A_0000.character();
            p2 = Color.BLACK;
            p3 = p2;
            p4 = Tileset.FLOOR_A_0000.description();
            p5 = Tileset.PREFIX_PATH + "FLOOR_" + biome + "_0000.png";
            TETile add = new TETile(p1, p2, p3, p4, p5);
            world[leftRight][botTop] = add;
        } else if (destination.description().equals("Trap")) {
            avatarData = destinationY + "_" + destinationX + "_" + avatarDataArray[2];
            if (render) {
                ter.updateCurrentTile(avatarData);
            }
            world[destinationX][destinationY] = directionalTile;
            p1 = Tileset.FLOOR_A_0000.character();
            p2 = Color.BLACK;
            p3 = p2;
            p4 = Tileset.FLOOR_A_0000.description();
            p5 = Tileset.PREFIX_PATH + "FLOOR_" + biome + "_0000.png";
            TETile add = new TETile(p1, p2, p3, p4, p5);
            world[leftRight][botTop] = add;
            String[] a = avatarData.split("_");
            int currentHealth = Integer.parseInt(a[2]);
            avatarData = a[0] + "_" + a[1] + "_" + (currentHealth - 1);
            if (currentHealth == 1 && render) {
                ter.gameOver();
            }
        } else if (destination.description().equals("Flag") && render) {
            ter.winGame();
        } else {
            //something blocking
            world[leftRight][botTop] = directionalTile;
        }
    }

    private TETile[][] generateWorld(String input) {
        System.out.println(input);
        // 0) Set random seed from input
        // 1) Fill everything with nothing (water XD)
        // 2) Given width and height, create a 2 dimensional int array of zones
        // n as number of rooms   --- dependency on distribution ?
        // 3) Parameters for room dimensions (by floor)   //master data
        // 4) Parameters for hallways between sectors     //master data
        // 5) Add floors  (to final world frame)
        // 6) Add walls (option for inefficiency)  helper adjacent  (to final world frame)
        // 7) Big flex owo
        this.seed(input);  // 0)
        if (StdRandom.uniform(0, 2) == 0) {
            biome = "A";
        } else {
            biome = "B";
        }
        TETile[][] finalWorldFrame = new TETile[WIDTH][HEIGHT];
        fillWater(finalWorldFrame);  // 1) done
        int[][] numRoomSector = numRoomSector();  // 2)
        rooms = computeRoom(numRoomSector);  // 3)
        addFloors(finalWorldFrame);  // 5)
        //Add vertices to the graph
        addHalls(numRoomSector, finalWorldFrame);
        addWalls(finalWorldFrame);  // 6)
        addWalls(finalWorldFrame);  // 6) for textures
        addWater(finalWorldFrame);  // 7)
        createCharacters(NUM_OF_TRAPS);
        addAvatar(finalWorldFrame, characters[0]); //Adds avatar to the map somewhat randomly
        addTraps(finalWorldFrame);
        addFlag(finalWorldFrame);
        //Adds traps into the world somewhat randomly
        return finalWorldFrame;
    }


    /**
     * Seed sets StdRandom to the input to create pseudorandom generation.
     * @param input of primitive type String
     */
    private void seed(String input) {
        long l = (long) input.hashCode();
        StdRandom.setSeed(l);
    }

    /**
     * FillWater initializes world with default water tiles.
     * @param world of type TETile[][]
     */
    private void fillWater(TETile[][] world) {
        int width = world.length;
        int height = world[0].length;
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                world[x][y] = Tileset.EMPTY_A_0000;
            }
        }
    }

    /**
     * NumRoomSector seeds each sector with a random number of rooms.
     * Rooms may overlap
     * **make sure rooms are connected for n > 1 rooms
     * @return array of room counts by sector of type int[][]
     */
    private int[][] numRoomSector() {
        int width = StdRandom.uniform(6, 9);
        int height = StdRandom.uniform(3, 5);
        totalSectors = width * height;
        int[][] returnArray = new int[height][width];
        for (int y = 0; y < height; y += 1) {
            for (int x = 0; x < width; x += 1) {
                boolean zero = StdRandom.uniform(0, 2) == 0;
                if (zero) {
                    returnArray[y][x] = 0;
                } else {
                    returnArray[y][x] = StdRandom.uniform(1, 5);
                }
            }
        }
        return returnArray;
    }

    /**
     * ComputeRoom returns mapping of sector index to string containing coordinates.
     * @param numRoomSector number of rooms per sector of type int[][]
     * @return mapping of type Map<Integer, String>
     */
    private Map<Integer, String> computeRoom(int[][] numRoomSector) {
        pivots = new HashSet<String>();
        copy = new HashSet<String>();
        Map<Integer, String> returnMap = new HashMap<>();
        int h = numRoomSector.length;
        int w = numRoomSector[0].length;
        int hBound = HEIGHT / h;
        int wBound = WIDTH / w;
        //create boxes (y,x)
        for (int i = 0; i < h; i += 1) {
            for (int j = 0; j < w; j += 1) {
                int bot0 = 0;
                int left0 = 0;
                int top0 = 0;
                int right0 = 0;
                for (int count = 0; count < numRoomSector[i][j]; count += 1) {
                    int bot = StdRandom.uniform((i * hBound) + 1, ((i + 1) * hBound) - 4);
                    int left = StdRandom.uniform((j * wBound) + 1, ((j + 1) * wBound) - 4);
                    int top = StdRandom.uniform(bot + 1, ((i + 1) * hBound) - 1);
                    int right = StdRandom.uniform(left + 1, ((j + 1) * wBound) - 1);
                    String temp = bot + "_" + left + "_" + top + "_" + right + "_";
                    if (count == 0) { //set first square
                        bot0 = bot;
                        left0 = left;
                        top0 = top;
                        right0 = right;
                        //assign a random tile as the pivot that is connected
                        int pivotH = StdRandom.uniform(bot, top + 1);
                        int pivotW = StdRandom.uniform(left, right + 1);
                        pivots.add(pivotH + "_" + pivotW);
                        copy.add(pivotH + "_" + pivotW);
                    }
                    if (returnMap.containsKey((w * i) + j)) {
                        returnMap.put((w * i) + j, returnMap.get((w * i) + j) + temp);
                    } else {
                        returnMap.put((w * i) + j, temp);
                    }
                    //append extra box to ensure connectivity
                    //find minimum distance of opposite points then construct box (inclusive)
                    int originDistance = Math.abs(left0 - right) + Math.abs(bot0 - top);
                    int desDistance = Math.abs(right0 - left) + Math.abs(top0 - bot);
                    if (originDistance < desDistance) {
                        int aBot = Math.min(bot0, top);
                        int aTop = Math.max(bot0, top);
                        int aLeft = Math.min(left0, right);
                        int aRight = Math.max(left0, right);
                        String a = aBot + "_" + aLeft + "_" + aTop + "_" + aRight + "_";
                        returnMap.put((w * i) + j, returnMap.get((w * i) + j) + a);
                    }
                }
            }
        }
        return returnMap;
    }

    /**
     * Have a set of connected rooms, each time append a room, attach pivot to a rando in set.
     * @return
     */
    private void addHalls(int[][] numRoomSector, TETile[][] finalWorldFrame) {
        Set<String> linkedPivots = new HashSet<String>();
        //Set one to the starting pivot in link.
        Iterator<String> pivotsIT = pivots.iterator();
        String temp = pivotsIT.next();
        pivots.remove(temp);
        linkedPivots.add(temp);
        while (!pivots.isEmpty()) {
            //Pick starting pivot not in set.
            pivotsIT = pivots.iterator();
            int randPivotStartIndex = StdRandom.uniform(1, pivots.size() + 1);
            String randPivotStart = null;

            for (int i = 0; i < randPivotStartIndex; i += 1) {
                randPivotStart = pivotsIT.next();
            }
            pivots.remove(randPivotStart);
            //Pick ending pivot in set.
            Iterator<String> linkedPivotsIT = linkedPivots.iterator();
            int randPivotEndIndex = StdRandom.uniform(1, linkedPivots.size() + 1);
            String randPivotEnd = null;
            for (int i = 0; i < randPivotEndIndex; i += 1) {
                randPivotEnd = linkedPivotsIT.next();
            }
            //Pick a random turning point for hallway
            //construct in two intervals
            String[] start = randPivotStart.split("_"); //bottop_leftright
            String[] end = randPivotEnd.split("_"); //bottop_leftright
            int starty = Integer.parseInt(start[0]);
            int endy = Integer.parseInt(end[0]);
            int startx = Integer.parseInt(start[1]);
            int endx = Integer.parseInt(end[1]);

            int left = Math.min(startx, endx);
            int bot = Math.min(starty, endy);
            int right = Math.max(startx, endx);
            int top = Math.max(starty, endy);
            char p1 = Tileset.FLOOR_A_0000.character();
            Color p2 = Color.BLACK;
            Color p3 = p2;
            String p4 = Tileset.FLOOR_A_0000.description();
            String p5 = Tileset.PREFIX_PATH + "FLOOR_" + biome + "_0000.png";
            TETile add = new TETile(p1, p2, p3, p4, p5);
            //////////////
            if ((startx < endx && starty < endy) || (endx < startx && endy < starty)) {
                if (StdRandom.uniform(0, 2) == 0) {
                    for (int i = left; i <= right; i += 1) {
                        finalWorldFrame[i][bot] = add;
                    }
                    for (int j = bot; j <= top; j += 1) {
                        finalWorldFrame[right][j] = add;
                    }
                } else {
                    for (int j = bot; j <= top; j += 1) {
                        finalWorldFrame[left][j] = add;
                    }
                    for (int i = left; i <= right; i += 1) {
                        finalWorldFrame[i][top] = add;
                    }
                }
            } else {
                if (StdRandom.uniform(0, 2) == 0) {
                    for (int i = left; i <= right; i += 1) {
                        finalWorldFrame[i][top] = add;
                    }
                    for (int j = bot; j <= top; j += 1) {
                        finalWorldFrame[right][j] = add;
                    }
                } else {
                    for (int j = bot; j <= top; j += 1) {
                        finalWorldFrame[right][j] = add;
                    }
                    for (int i = left; i <= right; i += 1) {
                        finalWorldFrame[i][top] = add;
                    }
                }
            }
        }
    }

    /**
     * AddFloors adds floor tiles to master tile array.
     * @param finalWorldFrame of type TETile[][]
     */
    private void addFloors(TETile[][] finalWorldFrame) {
        for (int i = 0; i < totalSectors; i += 1) {
            if (rooms.containsKey(i)) {
                String roomData = rooms.get(i);
                String[] roomDataArray = roomData.split("_");
                for (int j = 0; j < roomDataArray.length; j += 4) {
                    int bot = Integer.parseInt(roomDataArray[j + 0]);
                    int left = Integer.parseInt(roomDataArray[j + 1]);
                    int top = Integer.parseInt(roomDataArray[j + 2]);
                    int right = Integer.parseInt(roomDataArray[j + 3]);
                    for (int y = bot; y <= top; y += 1) {
                        for (int x = left; x <= right; x += 1) {
                            char p1 = Tileset.FLOOR_A_0000.character();
                            Color p2 = Color.BLACK;
                            Color p3 = p2;
                            String p4 = Tileset.FLOOR_A_0000.description();
                            String p5 = Tileset.PREFIX_PATH + "FLOOR_" + biome + "_0000.png";
                            TETile add = new TETile(p1, p2, p3, p4, p5);
                            finalWorldFrame[x][y] = add;
                        }
                    }
                }
            }
        }
    }


    /** createCharacters fills in the character array with n enemies and 1 avatar
     * Ensure n is never greater than number of raw rooms
     * @param n of type int
     */
    private void createCharacters(int n) {
        characters = new Characters[n + 1];
        characters[0] = new AvatarCharacter(biome);
        for (int i = 1; i < n + 1; i += 1) {
            characters[i] = new EnemyCharacter(biome);
        }
    }

    /** addCharacters adds all characters into a tile in the world
     * @param finalWorldFrame of type TETile[][]
     */
    private void addAvatar(TETile[][] finalWorldFrame, Characters avatar) {
        for (int i = 0; i < totalSectors; i += 1) {
            if (rooms.containsKey(i)) {
                String roomData = rooms.get(i);
                String[] roomDataArray = roomData.split("_");
                int bot = Integer.parseInt(roomDataArray[0]);
                int left = Integer.parseInt(roomDataArray[1]);
                finalWorldFrame[left][bot] = avatar.getTiles()[3];
                String loc = bot + "_" + left;
                avatarData = bot + "_" + left + "_8"; //y axis, x axis
                break;
            }
        }
    }

    /** addTraps adds traps to the map at specified amount NUM_OF_TRAPS
     *
     */
    private void addTraps(TETile[][] finalWorldFrame) {
        for (int x = 1; x < WIDTH - 1; x += 1) {
            for (int y = 1; y < HEIGHT - 1; y += 1) {
                if (finalWorldFrame[x][y].description().equals("Floor") && NUM_OF_TRAPS > 0) {
                    if (StdRandom.uniform(0, 10) == 0) {
                        char p1 = Tileset.ENEMY_A_3.character();
                        Color p2 = Color.BLACK;
                        Color p3 = p2;
                        String p4 = Tileset.ENEMY_A_3.description();
                        String p5 = Tileset.PREFIX_PATH + "TRAP_" + biome + "_3.png";
                        TETile add = new TETile(p1, p2, p3, p4, p5);
                        finalWorldFrame[x][y] = add;
                    }
                }
            }
        }
    }

    /**
     * Adds flag to world.
     * @param world
     */
    private void addFlag(TETile[][] world) {
        boolean found = true;
        while (found) {
            int randX = StdRandom.uniform(1, WIDTH - 1);
            int randY = StdRandom.uniform(1, HEIGHT - 1);
            if (world[randX][randY].description().equals("Floor")) {
                char p1 = Tileset.FLAG_A.character();
                Color p2 = Color.BLACK;
                Color p3 = p2;
                String p4 = Tileset.FLAG_A.description();
                String p5 = Tileset.PREFIX_PATH + "FLAG_" + biome + ".png";
                TETile add = new TETile(p1, p2, p3, p4, p5);
                world[randX][randY] = add;
                found = false;
            }
        }
    }



    /**
     * Adjacent returns array of size 4
     * Order - left, top, right, bot
     * 0 - nothing
     * 1 - a wall
     * 2 - a floor
     * @param finalWorldFrame of type TETile[][]
     * @param h height of current index of primitive type int
     * @param w width of current index of primitive type int
     * @return adjacents of type int[]
     */
    private int[] adjacent(TETile[][] finalWorldFrame, int h, int w) {
        int[] returnArray = new int[4];
        returnArray[0] = 0;
        returnArray[1] = 0;
        returnArray[2] = 0;
        returnArray[3] = 0;
        if (w - 1 >= 0) {
            if (finalWorldFrame[w - 1][h].description().equals("Floor")) {
                returnArray[0] = 2;
            } else if (finalWorldFrame[w - 1][h].description().equals("Wall")) {
                returnArray[0] = 1;
            } else if (finalWorldFrame[w - 1][h].description().equals("Trap")) {
                returnArray[0] = 3;
            } else if (finalWorldFrame[w - 1][h].description().equals("Avatar")) {
                returnArray[0] = 4;
            }
        }
        if (h + 1 < HEIGHT) {
            if (finalWorldFrame[w][h + 1].description().equals("Floor")) {
                returnArray[1] = 2;
            } else if (finalWorldFrame[w][h + 1].description().equals("Wall")) {
                returnArray[1] = 1;
            } else if (finalWorldFrame[w][h + 1].description().equals("Trap")) {
                returnArray[1] = 3;
            } else if (finalWorldFrame[w][h + 1].description().equals("Avatar")) {
                returnArray[1] = 4;
            }
        }
        if (w + 1 < WIDTH) {
            if (finalWorldFrame[w + 1][h].description().equals("Floor")) {
                returnArray[2] = 2;
            } else if (finalWorldFrame[w + 1][h].description().equals("Wall")) {
                returnArray[2] = 1;
            } else if (finalWorldFrame[w + 1][h].description().equals("Trap")) {
                returnArray[2] = 3;
            } else if (finalWorldFrame[w + 1][h].description().equals("Avatar")) {
                returnArray[2] = 4;
            }
        }
        if (h - 1 >= 0) {
            if (finalWorldFrame[w][h - 1].description().equals("Floor")) {
                returnArray[3] = 2;
            } else if (finalWorldFrame[w][h - 1].description().equals("Wall")) {
                returnArray[3] = 1;
            } else if (finalWorldFrame[w][h - 1].description().equals("Trap")) {
                returnArray[3] = 3;
            } else if (finalWorldFrame[w][h - 1].description().equals("Avatar")) {
                returnArray[3] = 4;
            }
        }
        return returnArray;
    }

    /**
     * AddWalls adds wall tiles to master tile array.
     * Depends on placements of walls
     * Two passes, one to place walls one to render orientation
     * @param finalWorldFrame of type TETile[][]
     */
    private void addWalls(TETile[][] finalWorldFrame) {
        for (int h = 0; h < HEIGHT; h += 1) {
            for (int w = 0; w < WIDTH; w += 1) {
                if (!(finalWorldFrame[w][h].description().equals("Floor"))) {
                    //Textures
                    int[] adjacentData = adjacent(finalWorldFrame, h, w);
                    String texture = "";
                    boolean foundFloor = false;
                    for (int direction = 0; direction < 4; direction += 1) {
                        if (adjacentData[direction] == 2) {
                            foundFloor = true;
                            texture += "0";
                        } else if (adjacentData[direction] == 1) {
                            texture += "1";
                        } else {
                            texture += "0";
                        }
                    }
                    if (foundFloor) {
                        char p1 = Tileset.WALL_A_0000.character();
                        Color p2 = Color.BLACK;
                        Color p3 = p2;
                        String p4 = Tileset.WALL_A_0000.description();
                        String p5 = Tileset.PREFIX_PATH + "WALL_" + biome + "_" + texture
                                + ".png";
                        TETile add = new TETile(p1, p2, p3, p4, p5);
                        finalWorldFrame[w][h] = add;
                    }
                }
            }
        }
    }

    /**
     * AddWater adds water tiles texture to master tile array.
     * Depends on placements of walls
     * @param finalWorldFrame of type TETile[][]
     */
    private void addWater(TETile[][] finalWorldFrame) {
        for (int h = 0; h < HEIGHT; h += 1) {
            for (int w = 0; w < WIDTH; w += 1) {
                if (finalWorldFrame[w][h].description().equals("Empty")) {
                    //Textures
                    int[] adjacentData = adjacent(finalWorldFrame, h, w);
                    String texture = "";
                    for (int direction = 0; direction < 4; direction += 1) {
                        if (adjacentData[direction] == 1) {
                            texture += "1";
                        } else {
                            texture += "0";
                        }
                    }
                    char p1 = Tileset.EMPTY_A_0000.character();
                    Color p2 = Color.BLUE;
                    Color p3 = p2;
                    String p4 = Tileset.EMPTY_A_0000.description();
                    String p5 = Tileset.PREFIX_PATH + "EMPTY_" + biome + "_" + texture + ".png";
                    TETile add = new TETile(p1, p2, p3, p4, p5);
                    finalWorldFrame[w][h] = add;
                }
            }
        }
    }

    /**
     * Accessor method for biome.
     * @return biome
     */
    public static String biome() {
        return biome;
    }

    /**
     * Accessor method for avatarData.
     * @return avatarData
     */
    public static String avatarData() {
        return avatarData;
    }
}
