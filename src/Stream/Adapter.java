package TweetBot.Stream;

import TweetBot.BotConfig;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import twitter4j.Status;

public class Adapter {
    
    private static int KeywordMatchCount(String input, String keywords[]){
        int count = 0;                
        for(String keyword : keywords){
            if(input.contains(keyword))count++;
        }        
        return count;
    }
    
    private static boolean KeywordMatchCount(long user, long ids[]){        
        for(long id : ids){
            if(user == id )return true;
        }        
        return false;
    }
    
    private static double TweetClassification(Status status)
    {
        BotConfig config = BotConfig.getInstance();
        
        int k1 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK1());
        int k2 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK2());
        int follow = KeywordMatchCount(status.getUser().getId(), config.getTweetTrack()) ? 2 : 1;
        
        return (Math.pow(k1, 2)) * k2 * follow;        
    }
    
    private static String Description(double classification)
    {
        if(classification >= 8)
            return "RE-TWEET";
        else if(classification >= 4)
            return "TWEET   ";
        else if(classification > 0)
            return "FAVORITE";
        else
            return "IGNORE  ";
    }
    
    public static void Input(Status status){
        
        if(status.isRetweet())
            return;
        
        BotConfig config = BotConfig.getInstance();
        double classification = TweetClassification(status);        
        int k1 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK1());
        int k2 = KeywordMatchCount(status.getText().toLowerCase(), config.getTweetFilterK2());
        int follow = KeywordMatchCount(status.getUser().getId(), config.getTweetTrack()) ? 2 : 1;
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();        
        
        System.out.println(dateFormat.format(date) + "************************************************");
        System.out.println("TWEET: " +status.getText());
        System.out.println("USER:  @" + status.getUser().getScreenName() + " - " + status.getUser().getId());                
        System.out.print("\taction: " + Description(classification));
        System.out.println(" (score:  " + ((int)classification) + ") ");
        System.out.print("\tdetail: ");
        System.out.print("[k1: " + k1 + "] ");
        System.out.print("[k2: " + k2 + "] ");
        System.out.println("[follow: " + follow + "]\n");
    }
    
}
