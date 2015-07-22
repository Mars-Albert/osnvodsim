package osnvodsim.video;

import osnvodsim.config.Configuration;

import osnvodsim.distribution.Prefetch;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;

import java.util.ArrayList;
import java.util.Iterator;

public class Repository {

    private Repository() {

    }

    public static ArrayList<Video> allVideos;
    private static int[][] videoRecord;   //����������ÿһ�д���һ����Ƶ��ÿһ�д���ͬ�����жȣ�ÿһ����ֵָ����Ƶ������ĳ����Ƶĳ�����ж��£���
    private static double[][] selectProbabilityVector;  //����������ÿһ�д���һ����Ƶ��ÿһ�д���ͬ�����жȣ�ÿ����ֵ����ѡ�����ĸ��ʣ�����ѡ���ĸ���Ƶֱ�Ӿ��ȵ��ڸ��������.

    public static void init() {
        Video.init();
        String ctgNames[] = Configuration.getStrings("ctg_name");
        int ctgAmounts[] = Configuration.getInts("ctg_amount");
        double ctgAvgLength[] = Configuration.getDoubles("ctg_avg_length");
        int popularityRank = Configuration.getInt("popularity_rank");
        // double amountRank=Configuration.getDouble("video_amount_with_rank");
        int numOfVideos = 0;

        videoRecord = new int[ctgNames.length][popularityRank];
        selectProbabilityVector = new double[ctgNames.length][popularityRank];

        Video.setCtgNames(ctgNames);
        //Video.setPopularityRank(popularityRank);

        for (int i : ctgAmounts)
            numOfVideos += i;

        allVideos = new ArrayList<Video>(numOfVideos);
        State.ru.setZipf(popularityRank, 1);

        int rankTmp;
        Video toAdd;
        for (int i = 0; i < ctgNames.length; i++) {
            for (int j = 0; j < ctgAmounts[i]; j++) {
                rankTmp = State.ru.getRankByZipf(true);
                toAdd = new Video(i, State.ru.randomInt(ctgAvgLength[i]), rankTmp);
                Output.traceOverlay(toAdd);
                Output.printOverlay(toAdd,"Video"+toAdd.getVideoID()+" initialized! Info:"+toAdd.toString());

                allVideos.add(toAdd);
          //      Output.setTracedVideo(toAdd);
                videoRecord[i][rankTmp]++;
            }
        }


        //for(Video v:allVideos)
        //		System.out.println(v);

        for (int i = 0; i < videoRecord.length; i++) {

            selectProbabilityVector[i] = State.ru.getSelProVecByZipf(videoRecord[i]);

         /*   for (int j = 0; j < videoRecord[i].length; j++)
                System.out.print(videoRecord[i][j]+"  :"+ Utilities.displayDouble(selectProbabilityVector[i][j])+"  ");

            System.out.print("\n");*/
        }


        //Prefetch.init();

    }

    public static Video selectVideo(int ctg)           //����ָ������Ƶ����ѡ��һ����Ƶ
    {
        int rank = State.ru.selectByProbabilityVector(selectProbabilityVector[ctg]);
        int sel, i;

        //    System.out.println("ctg="+ctg+" rank="+rank);
        if (videoRecord[ctg][rank] == 0)
            return selectVideo(ctg);
        else {
            sel = videoRecord[ctg][rank] > 1 ? State.ru.nextInt(videoRecord[ctg][rank]) : 0;
            //     System.out.println("ctg:"+ctg+" ,rank:"+rank+" ,sel:"+sel);
            for (i = 0; sel >= 0 && i < allVideos.size(); i++) {
                if (allVideos.get(i).getVideoCtg() == ctg && allVideos.get(i).getVideoPopularity() == rank)
                    sel--;
            }
            allVideos.get(i - 1).addAudience();
            return allVideos.get(i - 1);
        }

    }
    public static Iterator<Video> getVideoIterator() {
        return allVideos.iterator();
    }
    public static int numTotalVideo()
    {
        return allVideos.size();
    }

    public static Video getVideo(int index)
    {
        return allVideos.get(index);
        
        
    }


    public static Chunk getChunkByID(Video video, int chunkID) {
        return video.getChunkByID(chunkID);
    }


}
