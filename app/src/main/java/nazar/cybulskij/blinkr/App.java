package nazar.cybulskij.blinkr;

import android.app.Application;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import io.fabric.sdk.android.Fabric;
import nazar.cybulskij.blinkr.model.Comment;
import nazar.cybulskij.blinkr.model.Feed;


/**
 * Created by nazar on 01.10.15.
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        ParseObject.registerSubclass(Feed.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parseAppID), getString(R.string.parseClientID));



        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("PARSE", "Successfully subscribed to Parse!");
            }
        });

        if (ParseUser.getCurrentUser()==null){
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e==null){
                        ParseInstallation curent = ParseInstallation.getCurrentInstallation();
                       // curent.setObjectId("owner");
                        curent.put("username", ParseUser.getCurrentUser().getUsername());
                        curent.saveInBackground();

                    }
                }
            });
        }else{
            ParseInstallation curent = ParseInstallation.getCurrentInstallation();
          //  curent.setObjectId("owner");
            curent.put("username", ParseUser.getCurrentUser().getUsername());
            curent.saveInBackground();
        }




    }
}
