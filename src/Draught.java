public class Draught {
    Coords coords;
//    boolean
    boolean is_queen;
    boolean color; // 0 - white, 1 - black

    boolean CheckPosition() {
        return (coords.GetX() + coords.GetY()) % 2 == 0;
    }

    void Move(int delta_x, int delta_y) {
        coords.Move(delta_x, delta_y);
    }
}
