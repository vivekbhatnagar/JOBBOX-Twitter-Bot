package TweetBot.Stream;

import TweetBot.BotConfig;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import twitter4j.Status;
import twitter4j.Twitter;

public class Adapter {
    
    private enum BotAction {
        TWEET,
        RETWEET,
        FAVORITE,
        IGNORE
    }
    
    private int KeywordMatchCount(String input, String keywords[]){
        int count = 0;                
        for(String keyword : keywords){
            if(input.contains(keyword))count++;
        }        
        return count;
    }
    
    private boolean KeywordMatchCount(long user, long ids[]){        
        for(long id : ids){
            if(user == id )return true;
        }        
        return false;
    }
    
    private double Classification(Status status)
    {
        BotConfig config = BotConfig.getInstance();
        
        int k1 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK1());
        int k2 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK2());
        int follow = KeywordMatchCount(status.getUser().getId(), config.getTweetTrack()) ? 2 : 1;
        
        return (Math.pow(k1, 2)) * k2 * follow;        
    }
    
    private BotAction Action(Status status){
        double classification = Classification(status);
        if(classification >= 8)
            return BotAction.RETWEET;
        else if(classification >= 4)
            return BotAction.TWEET;
        else if(classification > 0)
            return BotAction.FAVORITE;
        else
            return BotAction.IGNORE;
    }
    
    private void ActionLog(Status status)
    {
        BotConfig config = BotConfig.getInstance(); 
        BotAction action = Action(status);
        double classification = Classification(status);
        
        int k1 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK1());
        int k2 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK2());
        int follow = KeywordMatchCount(status.getUser().getId(), config.getTweetTrack()) ? 2 : 1;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();  
        
        System.out.println(dateFormat.format(date) + "*************************************************************************************************************");
        System.out.println("TWEET: " +status.getText());
        System.out.println("USER:  @" + status.getUser().getScreenName() + " - " + status.getUser().getId());                
        System.out.print("\taction: " + action.toString());
        System.out.println(" (score:  " + ((int)classification) + ") ");
        System.out.print("\tdetail: ");
        System.out.print("[k1: " + k1 + "] ");
        System.out.print("[k2: " + k2 + "] ");
        System.out.println("[follow: " + follow + "]\n");
    }
    
    private void ActionExecute(Status status)            
    {
        Twitter twitter =BotConfig.getInstance().getTwitter();
        BotAction action = Action(status);
        try{
        switch(action){
            case TWEET:
                twitter.updateStatus(status.getText());
                break;
            case RETWEET:
                twitter.retweetStatus(status.getId());
                break;
            case FAVORITE:
                twitter.createFavorite(status.getId());
                break;
            default:
                break;
        }
        }catch(Exception e){
            e.printStackTrace();
        }
            
    }
    
    public void input(Status status){
        
        if(status.isRetweet())
            return;
        
        ActionLog(status);
        ActionExecute(status);
    }    
}
