package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

import java.sql.SQLSyntaxErrorException;
import java.util.Random;
import java.util.*;

import static byow.Core.Engine.HEIGHT;
import static byow.Core.Engine.WIDTH;
import static java.lang.Math.*;

public class GenerateBoard extends Draw {

    //    private static final int WIDTH = 70;
//    private static final int HEIGHT = 40;
    public static Random random;
    private final int MIN_ROOM_NUMBER = 10;
    private final int MAX_ROOM_NUMBER = 20;
    private final int MIN_ROOM_WIDTH_HEIGHT = 4;
    private final int MAX_ROOM_WIDTH_HEIGHT = 10;
    public ArrayList<Room> roomList;
    private HashMap<Room, Integer> roomtoindex;





    public GenerateBoard(Random random) {

        this.random = random;
        this.roomList = new ArrayList<>();
        this.roomtoindex = new HashMap<>();
    }

    public TETile[][] GenerateWorld(TETile[][] tiles) {

        /*        TETile[][] world = new TETile[WIDTH][HEIGHT];*/
        fillwithBlank(tiles);

        int numberofRooms = MIN_ROOM_NUMBER + random.nextInt(MAX_ROOM_NUMBER - MIN_ROOM_NUMBER);

        for (int i = 0; i < numberofRooms; i++) {
            int w = MIN_ROOM_WIDTH_HEIGHT + random.nextInt(MAX_ROOM_WIDTH_HEIGHT - MIN_ROOM_WIDTH_HEIGHT);
            int h = MIN_ROOM_WIDTH_HEIGHT + random.nextInt(MAX_ROOM_WIDTH_HEIGHT - MIN_ROOM_WIDTH_HEIGHT);
            int x = random.nextInt(WIDTH - w - 1) + 1;
            int y = random.nextInt(HEIGHT - h - 1) + 1;

            // create room
            Room newRoom = new Room(new Position(x, y), h, w);
            drawRoom(newRoom, tiles, Tileset.WALL, random);
            fillTheRoom(newRoom, tiles, Tileset.AVATAR);
            roomList.add(newRoom);
        }

        // hash map (room, index)
        WeightedQuickUnionUF wqroom = new WeightedQuickUnionUF(roomList.size());
        Room virtualMiddle = new Room(new Position(0, 0), HEIGHT / 2, WIDTH / 2);
        sortbyDist(roomList, virtualMiddle);

        for (int i = 0; i < roomList.size(); i++) {
            roomtoindex.put(roomList.get(i), i);
        }

        System.out.println(roomList.size());
        for (Room curroom : roomList) {

            // is rooms overlap, union them in wqroom
            for (int i = 0; i < roomList.size(); i++) {
                if (roomList.get(i) == curroom) {
                    continue;
                }

                if (doesOverlap(curroom, roomList.get(i))) {
                    wqroom.union(roomtoindex.get(curroom), roomtoindex.get(roomList.get(i)));
                }
            }

            // connect all neighbors
            ArrayList<Room> neighbors = getNeighbours(curroom, tiles);
            int numConnections = 0;
            for (int i = 0; i < 9; i++) {
                if (!wqroom.connected(roomtoindex.get(curroom), roomtoindex.get(neighbors.get(i)))) {
                    connect(curroom, neighbors.get(i), tiles);
                    wqroom.union(roomtoindex.get(curroom), roomtoindex.get(neighbors.get(i)));
                    numConnections++;
                }
            }
            if (numConnections == 0) {
                for (int i = 0; i < 5; i++) {
                    connect(curroom, neighbors.get(i), tiles);
                    wqroom.union(roomtoindex.get(curroom), roomtoindex.get(neighbors.get(0)));
                }
            }
        }
        drawTreasure(tiles, random);
        addAllLights(tiles);
        return tiles;
    }

    // fill board first, and then hollow


