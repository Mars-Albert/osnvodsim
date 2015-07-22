package osnvodsim.distribution;

import osnvodsim.simulator.State;

import java.util.*;

/**
 * Created by Mars on 2015/3/18.
 */


class Request {
    private int chunkID;

    public Request(int chunkID, Peer request)
    {
      this.chunkID=chunkID;
        this.request=request;

    }
    public Peer getRequest() {
        return request;
    }

    public int getChunkID() {
        return chunkID;
    }

    private Peer request;


}


class Action
{


    private int contribution;
    private boolean contact;
    private boolean newPeer;

    public Action(int contribution)
    {
        this.contribution=contribution;
        contact=false;
        newPeer=true;    //防止删除刚加入的节点
    }

    public void contribute(int contribution)
    {
        this.contribution+=contribution;
        contact=true;
    }
    public int getContribution() {
        return contribution;
    }
    public boolean hasContact()
    {
        return contact;
    }

    public void check()
    {
        newPeer=false;
        contact=false;
    }
    public boolean isNewPeer()
    {
        return newPeer;
    }
}

public class Neighbour {

    public static int INIT_CONTRIBUTION;

    private Map<Peer, Action> contributionOfNeighbours;  //邻居的贡献值
    private Map<Peer, Integer> neighbours;    //邻居 ，最近联系的cacheMap版本号(这个设计减少了offer的发送次数，对于未变化的缓存，无需定时更新)
    private Map<Integer, HashSet<Peer>> neighbourMap;  //ChunkID ,能提供该Chunk的邻居节点列表
    private ArrayList<Server> connectedServers;
    private Set<Request> requestQueue;

    private int round=0;

    public int getRound()    //两轮
    {
        if (round==0)
        {
            round++;
            return 0;
        }else
        {
            round=0;
            return 1;
        }
    }

    public static void init()
    {
        INIT_CONTRIBUTION=0;
    }

    public Iterator<Peer> getNeighboursIterator()
    {
        return neighbours.keySet().iterator();
    }
    public void removeNeighbour(Peer which)
    {
        neighbours.remove(which);
      //  contributionOfNeighbours.remove(which);
    }

    public Iterator<Request> getRequestQueue() {
        return requestQueue.iterator();
    }

    public void contribute(Peer neighbour, int value) {
        if (contributionOfNeighbours.containsKey(neighbour)) {
            int ov = contributionOfNeighbours.get(neighbour).getContribution();
            contributionOfNeighbours.get(neighbour).contribute(value);
        }
    }


    public void oneRequest(int chunkID, Peer peer)
    {
        requestQueue.add(new Request(chunkID,peer));
    }





    public void resetNeighbour()
    {

        Iterator<Peer> iterator = contributionOfNeighbours.keySet().iterator();
        Peer tmp;
        while (iterator.hasNext()) {
            tmp=iterator.next();
            contributionOfNeighbours.replace(tmp,new Action(INIT_CONTRIBUTION));
        }

        requestQueue=new HashSet<Request>();

        neighbourMap = new HashMap<Integer, HashSet<Peer>>();
        connectedServers = new ArrayList<Server>();

        round=0;
    }



    public Peer getRandomSeedSource(int chunkID) {


        Peer toSend=null;
        if (!neighbourMap.containsKey(chunkID))
            return null;
        int random;
        Iterator<Peer> iterator;


        //删除被踢出的邻居
        iterator = neighbourMap.get(chunkID).iterator();
        while (iterator.hasNext()) {
            if (!contributionOfNeighbours.containsKey(iterator.next()))
                iterator.remove();
        }

        if (neighbourMap.get(chunkID).size() != 0)     //邻居有该数据块
        {
            for (iterator = neighbourMap.get(chunkID).iterator(), random = State.ru.nextInt(neighbourMap.get(chunkID).size()); iterator.hasNext() && random >= 0; random--) {
                //      System.out.println("size:" + tmp.size() + " random:" + random);
                toSend = iterator.next();
              //  break;
            }
        }
        return toSend;
    }


    public boolean hasNeighbour(Peer which)
    {
        return neighbours.containsKey(which);
    }
    public int getVersion(Peer which)
    {
        return neighbours.get(which);
    }

    public ArrayList<Server> getConnectedServers()
    {
        return connectedServers;
    }

    public boolean isServerConnected(Peer server)
    {
        if (server instanceof Server)
        {
            return connectedServers.contains(server);
        }
        return false;
    }

    public boolean serverConnected()
    {
        return connectedServers.size()!=0;
    }

    public Server getRandomServer()
    {
        return connectedServers.get(connectedServers.size() == 1 ? 0 : State.ru.nextInt(connectedServers.size() - 1));
    }

    public void newVersion(Peer which,int version)
    {
        neighbours.replace(which,version);
    }

    public void updateNeighbourMap(Peer which,Bitmap map,int startPos)
    {
        HashSet<Peer> tmp;


        neighbours.replace(which, map.getVersion());

        //更新邻居节点缓存信息
        for (int i = startPos; i < map.length(); i++) {

            if (map.isDownloaded(i)) {
                //   Output.print(whose,"chunk"+i+" updated!\t");

                if (neighbourMap.containsKey(i)) {
                    tmp = neighbourMap.get(i);
                    tmp.add(which);
                } else {
                    tmp = new HashSet<Peer>();
                    tmp.add(which);
                    neighbourMap.put(i, tmp);
                }
            }
        }
    }

    public void addNeighbour(Peer which)
    {
        if (which instanceof Server)
        {
            connectedServers.add((Server)which);
        }else if (!neighbours.containsKey(which))
        {
            neighbours.put(which,-1);
            contributionOfNeighbours.put(which,new Action(INIT_CONTRIBUTION));
        }

    }


    public Neighbour()
    {
        contributionOfNeighbours = new HashMap<Peer, Action>();
        neighbours = new HashMap<Peer, Integer>();
    }

    public Map<Peer, Action> getContributionOfNeighbours()
    {




        return contributionOfNeighbours;
    }

    public Map<Peer, Integer> getNeighbours()
    {
        return neighbours;
    }

    public  Map<Integer, HashSet<Peer>> getNeighbourMap()
    {
        return neighbourMap;
    }

    public boolean isNeighbour(Peer peer)
    {
        return neighbours.containsKey(peer);
    }



}
