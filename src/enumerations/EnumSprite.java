package enumerations;

/**
 * Created by Dimitri on 03/03/2016.
 */
public enum EnumSprite {
    AGENT_SPRITE("images/sprite_agent");

    private String name = "";

    EnumSprite(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
