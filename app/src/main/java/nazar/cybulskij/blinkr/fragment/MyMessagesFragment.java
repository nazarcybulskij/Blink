package nazar.cybulskij.blinkr.fragment;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.FeedAdapter;
import nazar.cybulskij.blinkr.events.FeedEvent;
import nazar.cybulskij.blinkr.model.Feed;
import nazar.cybulskij.blinkr.model.MessagesEnum;

/**
 * Created by Tarasik on 31.12.2015.
 */
public class MyMessagesFragment extends ListFragment {

    ListView mListview;
    FeedAdapter mFeedAdapter;
    private final String LIST_VIEW_INSTANCE_STATE_KEY = "LIST_VIEW_INSTANCE_STATE_KEY";
    Parcelable mListInstanceState;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static MyMessagesFragment newInstance(int license) {
        MyMessagesFragment fragment = new MyMessagesFragment();
        Bundle args = new Bundle();
        args.putInt("license",license);
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_my_message, container, false);
        ButterKnife.bind(this, rootView);


        return rootView;
    }


    @Override
    public void onPause() {
        // Save ListView state @ onPause
        mListInstanceState = mListview.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mListview = getListView();
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.message_container, MessageFragment.newInstance(), "message");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                EventBus.getDefault().postSticky(new FeedEvent(mFeedAdapter.getItem(position)));



            }
        });


        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Feed> factory =
                new ParseQueryAdapter.QueryFactory<Feed>() {
                    public ParseQuery<Feed> create() {
                        MainActivity activity = (MainActivity) getActivity();
                        Location myLoc = (activity.getCurrentLocation() == null) ? activity.getLastLocation() : activity.getCurrentLocation();
                        // If location info isn't available, clean up any existing markers
                        final ParseGeoPoint myPoint = MainActivity.geoPointFromLocation(myLoc);
                        // Create the map Parse query
                        ParseQuery<Feed> query = Feed.getQuery();
                        String license = ParseInstallation.getCurrentInstallation().getString("license");
                        query.whereContains("License",license);
                        // Set up additional query filters
                        query.whereLessThanOrEqualTo("Reports", 2);
                        query.orderByDescending("createdAt");
                        query.setLimit(MainActivity.MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        if (mFeedAdapter == null) {
            mFeedAdapter = new FeedAdapter(getActivity(), factory);
            // Disable automatic loading when the adapter is attached to a view.
            mFeedAdapter.setAutoload(true);

            // Disable pagination, we'll manage the query limit ourselves
            mFeedAdapter.setPaginationEnabled(false);
            mListview.setAdapter(mFeedAdapter);
        } else {
            mListview.setAdapter(mFeedAdapter);
            doListQuery();
        }




    }

    @OnClick(R.id.left_icon)
    public void LeftIconClick(){
        ((MainActivity)getActivity()).openDrawer();
    }



    @Override
    public void onStart() {
        super.onStart();
        mFeedAdapter.notifyDataSetChanged();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /*
   * Set up a query to update the list view
   */
    public void doListQuery() {
        mFeedAdapter.loadObjects();
    }

}
