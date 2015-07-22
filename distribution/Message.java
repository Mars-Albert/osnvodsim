package osnvodsim.distribution;

import osnvodsim.config.Configuration;

/**
 * Created by Guowei on 2014/8/29.
 */
public class Message {

    public static double MESSAGE_SIZE;
    private static long count = 0;
    protected final long msgID = count++;
    private int type;

    public Message(int type) {
        this.type = type;
    }

    public static void init() {
        count = 0;
        MESSAGE_SIZE = Configuration.getDouble("message_size");
        //   OfferMessage.init();
    }

    public long getMsgID() {
        return msgID;
    }

    public int getType() {
        return type;
    }


}
