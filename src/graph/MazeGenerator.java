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

    /**
     * RECURSIVE DIVISION
     */
    public static void recursiveDivision()    {
        int columns = Controller.graph.getColumns();
        int lines = Controller.graph.getLines();
        recursiveDivision(0, columns + 1, lines, 0);
    }

    public static void recursiveDivision(int left, int right, int top, int bottom){
        int width = right - left;
        int height = top - bottom;

        if (width > 2 || height > 2) {
            if (width >= height)
                verticalBreak(left, right, top, bottom);
            else
                horizontalBreak(left, right, top, bottom);
        }
    }

    public static void verticalBreak(int left, int right, int top, int bottom){
        List<MapElement> obstaclesList = Controller.graph.getObstaclesList();
        int pace = Controller.graph.getPace();
        MapElement obstacle;
        Random rand = new Random();

        int pivot = rand.nextInt((right - left - 1) / 2) * 2 +  left + 2;
        for(int i = bottom; i < top; ++i){
            obstacle = new MapElement( pace * i, pivot * pace, pace, ResourcesUtils.getInstance().getObstacleImage());
            obstaclesList.add(obstacle);
        }

        int xWallToBreak = rand.nextInt((top - bottom) / 2) * 2 + 1 + bottom;
        obstaclesList.remove(new Location(xWallToBreak * pace, pivot * pace));
        recursiveDivision(left, pivot, top, bottom);
        recursiveDivision(pivot, right, top, bottom);
    }


    public static void horizontalBreak(int left, int right, int top, int bottom){
        List<MapElement> obstaclesList = Controller.graph.getObstaclesList();
        int pace = Controller.graph.getPace();
        MapElement obstacle;
        Random rand = new Random();

        int pivot =  bottom  + 2 + rand.nextInt((top - bottom - 1) / 2) * 2;
        if(pivot % 2 == 1) pivot++;

        for(int i = left; i < right; ++i){
            obstacle = new MapElement(pace * pivot, i * pace, pace, ResourcesUtils.getInstance().getObstacleImage());
            obstaclesList.add(obstacle);
        }

        int yWallToBreak = left + rand.nextInt((right-left)/2) * 2 + 1;
        obstaclesList.remove(new Location(pivot * pace, yWallToBreak * pace));
        recursiveDivision(left, right, top, pivot);
        recursiveDivision(left, right, pivot, bottom);
    }
    /**
     * END RECURSIVE DIVISION
     */


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
