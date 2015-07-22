package osnvodsim.event;

import osnvodsim.simulator.State;
import osnvodsim.distribution.*;
import osnvodsim.interactivity.Interactivity;
import osnvodsim.interactivity.User;

public class EventHandler {

    static private EventQueue eventQueue = EventQueue.getEventQueue();

    static private EventController SYSController, USERController, P2PController;

    private EventHandler() {

    }

    public static void setController(EventController ec) {
        if (ec instanceof SystemEventController)
            SYSController = ec;
        else if (ec instanceof UserEventController)
            USERController = ec;
        else if (ec instanceof PullBasedPeerController) {
            // System.out.println("true");
            P2PController = ec;
        }
    }

    public static void runSimulation() {

        setController(new SystemEventController());

        eventQueue.clear();
        Event.resetCounter();
        Interactivity.nextUser();          //第一个事件

        scheduleSystemEvent(State.getEnd(), SystemEventController.SIMULATION_END);    //最后一个事件
        scheduleSystemEvent(State.getTime(), SystemEventController.DISPLAY_RUNNING_STATE);
        Event ev = eventQueue.getEvent();
        while (ev != null) {
            if (!process(ev)) break;
            if ((ev = eventQueue.getEvent()) != null)
                State.setTime(ev.getTime());

        }

    }

    private static boolean process(Event event) {
        if (event instanceof SystemEvent)
            return SYSController.process(event);
        else if (event instanceof UserEvent)
            return USERController.process(event);
        else if (event instanceof P2PEvent)
            return P2PController.process(event);

        return false;
    }


	/*public static boolean scheduleEvent(double time,int eventGroup,int eventType,User who)
    {

		switch(eventGroup)
		{
			case Event.USER_EVENT: eventQueue.addEvent(new UserEvent(time,eventType,who)); break;
			case Event.P2P_EVENT: eventQueue.addEvent(new P2PEvent(time,eventType)); break;
            case Event.SYSTEM_EVENT: eventQueue.addEvent(new SystemEvent(time,eventType)); break;
		}	
		
		return false;
		
	}*/

    public static boolean scheduleUserEvent(double time, int eventType, User who) {
        eventQueue.addEvent(new UserEvent(time, eventType, who));
        return false;
    }

    public static boolean scheduleP2PEvent(double time, int eventType, P2PEventContainer pec) {

        eventQueue.addEvent(new P2PEvent(time, eventType, pec));
        return false;
    }

    public static boolean scheduleSystemEvent(double time, int eventType) {
        eventQueue.addEvent(new SystemEvent(time, eventType));
        return false;
    }

}
