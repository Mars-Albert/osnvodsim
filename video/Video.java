package osnvodsim.video;


import osnvodsim.config.Configuration;
import osnvodsim.interactivity.User;
import osnvodsim.statistics.Output;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class Video {

    private static int videoCounter;

    public static double VIDEO_RATE;
    public static String[] CATEGORIES;

    private List<Chunk> source;

    private final int videoID = videoCounter++;


    private int videoCtg;
    private double length;
    private int videoPopularity;
    private int watchedNum;
    private int numChunk;
    private HashSet<User> audiences;

    public Video() {

        System.out.println("Video" + videoID + " created!");

    }

    public Video(int ctg, double len, int popularity) {

        videoCtg = ctg;
        length = len;
        videoPopularity = popularity;
        Chunk.init();
        numChunk = (int) (length * VIDEO_RATE / Chunk.CHUNK_SIZE);
        audiences=new HashSet<User>();
        initData();
    }

    public void watch(User user)
    {
        audiences.add(user);
    }

    public HashSet<User> getAudiencesSet()
    {
        return audiences;
    }

    @Override
    public String toString() {
        return "Video" + videoID + " category: " + getVideoCtgName() + " length: " + length + " rank: " + videoPopularity;
    }

    public int getVideoID() {
        return videoID;
    }

    public static void setCtgNames(String[] ctgNames) {
        CATEGORIES = ctgNames;
    }

    /* public static void setPopularityRank(int popularityRank) {
         Video.popularityRank = popularityRank;
     }*/
    public int getVideoPopularity() {
        return videoPopularity;
    }

    public int getVideoCtg() {
        return videoCtg;
    }

    public String getVideoCtgName() {
        return CATEGORIES[videoCtg];
    }

    public double getLength() {
        return length;
    }

    public int amountOfChunks() {
        return numChunk;
    }


    public static void init() {
        videoCounter = 0;
        VIDEO_RATE = Configuration.getDouble("video_rate");
    }

    public void addAudience() {
        watchedNum++;
    }

    public int watchedNum() {
        return watchedNum;
    }

    public double timeInterval() {
        return Chunk.CHUNK_SIZE / VIDEO_RATE;
    }

    public int getChunkSeq(Chunk chunk) {
        return (int) (chunk.getChunkID() - source.get(0).getChunkID());
    }

    private void initData() {

        source = new ArrayList<Chunk>();
        for (int i = 0; i < numChunk; i++)
            source.add(new Chunk(this));

        Output.printUAEvent(this + "\t" + source.size() + "chunks added!");
    }

    public Chunk getChunkByID(int chunkID) {
        if (chunkID < 0 || chunkID >= source.size())
            return null;
        else
            return source.get(chunkID);

    }

}
