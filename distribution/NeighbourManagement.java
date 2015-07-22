package osnvodsim.distribution;

import osnvodsim.event.EventHandler;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Mars on 2015/3/18.
 */
public class NeighbourManagement {


    public static int CONTRIBUTION_PER_SEND;
    public static int CONTRIBUTION_PER_DOWNLOAD;

    public static void init()
    {
        CONTRIBUTION_PER_SEND=1;
        CONTRIBUTION_PER_DOWNLOAD=3;
    }

    /*public static class IntegerComparator implements Comparator<Integer>
    {
        @Override
        public int compare(Integer x, Integer y) {

            if (x>y)
                return -1;
            else if (x==y)
                return 0;
                else
            return 1;

        }

    }*/


    public static void pauseCriticize(Neighbour neighbour,int chunkID)
    {

        Iterator<Request> itr= neighbour.getRequestQueue();
        Request who;
        while (itr.hasNext())
        {
            who=itr.next();

            if (who.getChunkID()==chunkID)
            {
                neighbour.contribute(who.getRequest(), -2*NeighbourManagement.CONTRIBUTION_PER_DOWNLOAD);

            }
        }


    }


    public static void adjust(Peer which,Neighbour info,Cache cache,int session)
    {


        Set<Peer> deleted=new HashSet<Peer>();
        int[] contributionVector=new int[info.getNeighbours().size()];
   //     ArrayList<Integer> contribution = new ArrayList<Integer>();    //����������ֵ
        //  HashSet<Peer> deletedPeers=new HashSet<Peer>();       //������ֹ������һ��ɾ�����Ľڵ�

        Iterator<Map.Entry<Peer, Action>> iterator = info.getContributionOfNeighbours().entrySet().iterator();
        Peer peer;
        int value,i=0;
        Map.Entry<Peer, Action> mapTmp;
        Output.print(which, "Checking neighbours:*********************************\r\n");
        while (iterator.hasNext()) {
            mapTmp = iterator.next();
            peer = mapTmp.getKey();
            value = mapTmp.getValue().getContribution();
          //   mapTmp.getValue().check();

            //   Output.print(whose,"peer"+tmp.getPeerID()+": "+value+"\t");

            //     value*=0.8;
            //      contributionOfNeighbours.replace(peer, value);
   /*         if (value == 0) {   //ɾ��0���׵Ľڵ������ӣ����������ڵ㶼���˽�Ŀ�����������ݴ��䡣
                neighbours.remove(peer);
                deletedPeers.add(peer);
                Output.printTrack(whose,"peer"+peer.getPeerID()+"has been removed!"+"\t");
                iterator.remove();
            } else {
*/
            contributionVector[i++]=value;
            Output.printTrack(which, "peer" + peer.getPeerID() + ": " + value + "\t");
            //  }
        }
        Output.print(which, "\r\n");


     //ɾ������ϵ���ھӽڵ�
          //����ִ��һ��
            iterator = info.getContributionOfNeighbours().entrySet().iterator();
            while (iterator.hasNext()) {
                mapTmp = iterator.next();

                if (!mapTmp.getValue().hasContact()&& !mapTmp.getValue().isNewPeer()) {
                    Output.printTrack(which, "peer" + mapTmp.getKey().getPeerID() + " done nothing and been deleted!\t");
                //   System.out.println("peer" + mapTmp.getKey().getPeerID() + " done nothing and been deleted!\t");
                    info.removeNeighbour(mapTmp.getKey());
                    //    Output.printTrack(which, "peer" + peer.getPeerID() + ": " + value + " been deleted!\t");
                    deleted.add(mapTmp.getKey());
                    iterator.remove();

                    //     mapTmp.getKey().offer(which,playingVideo);

                }

            }



        //ɾ�������õĽڵ�
        iterator = info.getContributionOfNeighbours().entrySet().iterator();
        while (iterator.hasNext()) {
            mapTmp = iterator.next();
            peer = mapTmp.getKey();
            value = mapTmp.getValue().getContribution();
            if (value < 0 ) {
                info.removeNeighbour(peer);
                Output.printTrack(which, "peer" + peer.getPeerID() + ": " + value + " been deleted for slow response!\t");

                deleted.add(peer);
                iterator.remove();
            }
        }




        //ɾ������ֵ�͵ıߣ����е���
        if (info.getContributionOfNeighbours().size()>= Overlay.degree) {
            Arrays.sort(contributionVector);
            int bound = contributionVector[info.getNeighbours().size()-Overlay.degree];

/*
            for (Iterator<Integer> it=contribution.iterator();it.hasNext();)
                Output.printTrack(whose,it.next()+"\t");
*/
            iterator = info.getContributionOfNeighbours().entrySet().iterator();
            Output.printTrack(which, "need>" + bound);

            while (iterator.hasNext()) {
                mapTmp = iterator.next();
                peer = mapTmp.getKey();
                value = mapTmp.getValue().getContribution();
                if (value < bound &&!mapTmp.getValue().isNewPeer()) {
                    info.removeNeighbour(peer);
                    Output.printTrack(which, "peer" + peer.getPeerID() + ": " + value + " been deleted!\t");
                    deleted.add(peer);
                    iterator.remove();
                } else
                    Output.printTrack(which, "peer" + peer.getPeerID() + ": " + value);

            }
        }

        else {
            List<Peer> peers = P2PNetwork.trackerFunction(cache.getVideo());     //������Ҫ�Ľ�����Ҫ��ѡ�񽻸�overlay�����ֳ�overlay�Ĳ��졣

            shuffle(peers);   //����˳��

     //       int count=Overlay.degree - info.getContributionOfNeighbours().size();
            for (Peer tmp : peers) {
            //�����һ��Դ
                if (!info.hasNeighbour(tmp) && tmp != which && !deleted.contains(tmp)&& cache.isAltSource(tmp)) {

                    which.connect(tmp);
              //      if(count--<=0)
                        break;
                }
            }
        }

/*
        iterator = info.getContributionOfNeighbours().entrySet().iterator();
        while (iterator.hasNext())
        {
            mapTmp=iterator.next();

            if (!mapTmp.getValue().hasContact())
            {

                mapTmp.getKey().offer(which,playingVideo);

            }




        }*/


        Output.printTrack(which, "*****************************************\n");
        iterator = info.getContributionOfNeighbours().entrySet().iterator();
        while (iterator.hasNext()) {
            mapTmp = iterator.next();
            peer = mapTmp.getKey();
            value = mapTmp.getValue().getContribution();
            value *= 0.5;
            info.contribute(peer,Neighbour.INIT_CONTRIBUTION);
            mapTmp.getValue().check();
        }


        EventHandler.scheduleP2PEvent(State.getTime() + 10, PullBasedPeerController.EVENT_P2P_PERIODICAL_ADJUST, new P2PEventContainer(which, which, session));



    }

    public static void exch(List<Peer> list, int i, int j)
    {

        Peer swap = list.get(i);
        list.set(i,list.get(j));
        list.set(j,swap);
    }

    public static void shuffle(List<Peer> list)
    {
        int N = list.size();
        for (int i = 0; i < N; i++)
        {
            int r = State.ru.nextInt(N); // between i and N-1
            exch(list, i, r);
        }
    }

}
