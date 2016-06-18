package utils;

import element.Location;
import element.MapElement;
import enumerations.EnumImage;
import enumerations.EnumMode;
import javafx.application.Platform;
import javafx.scene.shape.Rectangle;
import sample.Controller;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by EquipeLabyrinthe on 22/05/2016.
 */
public class TimersHandler {
    public static Controller controller;
    public static Timer timer, timerBrowser, debugTimer, mazeGenerationTimer;

    /**
     * Global timer which stops animation when the agent is done
     */
    public static void startGlobalTimer() {
        cancelTimer(timer);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (Controller.agent != null && Controller.agent.isActionDone()) {
                        cancelTimer(timer);
                        Controller.agent.stopAnimation();
                        controller.removeAgentFromMap();
                        Controller.exit.changeImage(EnumImage.EXIT_CLOSED);
                        controller.disableButtons(false, controller.button_start, controller.button_restart);
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Handle tiles coloration using the list of locations to mark
     */
    public static void startDebugTimer() {
        if (!Controller.mode.equals(EnumMode.DEBUG)) {
            controller.runSimulation();
            return;
        }

        TimersHandler.cancelTimer(debugTimer);

        debugTimer = new Timer();
        debugTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (Controller.locationsToMark.isEmpty()) {
                        TimersHandler.cancelTimer(debugTimer);

                        // Run the simulation when the debug display has ended
                        controller.runSimulation();
                    }
                    else {
                        Rectangle rectangle = Controller.locationsToMark.pop();
                        controller.anchorPane.getChildren().add(rectangle);
                        Controller.markedLocations.add(rectangle);
                    }
                });
            }
        }, 0, 10);
    }

    /**
     * Cancels agent animation
     */
    public static void stopMovements(){
        if (Controller.agent != null)
            Controller.agent.stopAnimation();
    }


    /**
     * Handle timers cancelations
     * @param timers
     */
    public static void cancelTimer(Timer... timers){
        for (Timer timer : timers) {
            if (timer != null) {
                timer.purge();
                timer.cancel();
            }
        }
    }

    public static void cancelAll(){
        cancelTimer(timer, timerBrowser, debugTimer);
        stopMovements();
    }
}
