package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.io.Serializable;

public class World implements Serializable {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private TETile[][] tiles;
    private long SEED;
    RoomGenerator roomGenerator;
    Player player;

    // to generate a tiles, only seed is needed
    public World(long seed) {
        tiles = new TETile[WIDTH][HEIGHT];
        SEED = seed;
        fillWithNothing();
        roomGenerator = new RoomGenerator(WIDTH, HEIGHT, SEED, tiles);
        roomGenerator.drawRoomsWithWalls();
        player = new Player(SEED, tiles);
    }

    // Fills tiles in this world with NOTHING.
    private void fillWithNothing() {
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void movePlayer(int dx, int dy) {
        player.move(dx, dy);
    }

    public void putPlayer() {
        player.putPlayerInWorld();
    }

    public void putPlayer(int x, int y) {
        player.putPlayerInWorld(x, y);
    }

    public TETile[][] getTiles() {
        return tiles;
    }

    public Position getPlayerPosition() {
        return player.getPosition();
    }
}

