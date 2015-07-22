package osnvodsim.distribution;

import osnvodsim.statistics.Output;
import osnvodsim.video.Chunk;
import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Guowei on 2014/9/4.
 */
public class Cache {         //videoCache!!!!

    public static final int BUFFERING=0;
    public static  final int PLAYING=1;
    public static  final int INTERRUPTED=2;

    protected int playStatus;
    protected Video playingVideo;
    protected Bitmap playingBitmap;

    public boolean isBusy() {
        return busy;
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
    }

    protected boolean busy;


    private Map<Bitmap, Integer> cachedVideos;    //缓存位图+被请求次数

    private Set<Video> prefetchedVideos;

    private int playingRequest;
    protected int playing;
    private Peer whose;
    private int urgentCount;


    public void prefetch(ArrayList<Video> videos)
    {
        if (videos!=null)
        prefetchedVideos.addAll(videos);
    }
    public boolean isPrefetched()
    {
        return prefetchedVideos.contains(playingVideo);
    }




    public void setUrgent()
    {
        urgentCount++;
    }
    public boolean nextUrgentEvent(){

        if (urgentCount>3)
        {
            urgentCount=0;
            return true;
        }
        return false;
    }



    public Cache(Peer whose) {
        playingVideo = whose.getUser().getPlayingVideo();

        this.whose=whose;

        playStatus=BUFFERING;
       cachedVideos=new LinkedHashMap<Bitmap, Integer>();
        prefetchedVideos=new HashSet<Video>();

     //   playingBitmap=new VideoBitmap(playingVideo);
        busy=true;
        playing = 0;

    }

    public boolean isAltSource(Peer peer)
    {
        return peer.trackerCheck(playing);
    }

    public int getLatestVersion(Video video)
    {
        if (getVideoCache(video) != null)
            return getVideoCache(video).getVersion();
        else
            return -1;    //不存在
    }
    public Map<Bitmap, Integer> getCachedVideos() {
        return cachedVideos;
    }

    public Iterator<Video> getCachedIterator()
    {
          Iterator<Video> vdit=new Iterator<Video>() {
            Iterator<Bitmap> vbit=cachedVideos.keySet().iterator();
            @Override
            public boolean hasNext() {
                Boolean tmp=vbit.hasNext();
     //           System.out.println("video "+tmp);
                return tmp;
            }
            @Override
            public Video next() {
                Video tmp=vbit.next().getThisVideo();
          //      System.out.println("video"+ tmp.getVideoID());
                return  tmp;
            }
        };
     //   Iterator<VideoBitmap> vbit=cachedVideos.keySet().iterator();


        return vdit;

    }

    public int cachedVideoNum()
    {
        return cachedVideos.size();
    }

    public boolean cachedThisVideo(Video video)
    {
        return cachedVideos.containsKey(video);
    }

    public void addRequest(Video video)
    {
        if (playingVideo == video) {
            playingRequest++;
            return;
        }

        Map.Entry<Bitmap, Integer> mapTmp;
        Iterator<Map.Entry<Bitmap, Integer>> iterator = cachedVideos.entrySet().iterator();
        while (iterator.hasNext()) {
            mapTmp = iterator.next();
            if (mapTmp.getKey().getThisVideo() == video) {
                cachedVideos.put(mapTmp.getKey(), cachedVideos.get(mapTmp.getKey()) + 1);
                return;
            }
        }
        return;

    }

    public void deleteVideoCache(Bitmap cache)
    {
        if(cachedVideos.containsKey(cache))
        {
            Output.printTrack(whose, "Cache for video" + cache.getThisVideo().getVideoID() + " been deleted!");

            cachedVideos.remove(cache);
        }
    }
    public void deleteVideoCache(Video video)
    {

        Iterator<Bitmap> vbit=cachedVideos.keySet().iterator();
        Bitmap bitmap;
        while (vbit.hasNext())
        {
            bitmap=vbit.next();
            if (bitmap.getThisVideo()==video)
            {
                Output.printTrack(whose, "Cache for video" + bitmap.getThisVideo().getVideoID() + " been deleted!");
                vbit.remove();
            }


        }
    }
    public Bitmap getVideoCache()
    {
            return playingBitmap;
    }

