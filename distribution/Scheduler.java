package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;

/**
 * Created by Mars on 2015/3/20.
 */
public interface Scheduler {
    public static double FAST =0.3;
    public static double SLOW =0.7;

    public void schedule(Peer peer,Cache cache,Neighbour neighbour,int session);



}

class UrgentScheduler implements Scheduler {







    @Override
    public void schedule(Peer peer,Cache cache,Neighbour neighbour,int session) {
        SelectMessage selectMessage = new SelectMessage(peer.getUser().getPlayingVideo());
        Peer toSend ;
        int toDownload=-1,index=cache.getPlaying();

        double interval=cache.getVideo().timeInterval();

        if (neighbour.serverConnected())    //连接服务器，
        {

            while (!cache.getVideoCache().toTheEnd(index)&&index-cache.getPlaying()<PlayControl.URGENT_DISTANCE)      //具体数字可改进，计算服务器平均发送一个块的延迟
            {

                if (cache.getVideoCache().needAsUrgent(index))
                {

                    NeighbourManagement.pauseCriticize(neighbour,index);
                    toDownload=index;
                    cache.startDownloadByServer(index);
                    break;
                }
                index++;
            }


            if (toDownload != -1)    //选择紧急块
            {
                Output.printTrack(peer, "[++++++++ Urgent schedule activated ++++++++]   chunk" + toDownload);

                toSend = neighbour.getRandomServer();
                selectMessage.selectChunk(toDownload);
                peer.sending(toSend, selectMessage);

            }
        }
        else{
            //从普通节点选紧急块，防止加载时连服务器慢





        }

        if (cache.getPlayStatus()==Cache.BUFFERING)
        {
            interval*=FAST;
        }
        else
        {
            //interval;
        }


        EventHandler.scheduleP2PEvent(State.getTime() + interval, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));



    }

   /* @Override
    public void schedule(Peer peer,Cache cache,Neighbour neighbour,int session) {
        SelectMessage selectMessage = new SelectMessage(peer.getUser().getPlayingVideo());
        Peer toSend =null;
        int toDownload=-1,index=cache.getPlaying();

        double interval=cache.getVideo().timeInterval();



            while (!cache.getVideoCache().toTheEnd(index)&&index-cache.getPlaying()<PlayControl.URGENT_DISTANCE)      //具体数字可改进，计算服务器平均发送一个块的延迟
            {
              //  status=cache.getVideoCache().checkChunk(index);



                //紧急块在未连接服务器时依然可以P2P获得。
                    if (neighbour.serverConnected())
                    {
                        if (cache.getVideoCache().needAsUrgent(index)) {

                            cache.startDownloadByServer(index);
                            toDownload=index;
                            toSend = neighbour.getRandomServer();
                            break;
                        }

                    }
                    else
                    {
                        if (cache.getVideoCache().need(index))   //避免重复选
                        {

                       //     System.out.println("1111");
                            if ((toSend = neighbour.getRandomSeedSource(toDownload)) != null) {

                                System.out.println("222");
                                cache.startDownload(index);
                                toDownload = index;
                                break;

                            }

                        }
                    }

                index++;
            }


            if (toSend!=null)    //选择紧急块
            {
                Output.printTrack(peer, "-----------------Urgent schedule---------- from peer"+toSend.getPeerID()+"  chunk" + toDownload);


                selectMessage.selectChunk(toDownload);
                peer.sending(toSend, selectMessage);

            }



        if (cache.getPlayStatus()==Cache.BUFFERING)
        {
            interval*=FAST;
        }
        else
        {
            interval*=SLOW;
        }


        EventHandler.scheduleP2PEvent(State.getTime() + interval, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));



    }*/

}
class BufferScheduler implements Scheduler {




