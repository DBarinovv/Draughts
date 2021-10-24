import java.util.Scanner;

public class Main {
    static Scanner scn = new Scanner(System.in);

    public static void main(String[] args) throws DraughtsException {

        Board board = new Board();

        try {
            InputStartPositions(board);

            if (board.DEBUG) {
                System.out.println("\n");
                board.PrintBoard();
            }

            while (OneStep(board)) {
            }

            PrintAnswer(board);
            if (board.DEBUG) {
                System.out.println("\n\nANSWER:");
                board.PrintBoard();
            }
        }
        catch (DraughtsException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * Fill our board with start values
     * @param board Some Board object (playing desk)
     */
    public static void InputStartPositions(Board board) throws DraughtsException {
        String[] white_draughts = scn.nextLine().split(" ");
        String[] black_draughts = scn.nextLine().split(" ");

        for (String white_draught : white_draughts) {
            board.Add(Utils.CoordsFromString(white_draught), white_draught.matches("[A-H][1-9]") ? 3 : 1);
        }

        for (String black_draught : black_draughts) {
            board.Add(Utils.CoordsFromString(black_draught), black_draught.matches("[A-H][1-9]") ? 4 : 2);
        }
    }

    /**
     * Process one step
     * @param board Some Board object (playing desk)
     * @return if input has ended
     */
    public static boolean OneStep(Board board) throws DraughtsException {
        if (!scn.hasNextLine()) {
            scn.close();
            return false;
        }

        String[] step = scn.nextLine().split(" ");
        String white_step = step[0];
        String black_step = step[1];

        if (white_step.matches("[a-hA-H][0-9]-.*")) {
            if (board.MustEat()) {
                throw new DraughtsException("invalid move");
            }

            board.MoveStep(white_step);

            if (board.DEBUG) {
                System.out.println(white_step);
                board.PrintBoard();
            }
        } else {
            board.EatStep(white_step);

            if (board.DEBUG) {
                System.out.println(white_step);
                board.PrintBoard();
            }
        }

        if (black_step.matches("[a-hA-H][0-9]-.*")) {
            if (board.MustEat()) {
                throw new DraughtsException("invalid move");
            }

            board.MoveStep(black_step);

            if (board.DEBUG) {
                System.out.println(black_step);
                board.PrintBoard();
            }
        } else {
            board.EatStep(black_step);

            if (board.DEBUG) {
                System.out.println(black_step);
                board.PrintBoard();
            }
        }

        return true;
    }

    public static void PrintAnswer(Board board) {
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (board.board[i][j] == 3) {
                    System.out.printf("%c%d ", 'A' + j, i + 1);
                }
            }
        }
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (board.board[i][j] == 1) {
                    System.out.printf("%c%d ", 'a' + j, i + 1);
                }
            }
        }

        System.out.print("\n");

        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (board.board[i][j] == 4) {
                    System.out.printf("%c%d ", 'A' + j, i + 1);
                }
            }
        }
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (board.board[i][j] == 2) {
                    System.out.printf("%c%d ", 'a' + j, i + 1);
                }
            }
        }
    }
}
