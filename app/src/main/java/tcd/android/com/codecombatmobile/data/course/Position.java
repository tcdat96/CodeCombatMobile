package tcd.android.com.codecombatmobile.data.course;

public class Position {
    public float x;
    public float y;

    public Position(float x, float y) {
        set(x, y);
    }

    public Position(Position position) {
        set(position.x, position.y);
    }

    public void set(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
