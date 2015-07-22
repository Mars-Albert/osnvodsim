package osnvodsim.distribution;

import osnvodsim.config.Configuration;
import osnvodsim.video.Video;

/**
 * Created by Guowei on 2014/8/28.
 */
public class OverlayFactory {
    public static final String RANDOM_OVERLAY = "random";
    private static String overlayType;

    public static void init() {
        overlayType = Configuration.getString("overlay_type");
        if (overlayType.equals(RANDOM_OVERLAY)) {
            RandomOverlay.init();
        }
    }


    //可改进！用工厂模式改进
    public static Overlay genOverlay(Video video) {
        if (overlayType.equals(RANDOM_OVERLAY)) {
            return new RandomOverlay(video);
        } else
            return null;
    }


}
