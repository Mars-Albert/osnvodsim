package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.interactivity.User;
import osnvodsim.statistics.Output;
import osnvodsim.utilites.Utilities;
import osnvodsim.video.Repository;
import osnvodsim.video.Video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Mars on 2014/7/24.
 */
public class P2PNetwork {
    private static Collection<Overlay> p2pOverlay;
    public static ArrayList<Server> Servers;

    public static void init() {
        EventHandler.setController(new PullBasedPeerController());
        Network.init();
        OverlayFactory.init();
        PeerFactory.init();
        Network.init();
        Message.init();

        Iterator<Video> itv = Repository.getVideoIterator();
        p2pOverlay = new ArrayList<Overlay>();
        Servers = new ArrayList<Server>();
        while (itv.hasNext()) {
            p2pOverlay.add(OverlayFactory.genOverlay(itv.next()));
        }


        Peer tmp = PeerFactory.genPeer(null);
        while (tmp != null) {
            Output.printP2PEvent("Server" + tmp.getPeerID() + " created! Bandwidth:" + Utilities.displayDouble(tmp.getUploadBW()));
            Servers.add((Server) tmp);
            tmp = PeerFactory.genPeer(null);
        }

    }

    public static void getService(User who) {
        Overlay ol = getOverlay(who.getPlayingVideo());
        //   System.out.println("Overlay:"+ol+" video:"+ol.getVideo());
        ol.joinOverlay(who);


    }

    private static Overlay getOverlay(Video video) {
        for (Overlay o : p2pOverlay)
            if (o.getVideo() == video)
                return o;
        return null;
    }

    public static List<Peer> trackerFunction(Video overlay) {
        return getOverlay(overlay).tracker();
    }


    public static void dropService(User who) {

        Iterator<Video> itvr = who.getPlayedIterator();
        while (itvr.hasNext()) {
            getOverlay(itvr.next()).leaveOverlay(who);

        }
        who.leave();

        who.getPeer().destroy();

        Output.printTrack(who, "User" + who.getUserID() + " stayed in system for:" + Utilities.displayDouble(who.getLeaveTime() - who.getInitTime()) + " network overload:" + Utilities.displayPercentage(who.getPeer().networkOverload()) + "% be interrupted:" + who.getInterruptedTime() + "s");
        Output.stopTracing(who);

    }
/*    public static void printP2PNetwork()
    {
        for(Overlay o:p2pOverlay)
            o.printInfo();
    }*/

    public static boolean isServer(Peer peer) {
        return Servers.contains(peer);
    }

    public static void leaveOverlay(User who, Video video) {
        Overlay toLeave = getOverlay(video);
        if (toLeave != null)
            toLeave.leaveOverlay(who);
    }


    public static void printOverlay() {
        //输出服务器繁忙率
        Iterator<Server> its = Servers.iterator();
        Server tmp;
        while (its.hasNext()) {
            tmp = its.next();
            Output.printSTAT("server" + tmp.getServerID() + ": percentage of busy time: " + Utilities.displayPercentage(tmp.getUtilizationRate()) + "%");

        }

        //输出每个Overlay信息。
        Iterator<Overlay> ito = p2pOverlay.iterator();

        while (ito.hasNext()) {
            ito.next().printInfo();
        }


    }


}
