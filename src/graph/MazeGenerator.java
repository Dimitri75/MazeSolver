package graph;

import element.MapElement;
import utils.ResourcesUtils;

/**
 * Created by Dimitri on 24/05/2016.
 */
public class MazeGenerator {
    public static void basicMaze(Graph graph) {
        MapElement obstacle;
        if (graph.getObstaclesList() == null || graph.getObstaclesList().isEmpty())
            for (int x = 2; x < graph.getLines(); x *= 2) {
                for (int y = 3; y < graph.getColumns(); y++) {
                    if (y % x != 0) {
                        obstacle = new MapElement(x * graph.getPace(), y * graph.getPace(), graph.getPace(), ResourcesUtils.getInstance().getObstacle());
                        graph.getObstaclesList().add(obstacle);
                    }
                }
            }
        for (int y = 2; y < graph.getColumns(); y *= 2) {
            for (int x = 3; x < graph.getLines(); x++) {
                if (x % 5 == 0 || x % 5 == 1) {
                    obstacle = new MapElement(x * graph.getPace(), y * graph.getPace(), graph.getPace(), ResourcesUtils.getInstance().getObstacle());
                    graph.getObstaclesList().add(obstacle);
                }
            }
        }
    }
}
