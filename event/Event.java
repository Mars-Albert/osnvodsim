package osnvodsim.event;

abstract public class Event implements Comparable<Event> {

	
	
	/*public static final int USER_EVENT=0;
    public static final int P2P_EVENT=1;
    public static final int SYSTEM_EVENT=2;*/


    private static long eventCounter;
    protected final long eventID = eventCounter++;
    protected double time;

    public int getType() {
        return type;
    }

    protected int type;


    public Event(double time) {
        this.time = time;
    }

    public Event(double time, int type) {
        this.time = time;
        this.type = type;
    }


    public final double getTime() {
        return time;
    }

    public long getEventID() {
        return eventID;
    }


    @Override
    public int compareTo(Event arg) {
        // TODO Auto-generated method stub
        if (time > arg.getTime())
            return +1;
        if (time == arg.getTime())
            if (eventID > arg.getEventID())
                return +1;
            else if (eventID == arg.getEventID())
                return 0;
        return -1;
    }

    // abstract public boolean process();


    public static void resetCounter() {
        eventCounter = 0;
    }

}
