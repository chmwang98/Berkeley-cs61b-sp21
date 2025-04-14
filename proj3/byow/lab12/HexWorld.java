package byow.lab12;
import org.junit.Test;
import static org.junit.Assert.*;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 60;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    // Draw a row of tiles, with p as starting position
    public static void drawRow(TETile[][] tiles, Position p, TETile tile, int length) {
        for (int dx = 0; dx < length; dx++) {
            tiles[p.x + dx][p.y] = tile;
        }
    }

    // Fills the given 2D array of tiles with NOTHING.
    public static void fillWithNothing(TETile[][] tiles) {
        int height = tiles[0].length;
        int width = tiles.length;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private static TETile randomTile() {
        int tile = RANDOM.nextInt(5);
        switch (tile) {
            case 0:
                return Tileset.MOUNTAIN;
            case 1:
                return Tileset.GRASS;
            case 2:
                return Tileset.FLOWER;
            case 3:
                return Tileset.SAND;
            case 4:
                return Tileset.TREE;
            default:
                return Tileset.NOTHING;
        }
    }

    private static class Position {
        int x;
        int y;
        Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public Position shift(int dx, int dy) {
            return new Position(this.x + dx, this.y + dy);
        }
    }

    public static void addHexhelper(TETile[][] tiles, Position p, TETile tile, int b, int t) {
        Position start = p.shift(b, 0);
        drawRow(tiles, start, tile, t);

        Position startOfReflection = start.shift(0, -(2 * b + 1));
        drawRow(tiles, startOfReflection, tile, t);
        if (b > 0) {
            Position next = p.shift(0, -1);
            addHexhelper(tiles, next, tile, b - 1, t + 2);
        }
    }

    public static void addHexagon(TETile[][] world, Position p, TETile tile, int s) {
        if (s < 2) {
            return;
        }
        addHexhelper(world, p, tile, s - 1, s);
    }

    public static void addHexColumn(TETile[][] tiles, Position p, int s, int num) {
        if (num < 1) return;

        addHexagon(tiles, p, randomTile(), s);

        if (num > 1) {
            Position downNeighbor = getDownNeighbor(p, s);
            addHexColumn(tiles, downNeighbor, s, num - 1);
        }
    }

    public static Position getDownNeighbor(Position p, int s) {
        return p.shift(0, -2 * s);
    }

    public static Position getUpRightNeighbor(Position p, int s) {
        return p.shift(2 * s - 1, s);
    }

    public static Position getDownRightNeighbor(Position p, int s) {
        return p.shift(2 * s - 1, -s);
    }

    public static void drawWorld(TETile[][] tiles, Position p, int s, int numOnEdge) {
        addHexColumn(tiles, p, s, numOnEdge);

        for (int i = 1; i < numOnEdge; i++) {
            p = getUpRightNeighbor(p, s);
            addHexColumn(tiles, p, s, numOnEdge + i);
        }

        for (int i = numOnEdge - 2; i >= 0; i--) {
            p = getDownRightNeighbor(p, s);
            addHexColumn(tiles, p, s, numOnEdge + i);
        }
    }

    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        Position anchor = new Position(5, 35);
        fillWithNothing(world);
        drawWorld(world, anchor, 4, 3);

        ter.renderFrame(world);
    }

}
