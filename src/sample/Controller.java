package sample;

import element.Character;
import element.Location;
import element.MapElement;
import enumerations.*;
import graph.Graph;
import graph.MazeGenerator;
import graph.Vertex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import list.CircularQueue;
import utils.AnimationHandler;
import utils.ResourcesUtils;

import java.util.*;

public class Controller {
    @FXML
    private Slider slider_size;
    @FXML
    private Button button_start, button_restart, button_start_simpleDijkstra, button_start_dijkstraToEachOther, button_start_DFS, button_start_BFS;
    @FXML
    private CheckBox checkbox_debug;
    @FXML
    private VBox vbox_options;
    @FXML
    private Label label_error;
    @FXML
    private AnchorPane anchorPane;

    private static Integer PACE;

    private EnumMode mode;
    private boolean launched, started, pathFound;
    private Timer julietteTimer, timer, timerBrowser, debugTimer;
    private AnimationHandler julietteAnimation, romeoAnimation;
    private Graph graph;
    private Character romeo, juliette;
    private Thread romeoThread, julietteThread;
    private CircularQueue<Vertex> path;
    private List<Rectangle> markedLocations = new ArrayList<>();
    private static LinkedList<Rectangle> locationsToMark = new LinkedList<>();

    public Controller() {
    }

    @FXML
    public void start() {
        clear();
        initMap();
        displayButtons(true);

        if (checkbox_debug.isSelected())
            mode = EnumMode.DEBUG;
        else
            mode = EnumMode.NORMAL;

        vbox_options.setDisable(true);
        button_restart.setDisable(false);

        started = true;
        launched = false;
    }

    @FXML
    public void restart(){
        clearAll();
        displayButtons(false);
        vbox_options.setDisable(false);
        button_restart.setDisable(true);

        started = false;
        launched = false;
        showInstructions();
    }

    @FXML
    public void start_simpleDijkstra() {
        displayButtons(false);
        romeoRunTheShortestPathToVertex(graph.getVertexByLocation(juliette.getX(), juliette.getY()), mode);
        launched = true;
    }

    @FXML
    public void start_multipleBFS() {
        displayButtons(false);
        romeoAndJulietteFindEachOther();
        launched = true;
    }

    @FXML
    public void start_DFS() {
        displayButtons(false);
        romeoLooksForJuliette(EnumGraph.DFS);
        launched = true;
    }

    @FXML
    public void start_BFS() {
        displayButtons(false);
        romeoLooksForJuliette(EnumGraph.BFS);
        launched = true;
    }

    /**
     * Initializes the map
     */
    public void initMap() {
        slider_size.setFocusTraversable(false);
        button_start.setFocusTraversable(false);
        PACE = (slider_size.getValue() < 50) ? 50 : (int) slider_size.getValue();

        initGraph();
        initRomeoAndJuliette();
    }

    /**
     * Handles the initialization of both Romeo and Juliette
     */
    public void initRomeoAndJuliette() {
        try {
            romeo = new Character(0, 0, PACE, EnumImage.PANDA);
            juliette = new Character(0, 0, PACE, EnumImage.RACCOON);

            while (!checkIfNoCharacters(romeo.getLocation(), juliette)) {
                initCharacter(romeo);
                initCharacter(juliette);
            }
        } catch (Exception e) {
            label_error.setText("Bravo ! Maintenant c'est cassé. :(");
            e.printStackTrace();
        }
    }

    /**
     * Generic method to init a character at a random position
     * @param character
     */
    public void initCharacter(Character character) {
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
        graph.init();
    }

