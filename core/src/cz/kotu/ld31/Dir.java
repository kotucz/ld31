package cz.kotu.ld31;

public class Dir {
    public static final Dir
    O = new Dir(0, 0),
    E = new Dir(1, 0),
    N = new Dir(0, 1),
    W = new Dir(-1, 0),
    S = new Dir(0, -1);

    private final int dx, dy;

    public int dx() {
        return dx;
    }

    public int dy() {
        return dy;
    }

    private Dir(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    Vec vec() {
        return new Vec(dx, dy);
    }
}