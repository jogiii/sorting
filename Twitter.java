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
        // NOTE: do NOT add self here. Seeing your own tweets must be an
        // unconditional rule (handled separately in getNewsFeed), never
        // dependent on the follow/unfollow set - otherwise
        // unfollow(userId, userId) would wrongly remove self-visibility.
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
           // Skip self here - own tweets are always added separately
           // below, unconditionally. Without this guard, an explicit
           // follow(x, x) call would cause the user's own tweets to be
           // counted TWICE in the news feed.
           if(followerId == userId){
               continue;
           }
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

   public static void main(String[] args) {
       Twitter twitter = new Twitter();

       twitter.postTweet(1, 5);
       System.out.println(twitter.getNewsFeed(1));   // Expected: [5]

       twitter.follow(1, 2);
       twitter.postTweet(2, 6);
       System.out.println(twitter.getNewsFeed(1));   // Expected: [6, 5]

       twitter.unfollow(1, 2);
       System.out.println(twitter.getNewsFeed(1));   // Expected: [5]

       for (int i = 1; i <= 12; i++) {
           twitter.postTweet(1, 100 + i);
       }
       System.out.println(twitter.getNewsFeed(1));   // Expected: [112..103]

       // Regression test: self-follow/unfollow must NOT duplicate or
       // hide the user's own tweets.
       Twitter twitter2 = new Twitter();
       twitter2.postTweet(1, 100);
       twitter2.follow(1, 1);
       System.out.println(twitter2.getNewsFeed(1));  // Expected: [100]
       twitter2.unfollow(1, 1);
       System.out.println(twitter2.getNewsFeed(1));  // Expected: [100]
   }

}

