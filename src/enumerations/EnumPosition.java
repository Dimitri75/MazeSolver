package enumerations;

/**
 * Created by Dimitri on 05/11/2015.
 */
public enum EnumPosition {
    LEFT(0),
    RIGHT(1),
    UP(2),
    DOWN(3);

    private int index = 0;

    EnumPosition(int index){
        this.index = index;
    }

    public int toInteger() { return index; }

    public String toString(){
        return Integer.toString(index);
    }
}
