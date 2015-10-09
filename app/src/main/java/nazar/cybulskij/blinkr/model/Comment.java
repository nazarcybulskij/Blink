package nazar.cybulskij.blinkr.model;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

/**
 * Created by nazar on 04.10.15.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject {


    public String getCommentText() {
        return getString("comment");
    }

    public void setCommentText(String value) {
        put("comment", value);
    }

    public String getFeed() {
        return getString("Post");
    }

    public void setFeed(Feed value) {
        put("Post", value);
    }


    public ParseUser getUser() {
        return getParseUser("fromUser");
    }

    public void setUser(ParseUser value) {
        put("fromUser", value);
    }

    public static ParseQuery<Comment> getQuery() {
        return ParseQuery.getQuery(Comment.class);
    }
}
