package enumerations;

/**
 * Created by Dimitri on 01/11/2015.
 */
public enum EnumImage {
    PANDA("images/panda.png"),
    RACCOON("images/raccoon.png"),

    OBSTACLE("images/obstacle.png"),
    OBSTACLE1("images/obstacle1.png"),
    OBSTACLE2("images/obstacle2.png"),

    EXIT_CLOSED("images/door_closed.png"),
    EXIT_OPENED("images/door_opened.png");

    private String name = "";

    EnumImage(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
