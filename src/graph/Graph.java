package graph;

import element.Location;
import element.MapElement;
import enumerations.EnumColor;
import enumerations.EnumGraph;
import enumerations.EnumMode;
import javafx.scene.paint.Color;
import list.CircularQueue;
import sample.Controller;
import utils.ResourcesUtils;

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
        columns = pixelHeight / pace;

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

    public void clearGraph(){
        listVertex.clear();
        listEdges.clear();
    }

    /**
     * Initialize the graph according to the map and the obstacles
     */
    public void initForResolution() {
        clearGraph();
        boolean noObstacles;
        for (int y = 0; y < pixelHeight; y += pace) {
            Vertex leftVertex = null;
            for (int x = 0; x < pixelWidth; x += pace) {
                noObstacles = !obstaclesList.contains(new Location(x, y));

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

    public void fillWithWalls(){
        for (int y = 0; y < pixelHeight; y += pace)
            for (int x = 0; x < pixelWidth; x += pace)
                obstaclesList.add(new MapElement(x, y, pace, ResourcesUtils.getInstance().getObstacleImage()));
    }

    public void initForGeneration() {
        clearGraph();
        fillWithWalls();
        for (int y = 0; y < pixelHeight; y += pace) {
            Vertex leftVertex = null;
            for (int x = 0; x < pixelWidth; x += pace) {
                Vertex tmpVertex;
                tmpVertex = addVertex(x, y);

                if (leftVertex != null) {
                    addEdge(leftVertex, tmpVertex, EnumGraph.SPEED_NORMAL);
                    addEdge(tmpVertex, leftVertex, EnumGraph.SPEED_NORMAL);

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
     * Add the location to the timer which will display how the algorithm works
     */
    public void colorifyLocation(EnumMode mode, Vertex vertex, Color color){
        if (mode.equals(EnumMode.DEBUG))
            Controller.addLocationToMark(vertex.getLocation(), color);
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
        if (!listVertex.contains(start) || !listVertex.contains(destination))
            return null;

        Color debugColor = EnumColor.getColorAt(-1);
        PriorityQueue<Vertex> vertexQueue = computePaths(start);

        while (!vertexQueue.isEmpty()) {
            Vertex current = vertexQueue.poll();
            colorifyLocation(mode, current, debugColor);

            if (current.equals(destination))
                return getShortestPath(start, destination);

            workForAdjacencies(current, start, vertexQueue);
        }
        vertexQueue.clear();

        return null;
    }

    public List<Vertex> aStar(Vertex start, Vertex destination, EnumMode mode) {
        LinkedList<Vertex> openList = new LinkedList();
        LinkedList<Vertex> closedList = new LinkedList();
        Color debugColor = EnumColor.getColorAt(-1);

        start.cost = 0;
        start.estimatedCost = start.distanceEuclidienne(start,destination);
        start.pathParent = null;
        openList.add(start);

        while (!openList.isEmpty()) {
            Collections.sort(openList, new Comparator<Vertex>(){
                public int compare(Vertex v1, Vertex v2){
                    return (int)(v1.estimatedCost - v2.estimatedCost);
                }
            });

            Vertex n = openList.removeFirst();
            colorifyLocation(mode, n, debugColor);
            if (n == destination) {
                return constructPath(destination);
            }

            List<Vertex> neighborsList = n.getNeighbors();
            for (int i=0; i<neighborsList.size(); i++) {
                Vertex neighbor = neighborsList.get(i);
                boolean isContainsOpenList = openList.contains(neighbor);
                boolean isContainsClosedList = closedList.contains(neighbor);
                double nextCost = n.cost + n.getCost(neighbor);


                if (nextCost < neighbor.cost) {
                    if (isContainsOpenList) {
                        closedList.remove(neighbor);
                    }
                    if (isContainsClosedList) {
                        closedList.remove(neighbor);
                    }
                }


                if (!isContainsOpenList && !isContainsClosedList)
                {
                    neighbor.pathParent = n;
                    neighbor.cost = nextCost;
                    neighbor.estimatedCost = neighbor.distanceEuclidienne(neighbor,destination);
                    openList.add(neighbor);
                }
            }
            closedList.add(n);
        }

        return null;
    }

    public PriorityQueue<Vertex> computePaths(Vertex start){
        reinitVertices();
        start.setMinDistance(0.);
        PriorityQueue<Vertex> vertexQueue = new PriorityQueue<>();
        vertexQueue.add(start);
        return vertexQueue;
    }

    public void workForAdjacencies(Vertex current, Vertex start, PriorityQueue vertexQueue){
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

    public List<Vertex> getShortestPath(Vertex start, Vertex destination){
        List<Vertex> path = new ArrayList<>();
        for (Vertex vertex = destination; vertex != null; vertex = vertex.getPrevious(start))
            path.add(vertex);

        Collections.reverse(path);

        if (!path.contains(destination) || !path.contains(start))
            return null;

        return path;
    }

    private List<Vertex> constructPath(Vertex node) {
        LinkedList path = new LinkedList();
        while (node.pathParent != null) {
            path.addFirst(node);
            node = node.pathParent;
        }
        return path;
    }

    public List<Vertex> bfs(Vertex start, Vertex destination, EnumMode mode) {
        LinkedList closedList = new LinkedList();
        LinkedList openList = new LinkedList();
        Color debugColor = EnumColor.getColorAt(-1);

        openList.add(start);
        start.pathParent = null;

        while (!openList.isEmpty()) {
            Vertex n = (Vertex)openList.removeFirst();
            colorifyLocation(mode, n, debugColor);

            if (n == destination) {
                return constructPath(destination);
            }
            else {
                closedList.add(n);
                Iterator i = n.getNeighbors().iterator();
                while (i.hasNext()) {
                    Vertex neighbor = (Vertex)i.next();
                    if (!closedList.contains(neighbor) && !openList.contains(neighbor))
                    {
                        neighbor.pathParent = n;
                        openList.add(neighbor);
                    }
                }
            }
        }
        return null;
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
        colorifyLocation(mode, start, debugColor);

        Vertex current;
        while (!queue.isEmpty()) {
            current = queue.poll();
            loopToEnqueueAllAdjacencies(current, visitedVertices, queue, circularQueue, mode, debugColor);
        }
        return circularQueue;
    }

    public void loopToEnqueueAllAdjacencies(Vertex vertex, List<Vertex> visitedVertices, LinkedList<Vertex> queue, LinkedList<Vertex> path, EnumMode mode, Color debugColor){
        Vertex neighbor;
        for (Edge edge : vertex.getAdjacencies()) {
            neighbor = edge.getTarget();

            if (!visitedVertices.contains(neighbor)) {
                queue.add(neighbor);
                path.add(neighbor);
                visitedVertices.add(neighbor);
                colorifyLocation(mode, neighbor, debugColor );
            }
        }
    }

    /**
     * Uses the dfs method to return a filled CircularQueue
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
     * Browses the graph recursively using dfsMaze
     * @param currentVertex
     * @param allVertices
     */
    private void browseDFS(Vertex currentVertex, List<Vertex> allVertices, CircularQueue circularQueue, EnumMode mode, Color debugColor){
        circularQueue.add(currentVertex);
        allVertices.remove(currentVertex); // non visited
        colorifyLocation(mode, currentVertex, debugColor);

        if (allVertices.isEmpty())
            return;

        for (Edge e : currentVertex.getAdjacencies()) {
            if (allVertices.contains(e.getTarget())) {
                browseDFS(e.getTarget(), allVertices, circularQueue, mode, debugColor);
            }
        }
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

    public List<Vertex> getListVertices() {
        return listVertex;
    }
}