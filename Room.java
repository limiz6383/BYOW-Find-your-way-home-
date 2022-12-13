package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import java.util.*;

import static byow.Core.GenerateBoard.random;
import static java.lang.Math.abs;
import static java.lang.Math.max;

public class Room {
    private int height;
    private int width;
    private int x;
    private int y;
    private HashSet<Room> neighbors;
    private boolean light;
    public static final TETile[] LIGHTS =
            new TETile[]{Tileset.LIGHT2, Tileset.LIGHT3, Tileset.LIGHT4, Tileset.LIGHT5, Tileset.LIGHT6};


    public Room(Position p, int height, int width) {
        this.height = height;
        this.width = width;
        this.x = p.x;
        this.y = p.y;
        this.neighbors = new HashSet<>();
        this.light = true;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean getLight() {
        return light;
    }

    public void addNeighbor(Room room) {
        neighbors.add(room);
    }

    public boolean isNeighbor(Room room) {
        return neighbors.contains(room);
    }


    public void addLight(TETile[][] world) {
        boolean foundlight = false;
        int maxiteration = 0;
        while (!foundlight) {
            int rX = random.nextInt(this.getWidth()) + this.getX() + 1;
            int rY = random.nextInt(this.getHeight()) + this.getY();
            if (world[rX][rY] == Tileset.AVATAR) {
                world[rX][rY] = Tileset.LIGHT;
                spreadLight(world, rX, rY, this);
                foundlight = true;
            }
            if (maxiteration > 15) {
                break;
            }
            maxiteration++;
        }
        this.light = true;
//        lightSources.put(p, new Position(rX, rY));
    }

    private void spreadLight(TETile[][] world, int lightx, int lighty, Room room) {
        int maxXRadius = Math.max(room.getX() + room.getWidth() - lightx, lightx - room.getX());
        int maxYRadius = Math.max(room.getY() + room.getHeight() - lighty, lighty - room.getY());

        int maxRadius = Math.max(maxXRadius, maxYRadius);
//        for (int i = 1; i <= maxRadius; i++) {
//            spreadLightH(world, x - i, y + i, i * 2 + 1, i);
//            spreadLightV(world, x - i, y - i, i * 2 + 1, i);
//            spreadLightH(world, x - i, y - i, i * 2 + 1, i);
//            spreadLightV(world, x + i, y - i, i * 2 + 1, i);
//        }

        for (int y = lighty - maxRadius; y < lighty + maxRadius; y++) {
            for (int x = lightx - maxRadius; x < lightx + maxRadius; x++) {
                if (isInRoom(x, y, room)) {
                    if (world[x][y] == Tileset.AVATAR) {
                        world[x][y] = lightintensity(x, y, new Position(lightx, lighty));
                    }
                }
            }
        }
    }

    private TETile lightintensity(int x, int y, Position lightpos) {
        int maxdistance = Math.max(abs(lightpos.x - x), abs(lightpos.y - y));
        if (maxdistance >= 5) {
            return LIGHTS[4];
        } else {
            return LIGHTS[maxdistance];
        }
    }

    public void turnOffLight(TETile[][] world) {
        int x = this.getX();
        int y = this.getY();
        for (int i = x; i < x + this.getWidth(); i++) {
            for (int k = y; k < y + this.getHeight(); k++) {
                if (!(world[i][k] == Tileset.WALL) && (!(world[i][k] == Tileset.WATER)) && (!(world[i][k] == Tileset.UNLOCKED_DOOR))) {
                    world[i][k] = Tileset.AVATAR;
                }
            }
        }
        this.light = false;
    }

    public boolean isInRoom(int x, int y, Room room) {
        if (x >= room.getX() + room.getWidth() || y >= room.getY() + room.getHeight() || x < room.getX() || y < room.getY()) {
            return false;
        }
        return true;
    }

    public void spreadLightH(TETile[][] world, int x, int y, int length, int index) {
        for (int i = 0; i < length; i++) {
            if (isInRoom(x + i, y, this)) {
                TETile t = world[x + i][y];
//                if (t.equals(Tileset.AVATAR)) {
//                    previous = LIGHTS[index - 1];
                if (t.equals(Tileset.AVATAR)) {
                    if (index >= 5) {
                        world[x + i][y] = LIGHTS[4];
                    } else {
                        world[x + i][y] = LIGHTS[index - 1];
                    }
                }
            }
        }
    }

    public void spreadLightV(TETile[][] world, int x, int y, int length, int index) {
        for (int i = 0; i < length; i++) {
            if (isInRoom(x, y + i, this)) {
                TETile t = world[x][y + i];
//                if (t.equals(Tileset.AVATAR)) {
//                    previous = LIGHTS[index - 1];
                if (t.equals(Tileset.AVATAR)) {
                    if (index >= 5) {
                        world[x + i][y] = LIGHTS[4];
                    } else {
                        world[x][y + i] = LIGHTS[index - 1];
                    }
                }
            }
        }
    }

}


