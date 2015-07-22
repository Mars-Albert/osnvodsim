package osnvodsim.distribution;

import osnvodsim.interactivity.User;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.video.Chunk;
import osnvodsim.video.Video;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Guowei on 2014/8/1.
 */
public abstract class Peer {

    private static long count = 0;
    protected Router networkCard;
    protected User whoIsThis;
    protected final long PeerID = count++;


    protected boolean alive = true;

    protected Peer() {

    }



    public static void init() {

        PlayControl.init();
        NeighbourManagement.init();
        CacheManagement.init();
        Prefetch.init();
        count = 0;
    }

    public double getDownloadBW() {
        return networkCard.getDownloadBW();
    }

    ;

    public double getUploadBW() {
        return networkCard.getUploadBW();
    }

    ;

    public long getPeerID() {
        return PeerID;
    }

    public User getUser() {
        return whoIsThis;
    }


    public void sending(Peer to, Message msg) {
        networkCard.sending(to, msg);
    }

    public void sending(Peer to, Chunk chunk) {
        networkCard.sending(to, chunk);
        Output.printTrack(this, "Sending chunk" + chunk.getChunkID() + "(video" + chunk.getRelatedVideo().getVideoID() + ") to peer" + to.getPeerID());
    }

    public void receiving(Peer from, Chunk chunk, Message message) {
        if (chunk != null)
            networkCard.receiving(from, chunk);

        if (message != null)
            networkCard.receiving(from, message);
    }

    public abstract void messageReceived(Peer from, Message msg);

    public abstract void chunkReceived(Peer from, Chunk chunk);

    public  void offer(Peer to, Video which)
    {

    }
    public abstract void connect(Peer to);

    public abstract void ackConnection(Peer from);

    public abstract void confirmConnection(Peer from);


    public abstract boolean checkConnectivity(Peer from, Message msg);


    public double networkOverload() {

        //   Output.printTrack(this,"total idle: "+networkCard.totalIdleTime()+" init time: "+this.getUser().getInitTime()+" now: "+State.getTime());
        return 1 - networkCard.totalIdleTime() / (State.getTime() - this.getUser().getInitTime());
    }

    public void playNew() {
    }

    public boolean trackerCheck(int chunkID)
    {
        return false;
    }

    public boolean cachedThisVideo(Video video)
    {
        return false;
    }
    public int cachedVideoNum()
    {
        return 0;
    }

    public boolean isOnline() {
        return alive;
    }

    public void destroy() {

        //回收！！需要实现当节点退出时，其他节点与之通信时不做反应，然后增加动态删除邻居的功

        alive = false;
        //  super.finalize();
    }

}
