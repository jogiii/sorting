import java.util.*;

class Tweet implements Comparable<Tweet>{
    int time;
    int tweetId;

    Tweet(int tweetId, int time){
        this.tweetId = tweetId;
        this.time = time;
    }

    @Override
    public int compareTo(Tweet that) {
        return that.time - this.time;
    }
}

class User{
    int userId;
    HashSet<Integer> followers;
    List<Tweet> tweets;

    User(int userId){
        this.userId = userId;
        this.followers = new HashSet<>();
        this.tweets = new LinkedList<>();
    }

    public void addTweet(Tweet tweet){
        tweets.add(0,tweet);
    }

    public void addFollower(Integer followerId){
        followers.add(followerId);
    }

    public void removeFollower(Integer followerId) {
        followers.remove(followerId);
    }
}
public class Twitter {

   HashMap<Integer, User> userMap;
   int timeCounter;

   public Twitter(){
       userMap = new HashMap<>();
       timeCounter = 0;
   }

   public void postTweet(int userId, int tweetId){
       timeCounter++;
       if(! userMap.containsKey(userId)){
           userMap.put(userId,new User(userId));
       }
       User user = userMap.get(userId);
       user.addTweet(new Tweet(tweetId,timeCounter));
   }

   public List<Integer> getNewsFeed(int userId){
       if(! userMap.containsKey(userId)){
           return new ArrayList<>();
       }

       PriorityQueue<Tweet> pq = new PriorityQueue<>();
       User user = userMap.get(userId);

       for(int followerId : user.followers){
           int count = 0;
           for(Tweet tweet : userMap.get(followerId).tweets){
               pq.offer(tweet);
               count++;
               if(count > 10){
                   break;
               }
           }
       }
       int count = 0;
       for(Tweet tweet : user.tweets){
           pq.offer(tweet);
           count++;
           if(count > 10){
               break;
           }
       }

       List<Integer> res = new ArrayList<>();
       int index =0;
       while(!pq.isEmpty() && index <10){
            Tweet tweet = pq.poll();
            res.add(tweet.tweetId);
            index++;
       }
       return res;
   }

   public void follow(int followerId, int followeeId){
       if(!userMap.containsKey(followerId)){
           userMap.put(followerId,new User(followerId));
       }

       if(!userMap.containsKey(followeeId)){
           userMap.put(followeeId,new User(followeeId));
       }

       User user = userMap.get(followerId);
       user.addFollower(followeeId);
   }

   public void unfollow(int followerId, int followeeId){
       if(!userMap.containsKey(followerId) || !userMap.containsKey(followeeId)){
           return;
       }

       User user = userMap.get(followerId);
       user.removeFollower(followeeId);
   }




}

