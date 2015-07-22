package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.event.EventHandler;
import osnvodsim.simulator.State;
import osnvodsim.interactivity.User;
import osnvodsim.statistics.Output;
import osnvodsim.utilites.Utilities;
import osnvodsim.video.Chunk;
import osnvodsim.video.Repository;
import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Guowei on 2014/8/29.
 */
public class PullBasedPeer extends Peer {

    public static double CACHE_CHECK_CYCLE;



    private Cache videoCache;             //当前正在看的视频缓存
    private Neighbour neighbourInfo;
    private int session;



  //  private double dynamicSelectCycle;     //select的周期，动态地来决定，如果缓冲不足，就加快选择速度，如果缓存中足够，就减慢选择速度。


    public PullBasedPeer(User who, double dlBW, double ulBW) {
        super();
        whoIsThis = who;
        networkCard = new Router(this, dlBW, ulBW);

        neighbourInfo=new Neighbour();
        videoCache =new Cache(this);
        session = 0;

    }


    public static void init() {
        CACHE_CHECK_CYCLE = Configuration.getDouble("cache_check_cycle");
   //     CACHE_MANAGEMENT = Configuration.getString("cache_management_policy");
    }


    @Override
    public void messageReceived(Peer from, Message msg) {
        switch (msg.getType()) {
            case PullBasedPeerController.MESSAGE_CONNECT_REQ:             //收到连接请求消息
                //    Output.printP2PEvent("peer" + this.getPeerID() + " received the connection request from peer" + from.getPeerID());
                //      Output.printTrack(this);
                ackConnection(from);
                break;
            case PullBasedPeerController.MESSAGE_CONNECT_ACK:           //收到连接确认消息
                //     Output.printP2PEvent("peer" + this.getPeerID() + " received the the connection acknowledgement from peer" + from.getPeerID());
                //     Output.printTrack(this);
                confirmConnection(from);
                break;
            case PullBasedPeerController.MESSAGE_CHUNK_OFFER:        //收到offer消息
                receiveOffer(from, (OfferMessage) msg);
                //Select(from,(OfferMessage)msg);
                //  Output.printTrack(this,"peer" + this.getPeerID() + " received an offer from peer" + from.getPeerID());
                break;
            case PullBasedPeerController.MESSAGE_CHUNK_SELECT:
                sendSelectedChunks(from, (SelectMessage) msg);
                break;
        }
        //  super.messageReceived(from,msg);

    }

    @Override
    public void chunkReceived(Peer from, Chunk chunk) {

        if (chunk.getRelatedVideo() == whoIsThis.getPlayingVideo()) {


            Output.printTrack(this, "Receive the chunk" + chunk.getChunkID() + " from peer" + from.getPeerID() + " trans time:" + Utilities.displayDouble(State.getTime() - chunk.getInitTime()));
            if (chunkDownloaded(chunk)&&!(from instanceof Server))      //收到非重复的块才奖励
            {
                neighbourInfo.contribute(from,NeighbourManagement.CONTRIBUTION_PER_DOWNLOAD);
            }
        } else {
            Output.printTrack(this, "Denied the chunk" + chunk.getChunkID() + " from peer" + from.getPeerID() + ",for is not the video which is watching! trans time:" + Utilities.displayDouble(State.getTime() - chunk.getInitTime()));
        }
    }

    @Override
    public void connect(Peer to) {
        Message msgConReq = new Message(PullBasedPeerController.MESSAGE_CONNECT_REQ);
        sending(to, msgConReq);
        Output.printTrack(this, "Trying to connect to peer" + to.getPeerID());
    }

    @Override
    public void ackConnection(Peer from) {

        //根据自身情况来接受连接请求

        neighbourInfo.addNeighbour(from);

        Output.printTrack(this, "Received a connection request from peer" + from.getPeerID());
        Message msgConAck = new Message(PullBasedPeerController.MESSAGE_CONNECT_ACK);
        sending(from, msgConAck);

        offer(from, whoIsThis.getPlayingVideo());
        // super.ackConnection(from);

    }

