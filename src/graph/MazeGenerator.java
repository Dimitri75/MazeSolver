package graph;

import element.Location;
import element.MapElement;
import sample.Controller;
import utils.ResourcesUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

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

    /**
     * DFS MAZE
     */
    public static void dfsMaze(){
        Stack<Vertex> stack = new Stack<>();
        Controller.graph.getObstaclesList().clear();
        Controller.graph.initForGeneration();

        Vertex start = Controller.graph.getRandomVertex();

        Vertex vertex = start;
        do {
            stack.add(vertex);
            Controller.graph.getObstaclesList().remove(vertex);
            vertex.visited = true;
        } while ((vertex = dfsGetRandomNeighborsAndBreakWalls(vertex, stack)) != null);
    }

    public static Vertex dfsGetRandomNeighborsAndBreakWalls(Vertex vertex, Stack<Vertex> stack){
        Vertex randomNeighbor;
        Vertex current = vertex;

        while ((randomNeighbor = dfsGetRandomNeighbors(current)) == null){
            if (stack.isEmpty()) return null;
            current = stack.pop();
        }
        dfsBreakWall(current, randomNeighbor);

        return randomNeighbor;
    }

    /**
     * Get random neighbor of a location.
     * If none, return null
     *
     * @param location
     * @return
     */
    public static Vertex dfsGetRandomNeighbors(Vertex location){
        int pace = Controller.graph.getPace();
        Random random = new Random();
        ArrayList<Vertex> neighbors;

        neighbors = new ArrayList<>();
        neighbors.add(Controller.graph.getVertexByLocation(location.getX() + 2 * pace, location.getY()));
        neighbors.add(Controller.graph.getVertexByLocation(location.getX() - 2 * pace, location.getY()));
        neighbors.add(Controller.graph.getVertexByLocation(location.getX(), location.getY() + 2 * pace));
        neighbors.add(Controller.graph.getVertexByLocation(location.getX(), location.getY() - 2 * pace));

        ArrayList<Vertex> verticesToRemove = new ArrayList<>();
        for (Vertex neighbor : neighbors){
            if (neighbor == null || neighbor.visited)
                verticesToRemove.add(neighbor);
        }

        for (Vertex toRemove : verticesToRemove)
            neighbors.remove(toRemove);

        return neighbors.isEmpty() ? null : neighbors.get(random.nextInt(neighbors.size()));
    }

    /**
     * Break walls at given locations and the one between them
     * @param v1
     * @param v2
     */
    public static void dfsBreakWall(Vertex v1, Vertex v2){
        int x = v1.getX();
        int y = v1.getY();

        if (v1.getX() < v2.getX() && v1.getY() == v2.getY())
            x += Controller.graph.getPace();
        else if (v1.getX() == v2.getX() && v1.getY() < v2.getY())
            y += Controller.graph.getPace();
        else if (v1.getX() > v2.getX() && v1.getY() == v2.getY())
            x -= Controller.graph.getPace();
        else if (v1.getX() == v2.getX() && v1.getY() > v2.getY())
            y -= Controller.graph.getPace();

        Controller.graph.getObstaclesList().remove(new Location(x, y));
        Controller.graph.getObstaclesList().remove(v1.getLocation());
        Controller.graph.getObstaclesList().remove(v2.getLocation());
    }
    /**
     * END DFS MAZE
     */
}
