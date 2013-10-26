package TweetBot.Stream;

import TweetBot.BotConfig;
import java.util.List;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;


import twitter4j.User;


public class Listener implements StatusListener {

    private long _last_fav;
    private Twitter _twitter;
    private Adapter _adapter;

    public Listener() {
        _twitter = new TwitterFactory(BotConfig.getInstance().getTwitterConfig()).getInstance();
        _adapter = new Adapter();
        _last_fav = 0;
        //_twitter.verifyCredentials()
        User user = null;
        try {
            user = _twitter.verifyCredentials();
            System.out.println("Logged in using @" + user.getScreenName() + "'s account.");
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            //Just a test to see if we can get the home timeline
            _twitter.getHomeTimeline();
        } catch (TwitterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }        
    }

    @Override
    public void onStatus(Status status) {        
        Adapter.Input(status);        
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
        //System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
    }

    @Override
    public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
        //System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
    }

    @Override
    public void onScrubGeo(long userId, long upToStatusId) {
        //System.out.println("Got scrub_geo event userId:" + userId + " upToStatusId:" + upToStatusId);
    }

    @Override
    public void onStallWarning(StallWarning warning) {
        //System.out.println("Got stall warning:" + warning);
    }

    @Override
    public void onException(Exception ex) {
        //ex.printStackTrace();
    }
}
