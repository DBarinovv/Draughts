import java.util.ArrayList;

public class Board {
    int[][] board = new int[8][8]; // 0 - empty, 1 - white, 2 - black, 3 - white queen, 4 - black queen
    int whose_step = 1; // 1 - white, 0 - black
//    Draught[][] draughts = new Draught[8][8];

    public void MoveStep(String step) {
        Coords pos_from = new Coords(Utils.CoordsFromString(step));

//        System.out.printf("Step = %s color = %d ", step, GetColor(pos_from));
//        pos_from.Print("First");
        if (GetColor(pos_from) % 2 != whose_step) {
//            pos_from.Print("Second");
//            System.out.printf("Color = %d\n", GetColor(pos_from));
            throw new RuntimeException("Wrong draught color");
        }

        Coords pos_to = new Coords(Utils.CoordsFromString(step.substring(3)));

        if (IsQueen(pos_from)) {
            MoveStepQueen(pos_from, pos_to);
            return;
        }

        if (whose_step == 1) {
            if (pos_to.GetX() - pos_from.GetX() != 1) {
                throw new RuntimeException("Too far X step\n");
            }
        }
        else {
            if (pos_to.GetX() - pos_from.GetX() != -1) {
                throw new RuntimeException("Too far X step\n");
            }
        }

        if (Math.abs(pos_to.GetY() - pos_from.GetY()) != 1) {
            throw new RuntimeException("Too far Y step\n");
        }

        if (!CheckPosition(pos_to)){
            throw new RuntimeException("Step out of board\n");
        }

        if (GetColor(pos_to) != 0) {
            throw new RuntimeException("Position is occupied\n");
        }

        board[pos_to.GetX()][pos_to.GetY()] = GetColor(pos_from);
        board[pos_from.GetX()][pos_from.GetY()] = 0;

        whose_step = 1 - whose_step;
    }

    public void MoveStepQueen(Coords pos_from, Coords pos_to) {
        Coords dir_vec = new Coords(pos_to.GetX() - pos_from.GetX(), pos_to.GetY() - pos_from.GetY());

        if (!IsNormalVector(dir_vec)) {
            throw new RuntimeException("Wrong queen step");
        }

        dir_vec.MakeNormal();

        Coords next_coords = new Coords(pos_from);

        while (next_coords.GetX() != pos_to.GetX() && next_coords.GetY() != pos_to.GetY()) {
            next_coords.Move(dir_vec);

            if (!InBoard(next_coords) || GetColor(next_coords) != 0) {
                throw new RuntimeException("Obstacle on the queen way");
            }
        }

        board[pos_to.GetX()][pos_to.GetY()] = GetColor(pos_from);
        board[pos_from.GetX()][pos_from.GetY()] = 0;

        whose_step = 1 - whose_step;
    }

    public boolean IsNormalVector(Coords coords) {
        return Math.abs(coords.GetX()) == Math.abs(coords.GetY());
    }

    public void Add(Coords coords, int value) {
        if (!CheckPosition(coords)) {
            throw new RuntimeException("Wrong position");
        }

        board[coords.GetX()][coords.GetY()] = value;
    }

    public void Add(int x_pos, int y_pos, int value) {
        if (!CheckPosition(x_pos, y_pos)) {
            throw new RuntimeException("Wrong position");
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

    boolean CanEat (Coords coords) {
        var neighbours = FindNeighbours(coords);

        for (Coords neighbour : neighbours) {
            if (IsQueen(coords)) {
                Coords next_coords = neighbour;
                while (InBoard(next_coords)) {
                    if (GetColor(next_coords) == 0) {
                        continue;
                    }

                    if (!DifColors(coords, next_coords)) {
                        break;
                    } else {
                        next_coords = SumCoords(neighbour, DifCoords(neighbour, coords));

                        if (InBoard(next_coords)) {
                            if (GetColor(next_coords) == 0) {
                                return true;
                            }
                        } else {
                            break;
                        }
                    }

                    next_coords = SumCoords(next_coords, DifCoords(neighbour, coords));
                }
            } else {
                if (!DifColors(coords, neighbour)) {
                    continue;
                }

                Coords next_coords = SumCoords(neighbour, DifCoords(neighbour, coords));
                if (InBoard(next_coords)) {
                    if (GetColor(next_coords) == 0) {
                        return true;
                    }
                }
            }
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

    Coords DifCoords(Coords coords1, Coords coords2) {
        return new Coords(coords1.GetX() - coords2.GetX(), coords2.GetY() - coords2.GetY());
    }

    Coords SumCoords(Coords coords1, Coords coords2) {
        return new Coords(coords1.GetX() + coords2.GetX(), coords2.GetY() + coords2.GetY());
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