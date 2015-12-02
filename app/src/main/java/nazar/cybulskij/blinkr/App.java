package nazar.cybulskij.blinkr;

import android.app.Application;
import android.support.multidex.MultiDexApplication;
import android.util.Log;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
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
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import nazar.cybulskij.blinkr.model.Comment;
import nazar.cybulskij.blinkr.model.Feed;


/**
 * Created by nazar on 01.10.15.
 */
public class App extends MultiDexApplication {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "hluqFPKtfdEeE9cVoXZsksjdE";
    private static final String TWITTER_SECRET = "G5pIcD45KmDnoKbeOl2jA6qhyc4t77DZE5yPpHz81QmGDiygMq";
    public static AuthCallback authCallback;
    public static String phoneNumberId;
    public static boolean isLogIn;


    @Override
    public void onCreate() {
        super.onCreate();

        isLogIn = false;
        phoneNumberId = "";

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Crashlytics(), new TwitterCore(authConfig), new Digits());
        Fabric.with(this, new TwitterCore(authConfig), new Digits());

        ParseObject.registerSubclass(Feed.class);
        ParseObject.registerSubclass(Comment.class);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, getString(R.string.parseAppID), getString(R.string.parseClientID));


        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String userID = installation.getString("phoneNumber");
        if (userID != null && !userID.equals("0")) {
            phoneNumberId = userID;
            isLogIn = true;
        }

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.e("PARSE", "Successfully subscribed to Parse!");
            }
        });

        if (ParseUser.getCurrentUser() == null) {
            ParseAnonymousUtils.logIn(new LogInCallback() {
                @Override
                public void done(ParseUser user, ParseException e) {
                    if (e == null) {
                        ParseInstallation curent = ParseInstallation.getCurrentInstallation();
                        // curent.setObjectId("owner");
                        curent.put("username", ParseUser.getCurrentUser().getUsername());
                        curent.saveInBackground();
                    }
                }
            });
        } else {
            ParseInstallation curent = ParseInstallation.getCurrentInstallation();
            //  curent.setObjectId("owner");
            curent.put("username", ParseUser.getCurrentUser().getUsername());
            curent.saveInBackground();
        }

        authCallback = new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("phoneNumber", phoneNumber);
                installation.saveInBackground();
                String userID = installation.getString("phoneNumber");
                if (userID != null) {
                    phoneNumberId = userID;
                    isLogIn = true;
                }
            }

            @Override
            public void failure(DigitsException exception) {
                Log.i("LogIn", "failure: " + exception.toString());
            }
        };
    }
    public  static AuthCallback getAuthCallback(){
        return authCallback;
    }
    public static void logOut(){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("phoneNumber", "0");
        installation.put("license","");
        installation.saveInBackground();
        isLogIn = false;
    }
}
