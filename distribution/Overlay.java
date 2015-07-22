package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.event.P2PEventController;
import osnvodsim.interactivity.User;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.utilites.Utilities;
import osnvodsim.video.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mars on 2014/7/22.
 */
public abstract class Overlay {
    protected Video video;
    protected List<Peer> audiences;         //set!!!

    protected int maxAudiences;
    public static int degree;

    public Overlay(Video video) {
        this.video = video;
        audiences = new ArrayList<Peer>();
        maxAudiences = 0;
    }


    public Video getVideo() {
        return video;
    }

    public void joinOverlay(User who) {
        if (who.getPeer() == null) {
            Peer peer = PeerFactory.genPeer(who);
            //     Output.printP2PEvent("user"+who.getUserID()+" as peer" + peer.getPeerID() + " joined in Overlay(Video" + video.getVideoID()+")" );

            who.setPeer(peer);
        } else {
            //     Output.printP2PEvent("Overlay for Video"+video.getVideoID()+",peer"+who.getPeer().getPeerID()+" added!");
        }

        Output.printOverlay(getVideo(), "User" + who.getUserID() + "(peer" + who.getPeer().getPeerID() + ") join the Overlay(video" + getVideo().getVideoID() + ")!");
        Output.printP2PEvent("peer" + who.getPeer().getPeerID() + "(user" + who.getUserID() + ") joined the Overlay(Video" + video.getVideoID() + ")  ,DownloadBW:" + Utilities.displayDouble(who.getPeer().getDownloadBW()) + " UploadBW:" + Utilities.displayDouble(who.getPeer().getUploadBW()));
        Output.printTrack(who);

        EventHandler.scheduleP2PEvent(State.getTime(), P2PEventController.EVENT_PLAY_NEW_VIDEO, new P2PEventContainer(who.getPeer(), who.getPeer()));
        makeConnection(who.getPeer());
        audiences.add(who.getPeer());

        if (audiences.size() > maxAudiences)
            maxAudiences = audiences.size();


    }

    abstract public void makeConnection(Peer peer);

    public List<Peer> tracker() {
        return audiences;
    }


    public void leaveOverlay(User who) {
        audiences.remove(who.getPeer());
        Output.printOverlay(getVideo(), "User" + who.getUserID() + "(peer" + who.getPeer().getPeerID() + ") leave the Overlay(video" + getVideo().getVideoID() + ")!");
        Output.printTrack(who, "Overlay for Video" + video.getVideoID() + ",peer" + who.getPeer().getPeerID() + " exited!");
        Output.printP2PEvent("peer" + who.getPeer().getPeerID() + " leave the Overlay(Video" + video.getVideoID() + ")");
        Output.printTrack(who);


    }


    public void printInfo() {
        Output.printOverlay(this, " max users at the same time:" + maxAudiences);
        Output.printSYSEvent("Overlay for Video" + video.getVideoID() + "   max users:" + maxAudiences);
        //   for(Peer p:audiences)
        //     Output.printSYSEvent("Peer"+p.getPeerID());
    }


}
