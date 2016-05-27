package graph;

import element.MapElement;
import sample.Controller;
import utils.ResourcesUtils;

import java.util.List;

/**
 * Created by Dimitri on 24/05/2016.
 */
public class MazeGenerator {
    public static void basicMaze() {
        List<MapElement> obstaclesList = Controller.graph.getObstaclesList();
        int columns = Controller.graph.getColumns();
        int lines = Controller.graph.getLines();
        int pace = Controller.graph.getPace();
        MapElement obstacle;

        if (!obstaclesList.isEmpty())
            return;

        for (int x = 2; x < lines; x *= 2) {
            for (int y = 3; y < columns; ++y) {
                if (y % x != 0) {
                    obstacle = new MapElement(x * pace, y * pace, pace, ResourcesUtils.getInstance().getObstacle());
                    obstaclesList.add(obstacle);
                }
            }
        }

        for (int y = 2; y < columns; y *= 2) {
            for (int x = 3; x < lines; ++x) {
                if (x % 5 == 0 || x % 5 == 1) {
                    obstacle = new MapElement(x * pace, y * pace, pace, ResourcesUtils.getInstance().getObstacle());
                    obstaclesList.add(obstacle);
                }
            }
        }
    }

    public static void exampleMaze(){
        List<MapElement> obstaclesList = Controller.graph.getObstaclesList();
        int columns = Controller.graph.getColumns();
        int lines = Controller.graph.getLines();
        int pace = Controller.graph.getPace();
        MapElement obstacle;

        for (int y = 0; y < columns; ++y)
            for (int x = 0; x < lines; ++x){
                obstacle = new MapElement(x * pace, y * pace, pace, ResourcesUtils.getInstance().getObstacle());

                if (x % 2 == 0 && y % 2 == 0)
                    obstaclesList.add(obstacle);
            }
    }
}
