package osnvodsim.distribution;

import osnvodsim.video.Chunk;
import osnvodsim.video.Video;

/**
 * Created by Guowei on 2014/9/10.
 */
public class P2PEventContainer {


    private Peer from, to;
    private Message msgToSend;
    private Chunk chunkToSend;
    private Scheduler toSchedule;

    private int ctlInfo;
    private double latency;



    public P2PEventContainer(Peer from, Peer to, Scheduler scheduler,int session){
        this.from = from;
        this.to = to;
        toSchedule=scheduler;
        ctlInfo=session;

    }

    public P2PEventContainer(Peer from, Peer to, Message msg, Chunk chunk) {
        this.from = from;
        this.to = to;
        msgToSend = msg;
        chunkToSend = chunk;
        latency = 0;

    }


    public P2PEventContainer(Peer from, Peer to, Message msg) {
        this.from = from;
        this.to = to;
        msgToSend = msg;
        if (!from.equals(to))
            latency = Network.ping(from, to);

    }


    public P2PEventContainer(Peer from, Peer to, Chunk chunk) {
        this.from = from;
        this.to = to;
        chunkToSend = chunk;
        if (!from.equals(to))
            latency = Network.ping(from, to);
    }

    //处理事务，无延迟！
    public P2PEventContainer(Peer from, Peer to) {
        this.from = from;
        this.to = to;
        latency = 0;
    }

    public P2PEventContainer(Peer from, Peer to, int ctlInfo) {
        this.from = from;
        this.to = to;
        this.ctlInfo = ctlInfo;
        latency = 0;
    }

 /*   public Network.Connection getConnection()
    {
       return connection;
    }*/


    public int getCtlInfo() {
        return ctlInfo;
    }

    public double getLatency() {
        return latency;
    }

    public Peer getFrom() {
        return from;
    }

    public Peer getTo() {
        return to;
    }

    public Message getMessage() {
        return msgToSend;
    }

    public Chunk getChunk() {
        return chunkToSend;
    }

    public Scheduler getScheduler()
    {
        return toSchedule;
    }

}
