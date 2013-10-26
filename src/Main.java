package TweetBot;
import java.util.ArrayList;
import twitter4j.FilterQuery;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.*;

import TweetBot.Stream.Listener;



public class Main {
	  /**
     * Usage: java  twitter4j.examples.oauth.GetAccessToken [consumer key] [consumer secret]
     *
     * @param args message
     */	
   
    public static void main(String[] args) throws TwitterException {
    	
        BotConfig bot = BotConfig.getInstance();
                
        TwitterStream twitterStream = new TwitterStreamFactory(bot.getTwitterConfig()).getInstance();
        twitterStream.addListener(new Listener());      
        twitterStream.filter(new FilterQuery(0, bot.getTweetTrack(), bot.getTweetFilter()));        
    }
}