    private void fillwithBlank(TETile[][] tiles) {
        int height = HEIGHT;
        int width = WIDTH;
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                tiles[x][y] = Tileset.NOTHING;
            }
        }
    }

    private void connect(Room room1, Room room2, TETile[][] world) {
        // if rooms overlap, there is no need for hallway
        Room toproom;
        Room botroom;
        if (room1.getY() < room2.getY()) {
            toproom = room2;
            botroom = room1;
        } else {
            toproom = room1;
            botroom = room2;
        }

        Room leftroom;
        Room rightroom;
        if (room1.getX() <= room2.getX()) {
            leftroom = room1;
            rightroom = room2;
        } else {
            leftroom = room2;
            rightroom = room1;
        }
        if (roomExist(room1, world) && roomExist(room2, world)) {
            if (horizontalalign(toproom, botroom, world)) {
                drawHorizontalHallway(toproom, botroom, world);
            } else if (verticalalign(leftroom, rightroom)) {
                drawVerticalHallway(leftroom, rightroom, world);
            }

            // diagonal: one room on the top left, other room on the bottom right
            else if ((((room1.getX() + room1.getWidth() - 1) < room2.getX() + 2) && ((room1.getY() + room1.getHeight() - 1) > (room2.getY() + 2)))
                    || (((room2.getX() + room2.getWidth() - 1) < room1.getX() + 2) && ((room2.getY() + room2.getHeight() - 1) > (room1.getY() + 2)))) {
                drawBentHallwayLeft(room1, room2, world, random);
            }

            // diagonal: one room on the top right, other room on the left bottom
            else if ((((room1.getX() + room1.getWidth() - 1) < room2.getX() + 2) && ((room1.getY() + room1.getHeight() - 1) < room2.getY() + 2))
                    || (((room2.getX() + room2.getWidth() - 1) < room1.getX() + 2) && ((room1.getY() + room1.getHeight() - 1) < (room2.getY() + 2)))) {
                drawBentHallwayRight(room1, room2, world, random);
            }
        }
    }

    private boolean horizontalalign(Room toproom, Room botroom, TETile[][] world) {
        if (toproom.getY() + 2 <= botroom.getY() + botroom.getHeight() - 1) {
            return true;
        }
        return false;
    }

    private boolean verticalalign(Room leftroom, Room rightroom) {
        if (rightroom.getX() + 2 <= leftroom.getX() + leftroom.getWidth() - 1) {
            return true;
        }
        return false;
    }

    private boolean roomExist(Room room, TETile[][] world) {
        for (int i = room.getX(); i < room.getX() + room.getWidth(); i++) {
            for (int k = room.getY(); k < room.getY() + room.getHeight(); k++) {
                if (world[i][k] == Tileset.WALL || world[i][k] == Tileset.AVATAR) {
                    return true;
                }
            }
        }
        return false;
    }

    public ArrayList<Room> getNeighbours(Room curroom, TETile[][] world) {

        List<Double> mintomaxdistance = new ArrayList<>(); // edge
        HashMap<Double, Room> tempneighbours = new HashMap<>();
        ArrayList<Room> neighbors = new ArrayList<>();

        for (int i = 0; i < roomList.size(); i++) {
            // skip if we compare same room
            if (roomList.get(i) == curroom) {
                continue;
            }
            Double distancetoroom = calculateDistance(curroom, roomList.get(i));
            mintomaxdistance.add(distancetoroom);
            tempneighbours.put(distancetoroom, roomList.get(i));
        }

        // sort the list from closest to furthest distance
        Collections.sort(mintomaxdistance);
        for (double distance : mintomaxdistance) {
//            if (distance == 0.0) {
//                continue;
//            }
            // add neighbors in increasing distance order
            neighbors.add(tempneighbours.get(distance));

        }
        return neighbors;
    }

    private Double calculateDistance(Room room1, Room room2) {
        if (doesOverlap(room1, room2)) {
            return 0.0;
        }
        Double distance = min((sqrt(pow((room1.getX() - room2.getX()), 2) + pow((room1.getY() - (room2.getY() + room2.getHeight() - 1)), 2))),
                (sqrt(pow((room1.getX() - room2.getX()), 2) + pow((room1.getY() + room1.getHeight() - 1) - room2.getY(), 2))));
        return distance;
    }

    public boolean doesOverlap(Room room1, Room room2) {
        // if already overlaps, do not draw hallway
        return ((room1.getX() < room2.getX() + room2.getWidth() - 1) && (room2.getX() < room1.getX() + room1.getWidth() - 1)
                && (room1.getY() < room2.getY() + room2.getHeight() - 1) && (room2.getY() < room1.getY() + room1.getHeight() - 1));
    }

    public Position randomHollow(Room room, TETile[][] world) {
        Position hollowposition = null;
        for (int x = room.getX(); x < room.getWidth(); x++) {
            for (int y = room.getY(); y < room.getHeight(); y++) {
                // if tile is not floor
                if ((world[x][y] != Tileset.AVATAR) && (world[x][y] == Tileset.WALL)) {
                    world[x][y] = Tileset.AVATAR;
                    hollowposition = new Position(x, y);
                    break;
                }
            }

            if (hollowposition != null) {
                break;
            }
        }
        return hollowposition;
    }

/*    public static void main(String[] args) {
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        Random RANDOM = new Random(2873123);
        GenerateBoard board = new GenerateBoard(RANDOM);

        TETile[][] world = new TETile[WIDTH][HEIGHT];
        board.GenerateWorld(world);

        ter.renderFrame(world);
    } */

    private void sortbyDist(List<Room> rs, Room room) {
        Comparator<Room> c = new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                Room r1 = (Room) o1;
                Room r2 = (Room) o2;
                return (int) (calculateDistance(r1, room) - calculateDistance(r2, room));
            }
        };
        rs.sort(c);
    }


    public void addAllLights(TETile[][] world) {
        for (int i = 0; i < roomList.size(); i++) {
            Room room = roomList.get(i);
            room.addLight(world);
        }
    }
