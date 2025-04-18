package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;
import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Engine {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 40;
    private World world;
    private long SEED;

    /**
     * Method used for exploring a fresh tiles. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        displayMainMenu();
        boolean running = true;
        while (running) {
            if (StdDraw.hasNextKeyTyped()) {
                char mode = Character.toUpperCase(StdDraw.nextKeyTyped());
                running = handleMenuInput(mode);
            }
        }
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
     * In other words, both of these calls:
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
        // Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        InputParserResult parsed = parseInput(input);
        this.SEED = parsed.SEED;

        if (parsed.isNewGame) {
            world = new World(SEED);
            world.putPlayer();
        } else if (parsed.isLoad) {
            load();
        }

        // execute commands
        for (char command : parsed.commands.toCharArray()) {
            moveAvatar(command);
        }

        // save and quit
        if (parsed.saveAndQuit) {
            saveAndQuit();
        }

        return world.getTiles();
    }

    private InputParserResult parseInput(String input) {
        InputParserResult result = new InputParserResult();
        // change all letters to upper case
        input = input.toUpperCase();

        // if end with :Q, save game and quit after commands
        if (input.endsWith(":Q")) {
            result.saveAndQuit = true;
            input = input.substring(0, input.length() - 2);
        }

        char mode = input.charAt(0);
        if (mode == 'N') {
            result.isNewGame = true;
            Pattern p = Pattern.compile("N(\\d+)S([A-Z]*)");
            Matcher m = p.matcher(input);
            if (m.matches()) {
                result.SEED = Long.parseLong(m.group(1));
                result.commands = m.group(2);
            }
        } else if (mode == 'L') {
            result.isLoad = true;
            result.commands = input.substring(1);
        }
        return result;
    }

    public class InputParserResult {
        private boolean isNewGame = false;
        private boolean isLoad = false;
        private long SEED = 0;
        private String commands = "";
        private boolean saveAndQuit = false;
    }

    private void displayMainMenu() {
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        Font font = new Font("Monaco", Font.BOLD, 40);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 10, "CS61B: THE GAME");
        font = new Font("Monaco", Font.PLAIN, 20);
        StdDraw.setFont(font);
        StdDraw.text(WIDTH / 2, HEIGHT / 2 + 2, "New Game (N)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2, "Load Game (L)");
        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 2, "Quit (Q)");
        StdDraw.show();
    }

    private boolean handleMenuInput(char key) {
        switch (key) {
            case 'N':
                newGame();
                startGame();
                break;
            case 'L':
                load();
                startGame();
                break;
            case 'Q':
//                System.exit(0);
                break;
            default:
                drawFrame("Invalid mode: " + key + ", bye!");
                StdDraw.pause(2000);
                break;
        }
        return false;
    }

    private void newGame() {
        SEED = getSeedFromKeyboard();
        world = new World(SEED);
        world.putPlayer();
    }

    private void load() {
        try (BufferedReader reader = new BufferedReader(new FileReader("savedgame.txt"))) {
            // use the old seed to restore world
            SEED = Long.parseLong(reader.readLine());
            world = new World(SEED);
            // get the position of avatar and put in world
            String[] positionParts = reader.readLine().split(" ");
            int x = Integer.parseInt(positionParts[0]);
            int y = Integer.parseInt(positionParts[1]);
            world.putPlayer(x, y);
        } catch (IOException o) {
            o.printStackTrace();
        }
    }

    private void startGame() {
        ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        char commandBuffer = '\0';  // for dealing with ":Q"
        while (true) {
            ter.renderFrame(world.getTiles());
            displayHoverInfo();

            if (StdDraw.hasNextKeyTyped()) {
                char command = Character.toUpperCase(StdDraw.nextKeyTyped());

                if (commandBuffer == ':' && command == 'Q') {
                    saveAndQuit();
                    return;
                } else if (command == ':') {
                    commandBuffer = command;    // waiting for Q
                } else {
                    moveAvatar(command);
                    commandBuffer = '\0';   // clear buffer
                }
            }
        }
    }

    private void saveAndQuit() {
        try (FileWriter writer = new FileWriter("savedgame.txt")) {
            writer.write(SEED + "\n");
            writer.write(world.getPlayerPosition().toString());
        } catch (IOException o) {
            o.printStackTrace();
        }
//        System.exit(0);
    }

    private void displayHoverInfo() {
        int mouseX = (int) StdDraw.mouseX();
        int mouseY = (int) StdDraw.mouseY();
        if (0 <= mouseX && mouseX < WIDTH && 0 <= mouseY && mouseY < HEIGHT) {
            TETile tile = world.getTile(mouseX, mouseY);
            StdDraw.setPenColor(Color.WHITE);
            StdDraw.textLeft(1, HEIGHT - 1, tile.description());
            StdDraw.show();
        }
    }

    private long getSeedFromKeyboard() {
        String s = "";
        char ch;
        drawFrame("Please type the seed, end with s");
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                ch = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (ch == 'S') {
                    return Long.parseLong(s);
                }
                s += ch;
                drawFrame("Seed: " + s);
            }
        }
    }

    public void drawFrame(String s) {
        // Take the string and display it in the center of the screen
        StdDraw.clear(Color.BLACK);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setPenColor(Color.WHITE);

        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    public void moveAvatar(char command) {
        switch (command) {
            case 'W':
                world.movePlayer(0, 1);
                break;
            case 'A':
                world.movePlayer(-1, 0);
                break;
            case 'S':
                world.movePlayer(0, -1);
                break;
            case 'D':
                world.movePlayer(1, 0);
                break;
            default:
                break;
        }
    }
}
