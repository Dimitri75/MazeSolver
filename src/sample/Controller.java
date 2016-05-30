package sample;

import element.Character;
import element.Location;
import element.MapElement;
import enumerations.*;
import graph.Graph;
import graph.MazeGenerator;
import graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import utils.ResourcesUtils;
import utils.TimersHandler;

import java.util.*;

public class Controller {
    @FXML
    private Slider slider_size;
    @FXML
    public Button button_start, button_restart, button_start_dijkstra, button_start_astar;
    @FXML
    private CheckBox checkbox_debug;
    @FXML
    private VBox vbox_options;
    @FXML
    private Label label_error;
    @FXML
    public AnchorPane anchorPane;

    private static Integer PACE;

    public static EnumMode mode;
    private static boolean launched, started, pathFound;
    public static Graph graph;
    public static Character agent;
    public static MapElement exit;
    public static Thread agentThread;
    public static List<Rectangle> markedLocations = new ArrayList<>();
    public static LinkedList<Rectangle> locationsToMark = new LinkedList<>();
    private static Color lastColor;

    public Controller() {
    }

    @FXML
    public void start() {
        TimersHandler.controller = this;
        Character.controller = this;

        clear();
        initMap();
        mode = checkbox_debug.isSelected() ? EnumMode.DEBUG : EnumMode.NORMAL;

        displayButtons(true, button_start_dijkstra, button_start_astar);
        vbox_options.setDisable(true);
        button_restart.setDisable(false);
        started = true;
        launched = false;
    }

    @FXML
    public void restart(){
        clearAll();
        displayButtons(false, button_start_dijkstra, button_start_astar);
        vbox_options.setDisable(false);
        button_restart.setDisable(true);

        started = false;
        launched = false;
        showInstructions();
    }

    @FXML
    public void start_dijkstra() {
        displayButtons(false, button_start_dijkstra, button_start_astar);
        disableButtons(true, button_start, button_restart);

        agentRunsDijkstra(graph.getVertexByLocation(exit.getX(), exit.getY()), mode);
        launched = true;
    }

    @FXML
    public void start_astar() {
        displayButtons(false, button_start_dijkstra, button_start_astar);
        disableButtons(true, button_start, button_restart);

        agentRunsAStar(graph.getVertexByLocation(exit.getX(), exit.getY()), mode);
        launched = true;
    }

    /**
     * Initializes the map
     */
    public void initMap() {
        slider_size.setFocusTraversable(false);
        button_start.setFocusTraversable(false);
        PACE = (slider_size.getValue() < 25) ? 25 : (int) slider_size.getValue();
        if (!started) initGraph();

        initAgentAndExit();
    }

    /**
     * Handles the initialization of both the agent and the exit
     */
    public void initAgentAndExit() {
        try {
            exit = new MapElement(0, 0, PACE, EnumImage.EXIT_OPENED);
            agent = new Character(0, 0, PACE, EnumImage.AGENT, EnumSprite.AGENT_SPRITE);

            while (!checkIfNoCharacters(agent.getLocation(), exit)) {
                initCharacter(exit);
                initCharacter(agent);
            }
        } catch (Exception e) {
            label_error.setText("Bravo ! Maintenant c'est cassé. :(");
            e.printStackTrace();
        }
    }

    /**
     * Generic method to initForResolution a character at a random position
     * @param character
     */
    public void initCharacter(MapElement character) {
        Random random = new Random();

        int randX = -1;
        int randY = -1;

        while (randX + character.getShape().getWidth() > anchorPane.getWidth() ||
                randY + character.getShape().getHeight() > anchorPane.getHeight() ||
                !checkIfNoObstacles(randX, randY) ||
                randX < 0 || randY < 0) {
            randX = random.nextInt((int) anchorPane.getWidth());
            randY = random.nextInt((int) anchorPane.getHeight());

            randX -= randX % PACE;
            randY -= randY % PACE;
        }

        character.translateX(randX);
        character.translateY(randY);

        anchorPane.getChildren().add(character.getShape());
    }

    /**
     * Initializes the graph using obstacles and the size of the window
     */
    public void initGraph() {
        graph = new Graph((int) anchorPane.getWidth(), (int) anchorPane.getHeight(), PACE);
        initObstacles();
        graph.initForResolution();
    }

    /**
     * Place obstacles around the map according to the size of it
     */
    public void initObstacles() {
        //MazeGenerator.basicMaze();
        MazeGenerator.dfsMaze();

        for (MapElement obstacle : graph.getObstaclesList())
            anchorPane.getChildren().add(obstacle.getShape());
    }

    /**
     * Allow to add or remove obstacles by clicks
     * @param e
     */
    @FXML
    public void setObstacle(MouseEvent e) {
        if (started && !launched) {
            MapElement obstacle = null;
            int x = (int) e.getSceneX() - (int) e.getSceneX() % PACE;
            int y = (int) e.getSceneY() - (int) e.getSceneY() % PACE;

            if (checkIfNoObstacles(x, y) && checkIfNoCharacters(new Location(x, y), agent, exit)) {
                obstacle = new MapElement(x, y, PACE, ResourcesUtils.getInstance().getObstacle());
                anchorPane.getChildren().add(obstacle.getShape());
                graph.getObstaclesList().add(obstacle);
            }
            else {
                // Check if the position clicked is an obstacle
                for (MapElement element : graph.getObstaclesList())
                    if (element.getX() == x && element.getY() == y)
                        obstacle = element;

                // Remove the obstacle from the graph
                if (obstacle != null) {
                    anchorPane.getChildren().remove(obstacle.getShape());
                    graph.getObstaclesList().remove(obstacle);
                }
            }
            graph.initForResolution();
        }
    }

