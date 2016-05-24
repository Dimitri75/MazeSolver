package element;

import enumerations.EnumImage;
import enumerations.EnumMode;
import enumerations.EnumPosition;
import enumerations.EnumSprite;
import graph.Graph;
import graph.Vertex;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import sample.Controller;
import sample.TimersHandler;
import utils.AnimationHandler;

import java.util.List;
import java.util.TimerTask;

/**
 * Created by Dimitri on 21/10/2015.
 */
public class Character extends MapElement implements Runnable {
    private EnumPosition enumPosition;
    private List<Vertex> path;
    private boolean actionDone;
    private EnumSprite enumSprite;
    private AnimationHandler animationHandler;

    public Character(int x, int y, int shapeSize, EnumImage image, EnumSprite enumSprite) {
        super(x, y, shapeSize, image);
        enumPosition = EnumPosition.RIGHT;
        actionDone = true;
        this.enumSprite = enumSprite;
        animationHandler = new AnimationHandler(this, enumSprite);
    }

    public boolean isActionDone() {
        return actionDone;
    }

    public void changePosition() {
        if (enumPosition.equals(EnumPosition.LEFT)) {
            enumPosition = EnumPosition.RIGHT;
        } else {
            enumPosition = EnumPosition.LEFT;
        }
    }

    public EnumPosition getEnumPosition() {
        return enumPosition;
    }

    public boolean initPathDijkstra(Graph graph, Vertex start, Vertex destination, EnumMode mode) {
        actionDone = false;

        if (path != null)
            path.clear();

        path = graph.dijkstra(start, destination, mode);

        return path != null;
    }

    public void initPath(List<Vertex> path) {
        this.path = path;
    }

    public void setLocation(Location location){
        translateX(location.getX());
        translateY(location.getY());
    }

    @Override
    public void translateX(int x) {
        if (x < getX() && enumPosition.equals(EnumPosition.RIGHT)) {
            changePosition();
        } else if (x > getX() && enumPosition.equals(EnumPosition.LEFT)) {
            changePosition();
        }
        super.translateX(x);
    }

    @Override
    public void translateY(int y) {
        super.translateY(y);
    }

    @Override
    public void run() {
        if (path != null) {
            actionDone = false;
            for (Vertex vertex : path) {
                if (!actionDone) {
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }

                    Platform.runLater(() -> {
                        translateX(vertex.getX());
                        translateY(vertex.getY());
                    });
                }
            }
            actionDone = true;
            stopAnimation();
        }
    }

    public void animate(){
        if (enumSprite == null)
            return;

        if (animationHandler != null) {
            animationHandler.purge();
            animationHandler.cancel();
        }

        animationHandler = new AnimationHandler(this, enumSprite);
        animationHandler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (animationHandler != null)
                    animationHandler.changeFrame();
            }
        }, 0, 150);
    }

    /**
     * Handles cancelation of Juliette's animations and thread
     */
    public void stopAnimation() {
        TimersHandler.cancelTimer(animationHandler);
        Thread.currentThread().interrupt();
        animationHandler = null;
    }
}
