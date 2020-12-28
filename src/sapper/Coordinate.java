package sapper;

public class Coordinate {
    public int x, y;

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void incX(){
        this.x++;
    }

    public void incY(){
        this.y++;
    }

    public void decX(){
        this.x--;
    }

    public void decY(){
        this.y--;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Coordinate) {
            Coordinate to = (Coordinate) obj;
            return to.x == x && to.y == y;
        }
        return super.equals(obj);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
