package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TERenderer;

/** This is the main entry point for the program. This class simply parses
 *  the command line inputs, and lets the byow.Core.Engine class take over
 *  in either keyboard or input string mode.
 */
public class Main {
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;

    public static void main(String[] args) {
        System.out.println("start");
        if (args.length > 1) {
            System.out.println("Can only have one argument - the input string");
            System.exit(0);
        } else if (args.length == 1) {
            Engine engine = new Engine();
            TETile[][] toRender = engine.interactWithInputString(args[0]);
            System.out.println(engine.toString());
            TERenderer renderer = new TERenderer();
            renderer.initialize(WIDTH, HEIGHT);
            renderer.renderFrame(toRender);
        } else {
            Engine engine = new Engine();
            System.out.println("Keyboard time!");
            engine.interactWithKeyboard();
        }
    }
}
