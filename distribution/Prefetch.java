package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.interactivity.User;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.video.Repository;
import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Mars on 2015/3/14.
 */
public class Prefetch {


    public static double PREFETCH_LENGTH = 5;
    public static double ALPHA = 0.1;
    private static double[][] simMatrix;

    private static double lastUpdateTime;

    private static int k;
    private static int PREFETCH_NUM;
    public static String PREFETCH_ALG;

    public static void init() {
        simMatrix = new double[Repository.numTotalVideo()][];
        for (int i = 0; i < Repository.numTotalVideo(); i++)
            simMatrix[i] = new double[Repository.numTotalVideo() - i];
        //ע���ǶԳƾ��󣬽�ʡ�洢

        lastUpdateTime=0;
        updateSimMatrix();
        k = 5;
        PREFETCH_NUM = Configuration.getInt("prefetch_num");
        PREFETCH_ALG = Configuration.getString("prefetch_alg");
    }

    private static void printMatirx() {
        for (int i = 0; i < simMatrix.length; i++) {
            for (int j = 0; j < simMatrix[i].length; j++)
                System.out.print(simMatrix[i][j] + "\t");
            System.out.println("");
        }
    }

    public static double get(int row, int col) {
        return row > col ? simMatrix[col][row - col] : simMatrix[row][col - row];
    }

    public static void set(int row, int col, double val) {
        if (row > col)
            simMatrix[col][row - col] = val;
        else
            simMatrix[row][col - row] = val;


    }


    public static double similarity(Video i, Video j)     //��Ƶ������
    {

        if (i.getVideoID() == j.getVideoID() || i.getAudiencesSet().size() == 0 || j.getAudiencesSet().size() == 0)
            return 0;

        HashSet<User> result = new HashSet<User>();

        result.addAll(i.getAudiencesSet());
        result.retainAll(j.getAudiencesSet());          //����

        Iterator<User> itu = result.iterator();
        double total = 0;

        for (; itu.hasNext(); )
            total += (1 / Math.log(1 + itu.next().getWatchedNum()));


        total /= (Math.pow(i.getAudiencesSet().size(), ALPHA) * Math.pow(j.getAudiencesSet().size(), ALPHA));

        //System.out.println("Video "+i.getVideoID()+" Video"+j.getVideoID()+" sim:"+total);
        set(i.getVideoID(), j.getVideoID(), total);

        return total;
    }

    public static class MapKeyComparator implements Comparator<Double> {
        public int compare(Double str1, Double str2) {
            return str2.compareTo(str1);
        }
    }






    private static int[] topK(Video video) {
        if (k < 1 || k > Repository.numTotalVideo()) return null;


        Map<Double, Integer> result = new TreeMap<Double, Integer>(new MapKeyComparator());

        for (int i = 0; i < k; i++) {
            if (get(i, video.getVideoID()) != 0)
                result.put(get(i, video.getVideoID()), i);
        }

        int sorted[] = new int[k], i = 0;
        Iterator<Double> iterator = result.keySet().iterator();
        double tmp;

        if (result.size() != 0)
            while (iterator.hasNext() && i < k) {
                tmp = iterator.next();
                sorted[i] = result.get(tmp);
                //  System.out.println("Video "+video.getVideoID()+" Video"+sorted[i]+" sim:"+tmp);
                i++;
            }

        return sorted;
    }

    private static void updateSimMatrix() {
        for (int i = 0; i < Repository.numTotalVideo(); i++) {
            for (int j = i; j < Repository.numTotalVideo(); j++)
                similarity(Repository.getVideo(i), Repository.getVideo(j));

        }
        // printMatirx();
        // System.out.println("Matrix updated completed!\n");

    }

    public static double proSelectingAVideo(User user, Video video) {


        double total = 0;
        int[] topK = topK(video);
        for (int i = 0; i < k; i++) {

            //total+=similarity(video,Repository.getVideo(topK[i]));
            total += get(video.getVideoID(), topK[i]);


            //  System.out.println("Video "+video.getVideoID()+" Video"+Repository.getVideo(topK[i]).getVideoID()+" sim:"+get(video.getVideoID(),topK[i]));
        }
        total *= video.getAudiencesSet().size();
        //  Output.printTrack(user,"Video"+video.getVideoID()+" Pro:"+total);
        return total;

    }

    private static HashSet<Video> VA(User user) {
        HashSet<Video> videos = new HashSet<Video>();
        Iterator<User> itFriends = user.getFriends().iterator();
        Iterator<Video> tmp;

        while (itFriends.hasNext()) {
            tmp = itFriends.next().getPlayedIterator();
            while (tmp.hasNext())
                videos.add(tmp.next());
        }
      /*
        tmp=user.getPlayedIterator();
        while (tmp.hasNext())
            videos.add(tmp.next());
        */
        return videos;
    }

    

    
    
    
    public static ArrayList<Video> prefetch(User user) {

    	
    	if(PREFETCH_ALG.endsWith("none"))
    	{
    		return null;
    	}
    	else
    	if(PREFETCH_ALG.endsWith("random"))
    	{
    				
    		 ArrayList<Video> selected = new ArrayList<Video>();
    	    	int index=0;
    	    	while(index<PREFETCH_NUM)
    	    	{
    	    		selected.add(Repository.getVideo(State.ru.nextInt(Repository.numTotalVideo())));

    	    	}
    	         	        
    	        return selected; 		
    	}
    	
    	
    	
    	
        //ÿ��5�����һ�Ρ������ٶ�̫��
        if (State.getTime()-lastUpdateTime>5) {
            updateSimMatrix();
            lastUpdateTime=State.getTime();
        }
        Iterator<Video> itVA = VA(user).iterator();

        Video tmp;

        Map<Double, Video> topN = new TreeMap<Double, Video>(new MapKeyComparator());

        double pro;
        while (itVA.hasNext()) {
            tmp = itVA.next();
            if (!user.watched(tmp)) {
                pro = proSelectingAVideo(user, tmp);
                if (pro != 0) {
                    Output.printTrack(user, "Video" + tmp.getVideoID() + " Probability:" + pro);
                    topN.put(pro, tmp);
                }
            }
        }

        Output.printTrack(user, "So decisions are:");
        if (topN.size() == 0)
            return null;

        Iterator<Double> iterator = topN.keySet().iterator();
        Double key;

        ArrayList<Video> ranked = new ArrayList<Video>();

        int i = 0;
        while (iterator.hasNext() && i < PREFETCH_NUM) {
            key = iterator.next();
            tmp = topN.get(key);

            if (!user.watched(tmp)) {
                ranked.add(topN.get(key));
                Output.printTrack(user, "Video" + tmp.getVideoID());
                i++;
            } else {
                Output.printTrack(user, "Video" + tmp.getVideoID() + " has been watched!");
            }

        }
        return ranked;

    }


}
