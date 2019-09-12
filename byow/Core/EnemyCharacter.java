package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.awt.*;

public class EnemyCharacter implements Characters {
    private String name;
    private TETile[] tiles;

    public EnemyCharacter(String biome) {
        name = "Trap";
        tiles = new TETile[4];
        for (int i = 0; i < tiles.length; i += 1) {
            char p1 = Tileset.ENEMY_A_3.character();
            Color p2 = Color.BLUE;
            Color p3 = p2;
            String p4 = Tileset.ENEMY_A_3.description();
            String p5 = Tileset.PREFIX_PATH + "TRAP_" + biome + "_" + i + ".png";
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
