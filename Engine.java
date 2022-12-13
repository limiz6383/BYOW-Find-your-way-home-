package byow.Core;

import byow.InputDemo.InputSource;
import byow.InputDemo.KeyboardInputSource;
import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.*;
import java.util.Random;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 70;
    public static final int HEIGHT = 40;
    private Game game;

    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        setUpGame();
        drawMenuFrame();
        while (!StdDraw.hasNextKeyTyped()) {
            StdDraw.pause(1000);
        }

        InputSource input = new KeyboardInputSource();
        char typed;
        String alrtyped = "";
        game = null;
        long seed = 0;
        boolean styped = false;

        StdDraw.setFont();
        ter.initialize(WIDTH, HEIGHT + 3);
        TETile[][] finalWorldFrame;
        while (input.possibleNextInput()) {
            if (StdDraw.hasNextKeyTyped()) {
                typed = input.getNextKey();
                alrtyped += typed;

                if (alrtyped.contains("N")) {

                    // ask user for a seed
                    if (!alrtyped.contains("S")) {
                        drawFrame("Please enter a seed: ");
                    }

                    if (!alrtyped.contains("S") && Character.isDigit(typed))  {
                        seed = seed * 10 + Character.getNumericValue(typed);
                        drawFrame("Please enter a seed: " + seed);
                    }

                    // only creates game once
                    if (!styped && typed == 'S') {
                        System.out.print("sameline");
                        game = new Game(seed);
                        styped = true;
                        System.out.println("styped");
                        finalWorldFrame = game.getWorldframe();
                        ter.renderFrame(finalWorldFrame);
                        System.out.println("yes");
                    }

                    if (styped) {

                        if (typed == 'W' || typed == 'A' || typed == 'S' || typed == 'D') {
                            game.direction(typed);
                        }
                        
//                        if (typed == 'T') {
//                            System.out.println("tunnel");
//                            game.saveGameFile();
//                            tunnel = true;c
//                            Game tunnelVersion = new Game();
//                            finalWorldFrame = tunnelVersion.getWorldframe();
//                            ter.renderFrame(finalWorldFrame);
//                        }
                        if (typed == 'L') {
                            game.lightscameraaction();

                        }

                        if (game.getLives() == 0) {
                            drawFrame("Game over! You don't have any lives left :(");
                            StdDraw.pause(3000);
                            StdDraw.clear(Color.black);

                            drawMenuFrame();
                            StdDraw.pause(2000);
                            interactWithKeyboard();
                        }

                        // if they reached home
                        if (Game.treasure) {
                            Game.treasure = false;
                            drawFrame("Congrats! You found your home :)");
                            StdDraw.pause(3000);
                            StdDraw.clear(Color.black);

                            drawMenuFrame();
                            StdDraw.pause(3000);
                            interactWithKeyboard();
                        }

                        int delay = 0;
                        while (true) {
                            if (delay < 1000) {
                                delay++;

                                TETile mouseTile = game.getWorldframe()[mousePosition().x][mousePosition().y];
                                String tileDesc = mouseTile.description();
                                HUD(tileDesc, "Lives left: " + game.getLives());
                                ter.renderFrame(game.getWorldframe());
                                if (StdDraw.hasNextKeyTyped()) {
                                    break;
                                }
                            }
                        }

                        if (typed == ':') {
                            typed = input.getNextKey();
                            alrtyped += typed;
                            if (typed == 'Q') {
                                game.saveGameFile();
                                drawFrame("You have exited the game. Press 'L' to continue the game.");
                                drawMenuFrame();
                                if (typed == 'N') {
                                    interactWithKeyboard();
                                }
                            }
                        }

//                        if (typed == 'L') {
//                            Game continued = new Game();
//                            TETile[][] worldFrame = continued.getWorldframe();
//                            ter.renderFrame(worldFrame);
//                            String tileDesc = worldFrame[mousePosition().x][mousePosition().y].description();
//                            HUD(tileDesc, "Lives left: " + continued.getLives());
//                        }
                    }
                }

            }
        }
    }

    private void setUpGame() {
        StdDraw.setCanvasSize(WIDTH*16, HEIGHT*16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();
    }
    private void drawMenuFrame() {
     StdDraw.setPenColor(Color.PINK);
     Font fontBig = new Font("Monaco", Font.BOLD, 50);
     StdDraw.setFont(fontBig);
     StdDraw.text(WIDTH/2, HEIGHT/2 + 5, "FIND YOUR WAY HOME");

     StdDraw.setPenColor(Color.WHITE);
     Font subFont = new Font("Monaco", Font.PLAIN, 20);
     StdDraw.setFont(subFont);
     StdDraw.text(WIDTH/2, HEIGHT/2 - 5, "New Game (N)");
     StdDraw.text(WIDTH/2, HEIGHT/2 - 7, "Load Game (L)");
     StdDraw.text(WIDTH/2, HEIGHT/2 - 9, "Quit (Q)");

     StdDraw.show();
    }

    private void drawFrame(String s) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        Font subFont = new Font("Monaco", Font.PLAIN, 20);
        /*StdDraw.setFont(subFont);*/
        StdDraw.text(WIDTH/2, HEIGHT/2, s);
        StdDraw.show();
    }

    private void HUD(String tile, String health) {
        StdDraw.setPenColor(Color.WHITE);
        Font HUD = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(HUD);
        StdDraw.line(0, HEIGHT, WIDTH, HEIGHT);
        StdDraw.textLeft(2, HEIGHT + 1, health);
        StdDraw.textRight(WIDTH - 2, HEIGHT + 1, tile);

        StdDraw.show();
    }

    private Position mousePosition() {
        int mouseX = Math.min((int) StdDraw.mouseX(), WIDTH - 1);
        int mouseY = Math.min((int) StdDraw.mouseY(), HEIGHT - 1);
        return new Position(mouseX, mouseY);
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        ter.initialize(WIDTH, HEIGHT);

        String[] parse = input.split("[nNsS]"); //split into an array of individual characters

        TETile[][] finalWorldFrame = null;
        if (input.charAt(0) == 'N' || input.charAt(0) =='n') {

            long randomInt = Long.parseLong(parse[1]);
            Random random = new Random(randomInt);
            GenerateBoard board = new GenerateBoard(random);

            finalWorldFrame = new TETile[WIDTH][HEIGHT];
            board.GenerateWorld(finalWorldFrame);

            if (input.charAt(input.length() - 1) == 'S' || input.charAt(input.length() - 1) == 's') {
                ter.renderFrame(finalWorldFrame);
            }
        }
        return finalWorldFrame;
    }
}

//for i in world width:
//    for j in world length:
//        if within the radius:
//            world[i][j]tile
//    world[i][j] = black