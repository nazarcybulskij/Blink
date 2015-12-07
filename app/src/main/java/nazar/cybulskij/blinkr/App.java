package nazar.cybulskij.blinkr;

import android.support.multidex.MultiDexApplication;
import android.util.Log;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import nazar.cybulskij.blinkr.model.Comment;
import nazar.cybulskij.blinkr.model.Feed;


/**
 * Created by nazar on 01.10.15.
 */
public class App extends MultiDexApplication {

    private static final String USERNAME_KEY= "username";
    private static final String LOG_KEY= "PARSE";


    @Override
    public void onCreate() {
        super.onCreate();
//>>>>>>> 34bf2508fb09ca4c852f27143ff7cbdd50b0bc66
        ParseObject.registerSubclass(Feed.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parseAppID), getString(R.string.parseClientID));
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(LOG_KEY, "Successfully subscribed to Parse!");
            }
        });
        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        saveUsername();
                    }
                }
            });
        } else {
            saveUsername ();
        }
    }

    private void saveUsername () {
        ParseInstallation curent = ParseInstallation.getCurrentInstallation();
        curent.put(USERNAME_KEY, ParseUser.getCurrentUser().getUsername());
        curent.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e(LOG_KEY, "Successfully saved username!");
            }
        });
    }
}
