package sample;

import enumerations.EnumImage;
import javafx.application.Platform;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dimitri on 24/05/2016.
 */
public class TimersHandler {
    public static Controller controller;
    public static Timer timer, timerBrowser;

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
                    if (Controller.agent.isActionDone()) {
                        cancelTimer(timer);
                        Controller.agent.stopAnimation();

                        controller.removeAgentFromMap();
                        Controller.exit.changeImage(EnumImage.EXIT_CLOSED);
                    }
                });
            }
        }, 0, 300);
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
}
