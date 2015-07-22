package osnvodsim.event;

import java.util.PriorityQueue;

@SuppressWarnings("serial")
public class EventQueue extends PriorityQueue<Event> {

    static private boolean exist = false;

    private EventQueue() {
    }

    public static EventQueue getEventQueue() {
        if (exist == false) {
            exist = true;
            return new EventQueue();
        }

        return null;
    }

    public final boolean addEvent(Event ev) {
        return super.add(ev);
    }

    public final Event getEvent() {
        return super.poll();
    }

}
