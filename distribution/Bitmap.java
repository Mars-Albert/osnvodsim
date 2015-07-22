package osnvodsim.distribution;

import osnvodsim.video.Video;

import java.util.List;

/**
 * Created by Mars on 2015/3/19.
 */
public class Bitmap {

    public static final int UNFINISHED = 0;
    public static final int DOWNLOADED = 1;
    public static final int PENDING = 2;
    public static final int SELECTED = 3;
    private Video thisVideo;
    private int[] bitMap;
    private int version,versionCtl;


    public Bitmap(Video video)
    {
        bitMap=new int[video.amountOfChunks()];
        for (int i = 0; i < bitMap.length; i++)
            bitMap[i]=UNFINISHED;


        thisVideo=video;
        version=versionCtl = 0;

    }
    public int getVersion()
    {
        return version;
    }


    public Video getThisVideo()
    {
        return thisVideo;
    }


    public int checkChunk(int chunkID)
    {
        return bitMap[chunkID];
    }


    public void select(int chunkID)
    {
        bitMap[chunkID]=SELECTED;
    }



    public boolean isDownloaded(int chunkID) {
        //  System.out.println("chunkID:"+chunkID);
        if (chunkID >= bitMap.length)
            return true;
        if (bitMap[chunkID] == DOWNLOADED)
            return true;
        return false;
    }


    public boolean needAsUrgent(int chunkID)
    {
        if (chunkID >= bitMap.length)
            return false;
        if (bitMap[chunkID] !=DOWNLOADED && bitMap[chunkID]!=SELECTED) {
            return true;
        }
        return false;
    }
    public boolean need(int chunkID)
    {
        if (chunkID >= bitMap.length)
            return false;
        if (bitMap[chunkID] == UNFINISHED) {
            return true;
        }
        return false;
    }

    public boolean downloading(int chunkID)
    {
        if (chunkID >= bitMap.length)
            return false;
        if (bitMap[chunkID] == UNFINISHED) {
            bitMap[chunkID]= PENDING;
            return true;
        }
        return false;
    }
    public void downloaded(int chunkID)
    {
        if (chunkID >= bitMap.length)
            return ;

        bitMap[chunkID]=DOWNLOADED;

        //下载5个块更新一个版本，免得版本更新过快，导致交换开支过大。
        if (versionCtl == 5) {
            version++;
            versionCtl = 0;
        }
        versionCtl++;

    }

    public boolean toTheEnd(int chunkID) {
        return !(chunkID < bitMap.length);
    }

    public int length()
    {
        return bitMap.length;
    }
    public int lastSequentialDownload() {
        for (int i = 0; i < bitMap.length; i++)
            if (!isDownloaded(i))
                return i;
        return bitMap.length-1;
    }


}
