package osnvodsim.event;

/**
 * Created by Mars on 2014/7/24.
 */
public class SystemEvent extends Event {

    //    public static final int SIMULATION_END=0;
    public SystemEvent(double time, int type) {
        super(time, type);
    }
/*    @Override
    public boolean process() {
        return false;
    }*/
}
