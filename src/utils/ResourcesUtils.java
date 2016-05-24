package utils;

import enumerations.EnumImage;
import enumerations.EnumSprite;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import list.CircularQueue;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Random;

/**
 * Created by Dimitri on 01/11/2015.
 */
public class ResourcesUtils {
    public static ResourcesUtils INSTANCE = null;
    private HashMap<Integer, Image> obstaclesDictionnary;

    public static ResourcesUtils getInstance(){
        if (INSTANCE == null)
            INSTANCE = new ResourcesUtils();

        return INSTANCE;
    }

    private ResourcesUtils(){
        obstaclesDictionnary = new HashMap<>();

        obstaclesDictionnary.put(0, new Image(EnumImage.OBSTACLE1.toString()));
        obstaclesDictionnary.put(1, new Image(EnumImage.OBSTACLE2.toString()));
    }

    /**
     * Returns a random obstacle element
     * @return
     */
    public Image getObstacle(){
        Random random = new Random();
        return obstaclesDictionnary.get(random.nextInt(2) % 2);
    }

    /**
     * Gets all the frames from the given EnumSprite enumeration
     * @param sprite
     * @return
     */
    public AbstractMap.SimpleEntry<CircularQueue<ImagePattern>, CircularQueue<ImagePattern>> getFrames(EnumSprite sprite){
        String uri_left = sprite.toString() + "/LEFT";
        String uri_right = sprite.toString() + "/RIGHT";

        return new AbstractMap.SimpleEntry(getFilledQueue(uri_left), getFilledQueue(uri_right));
    }

    /**
     * Returns a CircularQueue containing all the images in the given folder
     * @param uri
     * @return
     */
    public CircularQueue<ImagePattern> getFilledQueue(String uri){

        CircularQueue<ImagePattern> circularQueue = new CircularQueue<>(4);
        String path;
        for (int i = 1; i < 5; i++){
            path = uri + "/walk" + i + ".png";
            circularQueue.add(new ImagePattern(new Image(path)));
        }

//        String url = getClass().getClassLoader().getResource(uri).getFile();
//        File directory = new File(url);
//        if (!directory.exists())
//            return null;
//
//        CircularQueue<ImagePattern> circularQueue = new CircularQueue<>(directory.listFiles().length);
//        String path;
//        for (File file : directory.listFiles()){
//            path = uri + "/" + file.getName();
//            circularQueue.addAndReturn(new ImagePattern(new javafx.scene.image.Image(path)));
//        }
        return circularQueue;
    }
}
