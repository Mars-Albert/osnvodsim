package osnvodsim.simulator;

import osnvodsim.config.*;
import osnvodsim.distribution.P2PNetwork;
import osnvodsim.event.EventHandler;
import osnvodsim.interactivity.Interactivity;
import osnvodsim.statistics.Output;
import osnvodsim.video.Repository;

public class Simulator {
	
	public static boolean releaseVersion=true;

    public static void main(String[] args) {

        //	System.out.println(args.length);

        Configuration.setConfig(args);

        //	Configuration.test();
        for (int i = 0; i < State.getConfigNums(); i++) // ���в�ͬ�����ļ�
        {
            State.initializeConfig();
            for (int j = 0; j < State.getExecCycles(); j++) // ���ж��
            {
                State.initializeSeed();
                System.out.println("------------------------------------------------------------------");
                System.out.println("runing: Configs: " + args[State.getRunningConfigs()]
                        + " Cycles: " + State.getRunningCycles() + " seed: " + State.ru.getSeed());
                Output.init();
                Repository.init();
                Interactivity.init();
                P2PNetwork.init();

                EventHandler.runSimulation();

                System.out.println();
                Output.printStatistics();

                //  Output.traceUser(10);
                //  P2PService.printP2PNetwork();
                System.out.println("----------------------- running completed! -----------------------\n\n");
            }
            State.average();

        }


/*
        boolean test[]=State.ru.randomSelect(8,2);
        for(int i=0;i<test.length;i++)
            System.out.print(test[i]+"\t");
*/


/*        System.out.println( State.ru.giveInt(3.5));
        System.out.println( State.ru.giveInt(3.6));
        System.out.println( State.ru.giveInt(3.4));
        System.out.println((int)3.5);
        System.out.println((int)3.6);
        System.out.println((int)3.4);*/
        // Configuration.test();

//		 int tmp[]=Configuration.getInts("upload_bandwidth");
//		 double tpd[]=Configuration.getDoubles("fraction");
//		 for(int i:tmp)
//			 System.out.println(i);
//		 for(double i:tpd)
//			 System.out.println(i); 


        // double i=Configuration.getDouble("packet_lose");
        // System.out.println(i);
    }

}
