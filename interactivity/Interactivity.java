package osnvodsim.interactivity;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;
import osnvodsim.event.EventHandler;
import osnvodsim.event.UserEventController;
import osnvodsim.statistics.Output;
import osnvodsim.utilites.Utilities;
import osnvodsim.video.Repository;
import osnvodsim.video.Video;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class Interactivity {


    public static ArrayList<User> allUsers;

    private static double selectingSameCtg;             //ѡ����һ����Ƶ������һ����Ƶͬһ������ĸ��ʡ�
    private static double selectingInterestedCtg;      //ѡ���û�����Ȥ��Ƶ����ĸ���
    private static double proWatchNext;

    private Interactivity() {

    }

    public static void init() {


        User.init();
        allUsers = new ArrayList<User>();

        selectingSameCtg = Configuration.getDouble("uta_selecting_same_category");
        selectingInterestedCtg = Configuration.getDouble("uta_selecting_interested_category");
     //   avgWatchedVideos = Configuration.getInt("uta_avg_watched_videos");
        proWatchNext=Configuration.getDouble("uta_probability_watch_next");
        EventHandler.setController(new UserEventController());

    }

    private static void genFriends(User who)
    {
        User tmp;
        if(allUsers.size()>20)
        {
            for(int i=0;i<10;i++)
            {
                tmp=allUsers.get(State.ru.nextInt(allUsers.size()));
                who.addFriend(tmp);
                tmp.addFriend(who);
            }
        }



    }


    public static void nextUser() {

        User who = new User();
        allUsers.add(who);

        genFriends(who);

        Output.traceUser(who);

        Output.printTrack(who, "A new user is coming:" + who.toString());
        Interactivity.nextVideo(who);

        EventHandler.scheduleUserEvent(State.getTime() + State.ru.genIntervalExp(), UserEventController.EVENT_USER_ARRIVE, who);
    }

 /*   private static User add()
    {
        int numInterest= State.ru.randomInt(userAvgInterests,1);
        User who=new User(numInterest);
        allUsers.add(who);
        return who;
    }*/

  /*  public static void watchNextVideo(User who)
    {
        int[] ctg;

        Video toWatch;
        if(who.getPlayingVideo()==null)      //�״β���
        {
            ctg = who.getInterestCtg();


            if(State.ru.isOK(selectingInterestedCtg)&&ctg.length!=0) {
               ;
            }
            else if(ctg.length!=Video.CATEGORIES.length)
            {

                boolean[] check=new boolean[Video.CATEGORIES.length];
                for(int i=0;i<check.length;i++)
                    check[i]=false;
                for(int i=0;i<ctg.length;i++)
                    check[ctg[i]]=true;
                ctg=new int[Video.CATEGORIES.length-ctg.length];

                for(int i=0,j=0;i<check.length;i++)
                {
                    if(!check[i]) {
                        ctg[j]=i;
                        j++;
                    }
                }

            }

//---------
            String sel="";
            for(int i=0;i<ctg.length;i++)
            {
                sel+=(Video.CATEGORIES[ctg[i]]+"\t");
            }

            System.out.println(who+" selecting ctg: "+sel);
//--------
            toWatch=VideoFactory.selectVideo(ctg[State.ru.nextInt(ctg.length)]);
        }
        else
        {


            toWatch=null;
        }

        who.watchVideo(toWatch);
        System.out.println("user"+who.getUserID()+" selected video"+toWatch);

    }*/


    private static Video selectOneVideo(User who)
    {
        Video toWatch;
        double ctgProbability[] = new double[Video.CATEGORIES.length];
        int ctgInterested[] = who.getInterestCtg();
        double si, li;
        int selCtg;

        //li:�û��ۿ�ÿһ����Ƶ���ࣨ��Ƶ�����û�ϲ������Ƶ���ࣩ�ĸ��ʣ����û�ϲ����������ʱ��ÿһ����Ƶ�ĸ��ʾ��ȣ�����ΪselectingInterestedCtg/ϲ���ĸ�����
        li = (ctgInterested.length == Video.CATEGORIES.length) ? 1.0 / Video.CATEGORIES.length : selectingInterestedCtg / ctgInterested.length;
        //�û��ۿ�ÿһ����Ƶ���ࣨ��Ƶ�������û�ϲ����Ƶ���ࣩ�ĸ��ʣ�����û�û����Ȥ����ôÿһ�ֵĸ��ʾ��ȣ��������£�
        si = (ctgInterested.length == 0) ? 1.0 / Video.CATEGORIES.length : (1 - selectingInterestedCtg) / (double) (Video.CATEGORIES.length - ctgInterested.length);

        for (int i = 0; i < ctgProbability.length; i++)
            ctgProbability[i] = si;

        for (int i = 0; i < ctgInterested.length; i++) {
            ctgProbability[ctgInterested[i]] = li;
        }

/*
            for(double i:ctgProbability)
            System.out.print(i+"  ");
                System.out.println("");
            */


        double ratio;

        //������ǵ�һ�β��ţ����޸ĸ��ʡ�
        if (who.getPlayingVideo() != null) {
            //�������ϴ�ͬһ����Ƶ�ĸ���ΪselectingSameCtg,�������͵ĸ��ʰ�����ѹ����
/*
                System.out.print("before:   ");
                for(double i:ctgProbability)
                    System.out.print(i+"  ");
                System.out.println("");
*/


            int lastCtg = who.getPlayingVideo().getVideoCtg();
            ratio = (1 - selectingSameCtg) / (1 - ctgProbability[lastCtg]);   //ѹ������

            for (int i = 0; i < ctgProbability.length; i++) {
                if (lastCtg == i) ctgProbability[i] = selectingSameCtg;
                else ctgProbability[i] *= ratio;
                //       tmp += ctgProbability[i];
            }
            //     System.out.println(tmp);  //check!!


        }

/*            System.out.print("after:   ");
            for(double i:ctgProbability)
                System.out.print(i+"  ");
            System.out.println("");*/

        selCtg = State.ru.selectByProbabilityVector(ctgProbability);
        //     System.out.println("sel="+selCtg);
        toWatch = Repository.selectVideo(selCtg);
        return toWatch;

    }


    public static void nextVideo(User who) {

        if (State.ru.isOK(proWatchNext) || who.getWatchedNum() == 0 &&who.getWatchedNum()<=Repository.numTotalVideo()) {
            Video toWatch = selectOneVideo(who);
            while (who.videoWatched(toWatch))   //��ֹ�ۿ��ظ���Ƶ
            {
                toWatch = selectOneVideo(who);
            }
            who.watchVideo(toWatch);
        } else {
            EventHandler.scheduleUserEvent(State.getTime(), UserEventController.EVENT_USER_LEAVE, who);
        }


    }

    public static void calculate() {
        double totalInterruption = 0;
        double totalTime = 0;
        double totalBufferTime = 0;
        int count = 0, watchedNum = 0;
        int totalDuplicate = 0;

        Iterator<User> itu = allUsers.iterator();
        User tmp;
        while (itu.hasNext()) {
            tmp = itu.next();
            if (tmp.getLeaveTime() == 0)
                tmp.leave();

            Output.printTrack(tmp, "User" + tmp.getUserID() + " stayed in system for:" + Utilities.displayDouble(tmp.getLeaveTime() - tmp.getInitTime()) + " network overload:"+Utilities.displayPercentage(tmp.getPeer().networkOverload())+"%,buffer time:" + tmp.getBufferingTime() + "s, be interrupted:" + tmp.getInterruptedTime() + "s");
           // Output.printTrack(tmp, "User" + tmp.getUserID() + " stayed in system for:" + Utilities.displayDouble(tmp.getLeaveTime() - tmp.getInitTime()) + ",buffer time:" + tmp.getBufferingTime() + "s, be interrupted:" + tmp.getInterruptedTime() + "s");

            totalBufferTime += tmp.getBufferingTime();
            watchedNum += tmp.getWatchedNum();
            totalInterruption += tmp.getInterruptedTime();
            totalTime += (tmp.getLeaveTime() - tmp.getInitTime());
            totalDuplicate += tmp.getDuplicateChunk();
            count++;
        }

        Output.printSTAT("total watch time:" + Utilities.displayDouble(totalTime) + "s , total interrupted time:" + totalInterruption + "s");
        Output.printSTAT("average watch time:" + Utilities.displayDouble(totalTime / count) + "s,average buffer time:" + Utilities.displayDouble(totalBufferTime / watchedNum) + "s, avg interrupted time:" + Utilities.displayDouble(totalInterruption / count) + "s");
        Output.printSTAT("total duplicate chunks:" + totalDuplicate + " average duplicate chunks:" + totalDuplicate / count);
        Output.printSTAT("percentage of interruption:" + Utilities.displayPercentage(totalInterruption / totalTime) + "%");


    }


}