    /**
     * Clears the map and remove elements
     */
    public void clearAll() {
        clearObstacles();
        clear();
    }

    /**
     * Remove the obstacles on the map and clean the list
     */
    public void clearObstacles(){
        for (MapElement obstacle : graph.getObstaclesList())
            anchorPane.getChildren().remove(obstacle.getShape());

        graph.getObstaclesList().clear();
    }

    /**
     * Cancel timers and clean GUI
     */
    public void clear(){
        clearLocations();
        TimersHandler.cancelTimer(TimersHandler.timer, TimersHandler.timerBrowser, TimersHandler.debugTimer);
        TimersHandler.stopMovements();

        label_error.setText("");

        removeAgentFromMap();

        if (exit != null) {
            anchorPane.getChildren().remove(exit.getShape());
            exit = null;
        }
    }

    public void removeAgentFromMap(){
        if (agent != null) {
            anchorPane.getChildren().remove(agent.getShape());
            agent = null;
        }
    }

    /**
     * Controls the buttons visibility
     * @param bool
     */
    public void displayButtons(boolean bool, Button... buttons){
        for (Button button : buttons)
            button.setVisible(bool);
    }

    /**
     * Controls the buttons
     * @param bool
     */
    public void disableButtons(boolean bool, Button... buttons){
        for (Button button : buttons)
            button.setDisable(bool);
    }

    /**
     * Starts simulation where the agent runs the shortest path to the given destination
     * @param destination
     */
    public void agentRunsDijkstra(Vertex destination, EnumMode mode){
        if (agentThread != null)
            agentThread.interrupt();

        Vertex agentVertex = graph.getVertexByLocation(agent.getX(), agent.getY());
        pathFound = agent.initPathDijkstra(graph, agentVertex, destination, mode);

        // The debug timer will starts the simulation after being done
        TimersHandler.startDebugTimer();
    }

    /**
     * Starts simulation where the agent runs the shortest path to the given destination
     * @param destination
     */
    public void agentRunsAStar(Vertex destination, EnumMode mode){
        if (agentThread != null)
            agentThread.interrupt();

        Vertex agentVertex = graph.getVertexByLocation(agent.getX(), agent.getY());
        pathFound = agent.initPathAStar(graph, agentVertex, destination, mode);

        // The debug timer will starts the simulation after being done
        TimersHandler.startDebugTimer();
    }

    public void runSimulation(){
        if (pathFound) {
            colorPath(agent.path);
            replaceElementsOnTop(exit.getShape(), agent.getShape());

            agentThread = new Thread(agent);
            TimersHandler.startGlobalTimer();
            agentThread.start();
        }
        else
            showAlertNoPathAvailable();
    }

    /**
     * Replace elements on top the scene by removing and readding them
     * @param shapes
     */
    public void replaceElementsOnTop(Shape... shapes){
        for (Shape shape : shapes){
            anchorPane.getChildren().remove(shape);
            anchorPane.getChildren().add(shape);
        }
    }

    /**
     * Returns true if an obstacle is found at the given position
     * @param x
     * @param y
     * @return
     */
    public static boolean checkIfNoObstacles(int x, int y) {
        for (MapElement obstacle : graph.getObstaclesList())
            if (obstacle.getX() == x && obstacle.getY() == y ||
                    x < 0 || y < 0 || x >= graph.getPixelWidth() || y >= graph.getPixelHeight())
                return false;
        return true;
    }

    public boolean checkIfNoCharacters(Location location, MapElement... characters) {
        for (MapElement character : characters){
            if (character.getX() == location.getX() && character.getY() == location.getY())
                return false;
        }
        return true;
    }

    /**
     * Clears previously marked locations
     */
    public void clearLocations(){
        if (markedLocations != null && !markedLocations.isEmpty()){
            for (Rectangle rectangle : markedLocations)
                anchorPane.getChildren().remove(rectangle);
            markedLocations.clear();
        }

        if (locationsToMark != null && !locationsToMark.isEmpty()){
            locationsToMark.clear();
        }
    }

    public void showAlertNoPathAvailable(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("No path available");
        alert.setContentText("We haven't found a correct path ! Parhaps should you think about removing few obstacles next time. 8)");
        alert.show();
        disableButtons(false, button_start, button_restart);
    }

    public static void showInstructions(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Maze Generator & Solver");
        alert.setContentText(EnumText.INSTRUCTIONS.toString());
        alert.show();
    }

    /**
     * Mark a location which will be colored by the debug timer
     * @param location
     * @param color
     */
    public static void addLocationToMark(Location location, Color color){
        locationsToMark.add(locationToRectangle(location, color));
        lastColor = color;
    }

    /**
     * Color a location in the scene
     * @param location
     * @param color
     */
    public void markLocation(Location location, Color color){
        Rectangle rectangle = locationToRectangle(location, color);
        markedLocations.add(rectangle);
        anchorPane.getChildren().add(rectangle);
    }

    public void colorPath(List<Vertex> path){
        for (Vertex vertex : path)
            markLocation(vertex.getLocation(), lastColor);
    }

    /**
     * Transform a location to a javafx Rectangle
     * @param location
     * @param color
     * @return
     */
    private static Rectangle locationToRectangle(Location location, Color color){
        Rectangle rectangle = new Rectangle(PACE, PACE);
        rectangle.setX(location.getX());
        rectangle.setY(location.getY());
        rectangle.setFill(color);
        rectangle.setStroke(Color.LIGHTGRAY);
        rectangle.setOpacity(0.5);
        return rectangle;
    }
}