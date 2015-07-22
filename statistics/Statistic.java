package osnvodsim.statistics;

import osnvodsim.distribution.Message;
import osnvodsim.distribution.Peer;
import osnvodsim.distribution.Server;
import osnvodsim.simulator.State;
import osnvodsim.utilites.Utilities;
import osnvodsim.video.Chunk;

/**
 * Created by Guowei on 2014/9/19.
 */
public class Statistic {


    private static double totalTranzDelay;
    private static long totalTranzCount;
    private static long totalServerSent;
    private static long totalMessageSent;


    public static void init() {

        totalTranzDelay = 0;
        totalTranzCount = 0;
        totalServerSent = 0;
        totalMessageSent = 0;
    }


    public static void recordTransmission(double time, Peer peer) {
        totalTranzDelay += (time);
        totalTranzCount++;

        if (peer instanceof Server)
            totalServerSent++;
    }

    public static void recordMessageSent() {
        totalMessageSent++;
    }

    public static void makeRecord() {
        double avg;
        if (totalTranzCount > 0) {
            avg = totalTranzDelay / totalTranzCount;

            Output.printSTAT("total chunk sent:" + totalTranzCount + " total message sent:" + totalMessageSent);
            Output.printSTAT("average chunk transmission delay is:" + Utilities.displayDouble(avg) + "s");
            State.addDelay(avg);
            Output.printSTAT("percentage of chunk sent by servers: " + Utilities.displayPercentage((double) totalServerSent / totalTranzCount) + "%");
            State.addServerAssistance((double) totalServerSent / totalTranzCount);
            Output.printSTAT("percentage of control information: " + Utilities.displayPercentage((double) totalMessageSent * Message.MESSAGE_SIZE / (totalTranzCount * Chunk.CHUNK_SIZE + totalMessageSent * Message.MESSAGE_SIZE)) + "%");


        }

    }


}
