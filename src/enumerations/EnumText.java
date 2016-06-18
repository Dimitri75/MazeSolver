package enumerations;

/**
 * Created by  EquipeLabyrinthe on 06/06/2016.
 */
public enum EnumText {
    INSTRUCTIONS(
            "Options :\n" +
            "- Checkbox \"Display algorithm\" : Display how the algorithm works to find paths\n" +
            "\n" +
            "- ComboBox \"Generation algorithm\" : Allow to choose the the algorithm which will be used to generate the maze\n" +
            "\n" +
            "- Slider \"Size\" : Handle the size of the map\n" +
            "\n" +
            "\n" +
            "\n" +
            "Get started :\n" +
            "1 - Configure your options\n" +
            "\n" +
            "2 - Press start to initialize the map and its components\n" +
            "\n" +
            "3 - You can now add or remove obstacles by clicking on the map\n" +
            "\n" +
            "4 - Press one of the buttons to start a simulation\n" +
            "\n" +
            "5 - Press start to restart\n" +
            "\n" +
            "6 - Press restart to change your options");

    private String content;

    EnumText(String content){
        this.content = content;
    }

    @Override
    public String toString() {
        return content;
    }
}
