package osnvodsim.distribution;

import osnvodsim.video.Video;

/**
 * Created by Guowei on 2014/9/3.
 */
public class OfferMessage extends Message {

    private Video which;

    private Bitmap chunksToOffer;


    public OfferMessage(Video which) {
        super(PullBasedPeerController.MESSAGE_CHUNK_OFFER);
        this.which = which;
    }

    public void processOfferMsg(Bitmap bm) {
        chunksToOffer = bm;
    }

    public Video getVideo() {
        return which;
    }

    public Bitmap getOffer() {
        return chunksToOffer;
    }

}
