package osnvodsim.video;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;

/**
 * Created by Mars on 2014/7/29.
 */
public class Chunk implements Cloneable {
    public static double CHUNK_SIZE;
    private static int counter = 0;

    private double initTime;

    protected final int chunkID = counter++;

    private Video relatedVideo;

    //   private double initTime;

    public Chunk(Video video) {
        relatedVideo = video;
        // refresh();
    }

    public Video getRelatedVideo() {
        return relatedVideo;
    }

    public int getChunkID() {
        return chunkID;
    }

    public static void init() {
        counter = 0;
        CHUNK_SIZE = Configuration.getDouble("chunk_size");

    }

 /*   public void refresh()
    {
        initTime= State.getTime();
    }*/

    public Chunk clone() {
        //    distributeCount++;

        initTime = State.getTime();
        Chunk tmp = null;
        try {
            tmp = (Chunk) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return tmp;
    }


    public double getInitTime() {
        return initTime;
    }


}
