import java.util.ArrayList;

public class Board {
    int[][] board = new int[8][8]; // 0 - empty, 1 - white, 2 - black, 3 - white queen, 4 - black queen
    int whose_step = 1; // 1 - white, 0 - black
    boolean DEBUG = false;

    /**
     * Process one step move for ordinary draught
     * @param step string of 2 characters
     */
    public void MoveStep(String step) throws DraughtsException {
        Coords pos_from = new Coords(Utils.CoordsFromString(step));

        if (GetColor(pos_from) % 2 != whose_step) {
            throw new DraughtsException("error");
        }

        Coords pos_to = new Coords(Utils.CoordsFromString(step.substring(3)));

        if (IsQueen(pos_from)) {
            MoveStepQueen(pos_from, pos_to);
            return;
        }

        if (Math.abs(pos_to.GetX() - pos_from.GetX()) != 1) {
            throw new DraughtsException("invalid move");
        }

        if (Math.abs(pos_to.GetY() - pos_from.GetY()) != 1) {
            throw new DraughtsException("white cell");
        }

        if (!CheckPosition(pos_to)){
            throw new DraughtsException("invalid move");
        }

        if (GetColor(pos_to) != 0) {
            throw new DraughtsException("busy cell");
        }

        MakeQueenIfPossible(pos_from, pos_to);

        board[pos_from.GetX()][pos_from.GetY()] = 0;

        whose_step = 1 - whose_step;
    }

    /**
     * If we go to the last line a draught changes to queen
     * @param pos_from Position from where we go
     * @param pos_to Position where we go
     */
    private void MakeQueenIfPossible(Coords pos_from, Coords pos_to) {
        if (pos_to.GetX() == 7 && GetColor(pos_from) == 1) {
            board[pos_to.GetX()][pos_to.GetY()] = 3;
        } else if (pos_to.GetX() == 0 && GetColor(pos_from) == 2) {
            board[pos_to.GetX()][pos_to.GetY()] = 4;
        }
        else {
            board[pos_to.GetX()][pos_to.GetY()] = GetColor(pos_from);
        }
    }

    /**
     * Process one step move for a queen
     * @param pos_from Position from where we go
     * @param pos_to Position where we go
     */
    public void MoveStepQueen(Coords pos_from, Coords pos_to) throws DraughtsException {
        Coords dir_vec = DifCoords(pos_to, pos_from);

        if (!IsNormalVector(dir_vec)) {
            throw new DraughtsException("invalid move");
        }

        dir_vec.MakeNormal();

        Coords next_coords = new Coords(pos_from);

        while (next_coords.GetX() != pos_to.GetX() && next_coords.GetY() != pos_to.GetY()) {
            next_coords.Move(dir_vec);

            if (!InBoard(next_coords) || GetColor(next_coords) != 0) {
                throw new DraughtsException("busy cell");
            }
        }

        board[pos_to.GetX()][pos_to.GetY()] = GetColor(pos_from);
        board[pos_from.GetX()][pos_from.GetY()] = 0;

        whose_step = 1 - whose_step;
    }

    /**
     * Process one eat step for ordinary draught
     * @param step string of 2 characters
     */
    public void EatStep(String step) throws DraughtsException {
        String[] StepArr = step.split(":");
        Coords[] CoordsArr = new Coords[StepArr.length];
        ArrayList<Coords> eaten = new ArrayList<Coords>();

        for (int i = 0; i < StepArr.length; i++) {
            CoordsArr[i] = Utils.CoordsFromString(StepArr[i]);
        }

        if (GetColor(Utils.CoordsFromString(StepArr[0])) % 2 != whose_step) {
            throw new DraughtsException("error");
        }

        for (int i = 0; i < CoordsArr.length; i++) {
            if (IsQueen(CoordsArr[i])) {
                EatStepQueen(CoordsArr, i, eaten, StepArr);
                ClearEatenDaughts(eaten);
                return;
            }

            if (i == 0) {
                continue;
            }

            if (GetColor(CoordsArr[i]) != 0) {
                throw new DraughtsException("invalid move");
            }
            Coords pos_from = CoordsArr[i - 1];
            Coords pos_to   = CoordsArr[i];
            Coords dir_vec = DifCoords(pos_to, pos_from);

            if (!IsNormalVector(dir_vec)) {
                throw new DraughtsException("invalid move");
            }

            if (Math.abs(dir_vec.GetX()) != 2 || Math.abs(dir_vec.GetY()) != 2) {
                throw new DraughtsException("invalid move");
            }

            Coords pos_betw = Average(pos_from, pos_to);
            if (!DifColors(pos_from, pos_betw)) {
                throw new DraughtsException("invalid move");
            }

            MakeQueenIfPossible(pos_from, pos_to);
            if (eaten.contains(pos_betw)) {
                throw new DraughtsException("error");
            }

            eaten.add(pos_betw);
            board[pos_from.GetX()][pos_from.GetY()] = 0;

            if (i != CoordsArr.length - 1) {
                if (DEBUG) {
                    System.out.println(StepArr[i - 1] + ":" + StepArr[i]);
                    PrintBoard();
                }
            }
        }

        whose_step = 1 - whose_step;

        ClearEatenDaughts(eaten);
    }

