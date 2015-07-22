package osnvodsim.simulator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import osnvodsim.config.Configuration;
import osnvodsim.utilites.RandomUtil;
import osnvodsim.utilites.Utilities;

public class State {

    private static int numConfigs = -1; // �����ļ�����
    private static int runningConfigs = -1; // ��ǰ���еڼ��������ļ�

    private static double time;
    private static double endingTime;

    private static int execCycles = -1; // ����һ�������ļ����еĴ���

    private static int runningCycles = -1; // ������������

    private static long seed = -1; // ��ǰ����
    private static String seedType;

    static final String SEED_TYPE_RANDOM = "random";
    static final String SEED_TYPE_SPECIFIED = "specified";
    static final String SEED = "seed";
    static final String SEED_TYPE = "seed_type";
    static final String EXEC_CYCLES = "exec_cycles";
    public static RandomUtil ru = null;
    

	public static void addInterruption(double interruption) {
		State.interruption[runningCycles-1]=interruption;
	}


	public static void addBufferTime(double bufferTime) {
		State.bufferTime[runningCycles-1] = bufferTime;
	}


	public static void addDelay(double delay) {
		State.delay[runningCycles-1] = delay;
	}


	public static void addServerOverload(double serverOverload) {
		State.serverOverload[runningCycles-1] = serverOverload;
	}


	public static void addServerAssistance(double serverAssistance) {
		State.serverAssistance[runningCycles-1] = serverAssistance;
	}

	private static double[] interruption,bufferTime,delay,serverOverload,serverAssistance;
    
    

    private State() {

    }

    public static void initializeConfig() // ��ÿ�θ��������ļ�ʱ����
    {

        // if(runningConfigs<numConfigs-1)
        runningConfigs++;
        runningCycles = 0;

        seedType = Configuration.getString(SEED_TYPE);
        execCycles = Configuration.getInt(EXEC_CYCLES);
        endingTime = Configuration.getDouble("exec_time");

        interruption=new double[execCycles];
        bufferTime =new double[execCycles];
        delay=new double[execCycles];
        serverOverload=new double[execCycles];
        serverAssistance=new double[execCycles];
        
        
        if (seedType.equals(SEED_TYPE_SPECIFIED)) {
            seed = Configuration.getLong(SEED);
            execCycles = 1;
        } else if (seedType.equals(SEED_TYPE_RANDOM))
            ;
        else {
            System.out.println("unknown seed type!!");
        }

    }

    public static void average()
    {
          try {
        	  
        	  
			PrintStream makeRecord=new PrintStream(Configuration.getString("output_root_path") + "osnvodsim_output" + File.separator+Configuration.getCfgFileName()[State.getRunningConfigs()] + File.separator+ "Results.txt");
			
			
			double tmp=0;
			
			makeRecord.println("Simulator running "+execCycles+" rounds!");
			makeRecord.println("each interruption: "+Arrays.toString(interruption)+"\n");
			for(double t:interruption)
				tmp+=t;
			makeRecord.println("avarage interruption: "+Utilities.displayPercentage(tmp/execCycles)+"%\n");
			
			
			
			tmp=0;
			
			
			makeRecord.println("each bufferTime: "+Arrays.toString(bufferTime)+"\n");
			for(double t:bufferTime)
				tmp+=t;
			makeRecord.println("avarage bufferTime: "+tmp/execCycles+"\n");
			
			tmp=0;
			
			
			makeRecord.println("each delay: "+Arrays.toString(delay)+"\n");
			for(double t:delay)
				tmp+=t;
			makeRecord.println("avarage delay: "+tmp/execCycles+"\n");
			
			tmp=0;
			
			
			makeRecord.println("each serverOverload: "+Arrays.toString(serverOverload)+"\n");
			for(double t:serverOverload)
				tmp+=t;
			makeRecord.println("avarage serverOverload: "+Utilities.displayPercentage(tmp/execCycles)+"%\n");
			
			tmp=0;
			
			
			makeRecord.println("each serverAssistance: "+Arrays.toString(serverAssistance)+"\n");
			for(double t:serverAssistance)
				tmp+=t;
			makeRecord.println("avarage serverAssistance: "+Utilities.displayPercentage(tmp/execCycles)+"%\n");
			

			makeRecord.close();
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public static void initializeSeed() // ÿ��ִ�ж�Ҫ����
    {

        // if(runningCycles<execCycles)
        runningCycles++;

        if (seedType.equals(SEED_TYPE_RANDOM))
            seed = new Random().nextLong();

        ru = new RandomUtil();
        ru.setSeed(seed);

        time = 0;

    }

	/*public static void initialize()
	{
		
		if(runningCycles>=execCycles)
		{
			runningConfigs++;
			runningCycles = 0;
			
			seedType = Configuration.getString(SEED_TYPE);
			execCycles = Configuration.getInt(EXEC_CYCLES);
			endingTime = Configuration.getDouble("exec_time");
			
			
			if (seedType.equals(SEED_TYPE_SPECIFIED)) {
				seed = Configuration.getLong(SEED);
				execCycles = 1;
			}
			
		}
		
			if (seedType.equals(SEED_TYPE_RANDOM))
				seed = new Random().nextLong();
		
			ru = new RandomUtil();
			ru.setSeed(seed);

			System.out.println("init complted! runningConfigs: " + runningConfigs
					+ " runningCycles: " + runningCycles + " seed: " + seed);

			time = 0;
			resetSystem();
		
		
	}*/

    public static int getExecCycles() {
        return execCycles;
    }

    public static int getRunningCycles() {
        return runningCycles;
    }

    //public static void setRunningCycles(int runningCycles) {
    //	State.runningCycles = runningCycles;
//	}

    public static void setConfig(int i) {
        numConfigs = i;
        // runningConfigs=0;

    }

    public static int getConfigNums() {
        return numConfigs;
    }

    public static int getRunningConfigs() {
        return runningConfigs;
    }

    public static double getTime() {
        return time;
    }

/*    public static double watchTime() {
        return (double)((int)(time*100))/100;
    }*/

    public static void setTime(double time) {
        State.time = time;
    }

    public static double getEnd() {
        return endingTime;
    }

/*	public static void setEndtime(long endingTime) {
		State.endingTime = endingTime;
	}*/


}
