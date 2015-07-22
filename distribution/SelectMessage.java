package osnvodsim.distribution;

import osnvodsim.video.Video;

import java.util.*;

/**
 * Created by Guowei on 2014/9/11.
 */
public class SelectMessage extends Message {

    private Video which;
    private int version;


    private Set<Integer> selectedChunks;

    public SelectMessage(Video video) {
        super(PullBasedPeerController.MESSAGE_CHUNK_SELECT);
        which = video;
        selectedChunks = new LinkedHashSet<Integer>();
        version = -1;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getVersion() {
        return version;
    }

    public void selectChunk(int chunkID) {
        selectedChunks.add(chunkID);
    }

    public Video getWhich() {
        return which;
    }

    public Iterator<Integer> getSelectIterator() {
        return selectedChunks.iterator();
    }


}
