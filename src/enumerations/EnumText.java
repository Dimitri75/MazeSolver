package enumerations;

/**
 * Created by Dimitri on 06/03/2016.
 */
public enum EnumText {
    INSTRUCTIONS(
            "Options :\n" +
            "- Checkbox \"Display algorithm\" : Display how the algorithm works to find paths\n" +
            "\n" +
            "- Slider \"Size\" : Handle the size of the map\n" +
            "\n" +
            "\n" +
            "Get started :\n" +
            "1 - Configure your options\n" +
            "\n" +
            "2 - Press start to initialize the map and its components\n" +
            "\n" +
            "3 - You can now add or remove obstacles by clicking on the map\n" +
            "\n" +
            "4 - Press one of the four buttons to start a simulation\n" +
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
