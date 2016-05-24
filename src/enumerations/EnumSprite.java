package enumerations;

/**
 * Created by Dimitri on 03/03/2016.
 */
public enum EnumSprite {
    RACCOON_SPRITE("images/sprite_raccoon"),
    PANDA_SPRITE("images/sprite_panda");

    private String name = "";

    EnumSprite(String name){
        this.name = name;
    }

    public String toString(){
        return name;
    }
}
