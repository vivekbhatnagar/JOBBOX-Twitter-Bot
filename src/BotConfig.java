package TweetBot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class BotConfig {

    private static BotConfig _singleton;

    public static synchronized BotConfig getInstance() {
        if (_singleton == null) {
            _singleton = new BotConfig();
        }
        return _singleton;
    }    
    
	private Twitter _twitter;
    private Configuration _twitter_conf;
    private String[] _tweet_filter;
    private String[] _tweet_filter_K1;
    private String[] _tweet_filter_K2;
    private long[] _tweet_track;
    private String[] _status_updates;
    private static final String STR_TWEET_FILTER_SEPATOR = "###";    
	private static final String PATH_OAUTH = "./config/oauth";
    private static final String PATH_TWEET_FILTER = "./config/tweet_filter";
    private static final String PATH_TWEET_TRACK = "./config/tweet_track";    
    private static final String PATH_STATUS_UPDATES = "./config/status_update";    
    

    private BotConfig() {        
		InitTwitter();
        InitTweetFilter();
        InitTweetTrack();
        InitStatusUpdate();
    }
	
	
	private void InitTwitter() {
        Path file = Paths.get(PATH_OAUTH);
        try {
            InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
					
			ConfigurationBuilder cb = new ConfigurationBuilder();
			 cb.setDebugEnabled(true)
			   .setOAuthConsumerKey(reader.readLine())
			   .setOAuthConsumerSecret(reader.readLine())
			   .setOAuthAccessToken(reader.readLine())
			   .setOAuthAccessTokenSecret(reader.readLine());
                 
			_twitter_conf = cb.build();
			_twitter = new TwitterFactory(_twitter_conf).getInstance();		

		} catch (IOException x) {
            System.err.println(x);
        }
                
    }
	
    private void InitTweetFilter() {
        Path file = Paths.get(PATH_TWEET_FILTER);
        try {
            InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            String line;            
            //Build the K1********
            ArrayList<String> tweetFilter = new ArrayList<>();            
            while (true){
                line = reader.readLine(); 
                if(line.startsWith(STR_TWEET_FILTER_SEPATOR))
                    break;                   
                tweetFilter.add("#" + line.toLowerCase() + " ");
            }
            _tweet_filter_K1 = tweetFilter.toArray(new String[tweetFilter.size()]);
            
            //Build the K2*******
            tweetFilter = new ArrayList<>();
            while (true){
                line = reader.readLine(); 
                if(line == null)break;                   
                tweetFilter.add(line.toLowerCase());
            }            
            _tweet_filter_K2 = tweetFilter.toArray(new String[tweetFilter.size()]);
            
            //Merge K1 and K2, get all********
            _tweet_filter = new String[_tweet_filter_K1.length + _tweet_filter_K2.length];
            System.arraycopy(_tweet_filter_K1, 0, _tweet_filter, 0, _tweet_filter_K1.length);
            System.arraycopy(_tweet_filter_K2, 0, _tweet_filter, _tweet_filter_K1.length, _tweet_filter_K2.length);

        } catch (IOException x) {
            System.err.println(x);
        }
    }
    
    private void InitTweetTrack() {
        ArrayList<String> trackListName = new ArrayList<>();        

        Path file = Paths.get(PATH_TWEET_TRACK);
        try {
            //Read the twitter user names
            InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            
            String line;
            while ((line = reader.readLine()) != null) {                
                trackListName.add(line);
            }
            String[] trackArrayName =
                    trackListName.toArray(new String[trackListName.size()]);             
            //Lookup the Ids, from the usernames
            Twitter twitter = new TwitterFactory(_twitter_conf).getInstance();  
            ResponseList<User> users = twitter.lookupUsers(trackArrayName);
            ArrayList<Long> trackListId = new ArrayList<>();
            for (User user : users) {                
                trackListId.add(Long.valueOf(user.getId()));
            }
            //Convert to array
            _tweet_track = new long[trackListId.size()];
            for(int i=0; i<_tweet_track.length; i++){
                _tweet_track[i] = trackListId.get(i).longValue();
            }           
            
        } catch (Exception x) {
            System.err.println(x);
        }
    }

    private void InitStatusUpdate() {
        ArrayList<String> statusUpdates = new ArrayList<>();

        Path file = Paths.get(PATH_STATUS_UPDATES);
        try {
            InputStream in = Files.newInputStream(file);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(in));
            
            String line;
            while ((line = reader.readLine()) != null) {
                statusUpdates.add(line);
            }

        } catch (IOException x) {
            System.err.println(x);
        }
        
        _status_updates = statusUpdates.toArray(new String[statusUpdates.size()]);
    }
    
	public Twitter getTwitter(){return _twitter;}
    public Configuration getTwitterConfig(){return _twitter_conf;}
    public String[] getTweetFilter(){return _tweet_filter_K1;}        
    public String[] getTweetFilterK1(){return _tweet_filter_K1;}    
    public String[] getTweetFilterK2(){return _tweet_filter_K2;}
    public long[] getTweetTrack(){return _tweet_track;}
    public String[] getStatusUpdates(){return _status_updates;}
}
