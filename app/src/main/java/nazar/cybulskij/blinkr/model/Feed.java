package nazar.cybulskij.blinkr.model;

import com.parse.ParseClassName;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by nazar on 02.10.15.
 */
@ParseClassName("Feed")
public class Feed extends ParseObject {

    public String getLicense() {
        return getString("License");
    }

    public void setLicense(String value) {
        put("License", value);
    }

    public String getStatus() {
        return getString("Status");
    }

    public void setStatus(String value) {
        put("Status", value);
    }

    public Number getRating() {
        return getNumber("rating");
    }

    public void setRating(Number value) {
        put("rating", value);
    }

    public Number getCommentsNumber() {
        return getNumber("CommentsNumber");
    }

    public void setCommentsNumber(Number value) {
        put("CommentsNumber", value);
    }
    public Number getReportsCount() {
        return getNumber("Reports");
    }

    public void setReportsCount(Number value) {
        put("Reports", value);
    }



    public ParseGeoPoint getLocation() {
        return getParseGeoPoint("location");
    }

    public void setLocation(ParseGeoPoint value) {
        put("location", value);
    }

    public static ParseQuery<Feed> getQuery() {
        return ParseQuery.getQuery(Feed.class);
    }

}
