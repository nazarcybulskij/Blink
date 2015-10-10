package nazar.cybulskij.blinkr.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseACL;
import com.parse.ParseUser;

import java.lang.reflect.Field;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.model.Feed;
import nazar.cybulskij.blinkr.view.LimitedEditText;


;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NewMessageFragment extends Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";


    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    @Bind(R.id.messegetext)
    LimitedEditText mTextMessage;
    @Bind(R.id.plate)
    EditText mPlate;
    @Bind(R.id.ratingBar)
    RatingBar mRatting;



    public  NewMessageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_navigation_drawer_new_message, container, false);
        ButterKnife.bind(this, view);
        mTextMessage.setMaxTextSize(200);
        return view;
    }









    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }


    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */






    @OnClick(R.id.post)
    public void onPostMessage(View v){



        String plate = mPlate.getText().toString();
        String text = mTextMessage.getText().toString();
        float rate = mRatting.getRating();






        Feed feedSave = new Feed();
        feedSave.setLicense(plate);
        feedSave.setStatus(text);
        feedSave.put("fromUser", ParseUser.getCurrentUser());
        MainActivity activity = (MainActivity)getActivity();
        feedSave.setLocation(MainActivity.geoPointFromLocation(activity.getLastLocation()));
        feedSave.setCommentsNumber(0);
        feedSave.put("Reports",0);
        feedSave.setRating(rate/10.0);

        ParseACL acl = new ParseACL();

        // Give public read access
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        feedSave.setACL(acl);

        feedSave.saveInBackground();

        MessagesListFragment fragment = (MessagesListFragment)getActivity().getFragmentManager().findFragmentByTag("messages");
        if (fragment!=null){
            fragment.doListQuery();
        }






    }
}