    /**
     * Process one eat step for a queen
     * @param CoordsArr Array if step coords
     * @param start_ind First interesting elem in CoordsArr
     * @param eaten Array of already eaten draughts (on this step)
     * @param StepArr Array if step coords in string representation
     */
    public void EatStepQueen (Coords[] CoordsArr, int start_ind, ArrayList<Coords> eaten, String[] StepArr) throws DraughtsException {
        for (int i = start_ind; i < CoordsArr.length; i++) {
            if (i == 0) {
                continue;
            }

            Coords pos_from = CoordsArr[i - 1];
            Coords pos_to   = CoordsArr[i];
            Coords dir_vec  = DifCoords(pos_to, pos_from);

            if (!IsNormalVector(dir_vec)) {
                throw new DraughtsException("invalid move");
            }

            dir_vec.MakeNormal();

            Coords next_coords = new Coords(pos_from);

            int obstacles_cnt = 0;
            while (next_coords.GetX() != pos_to.GetX() && next_coords.GetY() != pos_to.GetY()) {
                next_coords.Move(dir_vec);

                if (!InBoard(next_coords) || GetColor(next_coords) != 0) {
                    if (eaten.contains(next_coords)) {
                        throw new DraughtsException("invalid move");
                    }

                    if (obstacles_cnt == 0) {
                        obstacles_cnt++;
                        eaten.add(new Coords(next_coords));
                    }
                    else {
                        throw new DraughtsException("busy cell");
                    }
                }
            }

            board[pos_to.GetX()][pos_to.GetY()] = GetColor(pos_from);
            board[pos_from.GetX()][pos_from.GetY()] = 0;

            if (i != CoordsArr.length - 1) {
                if (DEBUG) {
                    System.out.println(StepArr[i - 1] + ":" + StepArr[i]);
                    PrintBoard();
                }
            }
        }

        whose_step = 1 - whose_step;
    }

    /**
     * Delete eaten draughts from board on this step
     * @param eaten Array of already eaten draughts (on this step)
     */
    public void ClearEatenDaughts(ArrayList<Coords> eaten) {
        for (Coords coords: eaten) {
            board[coords.GetX()][coords.GetY()] = 0;
        }
    }

    /**
     * Check if vector is normal
     * @param coords Coords of vector
     * @return if x coordinate is equal to y coordinate
     */
    public boolean IsNormalVector(Coords coords) {
        return Math.abs(coords.GetX()) == Math.abs(coords.GetY());
    }

    /**
     * Insert value (draught) in the board
     * @param coords Coords where to insert
     * @param value Inserted value
     */
    public void Add(Coords coords, int value) throws DraughtsException {
        if (!CheckPosition(coords)) {
            throw new DraughtsException("white cell");
        }

        board[coords.GetX()][coords.GetY()] = value;
    }

    public void Add(int x_pos, int y_pos, int value) throws DraughtsException {
        if (!CheckPosition(x_pos, y_pos)) {
            throw new DraughtsException("white cell");
        }

        board[x_pos][y_pos] = value;
    }

    public void PrintBoard () {
        System.out.print("  ");

        for (int i = 0; i < 8; i++) {
            System.out.printf(Utils.ANSI_GREEN_BACKGROUND + Utils.BLACK_BOLD + "%c " + Utils.ANSI_RESET, 'a' + i);
        }

        System.out.println(Utils.ANSI_RESET);

        for (int i = 0; i < 8; i++) {
            System.out.printf(Utils.ANSI_GREEN_BACKGROUND + Utils.BLACK_BOLD + "%d "+ Utils.ANSI_RESET, i + 1);
            for (int j = 0; j < 8; j++) {
                int color = board[i][j];
                if (color == 0) {
                    System.out.printf(Utils.ANSI_WHITE + "%d ", board[i][j]);
                }
                else if (color == 1) {
                    System.out.printf(Utils.CYAN_BOLD + "%d ", board[i][j]);
                }
                else if (color == 2) {
                    System.out.printf(Utils.RED_BOLD + "%d ", board[i][j]);
                }
                else if (color == 3) {
                    System.out.printf(Utils.CYAN_BOLD + "%d ", board[i][j]);
                }
                else {
                    System.out.printf(Utils.RED_BOLD + "%d ", board[i][j]);
                }

                System.out.print(Utils.ANSI_RESET);
            }

            System.out.printf(Utils.ANSI_GREEN_BACKGROUND + Utils.BLACK_BOLD + "%d "+ Utils.ANSI_RESET, i + 1);

            System.out.print("\n");
        }

        System.out.print("  ");

        for (int i = 0; i < 8; i++) {
            System.out.printf(Utils.ANSI_GREEN_BACKGROUND + Utils.BLACK_BOLD + "%c " + Utils.ANSI_RESET, 'a' + i);
        }

        System.out.println("\n");
    }

