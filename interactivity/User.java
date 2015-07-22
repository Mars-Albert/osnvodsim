package osnvodsim.interactivity;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;
import osnvodsim.distribution.P2PNetwork;
import osnvodsim.distribution.Peer;
import osnvodsim.statistics.Output;
import osnvodsim.video.Video;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class User {
    private static long counter = 0;
    public static double avgWatchedFraction;
  //  public static int userAvgInterests;
    private final long UserID = counter++;

    private int[] interestCtg;
    private Video playingVideo = null;
    private List<Video> playedVideos = new ArrayList<Video>();

    private HashSet<User> friends=new HashSet<User>();

    //------------------Í³¼Æ---------------
    private double initTime;
    private double leaveTime;
    //   private int interrupted;
    private double interruptedTime;
    private double bufferingTime;

    public boolean addFriend(User user)
    {
        return friends.add(user);
    }
    public boolean isFriend(User user)
    {
        return friends.contains(user);
    }

    public HashSet<User> getFriends()
    {
        return friends;
    }

    public int getDuplicateChunk() {
        return duplicateChunk;
    }

    private int duplicateChunk;

    public double getInitTime() {
        return initTime;
    }

    public double getBufferingTime() {
        return bufferingTime;
    }

    public void buffering(double time) {
        this.bufferingTime += time;
    }

    public double getLeaveTime() {
        return leaveTime;
    }


    public double getInterruptedTime() {
        return interruptedTime;
    }

    public double getWatchTime() {
        return watchTime;
    }

    private double watchTime;

    private Peer peer;


    public void setPeer(Peer peer) {
        this.peer = peer;
    }

    public Peer getPeer() {
        return peer;
    }

    /*    public User() {
             System.out.println("user"+UserID+" created!");
        }*/
    public User() {

        interestCtg = State.ru.genInterestVector();

        initTime = State.getTime();
        interruptedTime = 0;
        leaveTime = 0;
        duplicateChunk = 0;
        //   videoCount=0;

        //      System.out.println("creating: "+this);
    }

    public long getUserID() {
        return UserID;
    }


    public void interruption(double time) {
        interruptedTime += time;
    }

    public void leave() {
        leaveTime = State.getTime();
    }

    public void duplicate() {
        duplicateChunk++;
    }

    @Override
    public String toString() {
        String interest = "";
        for (int anInterestCtg : interestCtg) {
            interest += (Video.CATEGORIES[anInterestCtg] + ", ");
        }
        return "user:" + UserID + ",interest: " + interest;
    }

    public static void init() {
        counter = 0;
        avgWatchedFraction = Configuration.getDouble("uta_watched_fraction");
      //  userAvgInterests = Configuration.getInt("uta_avg_num_interest");
    }


    public void watchVideo(Video video) {
        double watchFraction = State.ru.randomDouble(avgWatchedFraction);
        if (watchFraction > 1) watchFraction = 1;
        else if (watchFraction < 0) watchFraction = 0;
        //     double watchTime;

        //      videoCount++;

        watchTime = video.getLength() * watchFraction;

/*            VideoRecord vr = new VideoRecord();
            vr.setVideo(video);
            vr.setWho(this);
            vr.setStartTime(State.getTime());
            vr.setEndTime(watchTime);
            playedVideos.add(vr);*/

        //     Output.printUAEvent(this + " played  video" + video.getVideoID() + ",start time:" + State.watchTime() + ",end time:" + vr.getEndTime());

        playedVideos.add(video);
        playingVideo = video;
        // Output.traceOneOverlay(this);
        video.watch(this);

        Output.printUAEvent("user" + this.getUserID() + " try to play the video:" + video.getVideoID() + "(" + video.getVideoCtgName() + ")");
        Output.printTrack(this);

        //      EventHandler.scheduleUserEvent(watchTime, UserEventController.EVENT_USER_NEXT_VIDEO, this);


        P2PNetwork.getService(this);  //!!!join!

    }

    public boolean videoWatched(Video video) {
        return playedVideos.contains(video);
    }

    public int getWatchedNum() {

        return playedVideos.size();
        //return videoCount;
    }

    public Iterator<Video> getPlayedIterator() {
        return playedVideos.iterator();
    }

    public Boolean watched(Video video)
    {
        return playedVideos.contains(video);

    }

    public Video getPlayingVideo() {
        return playingVideo;
    }

    public int[] getInterestCtg() {
        return interestCtg;
    }


}
