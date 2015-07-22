package osnvodsim.distribution;


import osnvodsim.event.Event;
import osnvodsim.event.P2PEvent;
import osnvodsim.event.P2PEventController;

/**
 * Created by Guowei on 2014/9/10.
 */
public class PullBasedPeerController extends P2PEventController {


    public static final int EVENT_P2P_PERIODICAL_SELECT = 3;
    public static final int EVENT_P2P_PERIODICAL_ADJUST = 4;
    public static final int EVENT_PLAY_CACHE_CHECK = 5;
    public static final int EVENT_PLAY_NEXT_CHUNK = 6;


    public static final int MESSAGE_CHUNK_OFFER = 3;
    public static final int MESSAGE_CHUNK_SELECT = 4;

    @Override
    public boolean process(Event event) {
        P2PEvent pending = (P2PEvent) event;
        Peer to, from;
        from = pending.getFrom();
        to = pending.getTo();
        switch (pending.getType()) {
            case EVENT_P2P_MESSAGE_RECEIVED:
                //      System.out.println("Time: "+State.getTime()+" peer"+to.getPeerID() + " received a message from peer "+from.getPeerID()+"  eventid:"+eventID);
                to.messageReceived(from, pending.getMsg());
                break;
            case EVENT_P2P_CHUNK_RECEIVED:
                to.chunkReceived(from, pending.getChunk());
                break;
            case EVENT_P2P_RECEIVING:
                if (to.checkConnectivity(from, pending.getMsg()))        //设计session来替换这种方式！
                    to.receiving(from, pending.getChunk(), pending.getMsg());
                break;
            case EVENT_PLAY_NEW_VIDEO:
                from.playNew();
                break;

            case EVENT_P2P_PERIODICAL_SELECT:
                if (((PullBasedPeer) to).permission(from, pending.getCtlInfo()))
                    ((PullBasedPeer) from).schedule(pending.getScheduler());
                break;
            case EVENT_P2P_PERIODICAL_ADJUST:
                if (((PullBasedPeer) to).permission(from, pending.getCtlInfo()))        //设计session来替换这种方式
                    ((PullBasedPeer) to).adjustNeighbours();
                break;
    /*        case EVENT_PLAY_CACHE_CHECK:
                if (((PullBasedPeer) to).permission(from, pending.getCtlInfo()))        //设计session来替换这种方式
                    ((PullBasedPeer) from).cacheCheck();
                break;*/
            case EVENT_PLAY_NEXT_CHUNK:
                if (((PullBasedPeer) to).permission(from, pending.getCtlInfo()))         //设计session来替换这种方式
                    ((PullBasedPeer) from).play();
                break;

        }
        return true;
    }
}
