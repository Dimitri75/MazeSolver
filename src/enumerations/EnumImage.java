package enumerations;

/**
 * Created by  EquipeLabyrinthe on 21/05/2016.
 */
public enum EnumImage {
    AGENT("images/agent.png"),

    OBSTACLE1("images/obstacle1.png"),
    OBSTACLE2("images/obstacle2.png"),
    OBSTACLE3("images/obstacle3.png"),
    OBSTACLE4("images/obstacle4.png"),
    OBSTACLE5("images/obstacle5.png"),
    OBSTACLE6("images/obstacle6.png"),
    OBSTACLE7("images/obstacle7.png"),


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
