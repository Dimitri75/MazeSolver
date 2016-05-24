package graph;

import element.Location;
import element.MapElement;
import enumerations.EnumColor;
import enumerations.EnumGraph;
import enumerations.EnumMode;
import javafx.scene.paint.Color;
import list.CircularQueue;
import sample.Controller;

import java.util.*;

/**
 * Created by Dimitri on 21/10/2015.
 */
public class Graph {
    private int pace;
    private int pixelWidth, pixelHeight;
    private int lines, columns;
    private List<Vertex> listVertex;
    private List<MapElement> obstaclesList;
    private List<Edge> listEdges;

    public Graph(int pixelWidth, int pixelHeight, int pace) {
        this.pixelWidth = pixelWidth;
        this.pixelHeight = pixelHeight;
        this.pace = pace;

        lines = pixelWidth / pace;
        columns = pixelHeight / pace - 1;

        obstaclesList = new ArrayList<>();
        listVertex = new ArrayList<>();
        listEdges = new ArrayList<>();
    }

    /**
     * Adds an edge to the graph
     *
     * @param source
     * @param target
     * @param movementSpeed
     * @return
     */
    private Edge addEdge(Vertex source, Vertex target, EnumGraph movementSpeed) {
        Edge edge = new Edge(source, target, movementSpeed);
        listEdges.add(edge);
        return edge;
    }

    /**
     * Adds a vertex to the graph
     *
     * @param x
     * @param y
     * @return
     */
    private Vertex addVertex(int x, int y) {
        Vertex vertex = new Vertex(x, y);
        listVertex.add(vertex);
        return vertex;
    }

    /**
     * Returns the vertex corresponding at the given coordinates
     *
     * @param x
     * @param y
     * @return
     */
    public Vertex getVertexByLocation(int x, int y) {
        return getVertexByLocation(new Location(x, y));
    }

    /**
     * Returns the vertex at the given location
     *
     * @param location
     * @return
     */
    public Vertex getVertexByLocation(Location location) {
        for (Vertex vertex : listVertex)
            if (vertex.getX() == location.getX() && vertex.getY() == location.getY())
                return vertex;

        return null;
    }

    /**
     * Initialize the graph according to the map and the obstacles
     */
    public void init() {
        boolean noObstacles;
        for (int y = 0; y < pixelHeight; y += pace) {
            Vertex leftVertex = null;
            for (int x = 0; x < pixelWidth; x += pace) {
                noObstacles = true;
                for (MapElement obstacle : obstaclesList)
                    if (obstacle.getX() == x && obstacle.getY() == y)
                        noObstacles = false;

                Vertex tmpVertex = null;
                if (noObstacles) {
                    tmpVertex = addVertex(x, y);

                    if (leftVertex != null) {
                        addEdge(leftVertex, tmpVertex, EnumGraph.SPEED_NORMAL);
                        addEdge(tmpVertex, leftVertex, EnumGraph.SPEED_NORMAL);
                    }

                    Vertex upVertex;
                    if (y != 0 && (upVertex = getVertexByLocation(x, y - pace)) != null) {

                        addEdge(upVertex, tmpVertex, EnumGraph.SPEED_NORMAL);
                        addEdge(tmpVertex, upVertex, EnumGraph.SPEED_NORMAL);
                    }
                }
                leftVertex = tmpVertex;
            }
        }
    }

    /**
     * Reinitializes vertices distance and previous attribute
     */
    private void reinitVertices() {
        for (Vertex vertex : listVertex) {
            vertex.setMinDistance(Double.POSITIVE_INFINITY);
            vertex.setMapPrevious(null);
        }
    }

    /**
     * Returns the shortest path in the graph between two vertices using dijkstra algorithm
     *
     * @param start
     * @param destination
     * @return
     */
    public List<Vertex> dijkstra(Vertex start, Vertex destination, EnumMode mode) {
        Color debugColor = EnumColor.getColorAt(-1);
        reinitVertices();

        // ComputePaths
        start.setMinDistance(0.);
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(start);

        while (!vertexQueue.isEmpty()) {
            Vertex current = vertexQueue.poll();

            if (mode.equals(EnumMode.DEBUG))
                Controller.addLocationToMark(current.getLocation(), debugColor);

            if (current.equals(destination))
                return getShortestPath(start, destination);

            for (Edge e : current.getAdjacencies()) {
                Vertex targetVertex = e.getTarget();
                double distanceThroughCurrent = current.getMinDistance() + e.getWeight();

                if (distanceThroughCurrent < targetVertex.getMinDistance()) {
                    targetVertex.setMinDistance(distanceThroughCurrent);
                    targetVertex.addPrevious(current, start);
                    vertexQueue.add(targetVertex);
                }
            }
        }
        vertexQueue.clear();

        return null;
    }

