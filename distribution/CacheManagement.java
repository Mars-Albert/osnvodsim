package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.interactivity.User;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Mars on 2015/3/16.
 */
public class CacheManagement {

    public static String CACHE_MANAGEMENT;
    public static int CACHE_STORED_NUM;
    public static void init() {
        CACHE_STORED_NUM= Configuration.getInt("cache_stored_num");
        CACHE_MANAGEMENT= Configuration.getString("cache_management_policy");
    }


    private static double popularity(User user, Video video ,Iterator<Peer> neighbourIt) {



        User tmp;
        int countThis = 0, countAll = 0;
        while (neighbourIt.hasNext()) {
            tmp = neighbourIt.next().getUser();
            if (tmp.watched(video))
                countThis++;
            countAll += tmp.getWatchedNum();

        }

        countThis++;
        countAll += user.getWatchedNum();
        Output.printTrack(user,"video"+video.getVideoID()+" popularity: "+(double) (countThis) / (double) countAll+" ");
        return (double) (countThis) / (double) countAll;


    }
    private static double cacheRequestPopularity(Peer peer,Video video,Cache cache)
    {
        double result;
        Iterator<Map.Entry<Bitmap, Integer>> itcv = cache.getCachedVideos().entrySet().iterator();
        int countThis = 0, countAll = 0;
        Map.Entry<Bitmap, Integer> mapTmp;
        while (itcv.hasNext()) {
            mapTmp = itcv.next();
            if (mapTmp.getKey().getThisVideo()== video)
            {
                countThis+=mapTmp.getValue();
            }
            countAll+=mapTmp.getValue();
        }

        if (countAll!=0)
        {
            result=(double) (countThis) / (double) countAll;
         //   Output.printTrack(peer,"video"+video.getVideoID()+" cache request popularity: "+(result==0?1:result)+" ");

            return (result==0?1:result);
        }

        else
            return 1;
    }



    private static double cacheStoredPopularity(Peer user, Video video, Neighbour info) {
    //    Iterator<Video> cache;
        Peer tmp;
        int count = 0, num = 0;
        double result;
        Iterator<Peer> nit=info.getNeighboursIterator();
        while (nit.hasNext()) {
          tmp=nit.next();
            num+=tmp.cachedVideoNum();
            if (tmp.cachedThisVideo(video))
                count++;

        }
        if (num != 0) {
            result=(double) count / (double) num ;
         //   Output.printTrack(user,"video"+video.getVideoID()+" cacheStoredPopularity: "+(result==0?1:result)+" ");
            return (result==0?1:result);
        }
        {
        //    Output.printTrack(user,"video"+video.getVideoID()+" cacheStoredPopularity: 1 ");
            return 1;
        }

    }


    private static double cacheValue(Peer peer, Video video,Neighbour neighbour) {


        Iterator<Video> cache;
        Peer tmp;
        int count = 0, numNeighbour = 0;
        Iterator<Peer> nit=neighbour.getNeighboursIterator();
        while (nit.hasNext()) {
            numNeighbour++;
            tmp = nit.next();

            if (!tmp.getUser().isFriend(peer.getUser()))
                continue;

          if (tmp.cachedThisVideo(video))
             count++;

        }
        if (count == 0)
        {
       //     Output.printTrack(peer,"video"+video.getVideoID()+" friends' cache  value: "+numNeighbour);
            return numNeighbour;}
        else {
         //   Output.printTrack(peer,"video"+video.getVideoID()+" friends' cache  value: "+(double) (numNeighbour - count) / (double) count);
            return (double) (numNeighbour - count) / (double) count;
        }

    }

    public static class MapKeyComparator implements Comparator<Double> {
        public int compare(Double str1, Double str2) {
            return str2.compareTo(str1);
        }
    }


    public static void manage(Peer peer,Cache cache,Neighbour neighbour)
    {
    	if(CACHE_MANAGEMENT.equals("none"))
    		return;
    	else
        if (CACHE_MANAGEMENT.equals("new"))
        {
            newMethod(peer,neighbour,cache);
        }
        else
            legacyMethod(peer,cache);

    }



