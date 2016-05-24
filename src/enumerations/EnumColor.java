package enumerations;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Created by Dimitri on 04/03/2016.
 */
public enum EnumColor {
    RED(Color.CRIMSON, 0),
    GREEN(Color.BLUEVIOLET, 1),
    BLUE(Color.GREEN, 2),
    ORANGE(Color.CHOCOLATE, 3),
    YELLOW(Color.BLUE, 4);

    private Color color;
    private int index;

    EnumColor(Color color, int index){
        this.color = color;
        this.index = index;
    }

    public int toInteger() {
        return index;
    }

    public Color toColor(){
        return color;
    }

    public static Color getColorAt(int index){
        for (EnumColor enumColor : EnumColor.values())
            if (enumColor.toInteger() == index)
                return enumColor.toColor();

        Random random = new Random();
        return getColorAt(random.nextInt(EnumColor.values().length));
    }
}
