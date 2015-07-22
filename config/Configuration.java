package osnvodsim.config;

import osnvodsim.simulator.*;

import java.io.File;

public class Configuration {

    //static private int configNums;
    static private XMLParser configs[];


    static private String cfgFileName[];

    private Configuration() {
    }

    public static void setConfig(String[] args) {
        if (configs == null) {


            int configNums = args.length;
            cfgFileName = new String[configNums];
            State.setConfig(configNums);

            configs = new XMLParser[configNums];
            try {
                for (int i = 0; i < configNums; i++) {
                    configs[i] = new XMLParser(args[i]);
                    cfgFileName[i] = new File(args[i]).getName();
                }
            } catch (Exception e) {
            }
        } else {
            System.err.println("Configration can be configured only once!");
        }

    }

    public static String[] getCfgFileName() {
        return cfgFileName;
    }

    public static String getString(String tagName) {

        String str = configs[State.getRunningConfigs()].getValue(tagName);
        if (str == null)
            return "can't get string:" + tagName;

        return str.trim();

    }

    public static int getInt(String tagName) throws NumberFormatException {
        String str = configs[State.getRunningConfigs()].getValue(tagName);
        if (str == null)
            return -1;

        return Integer.parseInt(str);
    }

    public static long getLong(String tagName) throws NumberFormatException {

        String str = configs[State.getRunningConfigs()].getValue(tagName);
        if (str == null)
            return -1;

        return Long.parseLong(str);
    }

    public static double getDouble(String tagName) throws NumberFormatException {

        String str = configs[State.getRunningConfigs()].getValue(tagName);
        if (str == null)
            return -1;

        return Double.parseDouble(str);
    }

    public static boolean getBoolean(String tagName)
            throws NumberFormatException {

        String str = configs[State.getRunningConfigs()].getValue(tagName);
        if (str == null)
            return false;

        return Boolean.parseBoolean(str);
    }


    public static String[] getStrings(String tagName) {

        return configs[State.getRunningConfigs()].getValues(tagName);

    }


    public static int[] getInts(String tagName) {
        String tmp[] = configs[State.getRunningConfigs()].getValues(tagName);
        int vals[] = new int[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            vals[i] = Integer.parseInt(tmp[i]);
        }
        return vals;
    }

    public static double[] getDoubles(String tagName) {
        String tmp[] = configs[State.getRunningConfigs()].getValues(tagName);
        double vals[] = new double[tmp.length];
        for (int i = 0; i < tmp.length; i++) {
            vals[i] = Double.parseDouble(tmp[i]);
        }
        return vals;
    }

    public static void test() {
//		System.out
//				.println("fraction:" + configs[0].containsElement("fraction"));
//		System.out
//				.println("category:" + configs[0].containsElement("category"));
//
//		configs[0].getValue("category");
//		configs[0].getValue("uta_avg_watched_num_video");
//		System.out.println("");
//		configs[0].search("ctg_name", "games");
//
//		System.out.println("");
//		configs[0].search("bandwidth_type", "1");
//
//		configs[0].getValueGroup("server");


        configs[0].getValues("server_bandwidth");
        configs[0].getValues("upload_bandwidth");
        configs[0].getValues("fraction");

		/*
		 * System.out.println(""); configs[0].getValue("bandwidth_type");
		 * System.out.println(configs[0].getValue("seed_type"));
		 * System.out.println(configs[0].getValue("category"));
		 */

    }

}