    /**
     * Check if some draught can eat
     * @return true if can
     */
    public boolean MustEat () {
        for (int j = 0; j < 8; j++) {
            for (int i = 0; i < 8; i++) {
                if (board[i][j] % 2 == whose_step) {
                    if (CanEat(new Coords(i, j))) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    boolean CanEat (Coords coords) {
        if (IsQueen(coords)) {
            if (CheckOneWayQueen(coords, new Coords(1, 1))) return true;
            if (CheckOneWayQueen(coords, new Coords(1, -1))) return true;
            if (CheckOneWayQueen(coords, new Coords(-1, 1))) return true;
            if (CheckOneWayQueen(coords, new Coords(-1, -1))) return true;
        }
        else {
            if (CheckOneWay(coords, new Coords(1, 1))) return true;
            if (CheckOneWay(coords, new Coords(1, -1))) return true;
            if (CheckOneWay(coords, new Coords(-1, 1))) return true;
            if (CheckOneWay(coords, new Coords(-1, -1))) return true;
        }

        return false;
    }

    boolean CheckOneWay (Coords coords, Coords dir_vec) {
        Coords next_coords = SumCoords(coords, dir_vec);
        Coords pos_to = SumCoords(next_coords, dir_vec);

        if (!InBoard(pos_to) || GetColor(pos_to) != 0) {
            return false;
        }

        return DifColors(coords, next_coords);
    }

    boolean CheckOneWayQueen (Coords coords, Coords dir_vec) {
        Coords next_coords = SumCoords(coords, dir_vec);

        while (InBoard(next_coords)) {
            if (GetColor(next_coords) == 0) {
                next_coords = SumCoords(next_coords, dir_vec);
                continue;
            }

            if (!DifColors(coords, next_coords)) {
                return false;
            }

            next_coords = SumCoords(next_coords, dir_vec);

            if (!InBoard(next_coords)) {
                return false;
            }

            return GetColor(next_coords) == 0;
        }

        return false;
    }

    boolean DifColors(Coords coords1, Coords coords2) {
        int color1 = GetColor(coords1);
        int color2 = GetColor(coords2);

        return DifColors(color1, color2);
    }

    boolean DifColors(int color1, int color2) {
        if ((color1 == 0) || (color2 == 0)) {
            return false;
        }

        if (color1 % 2 == 1) {
            return color2 % 2 == 0;
        }
        else {
            return color2 % 2 == 1;
        }
    }

    boolean IsQueen (Coords coords) {
        int color = GetColor(coords);

        return IsQueen(color);
    }

    boolean IsQueen(int color) {
        return (color == 3 || color == 4);
    }

    int GetColor (Coords coords) {
        return board[coords.GetX()][coords.GetY()];
    }

    ArrayList<Coords> FindNeighbours (Coords coords) {
        int x = coords.GetX();
        int y = coords.GetY();

        ArrayList<Coords> neighbours = new ArrayList<Coords>();
        for (int i = -1; i <= 1; i += 2) {
            for (int j = -1; j <= 1; j += 2) {
                if (InBoard(x + i) && InBoard(y + j)) {
                    neighbours.add(new Coords(x + i, y + j));
                }
            }
        }

        return neighbours;
    }

    Coords Average(Coords coords1, Coords coords2) {
        return new Coords((coords1.GetX() + coords2.GetX()) / 2, (coords1.GetY() + coords2.GetY()) / 2);
    }

    Coords DifCoords(Coords coords1, Coords coords2) {
        return new Coords(coords1.GetX() - coords2.GetX(), coords1.GetY() - coords2.GetY());
    }

    Coords SumCoords(Coords coords1, Coords coords2) {
        return new Coords(coords1.GetX() + coords2.GetX(), coords1.GetY() + coords2.GetY());
    }

    boolean InBoard(Coords coords) {
        return InBoard(coords.GetX()) && InBoard(coords.GetY());
    }

    boolean InBoard(int x) {
        return x >= 0 && x <= 7;
    }

    boolean CheckPosition(Coords coords) {
        return (coords.GetX() + coords.GetY()) % 2 == 0;
    }
    boolean CheckPosition(int x_pos, int y_pos) {
        return (x_pos + y_pos) % 2 == 0;
    }
}