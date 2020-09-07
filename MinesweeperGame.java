import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Minesweeper game class which represents a game of Minesweeper Alpha.
 */
public class MinesweeperGame {
    private int rows;
    private int cols;
    private boolean mineBlasted = false;
    private boolean minesLocated = false;
    private boolean restRevealed = false;
    private String[][] mainMinesweeperArray;
    private boolean[][] withMineArray;
    private boolean[][] isRevealedArray;
    private String[][] noFogArray;
    private int roundsCompleted = 0;
    private int totalMines = 0;
    Scanner keyboard = new Scanner(System.in);
    /**
     * Creates object instance of the {@link MinesweeperGame} class to read contents of seed file.
     *
     * @param filePath the seed file used to setup the game
     */

    public MinesweeperGame(String filePath) {
        try {
            File seedFile = new File(filePath);
            Scanner configScanner = new Scanner(seedFile);
            if (configScanner.hasNextInt()) {
                this.rows = configScanner.nextInt();
                if (rows < 5) {
                    printRowColError();  
                }
                if (configScanner.hasNextInt()) {
                    this.cols = configScanner.nextInt();
                    if (cols < 5) {
                        printRowColError();
                    }
                    if (configScanner.hasNextInt()) {
                        this.totalMines = configScanner.nextInt();
                    }
                } else {
                    printSeedFileError();
                }
                if ((this.totalMines > rows * cols)) {
                    printSeedFileError();
                }
            }
            this.mainMinesweeperArray = new String[rows][cols];
            this.withMineArray = new boolean[rows][cols];
            this.noFogArray = new String[rows][cols];
            this.isRevealedArray = new boolean[rows][cols];
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    mainMinesweeperArray[i][j] = "     ";
                    withMineArray[i][j] = false;
                    isRevealedArray[i][j] = false;
                }
            }
            for (int i = 0; i < totalMines; i++) {
                int mineRow;
                int mineCol;
                if (configScanner.hasNextInt()) {
                    mineRow = configScanner.nextInt();
                    mineCol = configScanner.nextInt();
                    if (mineRow > rows - 1 || mineCol > cols - 1) {
                        System.out.println("Seedfile Format Error: Cannot create game with ");
                        System.out.print(seedFile + ", because it is not formatted correctly.");
                        System.exit(1);
                    } else {
                        withMineArray[mineRow][mineCol] = true;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println(e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Prints System.exit(3).
     */
    private void printRowColError() {
    System.out.println();
                    System.out.print("Seedfile Value Error: "); 
                    System.out.print("Cannot create a mine field");
                    System.out.print("with that many rows and/or columns!");
                    System.exit(3);
    }

    /**
     * Prints System.exit(1).
     */
    private void printSeedFileError() {
        System.out.println("Seedfile Format Error: ");
        System.out.print("Cannot create game with ");
        System.out.print(seedFile + ", because it is not formatted correctly.");
        System.out.println();
        System.exit(1);
    }

    /**
     * Prints welcome banner.
     */
    public void printWelcome() {
        System.out.println("        _\n" +
                        "  /\\/\\ (_)_ __   ___  _____      _____  ___ _ __   ___ _ __\n" +
                        " /    \\| | '_ \\ / _ \\/ __\\ \\ /\\ / / _ \\/ _ \\ '_ \\ / _ \\ '__|\n" +
                        "/ /\\/\\ \\ | | | |  __/\\__ \\\\ V  V /  __/  __/ |_) |  __/ |\n" +
                        "\\/    \\/_|_| |_|\\___||___/ \\_/\\_/ \\___|\\___| .__/ \\___|_|\n" +
                        "                 A L P H A   E D I T I O N |_| v2020.sp");
    }

    /**
     * Shows commands available to user.
     */
    public void commandsAvailable() {
        System.out.println("Commands Available...\n" +
                           " - Reveal: r/reveal row col\n" +
                           " -   Mark: m/mark   row col\n" +
                           " -  Guess: g/guess  row col\n" +
                           " -   Help: h/help\n" +
                           " -   Quit: q/quit");
        roundsCompleted++;
    }

    /**
     * Prints minesweeper grid.
     */
    public void printMineField() {

        for (int i = 0; i < mainMinesweeperArray.length; i++) {
            System.out.print(" " + i + " |");
            for (int j = 0; j < mainMinesweeperArray[i].length; j++) {
                System.out.print(mainMinesweeperArray[i][j]);
                if (j < mainMinesweeperArray[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("|");
        }
        System.out.print("    ");
        for (int k = 0; k < mainMinesweeperArray[0].length; k++) {
            System.out.print("  " + k + "   ");
        }
        System.out.println();
        System.out.println();
    }

    /**
     * Returns the number of mines adjacent to the specified
     * square in the grid.
     *
     * @param row the row index of the square
     * @param col the column index of the square
     * @return the number of adjacent mines
     */
    private int getNumAdjMines(int row, int col) {
        int numAdjMines = 0;
        for (int i = row - 1; i <= row + 1; i++) {
            if (!(i >= 0 && i < mainMinesweeperArray.length)) {
                continue;
            }
            for (int j = col - 1; j <= col + 1; j++) {
                if ((i == row && j == col) || (!(j >= 0 && j < mainMinesweeperArray[0].length))) {
                    continue;
                } else {
                    if (withMineArray[i][j]) {
                        numAdjMines++;
                    }
                }
            }
        }
        return numAdjMines;
    }

    /**
     * Prints current number of rounds completed.
     */
    public void roundsCompleted() {
        System.out.println();
        System.out.println(" " + "Rounds Completed: " + roundsCompleted);
        System.out.println();
    }

    /**
     * Updates isRevealedArray by using getNumAdjMines method. 
     * Square can either be a mine or the number of adjacent mines.
     *
     * @param rowSel the row index
     * @param colSel the col index
     * @return true when revealed
     */
    public boolean revealCoordinates(int rowSel, int colSel) {
        if (!withMineArray[rowSel][colSel]) {
            mainMinesweeperArray[rowSel][colSel] = "  " + getNumAdjMines(rowSel, colSel) + "  ";
            roundsCompleted++;
            isRevealedArray[rowSel][colSel] = true;
            return true;
        } else {
            mineBlasted = true;
            return true;
        }

    }

    /**
     * Marks the desired square with an F.
     *
     * @param rowSel the row index
     * @param colSel the col index
     * @return true when marked
     */
    public boolean markCoordinates(int rowSel, int colSel) {

        if (withMineArray[rowSel][colSel]) {
            isRevealedArray[rowSel][colSel] = true;
            mainMinesweeperArray[rowSel][colSel] = "  F  ";
            roundsCompleted++;
            return true;
        } else {
            mainMinesweeperArray[rowSel][colSel] = "  F  ";
            roundsCompleted++;
            return true;
        }
    }

    /**
     * Marks the desired square with a "?".
     *
     * @param rowSel the row index
     * @param colSel the col index
     * @return true when marked with an "?"
     */
    public boolean guessCoordinates(int rowSel, int colSel) {
        if (true) {
            mainMinesweeperArray[rowSel][colSel] = "  ?  ";
            roundsCompleted++;
            return true;
        }
        return false;
    }

    /**
     * Prints noFogGrid which shows positions of all mines on the grid.
     */
    private void NoFogGrid() {
        for (int i = 0; i < noFogArray.length; i++) {
            System.out.print(i + " |");
            for (int j = 0; j < noFogArray[i].length; j++) {
                if (withMineArray[i][j] && mainMinesweeperArray[i][j].equals("  F  ")) {
                    System.out.print("< " + mainMinesweeperArray[i][j].trim() + " >");
                } else if (withMineArray[i][j]) {
                    System.out.print("<  " + mainMinesweeperArray[i][j].trim() + " >");
                } else {
                    System.out.print(mainMinesweeperArray[i][j]);
                }
                if (j < noFogArray[i].length - 1) {
                    System.out.print("|");
                }
            }
            System.out.println("|");
        }
        System.out.print("    ");
        for (int i = 0; i < mainMinesweeperArray[0].length; i++) {
            System.out.print(" " + i + "    ");
        }
        System.out.println();
    }

    /**
     * Calculates the game score.
     *
     * @return returns score
     */
    public double score() {

        if (roundsCompleted != 0) {
            return mainMinesweeperArray.length * 
            mainMinesweeperArray[0].length * 100.0 / roundsCompleted;
        } else {
            return 0;
        }
    }

    /**
     * Prints win message and exits game.
     */
    public void printWin() {
        System.out.println("\n" +
                           " ░░░░░░░░░▄░░░░░░░░░░░░░░▄░░░░ \"So Doge\"\n" +
                           " ░░░░░░░░▌▒█░░░░░░░░░░░▄▀▒▌░░░\n" +
                           " ░░░░░░░░▌▒▒█░░░░░░░░▄▀▒▒▒▐░░░ \"Such Score\"\n" +
                           " ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░░\n" +
                           " ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐░░░ \"Much Minesweeping\"\n" +
                           " ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░░\n" +
                           " ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒▌░░ \"Wow\"\n" +
                           " ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░░\n" +
                           " ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄▌░\n" +
                           " ░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒▌░\n" +
                           " ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒▐░\n" +
                           " ▐▒▒▐▀▐▀▒░▄▄▒▄▒▒▒▒▒▒░▒░▒░▒▒▒▒▌\n" +
                           " ▐▒▒▒▀▀▄▄▒▒▒▄▒▒▒▒▒▒▒▒░▒░▒░▒▒▐░\n" +
                           " ░▌▒▒▒▒▒▒▀▀▀▒▒▒▒▒▒░▒░▒░▒░▒▒▒▌░\n" +
                           " ░▐▒▒▒▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▒▄▒▒▐░░\n" +
                           " ░░▀▄▒▒▒▒▒▒▒▒▒▒▒░▒░▒░▒▄▒▒▒▒▌░░\n" +
                           " ░░░░▀▄▒▒▒▒▒▒▒▒▒▒▄▄▄▀▒▒▒▒▄▀░░░ CONGRATULATIONS!\n" +
                           " ░░░░░░▀▄▄▄▄▄▄▀▀▀▒▒▒▒▒▄▄▀░░░░░ YOU HAVE WON!\n" +
                           " ░░░░░░░░░▒▒▒▒▒▒▒▒▒▒▀▀░░░░░░░░ SCORE: \n" + score());

        System.exit(0);
    }

    /**
     * Checks if conditions to win are met.
     * 
     * @return returns true if all conditions to win the game are met
     */
    public boolean isWon() {
        boolean hasWon = true;

        for (int x = 0; x < isRevealedArray.length; x++) {
            for (int y = 0; y < isRevealedArray[0].length; y++) {
                if (!isRevealedArray[x][y]) {
                    hasWon = false;
                }
            }
        }
        return hasWon;
    }

    /**
     * Prints message when lost and exits game.
     */
    public void printLoss() {
        System.out.print(
            " Oh no... You revealed a mine!\n" +
            "  __ _  __ _ _ __ ___   ___    _____   _____ _ __\n" +
            " / _` |/ _` | '_ ` _ \\ / _ \\  / _ \\ \\ / / _ \\ '__|\n" +
            "| (_| | (_| | | | | | |  __/ | (_) \\ V /  __/ |\n" +
            " \\__, |\\__,_|_| |_| |_|\\___|  \\___/ \\_/ \\___|_|\n" +
            " |___/\n");
        System.exit(0);

    }

    /**
     * Checks the condition that makes the user lose the game.
     * 
     * @return returns true if all conditions to lose the game are met
     */
    public boolean isLost() {
        return mineBlasted;
    }

    /**
     * Prints game prompt to standard output and interprets user input from standard input.
     */
    public void promptUser() {
        System.out.print("minesweeper-alpha: ");
        String commands = keyboard.next();

        if (commands.equals("h") || commands.equals("help")) {
            commandsAvailable();
            roundsCompleted();
            printMineField();
        } else if (commands.equals("q") || commands.equals("quit")) {
            System.out.println("Quitting the game...\n" +
                               "Bye!");
            System.exit(0);
        } else if (commands.equals("nofog")) {
            roundsCompleted();
            NoFogGrid();
            System.out.println();
        } else if (commands.equals("m") || commands.equals("mark") || commands.equals("r")
            || commands.equals("reveal") || commands.equals("g") || commands.equals("guess")) {
            int row = keyboard.nextInt();
            int col = keyboard.nextInt();

            if (row < 0 || col < 0 || row > rows || col > cols) {
                System.out.println();
                System.out.println("Input Error: Command not recognized!\"");
            } else {
                switch (commands) {
                case "mark":
                case "m":
                    markCoordinates(row, col);
                    roundsCompleted();
                    printMineField();
                    break;
                case "reveal":
                case "r":
                    revealCoordinates(row, col);
                    roundsCompleted();
                    printMineField();
                    break;
                case "guess":
                case "g":
                    guessCoordinates(row, col);
                    roundsCompleted();
                    printMineField();
                    break;
                }
            }
        } else {
            System.out.println();
            System.out.println("Input Error: Command not recognized!\"");
            printMineField();
        }
        if (isLost()) {
            printLoss();
        } else if (isWon()) {
            printWin();
        }
    }

    /**
     * Initiates and executes main game loop.
     */
    public void play() {
        printWelcome();
        roundsCompleted();
        printMineField();
        while (!minesLocated && !restRevealed) {
            promptUser();
        }
    }
}
