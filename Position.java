package byow.Core;
import java.util.*;

public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public ArrayList getPosition(Room r) {
        ArrayList<Integer> result = new ArrayList<>();
        result.add(r.getX());
        result.add(r.getY());
        return result;
    }

    public Position shift(int width, int height) {
        return new Position(this.x + width, this.y + height);
    }


}
