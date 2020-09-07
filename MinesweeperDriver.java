/**
 * Minesweeper driver class which handles command-line arguments.
 */

public class MinesweeperDriver {
    public static void main(String[] args) {
    MinesweeperGame obj = new MinesweeperGame(args[1]);
		obj.play();
}
}
