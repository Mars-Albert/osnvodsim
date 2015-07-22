package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;
import osnvodsim.interactivity.User;

/**
 * Created by Guowei on 2014/8/28.
 */
public class PeerFactory {

    public static final String PULL_BASED_PEER = "pull_based";
    public static final String CLIENT_SERVER_PEER = "c/s";

    //   private static double bandwidth_variation;
    private static double probabilityVector[];
    private static double DlBWs[], ULBWs[];
    private static int serAllocated = 0;
    private static double serBandwidths[];
    private static String peerType;

    public static void init() {
        probabilityVector = Configuration.getDoubles("bw_fraction");
        //   bandwidth_variation = Configuration.getDouble("bandwidth_variation");

        DlBWs = Configuration.getDoubles("download_bandwidth");
        ULBWs = Configuration.getDoubles("upload_bandwidth");

        serBandwidths = Configuration.getDoubles("server_bandwidth");
        serAllocated = 0;

        peerType = Configuration.getString("peer_type");
        Peer.init();
        if (peerType.equals(PULL_BASED_PEER)) {
            PullBasedPeer.init();
        }

/*        double check=0;
        for(double d:probalityVector)
            check+=d;
        if(check<0.95||check>1.05) ;*/

    }

    public static Peer genPeer(User who) {
        if (who != null) {
            int type = State.ru.selectByProbabilityVector(probabilityVector);
            if (peerType.equals(PULL_BASED_PEER))
                return new PullBasedPeer(who, State.ru.randomDouble(DlBWs[type]), State.ru.randomDouble(DlBWs[type]));


        } else {
            if (serAllocated < serBandwidths.length)
                return new Server(serBandwidths[serAllocated++], serAllocated - 1);
        }
        return null;
    }


}
