package osnvodsim.distribution;


import osnvodsim.event.P2PEventController;
import osnvodsim.simulator.State;
import osnvodsim.video.Chunk;
import osnvodsim.video.Repository;

import java.util.Iterator;

/**
 * Created by Guowei on 2014/8/28.
 */
public class Server extends Peer {

    public int getServerID() {
        return serverID;
    }

    private int serverID;

    public Server(double bandwidth, int id) {
        super();
        serverID = id;
        networkCard = new Router(this, 9999999, bandwidth);
    }

    @Override
    public double getDownloadBW() {
        return -1;   //infinite!
    }

    @Override
    public double getUploadBW() {
        return networkCard.getUploadBW();
    }

    @Override
    public void messageReceived(Peer from, Message msg) {
        switch (msg.getType()) {
            case PullBasedPeerController.MESSAGE_CONNECT_REQ:             //收到连接请求消息
                //      Output.printP2PEvent("peer" + this.getPeerID() + "(Server"+serverID+") received the connection request from peer" + from.getPeerID());
                ackConnection(from);
                break;
/*            case P2PEventController.MESSAGE_CONNECT_ACK:           //服务器不可能收到这个消息！！

                Output.printP2PEvent("illegal!! (Server"+serverID+") can't receive a connection acknowledgement from any peers!!!");

                break;
            case P2PEventController.MESSAGE_CHUNK_OFFER:

              //impossible!
                break;*/

            case PullBasedPeerController.MESSAGE_CHUNK_SELECT:

                sendSelectedChunks(from, (SelectMessage) msg);
                break;

            default:
                break;
        }

    }

    @Override
    public void chunkReceived(Peer from, Chunk chunk) {

    }


    @Override
    public void ackConnection(Peer from) {
        //根据自身状况决定是否接受连接
        Message msgConAck = new Message(P2PEventController.MESSAGE_CONNECT_ACK);
        sending(from, msgConAck);
    }

    @Override
    public void connect(Peer to) {
        //unsupported operation!
    }

    @Override
    public void confirmConnection(Peer from) {
        //unsupported operation!
    }

    @Override
    public boolean checkConnectivity(Peer from, Message msg) {
        return true;
    }

    private void sendSelectedChunks(Peer from, SelectMessage sm) {
        Iterator<Integer> iterator = sm.getSelectIterator();
        //System.out.println("!!!S");
        while (iterator.hasNext()) {
            int tmp = iterator.next();
            //     System.out.println("Server is sending chunk"+tmp+" to peer"+from.getPeerID());
            sending(from, Repository.getChunkByID(sm.getWhich(), tmp));
        }
    }

    public double getUtilizationRate() {
        return 1 - networkCard.totalIdleTime() / State.getEnd();
    }
}
