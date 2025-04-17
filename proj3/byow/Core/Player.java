package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.Random;

public class Player implements Serializable {
    private Random random;
    private Position p;
    private TETile[][] tiles;

    public Player(long seed, TETile[][] world) {
        random = new Random(seed);
        this.tiles = world;
    }

    public void putPlayerInWorld() {
        while (true) {
            int x = RandomUtils.uniform(random, 0, tiles.length);
            int y = RandomUtils.uniform(random, 0, tiles[0].length);
            if (isFloor(x, y)) {
                p = new Position(x, y);
                tiles[x][y] = Tileset.AVATAR;
                return;
            }
        }
    }

    public void putPlayerInWorld(int x, int y) {
        p = new Position(x, y);
        tiles[x][y] = Tileset.AVATAR;
    }

    public void move(int dx, int dy) {
        if (isFloor(p.getX() + dx, p.getY() + dy)) {
            tiles[p.getX()][p.getY()] = Tileset.FLOOR;
            p.setX(p.getX() + dx);
            p.setY(p.getY() + dy);
            tiles[p.getX()][p.getY()] = Tileset.AVATAR;
        }
    }

    public boolean isFloor(int x, int y) {
        return tiles[x][y].equals(Tileset.FLOOR);
    }

    public Position getPosition() {
        return this.p;
    }
}