    /**
     * Place obstacles around the map according to the size of it
     */
    public void initObstacles() {
        MazeGenerator.basicMaze(graph);
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

            if (checkIfNoObstacles(x, y) && checkIfNoCharacters(new Location(x, y), romeo, juliette)) {
                obstacle = new MapElement(x, y, PACE, ResourcesUtils.getInstance().getObstacle());
                anchorPane.getChildren().add(obstacle.getShape());
                graph.getObstaclesList().add(obstacle);
            }
            else {
                for (MapElement element : graph.getObstaclesList()) {
                    if (element.getX() == x && element.getY() == y) {
                        obstacle = element;
                        anchorPane.getChildren().remove(element.getShape());
                    }
                }
                if (obstacle != null)
                    graph.getObstaclesList().remove(obstacle);
            }
            initGraph();
        }
    }

    /**
     * Clears the map and remove elements
     */
    public void clearAll() {
        clear();

        for (MapElement obstacle : graph.getObstaclesList())
            anchorPane.getChildren().remove(obstacle.getShape());
        graph.getObstaclesList().clear();
    }

    /**
     * Cancel timers and clean GUI
     */
    public void clear(){
        clearLocations();
        cancelTimer(timer, timerBrowser, debugTimer);
        stopMovements();

        label_error.setText("");

        if (romeo != null) {
            anchorPane.getChildren().remove(romeo.getShape());
            romeo = null;
        }

        if (juliette != null) {
            anchorPane.getChildren().remove(juliette.getShape());
            juliette = null;
        }
    }

    /**
     * Controls the buttons visibility
     * @param bool
     */
    public void displayButtons(boolean bool){
        button_start_simpleDijkstra.setVisible(bool);
        button_start_dijkstraToEachOther.setVisible(bool);
        button_start_DFS.setVisible(bool);
        button_start_BFS.setVisible(bool);
    }

    /**
     * Starts simulation where Romeo run the shortest path to the given destination
     * @param destination
     */
    public void romeoRunTheShortestPathToVertex(Vertex destination, EnumMode mode){
        stopRomeo();

        Vertex romeoVertex = graph.getVertexByLocation(romeo.getX(), romeo.getY());
        pathFound = romeo.initPathDijkstra(graph, romeoVertex, destination, mode);
        startDebugTimer();

        if (pathFound) {
            romeoThread = new Thread(romeo);
            startGlobalTimer();
            romeoThread.start();

            animateRomeo();
        }
        else {
            showAlertNoPathAvailable();
        }
    }

    /**
     * Starts simulation where Romeo and Juliette get to each other through the shortest path in the graph
     */
    public void romeoAndJulietteFindEachOther() {
        try {
            stopMovements();
            Vertex romeoVertex = graph.getVertexByLocation(romeo.getX(), romeo.getY());
            Vertex julietteVertex = graph.getVertexByLocation(juliette.getX(), juliette.getY());

            Vertex destination = graph.multipleBFS(mode, graph.getVertexByLocation(romeo.getLocation()), graph.getVertexByLocation(juliette.getLocation()));
            startDebugTimer();

            if (destination != null){
                romeo.initPath(graph.getShortestPath(romeoVertex, destination));
                juliette.initPath(graph.getShortestPath(julietteVertex, destination));

                romeoThread = new Thread(romeo);
                julietteThread = new Thread(juliette);

                startGlobalTimer();
                romeoThread.start();
                julietteThread.start();

                animateJuliette();
                animateRomeo();
            }
            else {
                showAlertNoPathAvailable();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * Starts simulation where Romeo tries to find Juliette without knowing her exact position
     */
    public void romeoLooksForJuliette(EnumGraph enumGraph){
        try {
            stopMovements();
            initBrowsingPathFrom(juliette, enumGraph);

            if (path == null || path.isEmpty()){
                showAlertNoPathAvailable();
                return;
            }

            startJulietteTimer();
            startTimerBrowser();

            animateJuliette();
            animateRomeo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Global timer which stops animation when Romeo and Juliette are done
     */
    public void startGlobalTimer() {
        cancelTimer(timer);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (romeo.isActionDone() && juliette.isActionDone())
                        cancelTimer(romeoAnimation, julietteAnimation, timer);
                });
            }
        }, 0, 300);
    }

    /**
     * Timer which handles the random walk of Juliette
     */
    public void startJulietteTimer() {
        cancelTimer(julietteTimer);

        julietteTimer = new Timer();
        julietteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(!walkRandomly(juliette)) {
                        cancelTimer(julietteTimer, julietteAnimation);
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Global timer which handles the movements of Romeo trying to find Juliette
     */
    public void startTimerBrowser() {
        cancelTimer(timerBrowser);

        timerBrowser = new Timer();
        timerBrowser.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (areLocationsClose(romeo.getLocation(), juliette.getLocation())){
                        stopJuliette();
                        romeoRunTheShortestPathToVertex(graph.getVertexByLocation(juliette.getLocation()), EnumMode.NORMAL);
                        cancelTimer(timerBrowser);
                    }

                    if (romeo.isActionDone()){
                        Location location = path.popFirstAndRepushAtTheEnd().getLocation();

                        if (areLocationsClose(graph.getVertexByLocation(romeo.getLocation()).getLocation(), location)){
                            animateRomeo();
                            romeo.setLocation(location);
                        }
                        else {
                            romeoRunTheShortestPathToVertex(graph.getVertexByLocation(location), EnumMode.NORMAL);
                        }
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Cancels both Romeo and Juliette
     */
    public void stopMovements(){
        stopRomeo();
        stopJuliette();
    }

    /**
     * Handles cancelation of Romeo's animations and thread
     */
    public void stopRomeo() {
        cancelTimer(romeoAnimation);

        if (romeoThread != null)
            romeoThread.interrupt();
        romeoThread = null;
    }

    /**
     * Handles cancelation of Juliette's animations and thread
     */
    public void stopJuliette() {
        cancelTimer(julietteTimer, julietteAnimation);

        if (julietteThread != null)
            julietteThread.interrupt();
        julietteThread = null;
    }

    /**
     * Handle timers cancelations
     * @param timers
     */
    public void cancelTimer(Timer... timers){
        for (Timer timer : timers) {
            if (timer != null) {
                timer.purge();
                timer.cancel();
            }
        }
    }

    /**
     * Randomly moves a character up, down, left or right
     * @param character
     */
    public boolean walkRandomly(Character character) {
        int x = character.getX();
        int y = character.getY();

        Map<Integer, Location> movementsDictionnary = new HashMap<>();
        movementsDictionnary.put(EnumPosition.LEFT.toInteger(), new Location(x - PACE, y));
        movementsDictionnary.put(EnumPosition.RIGHT.toInteger(), new Location(x + PACE, y));
        movementsDictionnary.put(EnumPosition.UP.toInteger(), new Location(x, y + PACE));
        movementsDictionnary.put(EnumPosition.DOWN.toInteger(), new Location(x, y - PACE));

        int possibleMovements = 0;
        for (Location location : movementsDictionnary.values()){
            if (checkIfNoObstacles(location.getX(), location.getY()))
                possibleMovements++;
        }

        if (possibleMovements == 0)
            return false;

        Random random = new Random();
        boolean hasMoved = false;
        while (!hasMoved) {
            int position = random.nextInt(4);
            Location location = movementsDictionnary.get(position);

            if (checkIfNoObstacles(location.getX(), location.getY())) {
                character.translateX(location.getX());
                character.translateY(location.getY());
                hasMoved = true;
            }
        }
        return true;
    }


    /**
     * Initializes a path to browse the graph
     * @param character
     */
    public void initBrowsingPathFrom(Character character, EnumGraph enumGraph) {
        Vertex start = graph.getVertexByLocation(character.getLocation());
        path = enumGraph.equals(EnumGraph.BFS) ? graph.browseBFS(start, mode) : graph.browseDFS(start, mode);
        startDebugTimer();
    }

    /**
     * Returns true if an obstacle is found at the given position
     * @param x
     * @param y
     * @return
     */
    public boolean checkIfNoObstacles(int x, int y) {
        for (MapElement obstacle : graph.getObstaclesList())
            if (obstacle.getX() == x && obstacle.getY() == y ||
                    x < 0 || y < 0 || x >= anchorPane.getWidth() || y >= anchorPane.getHeight())
                return false;
        return true;
    }

    public boolean checkIfNoCharacters(Location location, Character... characters) {
        for (Character character : characters){
            if (character.getX() == location.getX() && character.getY() == location.getY())
                return false;
        }
        return true;
    }

    /**
     * Returns true if two locations have been found close
     * @param location1
     * @param location2
     * @return
     */
    public boolean areLocationsClose(Location location1, Location location2) {
        if ((location1.getX() == location2.getX() && location1.getY() == location2.getY()) ||
                (location1.getX() == location2.getX() + PACE && location1.getY() == location2.getY()) ||
                (location1.getX() == location2.getX() - PACE && location1.getY() == location2.getY()) ||
                (location1.getX() == location2.getX() && location1.getY() == location2.getY() + PACE) ||
                (location1.getX() == location2.getX() && location1.getY() == location2.getY() - PACE) /*||
                (location1.getX() == location2.getX() + 2*PACE && location1.getY() == location2.getY()) ||
                (location1.getX() == location2.getX() - 2*PACE && location1.getY() == location2.getY()) ||
                (location1.getX() == location2.getX() && location1.getY() == location2.getY() + 2*PACE) ||
                (location1.getX() == location2.getX() && location1.getY() == location2.getY() - 2*PACE)*/) {
            return true;
        }
        return false;
    }

    /**
     * COntrols romeo's animation
     */
    public void animateRomeo(){
        if (romeoAnimation != null) {
            romeoAnimation.purge();
            romeoAnimation.cancel();
        }

        romeoAnimation = new AnimationHandler(romeo, EnumSprite.PANDA_SPRITE);
        romeoAnimation.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> romeoAnimation.changeFrame());
            }
        }, 0, 150);
    }

    /**
     * Controls juliette's animation
     */
    public void animateJuliette(){
        if (julietteAnimation != null) {
            julietteAnimation.purge();
            julietteAnimation.cancel();
        }

        julietteAnimation = new AnimationHandler(juliette, EnumSprite.RACCOON_SPRITE);
        julietteAnimation.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> julietteAnimation.changeFrame());
            }
        }, 0, 150);
    }


    /**
     * Clears previously marked locations
     */
    public void clearLocations(){
        if (markedLocations != null && !markedLocations.isEmpty()){
            for (Rectangle rectangle : markedLocations){
                anchorPane.getChildren().remove(rectangle);
            }
            markedLocations.clear();
        }

        if (locationsToMark != null && !locationsToMark.isEmpty()){
            locationsToMark.clear();
        }
    }

    /**
     * Mark a location which will be colored by the debug timer
     * @param location
     * @param color
     */
    public static void addLocationToMark(Location location, Color color){
        Rectangle rectangle = new Rectangle(PACE, PACE);
        rectangle.setX(location.getX());
        rectangle.setY(location.getY());
        rectangle.setFill(color);
        rectangle.setStroke(color.LIGHTGRAY);
        rectangle.setOpacity(0.5);

        locationsToMark.add(rectangle);
    }

    /**
     * Handle tiles coloration using the list of locations to mark
     */
    public void startDebugTimer() {
        if (!mode.equals(EnumMode.DEBUG))
            return;

        cancelTimer(debugTimer);

        debugTimer = new Timer();
        debugTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (locationsToMark.isEmpty())
                        cancelTimer(debugTimer);
                    else {
                        Rectangle rectangle = locationsToMark.pop();
                        anchorPane.getChildren().add(rectangle);
                        markedLocations.add(rectangle);
                    }
                });
            }
        }, 0, 10);
    }

    public void showAlertNoPathAvailable(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("No path available");
        alert.setContentText("We haven't found a correct path ! Parhaps should you think about removing few obstacles next time. 8)");
        alert.show();
    }

    public static void showInstructions(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information Dialog");
        alert.setHeaderText("Romeo & Juliet");
        alert.setContentText(EnumText.INSTRUCTIONS.toString());
        alert.show();
    }
}