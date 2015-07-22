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
        //���ɷֲ����ʱ��Ϊָ���ֲ���������һ�����ʱ�䡣
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

    public int getRankByZipf(boolean rev)   //rev :true ������ֵԽС������Խ��  ��false ������ֵԽ������Խ��
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

        //���ѡ��numInterest�����ظ�����Ȥ,
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

        //����������ѡ�����Ϊ�����С��ÿ����ֵΪѡ�����ĸ��ʴ�С��

        double pro = nextDouble(), total = 0;
        //    System.out.println("pro="+pro);
        for (int i = 0; i < ctgP.length; i++) {
            total += ctgP[i];
            if (pro < total)
                return i;

        }

        return ctgP.length - 1;

    }


    public boolean[] randomSelect(int size, int num)  //ѡ��Χ��С��ѡ�����
    {

        if (num >= size) return null;


        boolean select[] = new boolean[size];
        for (int i = 0; i < size; i++)
            select[i] = false;
        int wideSel, narrowSel;


        for (int i = 0; i < num; i++) {
            wideSel = nextInt(size - 1);
            if (!select[wideSel])             //o(1)���Ӷȣ���ѡ���ظ�ʱ��ֱ��ѡ��
            {
                select[wideSel] = true;
            } else                   //��ѡ���ظ�ʱ��o(n)���Ӷȵ������
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
