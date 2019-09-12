package byow.Core;

import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import byow.TileEngine.Tileset;

public class Menu {
    public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 30);
    public Menu() {
        drawMe();
    }

    public static void main(String[] args) {
        Menu test = new Menu();
    }

    public void drawMe() {
        StdDraw.picture(0.5, 0.5, Tileset.PREFIX_PATH + "TITLE_PAGE_B.png");
        StdDraw.setFont(TITLE_FONT);
        StdDraw.setPenColor(StdDraw.WHITE);
        //StdDraw.text(0.5, 0.85, "FAKE POKEMON GAME");
        StdDraw.setFont();
        StdDraw.text(0.5, 0.45, "New Game (N)");
        StdDraw.text(0.5, 0.40, "Load Game (L)");
        StdDraw.text(0.5, 0.35, "Replay Last Game (R)");
        StdDraw.text(0.5, 0.30, "Read Lore (P)");
        StdDraw.text(0.5, 0.25, "Quit (Q)");
        //StdDraw.show();
    }

    public void drawSeed(String seed) {
        drawMe();
        StdDraw.text(0.5, 0.20, "Seed: " + seed);
    }

    public void invalidSeed(char key, String seed) {
        drawMe();
        drawSeed(seed);
        StdDraw.text(0.5, 0.15, key + " is an invalid command, please key in a digit or 'S'");
    }

    public void invalidCommand(char c) {
        drawMe();
        StdDraw.text(0.5, 0.15, c + " is an invalid command, please key in 'N', 'L', or 'Q'");
    }

    public void drawLore() {
        StdDraw.picture(0.5, 0.5, Tileset.PREFIX_PATH + "LORE_B.png");
        StdDraw.text(0.5, 0.10, "Press 'B' to return to menu");
    }
}
