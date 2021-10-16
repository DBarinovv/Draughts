import java.util.Arrays;
import java.util.Locale;
import java.util.Scanner;

public class Main {
    static Scanner scn = new Scanner(System.in);

    public static void main(String[] args) {

        Board board = new Board();

        InputStartPositions(board);

        System.out.println("\n");
        board.PrintBoard();

        while (OneStep(board)) {}
    }

    public static void InputStartPositions(Board board) {
        String[] white_draughts = scn.nextLine().split(" ");
        String[] black_draughts = scn.nextLine().split(" ");

        for (String white_draught : white_draughts) {
            board.Add(Utils.CoordsFromString(white_draught), white_draught.matches("[A-H][1-9]") ? 3 : 1);
        }

        for (String black_draught : black_draughts) {
            board.Add(Utils.CoordsFromString(black_draught), black_draught.matches("[A-H][1-9]") ? 4 : 2);
        }
    }

    public static boolean OneStep(Board board) {
        String[] step = scn.nextLine().split(" ");
        String white_step = step[0];

        if (white_step.equals("")) {
            scn.close();
            return false;
        }
        String black_step = step[1];

        if (white_step.matches("[a-hA-H][0-9]-.*")) {
            board.MoveStep(white_step);

            System.out.println(white_step);
            board.PrintBoard();
        }
        else {

        }

        if (black_step.matches("[a-hA-H][0-9]-.*")) {
            board.MoveStep(black_step);

            System.out.println(black_step);
            board.PrintBoard();
        } else {

        }

        return true;
    }

}
