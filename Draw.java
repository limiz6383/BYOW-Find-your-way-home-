package byow.Core;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.*;

public class Draw {
    private int totalObs = 0;
    private final int minObs = 15;
    private final int WIDTH = 70;
    private final int HEIGHT = 40;
    private final int numTreasure = 2;
    private int treasure = 0;

    // check if x and y are out of bound

    public boolean TileinBound(int x, int y, TETile[][] world) {
        if (x < 0 || x >= world.length || y < 0 || y >= world.length) {
            return false;
        }
        return true;
    }


    private void drawVertical(int x, int y, int dest, TETile tile, TETile[][] world) {
        for (int i = 0; i < dest - y; i++) {
            if (TileinBound(x, y+i, world)) {
                if (world[x][y + i] == Tileset.AVATAR) {
                    continue;
                }
                world[x][y + i] = tile;
            } else {
                break;
            }
        }
    }

    private void drawHorizontal(int x, int y, int dest, TETile tile, TETile[][] world) {
        for (int i = 0; i < dest - x; i++) {
            if (TileinBound(x + i, y, world)) {
                if (world[x + i][y] == Tileset.AVATAR) {
                    continue;
                }
                world[x + i][y] = tile;
            } else {
                break;
            }
        }
    }

    public void drawRoom(Room room, TETile[][] world, TETile tile, Random random) {
        int height = room.getHeight();
        int width = room.getWidth();
        int x = room.getX();
        int y = room.getY();
        drawHorizontal(x, y, x + width, tile, world); //bottom
        drawHorizontal(x, y + height - 1, x + width, tile, world); // top
        drawVertical(x, y + 1, y + height, tile, world); // left
        drawVertical(x + width - 1, y + 1, y + height, tile, world); // right
//        this.random = new Random();
        int num = random.nextInt(2);
        if (num == 0) {
            drawObstacles(room, world, random);
            totalObs += 1;
        }
        if (totalObs < minObs) {
            drawObstacles(room, world, random);
            totalObs += 1;
        }

    }

    public void drawVerticalHallway(Room room1, Room room2, TETile[][] world) {
        int room1y = room1.getY();
        int room2y = room2.getY();
        Room toproom;
        Room bottomroom;
        if (room1y + room1.getHeight() > room2y + room2.getHeight()) {
            toproom = room1;
            bottomroom = room2;
        } else {
            toproom = room2;
            bottomroom = room1;
        }
        Room moreRightRoom = topOrBottom(toproom, bottomroom);
        int x1 = moreRightRoom.getX();
        int y = bottomroom.getY() + bottomroom.getHeight() - 1;
        // if x1 and y are the same
        int dest = toproom.getY();
        drawVertical(x1, y, dest, Tileset.WALL, world);
        int x2 = x1 + 2;
        drawVertical(x2, y, dest, Tileset.WALL, world);
        for (int i = y; i <= dest; i++) {
            world[x1 + 1][i] = Tileset.AVATAR;
        }
        // cave a hole in the room
        // create hallway (drawRoom)
    }

    public void drawHorizontalHallway(Room room1, Room room2, TETile[][] world) {
        Room leftroom;
        Room rightroom;
        if (room1.getX() < room2.getX()) {
            leftroom = room1;
            rightroom = room2;
        } else {
            leftroom = room2;
            rightroom = room1;
        }
        Room lowerRoom = leftOrRight(leftroom, rightroom);
        int x = leftroom.getX() + leftroom.getWidth() - 1;
        int y1 = lowerRoom.getY() + lowerRoom.getHeight() - 1;
        int dest = lowerRoom.getX() + 1;
        drawHorizontal(x, y1, dest, Tileset.WALL, world);

        int y2 = y1 - 2;
        drawHorizontal(x, y2, dest, Tileset.WALL, world);
        for (int i = x; i <= dest; i++) {
            world[i][y1 - 1] = Tileset.AVATAR;
        }
    }

    public void drawBentHallwayLeft(Room room1, Room room2, TETile[][] world, Random random) {
        int room1y = room1.getY();
        int room2y = room2.getY();
        Room toproom;
        Room bottomroom;
        if (room1y + room1.getHeight() > room2y + room2.getHeight()) {
            toproom = room1;
            bottomroom = room2;
        } else {
            toproom = room2;
            bottomroom = room1;
        }
//        random = new Random();
        int num = random.nextInt(3);
        if (num == 0) {
            rightDown(toproom, bottomroom, world);
        } else if (num == 1) {
            downRight(toproom, bottomroom, world);
        } else {
            rightDown(toproom, bottomroom, world);
            downRight(toproom, bottomroom, world);
        }
    }

    public void drawBentHallwayRight(Room room1, Room room2, TETile[][] world, Random random) {
        int room1y = room1.getY();
        int room2y = room2.getY();
        Room toproom;
        Room bottomroom;
        if (room1y + room1.getHeight() > room2y + room2.getHeight()) {
            toproom = room1;
            bottomroom = room2;
        } else {
            toproom = room2;
            bottomroom = room1;
        }
//        random = new Random();
        int num = random.nextInt(3);
        if (num == 0) {
            leftDown(toproom, bottomroom, world);
        } else if (num == 1) {
            downLeft(toproom, bottomroom, world);
        } else {
            leftDown(toproom, bottomroom, world);
            downLeft(toproom, bottomroom, world);
        }
    }

    private Room topOrBottom(Room room1, Room room2) {
        if (room1.getX() == max(room1.getX(), room2.getX())) {
            return room1;
        }
        return room2;
    }

    private Room leftOrRight(Room room1, Room room2) {
        int room1y = room1.getY() + room1.getHeight();
        int room2y = room2.getY() + room2.getHeight();
        if (room1y == min(room1y, room2y)) {
            return room1;
        }
        return room2;
    }

