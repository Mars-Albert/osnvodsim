package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Statistic;
import osnvodsim.video.Chunk;

/**
 * Created by Guowei on 2014/9/18.
 */
public class Router {

    private double downloadBW;

    public double getUploadBW() {
        return uploadBW;
    }

    public double getDownloadBW() {
        return downloadBW;
    }

    private double uploadBW;

    private Peer whose;
    private double whenBeFree;


    private double idleTime;

    public Router() {

    }

    public Router(Peer peer, double down, double up) {
        whose = peer;
        downloadBW = down;
        uploadBW = up;
        idleTime = 0;

    }


    public void sending(Peer to, Message message) {
        if (whenBeFree < State.getTime()) {
            idleTime += (State.getTime() - whenBeFree);
            whenBeFree = State.getTime() + Message.MESSAGE_SIZE / uploadBW;
        } else {
            whenBeFree += Message.MESSAGE_SIZE / uploadBW;
        }
        //   Output.printTrack(whose,"peer"+whose.getPeerID()+" has sent a message to peer"+to.getPeerID());
        EventHandler.scheduleP2PEvent(whenBeFree, PullBasedPeerController.EVENT_P2P_RECEIVING, new P2PEventContainer(whose, to, message));

    }

    public void sending(Peer to, Chunk chunk) {

        Chunk tmp = chunk.clone();

        if (whenBeFree < State.getTime()) {
            idleTime += (State.getTime() - whenBeFree);
            whenBeFree = State.getTime() + Chunk.CHUNK_SIZE / uploadBW;
        } else {
            whenBeFree += Chunk.CHUNK_SIZE / uploadBW;
        }
        //   Output.printTrack(whose,"peer"+whose.getPeerID()+" has sent the chunk"+chunk.getChunkID()+" to peer"+to.getPeerID());
        EventHandler.scheduleP2PEvent(whenBeFree, PullBasedPeerController.EVENT_P2P_RECEIVING, new P2PEventContainer(whose, to, tmp));
    }


    public void receiving(Peer from, Message message) {

        if (whenBeFree < State.getTime()) {
            idleTime += (State.getTime() - whenBeFree);
            whenBeFree = State.getTime() + Message.MESSAGE_SIZE / downloadBW;
        } else {
            whenBeFree += Message.MESSAGE_SIZE / downloadBW;
        }
        Statistic.recordMessageSent();
        EventHandler.scheduleP2PEvent(whenBeFree, PullBasedPeerController.EVENT_P2P_MESSAGE_RECEIVED, new P2PEventContainer(from, whose, message, null));

    }

    public void receiving(Peer from, Chunk chunk) {
        if (whenBeFree < State.getTime()) {
            idleTime += (State.getTime() - whenBeFree);
            whenBeFree = State.getTime() + Chunk.CHUNK_SIZE / downloadBW;
        } else {
            whenBeFree += Chunk.CHUNK_SIZE / downloadBW;
        }


        Statistic.recordTransmission(whenBeFree - chunk.getInitTime(), from);
        EventHandler.scheduleP2PEvent(whenBeFree, PullBasedPeerController.EVENT_P2P_CHUNK_RECEIVED, new P2PEventContainer(from, whose, null, chunk));
    }

    public double totalIdleTime() {
        return idleTime - ((whose instanceof Server) ? 0 : whose.getUser().getInitTime());
    }

    public double getAvgTranzDelay() {
        return 5;
    }


}
