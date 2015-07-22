package osnvodsim.event;

import osnvodsim.distribution.P2PNetwork;
import osnvodsim.interactivity.Interactivity;
import osnvodsim.statistics.Output;

/**
 * Created by Guowei on 2014/9/10.
 */
public class UserEventController implements EventController {

    public static final int EVENT_USER_ARRIVE = 0;
    public static final int EVENT_USER_LEAVE = 1;
    public static final int EVENT_USER_NEXT_VIDEO = 2;


    @Override
    public boolean process(Event event) {

        UserEvent toProcess = (UserEvent) event;
        switch (event.getType()) {
            case EVENT_USER_ARRIVE:
                Output.printUAEvent("Event" + toProcess.getEventID() + " a new user is coming! ");

                Interactivity.nextUser();
                break;
            case EVENT_USER_LEAVE:
                Output.printUAEvent("Event" + toProcess.getEventID() + " user" + toProcess.getWho().getUserID() + " has watched " + toProcess.getWho().getWatchedNum() + " videos and exited!");
                Output.printTrack(toProcess.getWho());

                P2PNetwork.dropService(toProcess.getWho());

                break;
            case EVENT_USER_NEXT_VIDEO:
                Output.printUAEvent("Event" + toProcess.getEventID() + " user" + toProcess.getWho().getUserID() + " has finished the video" + toProcess.getWho().getPlayingVideo().getVideoID());
                Output.printTrack(toProcess.getWho());
                Output.printTrack(toProcess.getWho(), "\r\n--------------------------------------------------------------------");
                Interactivity.nextVideo(toProcess.getWho());
                break;
        }
        return true;
    }
}
