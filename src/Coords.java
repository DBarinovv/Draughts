public class Coords {
    private int x;
    private int y;

    public Coords() {
        this.x = -1;
        this.y = -1;
    }

    public Coords(int new_x, int new_y) {
        this.x = new_x;
        this.y = new_y;
    }

    public Coords(Coords coords) {
        this.x = coords.x;
        this.y = coords.y;
    }

    int GetX() {
        return x;
    }

    int GetY() {
        return y;
    }

    void Move(int delta_x, int delta_y) {
        x += delta_x;
        y += delta_y;
    }

    void Move(Coords coords) {
        x += coords.GetX();
        y += coords.GetY();
    }

    void MakeNormal() {
        if (x != 0 && y != 0) {
            x /= Math.abs(x);
            y /= Math.abs(y);
        }
    }

    void Print() {
        System.out.printf("X = %d Y = %d\n", GetX(), GetY());
    }

    void Print(String str) {
        System.out.println(str);
        System.out.printf("X = %d Y = %d\n", GetX(), GetY());
    }
}
