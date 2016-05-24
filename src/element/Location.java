package element;

import interfaces.ILocation;

/**
 * Created by Dimitri on 02/03/2016.
 */
public class Location implements ILocation {
    int x, y;

    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public Location getLocation(){
        return this;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    public boolean equals(Object other){
        if (x == ((Location)other).getX() && y == ((Location)other).getY())
            return true;
        return false;
    }

    @Override
    public String toString() {
        return "Location{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
