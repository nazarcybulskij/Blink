package nazar.cybulskij.blinkr.events;


import nazar.cybulskij.blinkr.model.Feed;

/**
 * Created by nazar on 04.10.15.
 */
public class FeedEvent {

    Feed feed;


    public FeedEvent(Feed feed) {
        this.feed = feed;
    }


    public Feed getFeed() {
        return feed;
    }

    public void setFeed(Feed feed) {
        this.feed = feed;
    }


}