    private static void newMethod(Peer peer, Neighbour neighbour, Cache cache) {

     //   ArrayList<Video> ranked = new ArrayList<Video>();


        if (cache.cachedVideoNum() <= CACHE_STORED_NUM) return;


        Iterator<Video> cachedVideos = cache.getCachedIterator();

     //   int count = 0;

        Map<Double, Video> result = new TreeMap<Double, Video>(new MapKeyComparator());

        Video tmp;
        double value;
        while (cachedVideos.hasNext()) {
            tmp = cachedVideos.next();

            //     System.out.println("video"+tmp.getVideoID()+" cache ranking! ");
            value = cacheRequestPopularity(peer, tmp,cache) * cacheStoredPopularity(peer, tmp, neighbour) * cacheValue(peer, tmp,neighbour);
      //      Output.printTrack(peer,"final value of video"+tmp.getVideoID()+"  is "+value);

     //       count++;
            if (value != 0) {
                result.put(value, tmp);
            }
        }

        Iterator<Double> itd = result.keySet().iterator();
        int count = 0;

        while (itd.hasNext()) {
            value = itd.next();
            tmp = result.get(value);
                if (count++>=CACHE_STORED_NUM){
                   Output.printTrack(peer, "Video" + tmp.getVideoID() + " cache value:" + value + " deleted!");
                    cache.deleteVideoCache(tmp);

                        return;
                }
            else
                {
                    Output.printTrack(peer, "Video" + tmp.getVideoID() + " cache value:" + value+ " deleted!");
                }

        }

        ;
    }





    private static void legacyMethod(Peer peer, Cache cache)
    {

        Iterator<Map.Entry<Bitmap, Integer>> itcv = cache.getCachedVideos().entrySet().iterator();
        Map.Entry<Bitmap, Integer> mapTmp;
        while (itcv.hasNext()) {
            mapTmp = itcv.next();
            Output.printTrack(peer, "Cache for video" + mapTmp.getKey().getThisVideo().getVideoID() + " been requested for " + mapTmp.getValue() + " times!");
        }


        if (CACHE_MANAGEMENT.equals("least"))          //�����������
        {

            if (cache.cachedVideoNum() <= CACHE_STORED_NUM) return;

         //   List<Integer> values = new ArrayList<Integer>();
            int[] requests=new int[cache.cachedVideoNum()];
            int index=0;
            itcv = cache.getCachedVideos().entrySet().iterator();
            while (itcv.hasNext()) {
                mapTmp = itcv.next();
                requests[index++]=mapTmp.getValue();
           //     values.add(mapTmp.getValue());
            }

            Arrays.sort(requests);
          /*  values.sort(new Comparator<Integer>() {
                @Override
                public int compare(Integer x, Integer y) {
                    return (x < y) ? -1 : ((x == y) ? 0 : 1);
                }
            });*/

            int min = requests[0];

            itcv = cache.getCachedVideos().entrySet().iterator();
            while (itcv.hasNext()) {
                mapTmp = itcv.next();
                if (min == mapTmp.getValue()) {
                    Output.printTrack(peer, "Cache for video" + mapTmp.getKey().getThisVideo().getVideoID() + " been deleted!");
                    P2PNetwork.leaveOverlay(peer.getUser(), mapTmp.getKey().getThisVideo());
                    cache.deleteVideoCache(mapTmp.getKey());
                    //itcv.remove();
                    return;
                }
            }

        } else if (CACHE_MANAGEMENT.equals("oldest"))    //�����
        {
            if (cache.cachedVideoNum()<= CACHE_STORED_NUM) return;
            itcv = cache.getCachedVideos().entrySet().iterator();
            mapTmp = itcv.next();

            P2PNetwork.leaveOverlay(peer.getUser(), mapTmp.getKey().getThisVideo());
            cache.deleteVideoCache(mapTmp.getKey());
           // itcv.remove();
            return;
        }
        else if (CACHE_MANAGEMENT.equals("random"))    //���
        {
            if (cache.cachedVideoNum()<= CACHE_STORED_NUM) return;
            int toDel= State.ru.nextInt(cache.cachedVideoNum());
            itcv = cache.getCachedVideos().entrySet().iterator();
            while (itcv.hasNext()) {
                mapTmp = itcv.next();
                if (toDel==0) {

                    P2PNetwork.leaveOverlay(peer.getUser(), mapTmp.getKey().getThisVideo());
                    cache.deleteVideoCache(mapTmp.getKey());
                    //itcv.remove();
                    return;
                }
                toDel--;
            }
        }
    }



}
