package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.event.UserEventController;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;

/**
 * Created by Mars on 2015/3/18.
 */



public class PlayControl {


    public static  int BUFFER_DISTANCE;
    public static  int URGENT_DISTANCE;
    public static  int MAX_FREE_DISTANCE;
    public static double UNPLAYABLE_CHECK;

    public static void init()
    {
        BUFFER_DISTANCE=15;
        URGENT_DISTANCE=12;
        MAX_FREE_DISTANCE=20;
        UNPLAYABLE_CHECK=0.3;
    }


    public static double getPlayedTime(Cache status)
    {
        return status.getPlaying()*status.getVideo().timeInterval();
    }



    private  static  boolean urgentZoneExist(Cache status)
    {

        int playing=status.getPlaying();
        int index=playing;
        while (!status.getVideoCache().toTheEnd(index)&& index-playing<=URGENT_DISTANCE)
        {
            if (status.getVideoCache().checkChunk(index)== Bitmap.UNFINISHED ||status.getVideoCache().checkChunk(index)== Bitmap.PENDING)
                return true;

            index++;
        }


        return  false;

    }

    private  static  boolean bufferZoneExist(Cache status)
    {
        int playing=status.getPlaying();
        int index=playing+URGENT_DISTANCE;
        while (!status.getVideoCache().toTheEnd(index)&& index-playing<=URGENT_DISTANCE+BUFFER_DISTANCE)
        {
            if (status.getVideoCache().checkChunk(index)== Bitmap.UNFINISHED)
                return true;

            index++;
        }

        return  false;

    }





    public static int play(Peer peer,Cache status,int session)
    {

        if (getPlayedTime(status) > peer.getUser().getWatchTime()) {  //需要更换视频
            EventHandler.scheduleUserEvent(State.getTime(), UserEventController.EVENT_USER_NEXT_VIDEO, peer.getUser());

            return 0;
        }

        double nextTime;
        int distance=status.playableDistance();
        Output.printTrack(peer, "#Distance of urgent chunk:" + distance + "#");
        if (status.getPlayStatus()== Cache.BUFFERING)
        {
            if (distance>=URGENT_DISTANCE-6)            //缓冲变为播放的一刻
            {

                Output.printTrack(peer, "\tPlaying chunk" + status.getPlaying());

                status.playNext();
                status.setPlayStatus(Cache.PLAYING);
                nextTime=status.getVideo().timeInterval();


            }
            else
            {

                if (status.isPrefetched())
                {
                    int index=status.getPlaying();
                    while (!status.getVideoCache().toTheEnd(index) && index-status.getPlaying()<=URGENT_DISTANCE)
                    {
                        status.getVideoCache().downloaded(index++);
                    }
                    Output.printTrack(peer, "\tPrefetch success!");
                    nextTime=0.1;
                }

                Output.printTrack(peer, "\tBuffering");
                nextTime=status.getVideo().timeInterval()*UNPLAYABLE_CHECK;
                peer.getUser().buffering(nextTime);
                    //在缓冲中


            }

        }else
        {
            if (distance>0)
            {

                    status.setBusy(false);

                Output.printTrack(peer, "\tPlaying chunk" + status.getPlaying());
                status.playNext();
                status.setPlayStatus(Cache.PLAYING);
                nextTime=status.getVideo().timeInterval();



            }else
            {

                status.setBusy(true);

                Output.printTrack(peer, "Missing chunk" + status.getPlaying() + ", and paused!");
            //    Neighbour.pauseCriticize(status.getPlaying());
                nextTime=status.getVideo().timeInterval()*UNPLAYABLE_CHECK;
                status.setPlayStatus(Cache.INTERRUPTED);
                peer.getUser().interruption(nextTime);

               // return -1;
            }

        }


        EventHandler.scheduleP2PEvent(State.getTime() + nextTime, PullBasedPeerController.EVENT_PLAY_NEXT_CHUNK, new P2PEventContainer(peer, peer,session ));
        return 1;
    }
    /*private static void dynamicSelectCycle(Peer peer, Cache status, int session)
    {
        double interval;
        switch (status.getPlayStatus())
        {

            case Cache.BUFFERING:
                interval=status.getVideo().timeInterval()*0.3;
            case Cache.PLAYING:
                interval=status.getVideo().timeInterval()*0.8;
            case Cache.INTERRUPTED:
                interval=status.getVideo().timeInterval()*0.3;
            default:
                interval=status.getVideo().timeInterval();
        }

        EventHandler.scheduleP2PEvent(State.getTime() + interval, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, session));
    }*/





 /*   public static void scheduleUrgent(Peer peer, Cache status, Neighbour neighbour, int session)
    {

        int index=status.getPlaying();
        double ;
        while (!status.getVideoCache().toTheEnd(index) && index-status.getPlaying()<URGENT_DISTANCE)
        {


            switch (status.getPlayStatus())
            {
                case status.BUFFERING:


                        ;
                case status.PLAYING:

                    ;
                case status.INTERRUPTED:

                    ;
            }


            status.setUrgent();




            index++;

        }

        EventHandler.scheduleP2PEvent(State.getTime() + interval, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, session));


    }

*/


    public static void initSchedulers(Peer peer,int session)
    {
        EventHandler.scheduleP2PEvent(State.getTime() , PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer,new UrgentScheduler(), session));
        EventHandler.scheduleP2PEvent(State.getTime() , PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer,new BufferScheduler(), session));
        EventHandler.scheduleP2PEvent(State.getTime() , PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer,new FreeScheduler(), session));
    }




  /*  public static void schedule(Peer peer, Cache status, Neighbour neighbour, int session)
    {
        SelectMessage selectMessage = new SelectMessage(peer.getUser().getPlayingVideo());
        Peer toSend = null;
        int toDownload;




        //    EventHandler.scheduleP2PEvent(State.getTime() + dynamicSelectCycle, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(this, this, session));

        dynamicSelectCycle(peer, status, session);

        if (neighbour.serverConnected())    //连接服务器，
        {
            toDownload = status.selectUrgent();
            if (toDownload != -1)    //选择紧急块
            {
                Output.printTrack(peer, "urgent schedule from server!  chunk" + toDownload);

                toSend = neighbour.getRandomServer();
                selectMessage.selectChunk(toDownload);
                peer.sending(toSend, selectMessage);
                return;
            }
        }

        toDownload= status.select();
        if (toDownload==-1)   //下载完毕
            return;


        if ((toSend=neighbour.getRandomSeedSource(toDownload))!=null)
        {
            Output.printTrack(peer, "------------------------ Select chunk" + toDownload + " from peer" + toSend.getPeerID());
            selectMessage.setVersion(neighbour.getVersion(toSend));

        }
        else {
            if (!neighbour.serverConnected())
                return;

       //     toDownload = status.selectByServer();
            toSend = neighbour.getRandomServer();
            if (toDownload==-1) {
                Output.printTrack(peer, "peer" + peer.getPeerID() + " schedule nothing!:");
                return;
            }
        }




        if (toSend!=null) {
            Output.printTrack(peer,"Select chunk"+toDownload+" from peer"+toSend.getPeerID());
            status.startDownload(toDownload);
            selectMessage.selectChunk(toDownload);
            peer.sending(toSend, selectMessage);
        }


    }*/





}
