package sample;

import element.Location;
import enumerations.EnumMode;
import javafx.application.Platform;
import utils.AnimationHandler;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dimitri on 24/05/2016.
 */
public class TimersHandler {
    public static Timer julietteTimer, timer, timerBrowser;

    /**
     * Global timer which stops animation when Romeo and Juliette are done
     */
    public static void startGlobalTimer() {
        cancelTimer(timer);

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (Controller.romeo.isActionDone() && Controller.juliette.isActionDone()) {
                        cancelTimer(timer);
                        Controller.romeo.stopAnimation();
                        Controller.juliette.stopAnimation();
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Timer which handles the random walk of Juliette
     */
    public static void startJulietteTimer() {
        cancelTimer(julietteTimer);

        julietteTimer = new Timer();
        julietteTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if(!Controller.walkRandomly(Controller.juliette)) {
                        cancelTimer(julietteTimer);
                        Controller.juliette.stopAnimation();
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Global timer which handles the movements of Romeo trying to find Juliette
     */
    public static void startTimerBrowser(Controller controller) {
        cancelTimer(timerBrowser);

        timerBrowser = new Timer();
        timerBrowser.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    if (Controller.areLocationsClose(Controller.romeo.getLocation(), Controller.juliette.getLocation())){
                        Controller.juliette.stopAnimation();
                        controller.romeoRunTheShortestPathToVertex(Controller.graph.getVertexByLocation(Controller.juliette.getLocation()), EnumMode.NORMAL);
                        cancelTimer(timerBrowser, julietteTimer);
                    }

                    if (Controller.romeo.isActionDone()){
                        Location location = Controller.path.popFirstAndRepushAtTheEnd().getLocation();

                        if (Controller.areLocationsClose(Controller.graph.getVertexByLocation(Controller.romeo.getLocation()).getLocation(), location)){
                            Controller.romeo.animate();
                            Controller.romeo.setLocation(location);
                        }
                        else {
                            controller.romeoRunTheShortestPathToVertex(Controller.graph.getVertexByLocation(location), EnumMode.NORMAL);
                        }
                    }
                });
            }
        }, 0, 300);
    }

    /**
     * Cancels both Romeo and Juliette
     */
    public static void stopMovements(){
        if (Controller.romeo != null)
            Controller.romeo.stopAnimation();

        if (Controller.juliette != null)
            Controller.juliette.stopAnimation();

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
}
