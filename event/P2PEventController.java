package osnvodsim.event;

/**
 * Created by Guowei on 2014/10/28.
 */
public abstract class P2PEventController implements EventController {
    public static final int EVENT_P2P_MESSAGE_RECEIVED = 0;
    public static final int EVENT_P2P_CHUNK_RECEIVED = 1;
    public static final int EVENT_P2P_RECEIVING = 2;
    public static final int EVENT_PLAY_NEW_VIDEO = 7;

    public static final int MESSAGE_CONNECT_REQ = 0;
    public static final int MESSAGE_CONNECT_ACK = 1;


}