    public List<Vertex> getShortestPath(Vertex start, Vertex destination){
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = destination; vertex != null; vertex = vertex.getPrevious(start))
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }

    public Vertex multipleBFS(EnumMode mode, Vertex... vertex) {
        List<Vertex> listExplorators = new ArrayList<>();
        Map<Vertex, List<Vertex>> visitedVertices = new HashMap<>();
        Map<Vertex, LinkedList<Vertex>> verticesMap = new HashMap<>();

        List<Vertex> visitors;
        Vertex current, neighbor, toRemove = null;
        double distanceThroughCurrent;
        LinkedList<Vertex> queue;

        reinitVertices();
        for (Vertex explorator : vertex) {
            listExplorators.add(explorator);
            verticesMap.put(explorator, new LinkedList<>());

            queue = verticesMap.get(explorator);

            explorator.setMinDistance(0.);
            queue.add(explorator);

            visitVertex(explorator, explorator, visitedVertices, mode, EnumColor.getColorAt(listExplorators.indexOf(explorator)));
        }

        while (!listExplorators.isEmpty()) {
            for (Vertex explorator : listExplorators) {
                queue = verticesMap.get(explorator);
                toRemove = null;

                if ((current = queue.poll()) != null) {
                    for (Edge edge : current.getAdjacencies()) {
                        neighbor = edge.getTarget();
                        visitors = visitedVertices.get(neighbor);
                        distanceThroughCurrent = neighbor.getMinDistance() + edge.getWeight();

                        if ((visitors == null || !visitors.contains(explorator)) && distanceThroughCurrent <= neighbor.getMinDistance()){
                            neighbor.setMinDistance(distanceThroughCurrent);
                            neighbor.addPrevious(current, explorator);

                            visitVertex(neighbor, explorator, visitedVertices, mode, EnumColor.getColorAt(listExplorators.indexOf(explorator)));

                            if (visitedVertices.get(neighbor) != null && visitedVertices.get(neighbor).size() == vertex.length)
                                return neighbor;

                            queue.add(neighbor);
                        }
                    }
                } else toRemove = explorator;
            }
            if (toRemove != null)
                listExplorators.remove(toRemove);
        }
        return null;
    }

    public void visitVertex(Vertex toVisit, Vertex explorator, Map<Vertex, List<Vertex>> visitedVertices, EnumMode mode, Color debugColor){
        List<Vertex> listVisitors = visitedVertices.get(toVisit);

        if (listVisitors == null)
            listVisitors = new ArrayList<>();

        listVisitors.add(explorator);
        visitedVertices.put(toVisit, listVisitors);

        if (mode.equals(EnumMode.DEBUG))
            Controller.addLocationToMark(toVisit.getLocation(), debugColor);
    }

    public void loopToEnqueueAllAdjacencies(Vertex vertex, List<Vertex> visitedVertices, LinkedList<Vertex> queue, LinkedList<Vertex> path, EnumMode mode, Color debugColor){
        Vertex neighbor;
        for (Edge edge : vertex.getAdjacencies()) {
            neighbor = edge.getTarget();

            if (!visitedVertices.contains(neighbor)) {
                queue.add(neighbor);
                path.add(neighbor);
                visitedVertices.add(neighbor);

                if (mode.equals(EnumMode.DEBUG))
                    Controller.addLocationToMark(neighbor.getLocation(), debugColor);
            }
        }
    }

    /**
     * Browses the graph using BFS method
     * @param start
     * @return
     */
    public CircularQueue browseBFS(Vertex start, EnumMode mode){
        Color debugColor = EnumColor.getColorAt(-1);

        List<Vertex> visitedVertices = new ArrayList<>();
        LinkedList<Vertex> queue = new LinkedList<>();
        CircularQueue<Vertex> circularQueue = new CircularQueue<>(listVertex.size());

        queue.add(start);
        visitedVertices.add(start);

        if (mode.equals(EnumMode.DEBUG))
            Controller.addLocationToMark(start.getLocation(), debugColor);

        Vertex current;
        while (!queue.isEmpty()) {
            current = queue.poll();

            loopToEnqueueAllAdjacencies(current, visitedVertices, queue, circularQueue, mode, debugColor);
        }
        return circularQueue;
    }

    /**
     * Browses the graph recursively using DFS
     * @param currentVertex
     * @param allVertices
     */
    private void browseDFS(Vertex currentVertex, List<Vertex> allVertices, CircularQueue circularQueue, EnumMode mode, Color debugColor){
        circularQueue.add(currentVertex);
        allVertices.remove(currentVertex);

        if (mode.equals(EnumMode.DEBUG))
            Controller.addLocationToMark(currentVertex.getLocation(), debugColor);

        if (allVertices.isEmpty())
            return;

        for (Edge e : currentVertex.getAdjacencies()) {
            if (allVertices.contains(e.getTarget())) {
                browseDFS(e.getTarget(), allVertices, circularQueue, mode, debugColor);
            }
        }
    }

    /**
     * Uses the DFS method to return a filled CircularQueue
     * @param start
     * @return
     */
    public CircularQueue browseDFS(Vertex start, EnumMode mode){
        Color debugColor = EnumColor.getColorAt(-1);

        List<Vertex> allVertices = new ArrayList<>(listVertex);
        CircularQueue<Vertex> circularQueue = new CircularQueue<>(listVertex.size());
        browseDFS(start, allVertices, circularQueue, mode, debugColor);
        return circularQueue;
    }

    /**
     * Returns a random vertex
     * @return
     */
    public Vertex getRandomVertex(){
        Random random = new Random();
        int randIndex = random.nextInt(listVertex.size());
        return listVertex.get(randIndex);
    }

    public int getPace() {
        return pace;
    }

    public int getLines() {
        return lines;
    }

    public int getColumns() {
        return columns;
    }

    public int getPixelWidth() {
        return pixelWidth;
    }

    public int getPixelHeight() {
        return pixelHeight;
    }

    public List<MapElement> getObstaclesList() {
        return obstaclesList;
    }
}