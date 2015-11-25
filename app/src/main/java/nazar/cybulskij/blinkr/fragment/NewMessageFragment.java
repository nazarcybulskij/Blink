package nazar.cybulskij.blinkr.fragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.AutocompleteFilter;
import com.parse.ParseACL;
import com.parse.ParseUser;
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


    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    private CharSequence tmp;

    @Bind(R.id.messegetext)
    LimitedEditText mTextMessage;
    @Bind(R.id.plate)
    AutoCompleteTextView mPlate;
    // @Bind(R.id.ratingBar)
    // RatingBar mRatting;
    @Bind(R.id.reportButton)
    ImageButton reportButton;
    private PopupMenu popupMenu;

    public NewMessageFragment() {
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
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_message, container, false);
        ButterKnife.bind(this, view);
        String[] licenceShtats = getResources().getStringArray(R.array.Shtats_for_licelce);
        LicenseFragment.autoCompleteCreator(getActivity(), mPlate, licenceShtats);
        mPlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        LicenseFragment.hideKeyboard(getActivity());
        mTextMessage.setMaxTextSize(411);
        mTextMessage.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        mPlate.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mPlate.length() == 4) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        LicenseFragment.setFirstTwoChar(mPlate, tmp);
                        return true;
                    }
                }
                return false;
            }
        });
        mPlate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mPlate.getText().toString().trim().length() >= 4) {
                    Drawable indiactor = getResources().getDrawable(R.drawable.greencheck);
                    mPlate.setCompoundDrawablesWithIntrinsicBounds(null, null, indiactor, null);
                } else {
                    mPlate.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }
                tmp = s;
                if (s.length() == 3) {
                    LicenseFragment.setTextWithDash(mPlate, tmp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        reportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(getActivity(), reportButton);
                popupMenu.getMenuInflater().inflate(R.menu.poupup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                        mTextMessage.setText(item.getTitle());
                        mTextMessage.setSelection(mTextMessage.length());
                        return true;
                    }
                });
                LicenseFragment.hideKeyboard(getActivity());
                popupMenu.show();
            }
        });


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
    }
    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    @OnClick(R.id.post)
    public void onPostMessage(View v) {
        String plate = mPlate.getText().toString();
//        mPlate.setText("");
        String text = mTextMessage.getText().toString();
//        mTextMessage.setText("");
        //  float rate = mRatting.getRating();
        if ((plate.trim().length() <= 3) || (text.trim().equals(""))) {
            Toast.makeText(getActivity(),"Please check that you enter license plate and message!",Toast.LENGTH_LONG).show();
            return;
        }
        Feed feedSave = new Feed();
        feedSave.setLicense(plate);
        feedSave.setStatus(text);
        feedSave.put("fromUser", ParseUser.getCurrentUser());
        MainActivity activity = (MainActivity) getActivity();
        feedSave.setLocation(MainActivity.geoPointFromLocation(activity.getLastLocation()));
        feedSave.setCommentsNumber(0);
        feedSave.put("Reports", 0);
        //feedSave.setRating(rate/10.0);
        ParseACL acl = new ParseACL();
        // Give public read access
        acl.setPublicReadAccess(true);
        acl.setPublicWriteAccess(true);
        feedSave.setACL(acl);
        feedSave.saveInBackground();
        LeftIconClick();
        LicenseFragment.hideKeyboard(getActivity());
    }
    @OnClick(R.id.left_icon)
    public void LeftIconClick() {
        ViewPager vp = (ViewPager) getActivity().findViewById(R.id.container_view_pager);
        vp.setCurrentItem(vp.getCurrentItem() - 1);
        LicenseFragment.hideKeyboard(getActivity());
    }
}