    private void rightDown(Room top, Room bottom, TETile[][] world) {
        int x1 = top.getX() + top.getWidth() - 1;
        int y1 = top.getY();
        int y2 = y1 + 2;
        int dest1 = bottom.getX() + 1;
        int dest2 = dest1 + 2;
        drawHorizontal(x1, y1, dest1, Tileset.WALL, world);
        drawHorizontal(x1, y2, dest2, Tileset.WALL, world);
        for (int i = x1; i <= dest1; i++) {
            world[i][y1 + 1] = Tileset.AVATAR;
        }

        int y3 = bottom.getY() + bottom.getHeight() - 1;
        drawVertical(bottom.getX(), y3 , y1, Tileset.WALL, world);
        drawVertical(bottom.getX() + 2, y3, y2, Tileset.WALL, world);
        for (int i = y3; i <= y1 + 1; i++) {
            world[dest1][i] = Tileset.AVATAR;
        }
    }

    private void downRight(Room top, Room bottom, TETile[][] world) {
        int x1 = top.getX() + top.getWidth() - 1;
        int y1 = bottom.getY() + bottom.getHeight() - 1;
        int y2 = y1 - 2;
        int dest1 = top.getY();
        int x2 = x1 - 2;
        drawVertical(x1, y1, dest1, Tileset.WALL, world);
        drawVertical(x2, y2, dest1, Tileset.WALL, world);
        for (int i = y1 -1; i <= dest1; i++) {
            world[x1 - 1][i] = Tileset.AVATAR;
        }

        int x3 = x2 + 1;
        int dest2 = bottom.getX();
        drawHorizontal(x3, y2, dest2, Tileset.WALL, world);
        drawHorizontal(x1, y1, dest2, Tileset.WALL, world);
        for (int i = x3; i <= dest2; i++) {
            world[i][y1 - 1] = Tileset.AVATAR;
        }
    }

    private void leftDown(Room top, Room bottom, TETile[][] world) {
        int x1 = bottom.getX() + bottom.getWidth() - 1;
        int y1 = bottom.getY() + bottom.getHeight() - 1;
        int x2 = x1 - 2;
        int dest1 = top.getY();
        int dest2 = dest1 + 2;
        drawVertical(x1, y1, dest1, Tileset.WALL, world);
        drawVertical(x2, y1, dest2, Tileset.WALL, world);
        for (int i = y1; i <= dest1 + 1; i++) {
            world[x1 - 1][i] = Tileset.AVATAR;
        }

        int dest3 = top.getX() + 1;
        drawHorizontal(x1, dest1, dest3, Tileset.WALL, world);
        drawHorizontal(x2, dest2, dest3, Tileset.WALL, world);
        for (int i = x1 - 1; i <= dest3; i++) {
            world[i][dest1 + 1] = Tileset.AVATAR;
        }
    }

    private void downLeft(Room top, Room bottom, TETile[][] world) {
        int x1 = top.getX();
        int y1 = bottom.getY() + bottom.getHeight() - 1;
        int y2 = y1 - 2;
        int dest1 = top.getY();
        int x2 = x1 + 2;
        drawVertical(x1, y1, dest1, Tileset.WALL, world);
        drawVertical(x2, y2, dest1, Tileset.WALL, world);
        for (int i = y1 - 1; i <= dest1; i++) {
            world[x1 + 1][i] = Tileset.AVATAR;
        }

        int x3 = bottom.getX() + bottom.getWidth() - 1;
        drawHorizontal(x3, y1, x1, Tileset.WALL, world);
        drawHorizontal(x3, y2, x2, Tileset.WALL, world);
        for (int i = x3; i <= x1 + 1; i++) {
            world[i][y1 -1] = Tileset.AVATAR;
        }
    }

    public void fillTheRoom(Room room, TETile[][] world, TETile tile) {
        int x = room.getX()+ 1;
        int y = room.getY() + 1;
        int h = room.getHeight() - 2;
        int w = room.getWidth() - 2;
        for (int i = y; i < y + h; i++) {
            for (int k = x; k < x + w; k++) {
                if (world[k][i] == Tileset.WATER) {
                    continue;
                }
                world[k][i] = tile;
            }
        }
    }

    public void drawObstacles(Room room, TETile[][] world, Random random) {
//        this.random = new Random();
        int roomx = room.getX();
        int roomy = room.getY();
        boolean drawn = false;
        while (drawn == false) {
            int randomx = roomx + random.nextInt(room.getWidth());
            int randomy = roomy + random.nextInt(room.getHeight());
            if (!(world[randomx][randomy] == Tileset.WALL)) {
                world[randomx][randomy] = Tileset.WATER;
                drawn = true;
            }
        }
    }

//    public void drawtreasure(Room room, TETile[][] world) {
//        this.random = new Random();
//        int roomx = room.getX();
//        int roomy = room.getY();
//        boolean drawn = false;
//        while (drawn == false) {
//            int randomx = roomx + random.nextInt(room.getWidth());
//            int randomy = roomy + random.nextInt(room.getHeight());
//            if (!(world[randomx][randomy] == Tileset.WALL) && !(world[randomx][randomy] == Tileset.WATER)) {
//                world[randomx][randomy] = Tileset.UNLOCKED_DOOR;
//                drawn = true;
//            }
//        }
//    }

    public void drawTreasure(TETile[][] world, Random random) {
//        this.random = new Random();
        while(treasure < numTreasure) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (world[x][y] == Tileset.AVATAR) {
                world[x][y] = Tileset.UNLOCKED_DOOR;
                treasure++;
            }
        }
    }


}
