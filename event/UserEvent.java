package osnvodsim.event;

import osnvodsim.interactivity.User;

public class UserEvent extends Event {

/*	public static final int EVENT_USER_ARRIVE=0;
    public static final int EVENT_USER_LEAVE=1;
    public static final int EVENT_USER_NEXT_VIDEO=2;*/
/*	public UserEvent(double time) {
		super(time);
	}*/

    private User who;

    public UserEvent(double time, int type, User who) {
        super(time, type);
        this.who = who;
    }

    public User getWho() {
        return who;
    }
/*	@Override
	public boolean process()
	{

		switch(type)
		{
			case EVENT_USER_ARRIVE:
                Output.printUAEvent("Event"+eventID+"  Time: " + State.watchTime() + " a new user is coming! ");
                Interactivity.nextVideo(who);
				Interactivity.nextUser();
				break;
			case EVENT_USER_LEAVE:
                Output.printUAEvent("Event"+eventID+"  Time: " + State.watchTime() + " user" + who.getUserID() + " has watched " + who.getWatchedNum() + " videos and exited!");
                P2PNetwork.dropService(who);
                break;
            case EVENT_USER_NEXT_VIDEO:
                Output.printUAEvent("Event"+eventID+"  Time: " + State.watchTime() + " user" + who.getUserID() + " has finished the video" + who.getPlayingVideo().getVideoID() + "");
                Interactivity.nextVideo(who);
                break;
		
		
		}

		return true;	
	}*/
}
