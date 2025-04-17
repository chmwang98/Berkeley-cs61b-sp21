package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.io.Serializable;
import java.util.List;

public class Room implements Serializable {
    private Position p;    // coordinate of bottom left point in room
    private int width, height;
    private TETile[][] tiles;

    public Room(Position p, int width, int height, TETile[][] tiles) {
        this.p = p;
        this.width = width;
        this.height = height;
        this.tiles = tiles;
    }

    // check if the room is within the tiles bounds
    public boolean isValid() {
        int worldWidth = tiles.length;
        int worldHeight = tiles[0].length;
        return (p.getX() + width < worldWidth) && (p.getY() + height < worldHeight);
    }

    // draw the floor of the room
    public void drawRoom() {
        for (int x = p.getX(); x < p.getX() + width; x++) {
            for (int y = p.getY(); y < p.getY() + height; y++) {
                tiles[x][y] = Tileset.FLOOR;
            }
        }
    }

    // check if room overlaps with another room
    public boolean isOverlapping(Room room) {
        return !(p.getX() + width + 1 < room.p.getX()
                || room.p.getX() + room.width + 1 < p.getX()
                || p.getY() + height + 1 < room.p.getY()
                || room.p.getY() + room.height + 1 < p.getY());
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
        int xCenter = p.getX() + width / 2;
        int yCenter = p.getY() + height / 2;
        return new Position(xCenter, yCenter);
    }
}
