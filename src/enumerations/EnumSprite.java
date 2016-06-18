package enumerations;

/**
 * Created by  EquipeLabyrinthe on 03/06/2016.
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
