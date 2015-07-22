package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.simulator.State;
import osnvodsim.statistics.Output;
import osnvodsim.video.Video;

/**
 * Created by Mars on 2014/7/24.
 */
public class RandomOverlay extends Overlay {


    public RandomOverlay(Video video) {
        super(video);
        Output.printP2PEvent("RandomOverlay for Video" + video.getVideoID() + " has been setup!");
    }

    public static void init() {
        degree = Configuration.getInt("degree");
    }

    @Override
    public void makeConnection(Peer peer) {
        //    System.out.println("Peer"+peer.getPeerID()+" makeConnection in Overlay for Video"+this.video.getVideoID());
        peer.connect(P2PNetwork.Servers.get(P2PNetwork.Servers.size() > 1 ? State.ru.nextInt(P2PNetwork.Servers.size()) : 0));

        int overlaySize = audiences.size();

        if (overlaySize <= degree)  //���ص����ڽڵ�ƫ��ʱ
        {
            for (int i = 0; i < overlaySize; i++) {
                peer.connect(audiences.get(i));
            }
            //�������һ��������
            //     peer.connect(P2PNetwork.Servers.get(P2PNetwork.Servers.size()>1?State.ru.nextInt(P2PNetwork.Servers.size()):0));
        } else {
            boolean sel[] = State.ru.randomSelect(overlaySize, degree);    //�����ѡ��degree�����ظ��Ľڵ�
            for (int i = 0; i < overlaySize; i++) {
                if (sel[i])
                    peer.connect(audiences.get(i));
            }
        }




    }


}
