package osnvodsim.event;

import osnvodsim.simulator.State;

/**
 * Created by Guowei on 2014/9/10.
 */
public class SystemEventController implements EventController {
    public static final int SIMULATION_END = 0;
    public static final int DISPLAY_RUNNING_STATE = 1;

    @Override
    public boolean process(Event event) {
        switch (event.getType()) {
            case SIMULATION_END:
                return false;

            case DISPLAY_RUNNING_STATE:

                System.out.println("simulator running for " + (int) State.getTime() + "s!");
                EventHandler.scheduleSystemEvent(State.getTime() + 10, DISPLAY_RUNNING_STATE);

                return true;

        }


        return false;
    }
}
