package osnvodsim.statistics;

import osnvodsim.config.Configuration;
import osnvodsim.distribution.Overlay;
import osnvodsim.distribution.P2PNetwork;
import osnvodsim.distribution.Peer;
import osnvodsim.interactivity.Interactivity;
import osnvodsim.interactivity.User;
import osnvodsim.simulator.Simulator;
import osnvodsim.simulator.State;
import osnvodsim.video.Video;

import java.io.*;
import java.util.HashMap;

/**
 * Created by Guowei on 2014/8/27.
 */
public class Output {

/*    public static StringBuffer SBUA;
    public static StringBuffer SBP2P;
    public static StringBuffer SBSYS;*/

    private static PrintStream psUSER;
    private static PrintStream psP2P;
    private static PrintStream psSYS;
    private static PrintStream psSTAT;

    private static String workingPath;
    private static String userInfoPath;
    private static String overlayInfoPath;

    private static boolean deleted = false;

    private static HashMap<User, PrintStream> traceUser;

    private static HashMap<Video,PrintStream> traceOverlay;

    private static Video tracedVideo;

    private static String printBuffer;


    public static void init() {
        Statistic.init();

        
        
    	if(Simulator.releaseVersion)
    	{
    		
            String rootFolder = Configuration.getString("output_root_path") + "osnvodsim_output" + File.separator;
            workingPath = rootFolder + Configuration.getCfgFileName()[State.getRunningConfigs()] + File.separator;
    		
            File path;
            if (!deleted) {       //��ֻ֤�ڵ�һ�γ�ʼ��ʱɾ���ĵ���
                path = new File(rootFolder);
                if (path.isDirectory()) {
                    //ɾ�������ļ�
                    deleteDir(path);
                }
                deleted = true;
            }
            path = new File(workingPath);
            path.mkdirs();
    		
    		
    		return;
    	}
        
        
        
        
        traceUser = new HashMap<User, PrintStream>();
        traceOverlay=new HashMap<Video, PrintStream>();

        if (psUSER != null)
            psUSER.close();
        if (psP2P != null)
            psP2P.close();
        if (psSYS != null)
            psSYS.close();
        if(psSTAT !=null)
            psSYS.close();

        String rootFolder = Configuration.getString("output_root_path") + "osnvodsim_output" + File.separator;
        workingPath = rootFolder + Configuration.getCfgFileName()[State.getRunningConfigs()] + File.separator + "cycle" + State.getRunningCycles() + File.separator;
        String uafileName = Configuration.getString("user_activity_information");
        String p2pfileName = Configuration.getString("distribution_information");
        String sysfileName = Configuration.getString("system_information");
        String statfileName= Configuration.getString("statistics");
        try {

            File path;
            if (!deleted) {       //��ֻ֤�ڵ�һ�γ�ʼ��ʱɾ���ĵ���
                path = new File(rootFolder);
                if (path.isDirectory()) {
                    //ɾ�������ļ�
                    deleteDir(path);
                }
                deleted = true;
            }
            userInfoPath=workingPath + "user-info"+ File.separator;
            overlayInfoPath=workingPath + "overlay-info"+ File.separator;
            path = new File(userInfoPath);
            path.mkdirs();
            path = new File(overlayInfoPath);
            path.mkdirs();

            psUSER = new PrintStream(new FileOutputStream(workingPath + uafileName));
            psP2P = new PrintStream(new FileOutputStream(workingPath + p2pfileName));
            psSYS = new PrintStream(new FileOutputStream(workingPath + sysfileName));
            psSTAT = new PrintStream(new FileOutputStream(workingPath + statfileName));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public static void printUAEvent(String s) {
    	if(!Simulator.releaseVersion)
    	{
        psUSER.printf("Time: %.2f", State.getTime());
        psUSER.println("\t" + s);
        printBuffer = s;
    	}
    }

    public static void printP2PEvent(String s) {
    	if(!Simulator.releaseVersion)
    	{
        psP2P.printf("Time: %.2f", State.getTime());
        psP2P.println("\t" + s);
        printBuffer = s;
    	}
    }

    public static void printSYSEvent(String s) {
    	if(!Simulator.releaseVersion)
    	{
        psSYS.printf("Time: %.2f", State.getTime());
        psSYS.println("\t" + s);
    }
    }

    public static void printSTAT(String s)
    {
    	if(!Simulator.releaseVersion)
        psSTAT.println("\t" + s);
    }
    public static void printStatistics() {

        Statistic.makeRecord();
        Interactivity.calculate();
        P2PNetwork.printOverlay();
  

    }

    //�����׷��ָ���������û������û���������Ϊ���������һ���ļ���
    public static void traceUser(User who) {
    	if(!Simulator.releaseVersion)
        if (traceUser.size() < 10) {
            if (State.ru.isOK(0.3)) {
                try {
                    traceUser.put(who, new PrintStream(new FileOutputStream(userInfoPath+ "User" + who.getUserID() + ".txt")));

                    //    System.out.println("User"+who.getUserID()+" been traced!!");


                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void traceOverlay(Video video)
    {

    	if(!Simulator.releaseVersion)
        if (!traceOverlay.containsKey(video))
        {
            try {
              traceOverlay.put(video,new PrintStream(new FileOutputStream(overlayInfoPath+ "Overlay" + video.getVideoID() + ".txt")));
            }
                catch (FileNotFoundException e) {
               e.printStackTrace();
            }
        }

    }
    public static void printOverlay(Object video,String s)
    {
    	if(!Simulator.releaseVersion)
    	{
        Video tmp;
        PrintStream ps;
        if (video instanceof Video)
            tmp=(Video)video;
        else if (video instanceof Overlay)
            tmp=((Overlay) video).getVideo();
        else  return;

        if (traceOverlay.containsKey(tmp))
        {
            ps=traceOverlay.get(tmp);
            ps.printf("Time: %.2f", State.getTime());
            ps.println("\t" + s);
        }
    	}

    }


    public static void traceOneOverlay(User who) {

    	if(!Simulator.releaseVersion){
        if (who.getPlayingVideo() == tracedVideo)
            try {
                traceUser.put(who, new PrintStream(new FileOutputStream(userInfoPath + "User" + who.getUserID() + ".txt")));

                //    System.out.println("User"+who.getUserID()+" been traced!!");


            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    	}

    }


    public static void setTracedVideo(Video video) {
    	
    	if(!Simulator.releaseVersion)
    	{
        if (tracedVideo == null)
            if (State.ru.isOK(0.3))
                tracedVideo = video;
    	}
    }

    public static void stopTracing(User who) {
    	
    	if(!Simulator.releaseVersion)
    	{
        if (traceUser.containsKey(who)) {
            traceUser.remove(who);
        }
    	}

    }

    public static void printTrack(Object user, String s) {
    	if(!Simulator.releaseVersion)
    	{
        printBuffer = s;
        printTrack(user);
    }
    }
    public static void print(Object user, String s) {
    	
    	if(!Simulator.releaseVersion)
    	{
        User tmp;
        PrintStream ps;
        if (user instanceof User)
            tmp = (User) user;
        else if (user instanceof Peer)
            tmp = ((Peer) user).getUser();
        else return;

        if (traceUser.containsKey(tmp)) {
            ps = traceUser.get(tmp);
            //   ps.printf("Time: %.2f", State.getTime());
            ps.print("\t" + s);
        }
    	}

    }

    public static void printTrack(Object user) {
    	
    	if(!Simulator.releaseVersion)
    	{
        User tmp;
        PrintStream ps;
        if (user instanceof User)
            tmp = (User) user;
        else if (user instanceof Peer)
            tmp = ((Peer) user).getUser();
        else return;

        if (traceUser.containsKey(tmp)) {
            ps = traceUser.get(tmp);
            ps.printf("Time: %.2f", State.getTime());
            ps.println("\t" + printBuffer);
        }
    	}
    }

    private static void deleteDir(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            for (int i = 0; i < children.length; i++) {
                deleteDir(children[i]);
            }
        }
        dir.delete();
    }


}