//
//    public void addLight(TETile[][] world, int i ) {
//        Room r  = roomList.get(i);
//        boolean light = false;
//        while (!light) {
//            int rX = random.nextInt(r.getWidth()) + r.getX();
//            int rY = random.nextInt(r.getHeight()) + r.getY();
//            if (world[rX][rY] == Tileset.AVATAR) {
//                world[rX][rY] = Tileset.LIGHT;
//                spreadLight(world, rX, rY, r);
//                light = true;
//            }
//        }
////        lightSources.put(p, new Position(rX, rY));
//    }
//
//    public void spreadLight(TETile[][] world, int lightx, int lighty, Room room) {
//        int maxXRadius = Math.max(room.getX() + room.getWidth() - lightx, lightx - room.getX());
//        int maxYRadius = Math.max(room.getY() + room.getHeight() - lighty, lighty - room.getY());
//
//        int maxRadius = Math.max(maxXRadius, maxYRadius);
//
//        for (int y = lighty - maxRadius; y < lighty + maxRadius; y++) {
//            for (int x = lightx - maxRadius; x < lightx + maxRadius; x++) {
//                if (isInRoom(x, y, room)) {
//                    if (world[x][y] == Tileset.AVATAR) {
//                        world[x][y] = lightintensity(world, x, y, new Position(lightx, lighty));
//                    }
//                }
//            }
//        }
//    }
//
//    private TETile lightintensity(TETile[][] world, int x, int y, Position lightpos) {
//        int maxdistance = Math.max(abs(lightpos.x - x), abs(lightpos.y - y));
//        if (maxdistance >= 5) {
//            return LIGHTS[4];
//        } else {
//            return LIGHTS[maxdistance];
//        }
//    }
//
//    private void turnOffLight(TETile[][] world, Room room) {
//        int x = room.getX();
//        int y = room.getY();
//        for (int i = x; i < x + room.getWidth(); i++) {
//            for (int k = y; k < y + room.getHeight(); k++) {
//                if (world[i][k] != Tileset.WALL || world[i][k] != Tileset.WATER || world[i][k] != Tileset.UNLOCKED_DOOR) {
//                    world[i][k] = Tileset.AVATAR;
//                }
//            }
//        }
//    }

//        Room r = roomList.get(idx);
//        int maxX = Math.max(r.getX() + r.getWidth() - x, x - r.getX());
//        int maxY = Math.max(r.getY() + r.getHeight() - y, y - r.getY());
//        int length = Math.max(maxX, maxY);
//        for (int i = 1; i <= length; i++) {
//            spreadLightH(world, x - i, y + i, i * 2 + 1, r, i);
//            spreadLightV(world, x - i, y - i, i * 2 + 1, r, i);
//            spreadLightH(world, x - i, y - i, i * 2 + 1, r, i);
//            spreadLightV(world, x + i, y - i, i * 2 + 1, r, i);
//        }
//    }


//    public void spreadLightH(TETile[][] world, int x, int y, int length, Room room, int index) {
//        for (int i = 0; i < length; i++) {
//            if (isInRoom(x + i, y, room)) {
//                TETile t = world[x + i][y];
////                if (t.equals(Tileset.AVATAR)) {
////                    previous = LIGHTS[index - 1];
//                } else if (t.equals(Tileset.FLOWER)) {
//                    flowers.replace(new Position(x + i, y), LIGHTS[index - 1]);
//                } else {
//                    world[x + i][y] = LIGHTS[index - 1];
//                }
//            }
//        }
//    }

//    public void spreadLightV(TETile[][] world, int x, int y, int length, Position p, int index) {
//        for (int i = 0; i < length; i++) {
//            if (withinRoom(x, y + i, p)) {
//                TETile t = world[x][y + i];
//                if (t.equals(Tileset.AVATAR)) {
//                    previous = LIGHTS[index - 1];
//                } else if (t.equals(Tileset.FLOWER)) {
//                    flowers.replace(new Position(x + i, y), LIGHTS[index - 1]);
//                } else {
//                    world[x][y + i] = LIGHTS[index - 1];
//                }
//            }
//        }
//    }

    public boolean isInRoom(int x, int y, Room room) {
        if (x >= room.getX() + room.getWidth() || y >= room.getY() + room.getHeight() || x < room.getX() || y < room.getY()) {
            return false;
        }
        return true;
    }

    public int numberofRooms() {
        return roomList.size();
    }

}
