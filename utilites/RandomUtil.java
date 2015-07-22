package osnvodsim.utilites;

import java.math.BigDecimal;
import java.util.Random;

import osnvodsim.config.Configuration;
import osnvodsim.interactivity.User;
import osnvodsim.simulator.State;
import osnvodsim.video.Video;

@SuppressWarnings("serial")
public class RandomUtil extends Random {

    public double random_variation;
    private double lambda;
    private double[] zipf;

    private long seed;

    public RandomUtil() {
        lambda = Configuration.getDouble("uta_avg_incomings_per_second");
        random_variation=Configuration.getDouble("random_variation");
        if (random_variation > 1 || random_variation < 0) {
            System.out.println("invalid random variation!");
            System.exit(0);
        }
    }

    public void setSeed(long seed) {
        this.seed = seed;
        super.setSeed(seed);
    }

    public long getSeed() {
        return seed;
    }

    public boolean isOK(double pro) {
        return nextDouble() <= pro;
    }

    public double genIntervalExp() {
        //泊松分布间隔时间为指数分布，生成下一个间隔时间。
        return -1 / lambda * Math.log(nextDouble());


    }

    public double randomDouble2(double min, double max) {
        if (max < min) return 0;
        return nextDouble() * (max - min) + min;
    }

    public double randomDouble(double base) {
        return base * (1 - random_variation + 2 * random_variation * nextDouble());
    }


    public int randomInt(double base) {
        BigDecimal b = new BigDecimal(randomDouble(base));
        return b.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
    }


/*    public int getRank(int total,double prob)
    {
        double rnd=nextDouble();
        double multi;
        double ac=0;
        for(int i=0;i<total-1;i++)
        {
            multi=prob*(1-ac);
            ac+=multi;
            if(rnd-ac<0) return i;
        }
        return total-1;
    }*/

    public void setZipf(int rank, double B) {
        double[] k = new double[rank];
        double total = 0;
        zipf = new double[rank];

        if (rank <= 0) return;

        for (int i = 0; i < rank; i++) {
            k[i] = 1 / Math.pow(i + 1, B);
            total += k[i];
        }

        for (int i = 0; i < rank; i++) {
            //      zipf[i]=k[i]/total+last;
            zipf[i] = k[i] / total;
            //    last=zipf[i];
        }
/*
        for(double i:zipf)
         System.out.println(i);*/

    }

    public double[] getSelProVecByZipf(int[] amount) {
        if (amount == null) return null;
        double vector[] = new double[amount.length], total = 0;

        for (int i = 0; i < amount.length; i++) {
            total += amount[i] * zipf[i];
        }
        for (int i = 0; i < amount.length; i++) {
            vector[i] = amount[i] * zipf[i] / total;
        }
        return vector;
    }

    public int getRankByZipf(boolean rev)   //rev :true 排名数值越小，排名越高  ，false 排名数值越大，排名越高
    {
        double pro = nextDouble(), tmp = 0;
        int i;

        if (zipf == null) return -1;

        if (!rev)
            for (i = 0; i < zipf.length; i++) {
                tmp += zipf[i];
                if (pro <= tmp) return i;
            }
        else
            for (i = 0; i < zipf.length; i++) {
                tmp += zipf[i];
                if (pro <= tmp) return zipf.length - i - 1;
            }
        return zipf.length - 1;
    }

    public int[] genInterestVector() {

        int numInterest = State.ru.nextInt(Video.CATEGORIES.length);

     //   numInterest = Video.CATEGORIES.length >= numInterest ? numInterest : Video.CATEGORIES.length;
        int[] interestCtg = new int[numInterest];
        boolean[] check = new boolean[Video.CATEGORIES.length];
        int cnt, k = 0;
        for (int i = 0; i < Video.CATEGORIES.length; i++)
            check[i] = false;

        //随机选择numInterest个不重复的兴趣,
        for (int i = 0; i < numInterest; i++) {
            cnt = State.ru.nextInt(Video.CATEGORIES.length - i);
            //     System.out.println("cnt:" + cnt);
            for (int j = 0; cnt >= 0; j++) {
                if (!check[j])
                    cnt--;
                k = j;
            }
            check[k] = true;
            interestCtg[i] = k;
        }


        return interestCtg;

             /*   for(boolean i:check)
            System.out.print(i+"\t");
        System.out.println("");
        for(int i:interestCtg)
            System.out.print(i+"\t");
        System.out.println("");*/


    }

    public int selectByProbabilityVector(double[] ctgP) {

        //概率向量，选择个数为数组大小，每个数值为选择它的概率大小。

        double pro = nextDouble(), total = 0;
        //    System.out.println("pro="+pro);
        for (int i = 0; i < ctgP.length; i++) {
            total += ctgP[i];
            if (pro < total)
                return i;

        }

        return ctgP.length - 1;

    }


    public boolean[] randomSelect(int size, int num)  //选择范围大小，选择个数
    {

        if (num >= size) return null;


        boolean select[] = new boolean[size];
        for (int i = 0; i < size; i++)
            select[i] = false;
        int wideSel, narrowSel;


        for (int i = 0; i < num; i++) {
            wideSel = nextInt(size - 1);
            if (!select[wideSel])             //o(1)复杂度，当选择不重复时，直接选择
            {
                select[wideSel] = true;
            } else                   //当选择重复时，o(n)复杂度的向后检查
            {
                narrowSel = nextInt(size - 1 - i);
                int j;

                for (j = 0; j < size; j++) {
                    //      System.out.println("i="+i+" j="+j+" narrowSel="+narrowSel+" schedule["+j+"]="+schedule[j]);
                    if (!select[j]) narrowSel--;
                    if (narrowSel < 0 && !select[j]) {
                        select[j] = true;
                        break;
                    }
                }


            }

        }

        return select;
    }


}