    public Bitmap getVideoCache(Video video)
    {
        if (playingVideo == video)
            return playingBitmap;
        else {
            Bitmap tmp;
            Iterator<Map.Entry<Bitmap, Integer>> iterator = cachedVideos.entrySet().iterator();
            while (iterator.hasNext()) {
                tmp = iterator.next().getKey();
                if (tmp.getThisVideo() == video)
                    return tmp;
            }
            return null;
        }

    }

    public void finishThisVideo()
    {
        if (playingBitmap!=null)   //播放第一个视频不加入缓存
            cachedVideos.put(playingBitmap,playingRequest);
    }


    public void watchNewVideo(Video video)
    {
        //cachedVideos.add(video);
        playingBitmap=new Bitmap(video);
        playingVideo =video;
        playing=0;
        playStatus=BUFFERING;

      //  prefetchedVideos=new HashSet<Video>();

        playingRequest=0;
        urgentCount=0;
        playing=0;
        //重置请求数
        Iterator<Map.Entry<Bitmap, Integer>> itcv = cachedVideos.entrySet().iterator();
        Map.Entry<Bitmap, Integer> mapTmp;
        while (itcv.hasNext()) {
            mapTmp = itcv.next();
            cachedVideos.put(mapTmp.getKey(), 0);
        }
        busy=true;

    }

    public int getPlayStatus()
    {
        return playStatus;
    }

    public void playNext()
    {
        playing++;
    }
    public void setPlayStatus(int status)
    {
        playStatus=status;
    }
    //改进，尝试使用静态类做算法，将算法与数据分离





    public int playableDistance() {
        return playingBitmap.lastSequentialDownload() - playing;
    }

    public int getPlaying() {
        return playing;
    }


    public boolean notImportant(int index) {
        return index - playing > 15;
    }


/*    public boolean isUrgent(int index)
    {
        return index-playing<PlayControl.URGENT_DISTANCE;
    }*/

/*    public int selectUrgent()
    {
        int index = playing,status;
        while (!playingBitmap.toTheEnd(index)&&index-playing<PlayControl.URGENT_DISTANCE)      //具体数字可改进，计算服务器平均发送一个块的延迟
        {
            status=playingBitmap.checkChunk(index);

            if (status!=VideoBitmap.DOWNLOADED &&status!=VideoBitmap.SELECTED)
            {

                playingBitmap.select(index);
                return index;
            }

            index++;
        }
        return  -1;

    }*/



/*    public int selectByServer() {
        int index = playing;

        while (playingBitmap.toTheEnd(index) && !notImportant(index)) {
            if (playingBitmap.need(index)) {

                playingBitmap.select(index);
                return index;
            }
            index++;
        }
        return -1;   //这次不选择
    }*/


/*    public int select() {
        int index = playing;
        while (!playingBitmap.toTheEnd(index)) {

            if (playingBitmap.need(index)) {
                return index;
            }
            index++;
        }
        return -1;

    }*/


    //改进！使用深拷贝！否则版本机制无意义！


    public void startDownloadByServer(int chunkID)
    {
        playingBitmap.select(chunkID);
    }

    public void startDownload(int chunkID)
    {
        playingBitmap.downloading(chunkID);

    }


    public boolean download(Chunk chunk) {
        if (playingBitmap.isDownloaded(chunk.getChunkID())) {
            whose.getUser().duplicate();
            Output.printTrack(whose,"Chunk"+chunk.getChunkID()+" has downloaded! \t\tDuplicated!");
            return false;
        }
        playingBitmap.downloaded(chunk.getChunkID());
        return true;


    }



    public Video getVideo() {
        return playingVideo;
    }

}
