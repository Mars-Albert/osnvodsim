package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;

import java.util.HashMap;

/**
 * Created by Guowei on 2014/8/28.
 */
public class Network {

    public static class Connection {

        private Peer from, to;
        private double latency;

        private Connection(Peer from, Peer to, double latency) {
            this.from = from;
            this.to = to;
            this.latency = latency;
        }

        public Peer getFrom() {
            return from;
        }

        public Peer getTo() {
            return to;
        }

        public double getLatency() {
            return latency;
        }
    }


    private static HashMap<Peer, HashMap<Peer, Double>> latencyMap;   //¶þÎ¬ÑÓ³Ù¾ØÕóÍ¼
    public static double min, max;
    public static double packetLoss;


    private static double getMaxBW(Peer from, Peer to) {
        return from.getUploadBW() > to.getDownloadBW() ? to.getDownloadBW() : from.getUploadBW();
    }

    public static Connection getConnection(Peer from, Peer to) {
        double latency = ping(from, to);
        Connection con = new Connection(from, to, latency);
        return con;
    }

    public static double ping(Peer from, Peer to) {
        HashMap<Peer, Double> tmp;
        ;
        double latency;
        if (!latencyMap.containsKey(from)) {

            latency = State.ru.randomDouble2(min, max);
            tmp = new HashMap<Peer, Double>();
            tmp.put(to, latency);
            latencyMap.put(from, tmp);
        } else {
            if (latencyMap.get(from).containsKey(to)) {
                latency = latencyMap.get(from).get(to);
            } else {
                latency = State.ru.randomDouble2(min, max);
                latencyMap.get(from).put(to, latency);
            }
        }
        return latency;
    }


    public static void init() {
        latencyMap = new HashMap<Peer, HashMap<Peer, Double>>();
        min = Configuration.getDouble("min_latency");
        max = Configuration.getDouble("max_latency");
        packetLoss = Configuration.getDouble("packet_lose");
    }
}
