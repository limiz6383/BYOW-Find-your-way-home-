package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import org.junit.Test;

import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;
import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;  // Import this class to handle errors
import java.util.Scanner;

public class Game {
    private long seed;
    private Position currPos;
    private String gameString;
    private Random random;
    public TETile[][] world;
    private TETile[][] worldframe;
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;
    private int lives;
    private boolean obstacle = false;
    public static boolean treasure = false;
    public ArrayList<Room> roomList;
    public Game(long seed) {
        this.seed = seed;

        init(seed, 5);
    }

    public Game() {
        try {
            File myfile = new File("game.txt");
            Scanner myreader = new Scanner(myfile);
//            this.seed = Long.parseLong(myreader.nextLine());
////            int xpos = Integer.parseInt(myreader.nextLine());
////            int ypos = Integer.parseInt(myreader.nextLine());
////            this.currPos = new Position(xpos, ypos);
//            this.lives = Integer.parseInt(myreader.nextLine());
            myreader.close();
            init(this.seed, this.lives);
        } catch (FileNotFoundException e) {
            System.out.println("error:((");
            e.printStackTrace();
        }
    }


    private void init(long seed, int remainingLives) {
        random = new Random(seed);
        GenerateBoard board = new GenerateBoard(random);
        roomList = board.roomList;
        worldframe = new TETile[WIDTH][HEIGHT];
        world = board.GenerateWorld(worldframe);
        // currPos is null for some reason
        currPos = startPoint(random);
        world[currPos.x][currPos.y] = Tileset.TREE;
        lives = remainingLives;
    }

    public ArrayList getRoomList() {
        return roomList;
    }

    public void lightscameraaction() {
        System.out.println(roomList);
        Random randnum = new Random();
        int num = randnum.nextInt(roomList.size());
        Room room = roomList.get(num);
        if (room.getLight()) {
            room.turnOffLight(world);
        } else {
            room.addLight(world);
        }
    }


    public void direction(char c) {
        if (c == 'W') {
            setNewPos(currPos.x, currPos.y + 1, currPos.x, currPos.y);
        } else if (c == 'A') {
            setNewPos(currPos.x - 1, currPos.y, currPos.x, currPos.y);
        } else if (c == 'D') {
            setNewPos(currPos.x + 1, currPos.y, currPos.x, currPos.y);
        } else if (c == 'S') {
            setNewPos(currPos.x, currPos.y - 1, currPos.x, currPos.y);
        }
    }

    private void setNewPos(int x, int y, int prex, int prey) {

        if (isWalkable(world[x][y])) {
            currPos = new Position(x, y);
            if (obstacle) {
                world[prex][prey] = Tileset.WATER;
                world[x][y] = Tileset.TREE;
                obstacle = false;
            } else {
                world[prex][prey] = Tileset.AVATAR;
                world[x][y] = Tileset.TREE;
            }
        } else if (world[x][y] == Tileset.WATER) {
            lives--;
            currPos = new Position(x, y);
            world[prex][prey] = Tileset.AVATAR;
            world[x][y] = Tileset.TREE;
            obstacle = true;
        }
    }

    private boolean isWalkable(TETile tile) {
        if (tile.equals(Tileset.UNLOCKED_DOOR)) {
            treasure = true;
        }
        return tile.equals(Tileset.AVATAR) || tile.equals(Tileset.UNLOCKED_DOOR) || tile.equals(Tileset.LIGHT2) || tile.equals(Tileset.LIGHT3) ||
            tile.equals(Tileset.LIGHT4) || tile.equals(Tileset.LIGHT5) || tile.equals(Tileset.LIGHT6) || tile.equals(Tileset.LIGHT);
    }

    private Position startPoint(Random random) {
        while (true) {
            int x = random.nextInt(WIDTH);
            int y = random.nextInt(HEIGHT);
            if (isWalkable(world[x][y]) && world[x][y] != Tileset.WATER) {
                System.out.println("creating start point");
                return new Position(x, y);
            }
        }
    }

//    @source: https://www.w3schools.com/java/java_files_create.asp
    public void saveGameFile() {
        File file = new File("./game.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter writer = new PrintWriter("game.txt", StandardCharsets.UTF_8);
            writer.println(seed);
//            writer.println(currPos.x);
//            writer.println(currPos.y);
            writer.println(lives);
            writer.close();
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    public int getLives() {
        return lives;
    }

    public TETile[][] getWorldframe() {
        return worldframe;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Position getPosition() {
        return currPos;
    }

//    private ArrayList<ArrayList> getvisionRadius(Position user) {
////        System.out.println(user);
//        int xLeft = user.x - 2;
//        int xRight = user.x + 2;
//        int yTop = user.y + 2;
//        int yBottom = user.y - 2;
//        ArrayList<ArrayList> coordinRadius = new ArrayList<>(25);
//        for (int y = yBottom; y <= yTop; y++) {
//            for (int x = xLeft; x <= xRight; x++) {
//                ArrayList newpos = new ArrayList();
//                newpos.add(x);
//                newpos.add(y);
//                coordinRadius.add(newpos);
//            }
//        }
//        return coordinRadius;
//    }

//    private TETile[][] tunnelVisionWorld(TETile[][] oldWorld) {
//        TETile[][] newWorld = new TETile[WIDTH][HEIGHT];
//        ArrayList<ArrayList> coords = getvisionRadius(currPos);
//        for (int i = 0; i < WIDTH; i++) {
//            for (int k = 0; k < HEIGHT; k++) {
//                ArrayList newpos = new ArrayList();
//                newpos.add(i);
//                newpos.add(k);
//                if (coords.contains(newpos)) {
//                    newWorld[i][k] = oldWorld[i][k];
//                } else {
//                    newWorld[i][k] = Tileset.NOTHING;
//                }
//            }
//        }
//        return newWorld;
//    }
}
