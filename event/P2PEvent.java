package osnvodsim.event;

import osnvodsim.distribution.*;
import osnvodsim.video.Chunk;
import osnvodsim.video.Video;

public class P2PEvent extends Event {
/*	public static final int EVENT_P2P_MESSAGE_RECEIVED=0;
    public static final int EVENT_P2P_CONTEXT_SENT=1;
    public static final int EVENT_P2P_CHUNK_RECEIVED=2;*/


    private Peer from, to;
    private Message msg;
    private Chunk chunk;
    private Scheduler scheduler;
    private int ctl;

    public P2PEvent(double time, int type, P2PEventContainer pec) {
        // TODO Auto-generated constructor stub
        super(time, type);
        super.time += pec.getLatency();       //º”…œÕ¯¬Á—”≥Ÿ
        from = pec.getFrom();
        to = pec.getTo();
        msg = pec.getMessage();
        chunk = pec.getChunk();
        ctl = pec.getCtlInfo();
        scheduler=pec.getScheduler();
        //    System.out.println("Peer"+from.getPeerID()+"to Peer"+to.getPeerID()+" lat:"+con.getLatency());

    }

    public Message getMsg() {
        return msg;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public Peer getFrom() {
        return from;
    }

    public Peer getTo() {
        return to;
    }

    public int getCtlInfo() {
        return ctl;
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }
/*
    @Override
	public boolean process()
	{
//		System.out.println("Time: "+State.getTime()+" p2pEvnet processed! id:"+eventID);

        switch (type)
    {
        case EVENT_P2P_MESSAGE_RECEIVED:
            //      System.out.println("Time: "+State.getTime()+" peer"+to.getPeerID() + " received a message from peer "+from.getPeerID()+"  eventid:"+eventID);
            peer.getPendingMessage().process();
            break;

    }
        return true;
		
	}*/


}
