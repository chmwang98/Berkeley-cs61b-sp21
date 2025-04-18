package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RoomGenerator implements Serializable {
    private int worldWidth, worldHeight;
    private int maxRooms;
    private Random random;
    private TETile[][] tiles;
    private List<Room> rooms;

    public RoomGenerator(int width, int height, long seed, TETile[][] tiles) {
        this.worldWidth = width;
        this.worldHeight = height;
        this.random = new Random(seed);
        this.maxRooms = RandomUtils.uniform(random, 25, 40);
        this.tiles = tiles;
        this.rooms = new ArrayList<>();
    }

    public void drawRoomsWithWalls() {
        generateRooms();
        connectRooms();
        drawWalls();
    }

    public void generateRooms() {
        int attempts = 0;
        int maxAttempts = maxRooms * 5;
        while (rooms.size() < maxRooms && attempts < maxAttempts) {
            attempts++;
            int roomWidth = RandomUtils.uniform(random, 3, 8);
            int roomHeight = RandomUtils.uniform(random, 3, 8);
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
        int n = rooms.size();
        boolean[] isConnected = new boolean[n];
        isConnected[0] = true;

        // Use Prim's algorithm to connect rooms
        for (int i = 1; i < n; i++) {
            int minDist = Integer.MAX_VALUE;
            int fromIndex = -1;
            int toIndex = -1;

            for (int j = 0; j < n; j++) {
                if (isConnected[j]) {
                    for (int k = 0; k < n; k++) {
                        if (!isConnected[k]) {
                            int dist = manhattanDistance(rooms.get(j), rooms.get(k));
                            if (dist < minDist) {
                                minDist = dist;
                                fromIndex = j;
                                toIndex = k;
                            }
                        }
                    }
                }
            }

            if (fromIndex != -1 && toIndex != -1) {
                connect(rooms.get(fromIndex), rooms.get(toIndex));
                isConnected[toIndex] = true;
            }
        }
    }

    private int manhattanDistance(Room r1, Room r2) {
        Position c1 = r1.getCenter();
        Position c2 = r2.getCenter();
        return Math.abs(c1.getX() - c2.getX()) + Math.abs(c1.getY() - c2.getY());
    }

    private void connect(Room r1, Room r2) {
        Position c1 = r1.getCenter();
        Position c2 = r2.getCenter();

        if (random.nextBoolean()) {
            drawHorizontal(c1.getX(), c2.getX(), c1.getY());
            drawVertical(c1.getY(), c2.getY(), c2.getX());
        } else {
            drawVertical(c1.getY(), c2.getY(), c1.getX());
            drawHorizontal(c1.getX(), c2.getX(), c2.getY());
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
        int[][] offsets = {
                {-1, 0}, {-1, 1}, {0, 1}, {1, 1},
                {1, 0}, {1, -1}, {0, -1}, {-1, -1}
        };
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
