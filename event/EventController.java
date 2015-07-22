package osnvodsim.event;

/**
 * Created by Guowei on 2014/9/10.
 */
public interface EventController {


    //事件处理的跳转逻辑接口。
    boolean process(Event event);
}
