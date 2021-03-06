package nazar.cybulskij.blinkr.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.cocosw.bottomsheet.BottomSheet;
import com.digits.sdk.android.Digits;
import com.parse.ParseInstallation;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nazar.cybulskij.blinkr.App;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.DrawerAdapter;
import nazar.cybulskij.blinkr.util.Utils;


/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerSettingsFragment extends Fragment {

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
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;


    public NavigationDrawerSettingsFragment() {
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
        selectItem(-1, mCurrentSelectedPosition);
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

        View v = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        ButterKnife.bind(this, v);
        mDrawerListView = (ExpandableListView) v.findViewById(R.id.list);

        //Создаем набор данных для адаптера
        ArrayList<ArrayList<String>> groups = new ArrayList<ArrayList<String>>();
        ArrayList<String> children1 = new ArrayList<String>();
        ArrayList<String> children2 = new ArrayList<String>();
        ArrayList<String> children3 = new ArrayList<String>();
        ArrayList<String> children4 = new ArrayList<>();
        children1.add("My Posts");
        children1.add("My Comments");
        children1.add("My Messages");
        children1.add("My License Plate #");
        groups.add(children1);
        children2.add("Share Blinkr");
        children2.add("Rate Blinkr");

        groups.add(children2);
        children3.add("About Blinkr");
        children3.add("Rules and Regulations");
        children3.add("Terms of Service");
        children3.add("Privacy Policy");
        groups.add(children3);
        children4.add("Log Out");
        groups.add(children4);

        ArrayList<String> titles = new ArrayList<>();
        titles.add("MY STUFF");
        titles.add("FUN STUFF");
        titles.add("SERIOUS STUFF");
        titles.add("");


        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v,
                                        int groupPosition, long id) {
                mDrawerListView.expandGroup(groupPosition);
                return true; // This way the expander cannot be collapsed
            }
        });

        DrawerAdapter adapter = new DrawerAdapter(getActivity(), groups, titles);

        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                if (groupPosition == 1 && childPosition == 0) {
                    utils.saveFile(utils.getBitmapFromView(getView()));
                    showSheet();
                } else {
                    selectItem(groupPosition, childPosition);
                }
                return false;
            }
        });
        View footer = View.inflate(getActivity(), R.layout.footer_drawer, null);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Create the Intent */
                final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);

                /* Fill it with Data */
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"support@blinkrapp.com"});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                /* Send it off to the Activity-Chooser */
                getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });

        mDrawerListView.addFooterView(footer);


        mDrawerListView.setAdapter(adapter);


        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return v;
    }


    public void showSheet() {

        new BottomSheet.Builder(getActivity(), R.style.BottomSheet_Dialog)
                .grid() // <-- important part
                .sheet(R.menu.menu_bottom_sheet)
                .listener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        utils.saveFile(utils.drawableToBitmap(getResources().getDrawable(R.drawable.loading)));

                        switch (which) {

                            case R.id.Email:
                                shareEmail();
                                break;
                            case R.id.Facebook:
                                shareFacebook();
                                break;
                            case R.id.Twitter:
                                shareTwitter();

                                break;
                            case R.id.Whatsapp:
                                shareWhatsapp();
                                break;


                        }

                    }
                }).show();


    }

    Utils utils = new Utils();

    private void shareEmail() {

        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath = new File(filename);  //optional //internal storage
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        emailIntent.setType("image/jpeg");
        emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                /* Fill it with Data */
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{""});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                /* Send it off to the Activity-Chooser */
        getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    private void shareFacebook() {


        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath = new File(filename);  //optional //internal storage
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("facebook")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                getActivity().startActivity(shareIntent);
                break;
            }
        }

    }


    private void shareTwitter() {
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath = new File(filename);  //optional //internal storage
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("twitter"))//"com.twitter.android.PostActivity".equals(app.activityInfo.name)
            {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                getActivity().startActivity(shareIntent);
                break;
            }
        }

    }

    private void shareWhatsapp() {

        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath = new File(filename);  //optional //internal storage
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);


        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList) {
            if ((app.activityInfo.name).contains("com.whatsapp")) {
                final ActivityInfo activity = app.activityInfo;
                final ComponentName name = new ComponentName(
                        activity.applicationInfo.packageName, activity.name);
                shareIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                shareIntent.setComponent(name);
                getActivity().startActivity(shareIntent);
                break;
            }
        }

    }


    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    @OnClick(R.id.rigth_icon)
    public void RigthIconClick() {
        selectItem(-1, 0);

        //mDrawerLayout.closeDrawer(Gravity.LEFT);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.stub,  /* "open drawer" description for accessibility */
                R.string.stub  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()


            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().invalidateOptionsMenu(); // calls onPrepareOptionsMenu()

            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void selectItem(int grooup, int childr) {


        mCurrentSelectedPosition = childr;


        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(childr, true);
        }
        filter(grooup, childr);

    }


    public void filter(int group, int childr) {
        if (group == 1 && childr == 1) {
            //rate  Blinckr
            final String appPackageName = getActivity().getPackageName(); // getPackageName() from Context or Activity object
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }


            return;
        }

        if (group == 2 && childr == 2) {
            //terms
            String url = "www.blinkrapp.co/tos";
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            return;
        }

        if (group == 2 && childr == 3) {
            //privace
            String url = "www.blinkrapp.co/privacy";
            if (!url.startsWith("http://") && !url.startsWith("https://"))
                url = "http://" + url;

            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(browserIntent);

            return;
        }


        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(group, childr);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
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
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int group, int childr);
    }
}
