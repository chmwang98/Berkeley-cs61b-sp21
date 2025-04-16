package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomGenerator {
    public int worldWidth, worldHeight;
    public int maxRooms;
    public Random random;
    public TETile[][] tiles;
    public List<Room> rooms;

    public RoomGenerator(int width, int height, long seed, TETile[][] tiles) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.random = new Random(seed);
        this.maxRooms = RandomUtils.uniform(random, 15, 25);
        this.tiles = tiles;
        this.rooms = new ArrayList<>();
    }

    public void generateRooms() {
        for (int i = 0; i < maxRooms; i++) {
            int roomWidth = RandomUtils.uniform(random, 2, 6);
            int roomHeight = RandomUtils.uniform(random, 2, 6);
            int x = RandomUtils.uniform(random, 1, worldWidth - roomWidth - 1);
            int y = RandomUtils.uniform(random, 1, worldHeight - roomHeight - 1);
            Position p = new Position(x, y);

            Room newRoom = new Room(p, roomWidth, roomHeight, tiles);

            if (newRoom.isValid() && !newRoom.isOverlapping(rooms)) {
                rooms.add(newRoom);
                newRoom.drawRoom();
            }
        }
    }

    // generate hallways between different rooms
    public void connectRooms() {
        for (int i = 1; i < rooms.size(); i++) {
            Position center1 = rooms.get(i - 1).getCenter();
            Position center2 = rooms.get(i).getCenter();
            drawHorizontal(center1.x, center2.x, center1.y);
            drawVertical(center1.y, center2.y, center2.x);
        }
    }

    public void drawHorizontal(int x1, int x2, int y) {
        for (int x = Math.min(x1, x2); x <= Math.max(x1, x2); x++) {
            tiles[x][y] = Tileset.FLOOR;
        }
    }

    public void drawVertical(int y1, int y2, int x) {
        for (int y = Math.min(y1, y2); y <= Math.max(y1, y2); y++) {
            tiles[x][y] = Tileset.FLOOR;
        }
    }

    public void drawWalls() {
        int[][] offsets = {{-1, 0}, {-1, 1}, {0, 1}, {1, 1},
                            {1, 0}, {1, -1}, {0, -1}, {-1, -1}};
        for (int x = 0; x < worldWidth; x++) {
            for (int y = 0; y < worldHeight; y++) {
                if (tiles[x][y].equals(Tileset.FLOOR)) {
                    for (int[] o : offsets) {
                        int nx = x + o[0];
                        int ny = y + o[1];
                        if (tiles[nx][ny].equals(Tileset.NOTHING)) {
                            tiles[nx][ny] = Tileset.WALL;
                        }
                    }
                }
            }
        }
    }
}