    @Override
    public void schedule(Peer peer,Cache cache,Neighbour neighbour,int session) {

        SelectMessage selectMessage = new SelectMessage(peer.getUser().getPlayingVideo());
        Peer toSend ;
        int toDownload=-1;

        double interval=cache.getVideo().timeInterval();


      //  Output.printTrack(peer, "Buffer scheduler!!!!!!" );
        int index=cache.getPlaying()+PlayControl.URGENT_DISTANCE;

        while (!cache.getVideoCache().toTheEnd(index)) {

            if (cache.getVideoCache().need(index)) {
                toDownload=index;

                break;
            }
            index++;
        }
    //        toDownload= cache.select();
            if (toDownload==-1)   //无需下载
                return;

            if (toDownload-cache.playing<=PlayControl.URGENT_DISTANCE+PlayControl.BUFFER_DISTANCE)    //在这个区间内
            {

          //      System.out.println("!!");
                if ((toSend = neighbour.getRandomSeedSource(toDownload)) != null) {
             //       Output.printTrack(peer, "------------Buffer scheduler------------ Select chunk" + toDownload + " from peer" + toSend.getPeerID());
                    selectMessage.setVersion(neighbour.getVersion(toSend));

                } else {
                    if (!neighbour.serverConnected()) {
                        EventHandler.scheduleP2PEvent(State.getTime() + cache.getVideo().timeInterval()*0.65, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));
                        return;
                    }
                 //   toDownload = cache.select();
                    toSend = neighbour.getRandomServer();

                }
                if (toSend != null) {
                    Output.printTrack(peer, "[-------- Buffer scheduler activated --------] Select chunk" + toDownload + " from peer" + toSend.getPeerID());
                    if (toSend instanceof Server)
                        cache.startDownloadByServer(toDownload);
                    cache.startDownload(toDownload);
                    selectMessage.selectChunk(toDownload);
                    peer.sending(toSend, selectMessage);
                }
            }


        if (cache.getPlayStatus()==Cache.BUFFERING)                     //为加快加载，应先照顾urgent scheduler!
        {
            interval*=SLOW;
        }
        else {
            if (cache.playableDistance() <= PlayControl.URGENT_DISTANCE) {             //使得 缓冲区快速推进，免得频繁进入缓冲区
                interval *= FAST;
            } else if (cache.playableDistance() < (PlayControl.URGENT_DISTANCE + PlayControl.BUFFER_DISTANCE)) {             //线性关系
                interval = interval * ( (SLOW-FAST)*(cache.playableDistance() - PlayControl.URGENT_DISTANCE)/PlayControl.BUFFER_DISTANCE + FAST);
            } else {
                interval *= SLOW;
            }

        }
        EventHandler.scheduleP2PEvent(State.getTime() + interval, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));




    }



}
class FreeScheduler implements Scheduler {




    @Override
    public void schedule(Peer peer,Cache cache,Neighbour neighbour,int session) {



        SelectMessage selectMessage = new SelectMessage(peer.getUser().getPlayingVideo());
        Peer toSend ;
        int toDownload=-1;

        int index=cache.getPlaying();

        while (!cache.getVideoCache().toTheEnd(index)&&index< cache.getPlaying()+PlayControl.URGENT_DISTANCE+PlayControl.URGENT_DISTANCE-1)
        {
            if (!cache.getVideoCache().isDownloaded(index))
            {
                //前两个区域仍有缺块，跳出
                EventHandler.scheduleP2PEvent(State.getTime() + cache.getVideo().timeInterval()*FAST, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));
                return;

            }
            index++;
        }
        index=cache.getPlaying()+PlayControl.URGENT_DISTANCE+PlayControl.URGENT_DISTANCE;
        while (!cache.getVideoCache().toTheEnd(index)) {

            if (cache.getVideoCache().need(index)) {
                toDownload=index;

                break;
            }
            index++;
        }
        //        toDownload= cache.select();
        if (toDownload==-1)   //无需下载
            return;


            if ((toSend = neighbour.getRandomSeedSource(toDownload)) != null) {
      //          Output.printTrack(peer, "--------Free Scheduler------------ Select chunk" + toDownload + " from peer" + toSend.getPeerID());
                selectMessage.setVersion(neighbour.getVersion(toSend));

            }


            if (toSend != null) {
                Output.printTrack(peer, "[~~~~~~~~ Free Scheduler activated ~~~~~~~~] Select chunk" + toDownload + " from peer" + toSend.getPeerID());
                cache.startDownload(toDownload);
                selectMessage.selectChunk(toDownload);
                peer.sending(toSend, selectMessage);
            }

        EventHandler.scheduleP2PEvent(State.getTime() + cache.getVideo().timeInterval()*FAST, PullBasedPeerController.EVENT_P2P_PERIODICAL_SELECT, new P2PEventContainer(peer, peer, this,session));







    }

}

class PrefetchScheduler implements Scheduler{


    @Override
    public void schedule(Peer peer, Cache cache, Neighbour neighbour, int session) {








    }
}