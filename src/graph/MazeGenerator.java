package graph;

import element.Location;
import element.MapElement;
import sample.Controller;
import utils.ResourcesUtils;

import java.util.*;



/**
 * Created by EquipeLabyrinthe on 24/05/2016.
 */
public class MazeGenerator {

    /**
     * RECURSIVE DIVISION
     */
    public static void recursiveDivision()    {
        int columns = Controller.graph.getColumns();
        int lines = Controller.graph.getLines();
        recursiveDivision(0, columns, lines, 0);

        Controller.graph.getObstaclesList().add(
                new MapElement(0 , 0, Controller.graph.getPace(), ResourcesUtils.getInstance().getObstacleImage()
        ));
    }

    private static void recursiveDivision(int left, int right, int top, int bottom){
        int width = right - left;
        int height = top - bottom;

        if (width > 2 || height > 2) {
            if (width >= height)
                verticalBreak(left, right, top, bottom);
            else
                horizontalBreak(left, right, top, bottom);
        }
    }

    private static void verticalBreak(int left, int right, int top, int bottom){
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


    private static void horizontalBreak(int left, int right, int top, int bottom){
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

        int yWallToBreak = left + rand.nextInt((right-left) / 2) * 2 + 1;
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

        Vertex current = start;
        do {
            stack.add(current);
            Controller.graph.getObstaclesList().remove(current);
            current.visited = true;
            current = dfsGetRandomNeighborsAndBreakWalls(current, stack);
        } while (current != null);
    }

    private static Vertex dfsGetRandomNeighborsAndBreakWalls(Vertex vertex, Stack<Vertex> stack){
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
    private static Vertex dfsGetRandomNeighbors(Vertex location){
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
    private static void dfsBreakWall(Vertex v1, Vertex v2){
        Location location = getLocationInBetween(v1, v2);

        Controller.graph.getObstaclesList().remove(v1.getLocation());
        Controller.graph.getObstaclesList().remove(location);
        Controller.graph.getObstaclesList().remove(v2.getLocation());
    }
    /**
     * END DFS MAZE
     */

    /**
     * KRUSKAL
     */
    public static void kruskalMaze()
    {
        Controller.graph.getObstaclesList().clear();
        Controller.graph.initForGeneration();

        Random rand = new Random();

        List<Vertex> cases = Controller.graph.getListVertices();
        ArrayList<Edge> walls = new ArrayList<Edge>(Controller.graph.getListEdges());

        //Mélange l'ordre des murs
        for (int i = 0; i < walls.size(); ++i)
        {
            int e1 = rand.nextInt(walls.size());
            int e2 = rand.nextInt(walls.size());
            Collections.swap(walls,e1,e2);
        }

        //Au debut toutes les cases ont un ID différent
        for (int i = 0; i < cases.size(); ++i)
        {
            cases.get(i).id = i;
        }

        while (!endKruskalGeneration(cases) && !walls.isEmpty())
        {
            //On tire un mur au hasard
            Edge wall = walls.get(walls.size() - 1);

            Vertex v1 = wall.getSource();
            Vertex v2 = wall.getTarget();

            kruskalChangeIDTo(cases, v1.id, v2.id);

            kruskalBreakWall(v1,v2);

            walls.remove(walls.size() - 1);
        }
    }

    private static boolean endKruskalGeneration( List<Vertex> cases)
    {
        int first_id = cases.get(0).id;
        for (int i = 1; i < cases.size(); ++i)
        {
            if (cases.get(i).id != first_id) {
                return false;
            }
        }
        return true;
    }

    private static void kruskalChangeIDTo(List<Vertex> cases, int id_to_change, int new_id)
    {
        for (int i = 0; i < cases.size(); ++i)
        {
            if (cases.get(i).id == id_to_change) {
                cases.get(i).id = new_id;
            }
        }
    }

    /**
     * Break wall in between
     * @param v1
     * @param v2
     */
    private static void kruskalBreakWall(Vertex v1, Vertex v2)
    {
        Location location = getLocationInBetween(v1, v2);
        Controller.graph.getObstaclesList().remove(location);
        Controller.graph.getObstaclesList().remove(v1.getLocation());
        Controller.graph.getObstaclesList().remove(v2.getLocation());
    }


    /**
     * END KRUSKAL
     */

    /**
     * PRIM'S ALGORITHM
     * http://stackoverflow.com/questions/29739751/implementing-a-randomly-generated-maze-using-prims-algorithm
     */
    public static void primsMaze(){
        List<Vertex> frontiersList = new ArrayList<>();

        //A Grid consists of a 2 dimensional array of cells.
        //A Cell has 2 states: Blocked or Passage.
        //Start with a Grid full of Cells in state Blocked.
        Controller.graph.getObstaclesList().clear();
        Controller.graph.initForGeneration();

        //Pick a random Cell, set it to state Passage and Compute its frontier cells.
        //A frontier cell of a Cell is a cell with distance 2 in state Blocked and within the grid.
        Vertex cell = Controller.graph.getRandomVertex();
        Controller.graph.getObstaclesList().remove(cell.getLocation());
        computeFrontiers(cell, frontiersList);


        //While the list of frontier cells is not empty:
        while (!frontiersList.isEmpty()) {
            //Pick a random frontier cell from the list of frontier cells.
            int randomIndex = new Random().nextInt(frontiersList.size());
            Vertex frontier = frontiersList.get(randomIndex);

            //Let neighbors(frontierCell) = All cells in distance 2 in state Passage. Pick a random neighbor
            Vertex neighbor = pickRandomNeighbor(frontier);

            //connect the frontier cell with the neighbor by setting the cell in-between to state Passage.
            primsBreakWall(frontier, neighbor);

            //Compute the frontier cells of the chosen frontier cell and add them to the frontier list.
            computeFrontiers(frontier, frontiersList);

            //Remove the chosen frontier cell from the list of frontier cells.
            frontiersList.remove(frontier);
        }

    }

    /**
     * Pick a random neighbor
     * @param vertex
     * @return
     */
    private static Vertex pickRandomNeighbor(Vertex vertex){
        List<Vertex> neighbors = getNeighbors(vertex);
        for (Vertex wall : neighbors)
            Controller.graph.getObstaclesList().remove(wall);

        return neighbors.get(new Random().nextInt(neighbors.size()));
    }


    /**
     * Break wall in between
     * @param v1
     * @param v2
     */
    private static void primsBreakWall(Vertex v1, Vertex v2){
        Location location = getLocationInBetween(v1, v2);
        Controller.graph.getObstaclesList().remove(location);
        Controller.graph.getObstaclesList().remove(v1.getLocation());
        Controller.graph.getObstaclesList().remove(v2.getLocation());
    }

    /**
     * Returns the location between two vertices
     * @param v1
     * @param v2
     * @return
     */
    private static Location getLocationInBetween(Vertex v1, Vertex v2){
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

        return new Location(x, y);
    }

    /**
     * Add frontiers of a cell to the set
     * @param vertex
     * @param frontiersList
     */
    private static void computeFrontiers(Vertex vertex, List frontiersList){
        for (Vertex wall : getNeighbors(vertex))
            if (Controller.graph.getObstaclesList().contains(wall.getLocation()) && !frontiersList.contains(wall))
                frontiersList.add(wall);
    }


    /**
     * Returns the neighbors list of the given vertex
     * @param vertex
     * @return
     */
    private static List<Vertex> getNeighbors(Vertex vertex){
        List<Vertex> wallsToReturn = new ArrayList<>();

        int pace = Controller.graph.getPace();
        Location location = vertex.getLocation();

        Vertex[] walls = new Vertex[4];
        walls[0] = Controller.graph.getVertexByLocation(location.getX() + 2 * pace, location.getY());
        walls[1] = Controller.graph.getVertexByLocation(location.getX() - 2 * pace, location.getY());
        walls[2] = Controller.graph.getVertexByLocation(location.getX(), location.getY() + 2 * pace);
        walls[3] = Controller.graph.getVertexByLocation(location.getX(), location.getY() - 2 * pace);

        for (Vertex wall : walls)
            if (wall != null)
                wallsToReturn.add(wall);

        return wallsToReturn;
    }
}