    @Override
    public void confirmConnection(Peer from) {


        neighbourInfo.addNeighbour(from);
     //   addNeighbour(from);

        Output.printTrack(this, "Has connected to peer" + from.getPeerID());

        //发送空select消息，第三次握手
        SelectMessage msg = new SelectMessage(this.getUser().getPlayingVideo());
        sending(from, msg);

        offer(from, whoIsThis.getPlayingVideo());

    }

    //用来限定周期性任务活动范围，以免越界！
    public boolean permission(Peer from, int ctrInfo) {
        if (!alive) return false;

        if (ctrInfo != session)
            return false;

        return true;
    }

    @Override
    public boolean checkConnectivity(Peer from, Message msg) {
        if (!alive) return false;

        if (msg != null) {
            if (msg.getType() == PullBasedPeerController.MESSAGE_CONNECT_REQ || msg.getType() == PullBasedPeerController.MESSAGE_CONNECT_ACK)
                return true;
        }
        return neighbourInfo.isServerConnected(from) || neighbourInfo.isNeighbour(from);
    }


    //===============================cache=====================================================


    private boolean chunkDownloaded(Chunk chunk) {
        return videoCache.download(chunk);

    }



    //=========================cacheManagement=========================================================================



    private void prefetch() {
        Output.printTrack(this, "-----------------Prefetch-----------------");
        videoCache.prefetch(Prefetch.prefetch(this.getUser()));
        Output.printTrack(this, "-----------------Prefetch-----------------");


    }


    private void cacheManagement() {

        ArrayList<Video> videos;
        Output.printTrack(this, "-----------------Cache refresh-----------------");

        CacheManagement.manage(this,videoCache,neighbourInfo);
        Output.printTrack(this, "-----------------Cache refresh-----------------");


    }


    //==================================================================================================


    private void sendSelectedChunks(Peer from, SelectMessage sm) {

        //版本不一致，顺便发送新的offer

        if (videoCache.isBusy()) return;

        int latestVersion = videoCache.getLatestVersion(from.getUser().getPlayingVideo());

        if (latestVersion == -1)     //视频缓存不存在
            return;
        else if (latestVersion != sm.getVersion()) {
            offer(from, sm.getWhich());
      //      Output.printTrack(this, "Latest version:" + latestVersion + ", selected version:" + sm.getVersion() + ",requested for the Video" + from.getUser().getPlayingVideo().getVideoID() + ", so sending offer to peer" + from.getPeerID());
        }

        Iterator<Integer> iterator = sm.getSelectIterator();
        while (iterator.hasNext()) {
            videoCache.addRequest(from.getUser().getPlayingVideo());
            sending(from, Repository.getChunkByID(sm.getWhich(), iterator.next()));
            neighbourInfo.contribute(from,NeighbourManagement.CONTRIBUTION_PER_SEND);

        }


    }

    public void offer(Peer to, Video which) {
        OfferMessage offerMsg = new OfferMessage(which);

        Bitmap tmp = videoCache.getVideoCache(offerMsg.getVideo());
        if (tmp != null) {
            offerMsg.processOfferMsg(tmp);
            sending(to, offerMsg);
        }

        //   Output.printTrack(this,"peer"+this.getPeerID()+" send an offer to peer"+to.getPeerID());
        //    EventHandler.scheduleP2PEvent(State.getTime()+CoreFunction.CONTEXT_EXCHANGE_CYCLE,P2PEventController.EVENT_P2P_PERIODICAL_SELECT,new P2PEventContainer(this,to));

    }

    private void receiveOffer(Peer from, OfferMessage om) {
        // receiveOfferMsg(from, om);
        //更新context！

        Bitmap offer = om.getOffer();

       Output.printTrack(this, "Updating cache from peer" + from.getPeerID() + " size:" + offer.length());

        neighbourInfo.updateNeighbourMap(from, offer, videoCache.getPlaying());


    }


