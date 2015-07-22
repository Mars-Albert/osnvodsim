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

        if (neighbour.serverConnected())    //���ӷ�������
        {

            while (!cache.getVideoCache().toTheEnd(index)&&index-cache.getPlaying()<PlayControl.URGENT_DISTANCE)      //�������ֿɸĽ������������ƽ������һ������ӳ�
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


            if (toDownload != -1)    //ѡ�������
            {
                Output.printTrack(peer, "[++++++++ Urgent schedule activated ++++++++]   chunk" + toDownload);

                toSend = neighbour.getRandomServer();
                selectMessage.selectChunk(toDownload);
                peer.sending(toSend, selectMessage);

            }
        }
        else{
            //����ͨ�ڵ�ѡ�����飬��ֹ����ʱ����������





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



            while (!cache.getVideoCache().toTheEnd(index)&&index-cache.getPlaying()<PlayControl.URGENT_DISTANCE)      //�������ֿɸĽ������������ƽ������һ������ӳ�
            {
              //  status=cache.getVideoCache().checkChunk(index);



                //��������δ���ӷ�����ʱ��Ȼ����P2P��á�
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
                        if (cache.getVideoCache().need(index))   //�����ظ�ѡ
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


            if (toSend!=null)    //ѡ�������
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
            if (toDownload==-1)   //��������
                return;

            if (toDownload-cache.playing<=PlayControl.URGENT_DISTANCE+PlayControl.BUFFER_DISTANCE)    //�����������
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


        if (cache.getPlayStatus()==Cache.BUFFERING)                     //Ϊ�ӿ���أ�Ӧ���չ�urgent scheduler!
        {
            interval*=SLOW;
        }
        else {
            if (cache.playableDistance() <= PlayControl.URGENT_DISTANCE) {             //ʹ�� �����������ƽ������Ƶ�����뻺����
                interval *= FAST;
            } else if (cache.playableDistance() < (PlayControl.URGENT_DISTANCE + PlayControl.BUFFER_DISTANCE)) {             //���Թ�ϵ
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
                //ǰ������������ȱ�飬����
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
        if (toDownload==-1)   //��������
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