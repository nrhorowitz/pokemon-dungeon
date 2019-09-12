package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;

public class AvatarCharacter implements Characters {
    private String name;
    private TETile[] tiles; //0 = left, 1 = top, 2 = right, 3 = bot

    public AvatarCharacter(String biome) {
        name = "Avatar";
        tiles = new TETile[4];
        for (int i = 0; i < tiles.length; i += 1) {
            char p1 = Tileset.AVATAR_A_3.character();
            Color p2 = Color.BLUE;
            Color p3 = p2;
            String p4 = Tileset.AVATAR_A_3.description();
            String p5 = Tileset.PREFIX_PATH + "AVATAR_" + biome + "_" + i + ".png";
            tiles[i] = new TETile(p1, p2, p3, p4, p5);
        }
    }

    @Override
    public String getName() {
        return name;
    }
    @Override
    public TETile[] getTiles() {
        return tiles;
    }
}
