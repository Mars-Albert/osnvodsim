package osnvodsim.utilites;

/**
 * Created by Guowei on 2014/9/19.
 */
public class Utilities {

    public static double displayDouble(double num) {
        return (double) ((int) (num * 100)) / 100;
    }

    public static double displayPercentage(double num) {
        return (double) ((int) (num * 10000)) / 100;
    }

}