    //先检查紧急区，缺块直接服务器；否则P2P；若没资源，再服务器，若缓冲区充足，不选择。

    public void schedule(Scheduler toSchedule)
    {
       toSchedule.schedule(this,videoCache,neighbourInfo,session);
    }
    @Override
    public boolean trackerCheck(int chunkID)
    {
        return videoCache.getVideoCache().isDownloaded(chunkID);
    }

    @Override
    public void sending(Peer to, Message msg) {
        if (msg instanceof SelectMessage && !(to instanceof Server))
        {
            Iterator<Integer> selectedChunks=((SelectMessage) msg).getSelectIterator();
            while (selectedChunks.hasNext())
            {
                neighbourInfo.oneRequest(selectedChunks.next(),to);
            }
        }
        networkCard.sending(to, msg);
    }

   /* public void Select()   //定时发送select消息
    {

        PlayControl.schedule(this, videoCache, neighbourInfo, session);
     *//*   SelectMessage selectMessage = new SelectMessage(whoIsThis.getPlayingVideo());
        Peer toSend = null;
        int toDownload;




    //    EventHandler.scheduleP2PEvent(State.getTime() + dynamicSelectCycle, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(this, this, session));

        PlayControl.dynamicSelect(this,videoCache,session);
        if (neighbourInfo.serverConnected())    //连接服务器，
        {
            toDownload = videoCache.selectUrgent();
            if (toDownload != -1)    //选择紧急块
            {
                Output.printTrack(this, "urgent schedule from server!  chunk" + toDownload);

                toSend = neighbourInfo.getRandomServer();
                selectMessage.selectChunk(toDownload);
                sending(toSend, selectMessage);
                return;
            }
        }

        toDownload= videoCache.schedule();
        if (toDownload==-1)   //下载完毕
            return;


        if ((toSend=neighbourInfo.getRandomSeedSource(toDownload))!=null)
        {
            Output.printTrack(this, "------------------------ Select chunk" + toDownload + " from peer" + toSend.getPeerID());
            selectMessage.setVersion(neighbourInfo.getVersion(toSend));

        }
        else {
            if (!neighbourInfo.serverConnected())
                return;

            toDownload = videoCache.selectByServer();
            toSend = neighbourInfo.getRandomServer();
            if (toDownload==-1) {
                Output.printTrack(this, "peer" + getPeerID() + " schedule nothing!:");
                return;
            }
        }




        if (toSend!=null) {
            Output.printTrack(this,"Select chunk"+toDownload+" from peer"+toSend.getPeerID());
            videoCache.startDownload(toDownload);
            selectMessage.selectChunk(toDownload);
            sending(toSend, selectMessage);
        }*//*

    }*/


    //=========================================play control=============================================================
    public void play() {
        PlayControl.play(this, videoCache, session);
    }

    @Override
    public boolean cachedThisVideo(Video video)
    {
        return videoCache.cachedThisVideo(video);
    }
    @Override
    public int cachedVideoNum()
    {
        return videoCache.cachedVideoNum();
    }
    @Override
    public void playNew() {              //更换视频初始化




        neighbourInfo.resetNeighbour();
        session++;

        //重置邻居贡献数
        videoCache.finishThisVideo();
        cacheManagement();  //缓存管理（清理）

        //dynamicSelectCycle = videoCache.getVideo().timeInterval() ;
        EventHandler.scheduleP2PEvent(State.getTime() + 5, PullBasedPeerController.EVENT_P2P_PERIODICAL_ADJUST, new P2PEventContainer(this, this, session));
     //   System.out.println("new video"+whoIsThis.getPlayingVideo());
        videoCache.watchNewVideo(whoIsThis.getPlayingVideo());
       prefetch();
        play();
        PlayControl.initSchedulers(this,session);

    //    Select();
    }

    //--------------------------------------------------------------------------------------------------------------------------


    //==================================================================================================================================



    public void adjustNeighbours()
    {

       NeighbourManagement.adjust(this,neighbourInfo, videoCache, session);

    }


    //==================================================================================================================================


}
