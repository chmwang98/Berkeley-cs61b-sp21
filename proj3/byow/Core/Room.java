package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.List;

public class Room {
    public Position p;    // coordinate of bottom left point in room
    public int width, height;
    public TETile[][] tiles;

    public Room(Position p, int width, int height, TETile[][] tiles) {
        this.p = p;
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    // check if the room is within the world bounds
    public boolean isValid() {
        int worldWidth = tiles.length;
        int worldHeight = tiles[0].length;
        return (p.x + width < worldWidth) && (p.y + height < worldHeight);
    }

    // draw the floor of the room
    public void drawRoom() {
        for (int x = p.x; x < p.x + width; x++) {
            for(int y = p.y; y < p.y + height; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    // check if room overlaps with another room
    public boolean isOverlapping(Room room) {
        return !(p.x + width < room.p.x || room.p.x + room.width < p.x ||
                p.y + height < room.p.y || room.p.y + room.height < p.y);
    }

    // check if room overlaps with any room from a list
    public boolean isOverlapping(List<Room> rooms) {
        for (Room room : rooms) {
            if (isOverlapping(room)) {
                return true;
            }
        }
        return false;
    }

    public Position getCenter() {
        int xCenter = p.x + width / 2;
        int yCenter = p.y + height / 2;
        return new Position(xCenter, yCenter);
    }
}
