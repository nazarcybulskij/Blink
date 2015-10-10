package nazar.cybulskij.blinkr.fragment;

/**
 * Created by nazar on 26.09.15.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import info.hoang8f.android.segmented.SegmentedGroup;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.FeedAdapter;
import nazar.cybulskij.blinkr.events.FeedEvent;
import nazar.cybulskij.blinkr.model.Feed;
import nazar.cybulskij.blinkr.model.MessagesEnum;

/**
 * A placeholder fragment containing a simple view.
 */
public  class MessagesListFragment extends Fragment {

    @Bind(R.id.list)
    ListView mListview;
    FeedAdapter mFeedAdapter;
    MessagesEnum state = MessagesEnum.NEABY;
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
    public static MessagesListFragment newInstance(int sectionNumber) {
        MessagesListFragment fragment = new MessagesListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public MessagesListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);


        SegmentedGroup segmented = (SegmentedGroup) rootView.findViewById(R.id.segmented);
        segmented.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.one) {
                    state = MessagesEnum.NEABY;
                } else {
                    state = MessagesEnum.RECENT;
                }
                doListQuery();
            }
        });



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
//        final RealmResults<Message> users = realm.where(Message.class).findAll();
//        users.sort("id", RealmResults.SORT_ORDER_DESCENDING);
//        mAdapter = new MessageAdapter(getActivity(),users,true);

        if (mListview.getHeaderViewsCount() == 0) {
            View header = View.inflate(getActivity(), R.layout.header_layout, null);
            mListview.addHeaderView(header);
        }

        //mListview.setAdapter(mAdapter);




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
                        // Set up additional query filters
                        if (state == MessagesEnum.NEABY)
                            query.whereWithinMiles("location", myPoint, 15.0);
                        query.whereLessThanOrEqualTo("Reports", 2);
                        query.orderByDescending("createdAt");
                        query.setLimit(MainActivity.MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        if (mFeedAdapter == null) {
            mFeedAdapter = new FeedAdapter(getActivity(), factory);
            // Disable automatic loading when the adapter is attached to a view.
            mFeedAdapter.setAutoload(false);

            // Disable pagination, we'll manage the query limit ourselves
            mFeedAdapter.setPaginationEnabled(false);
            mListview.setAdapter(mFeedAdapter);
        } else {
            mListview.setAdapter(mFeedAdapter);
        }



        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, MessageFragment.newInstance(), "message");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                EventBus.getDefault().postSticky(new FeedEvent(mFeedAdapter.getItem(position - 1)));


            }
        });


        if (mListInstanceState != null) {
            mListview.onRestoreInstanceState(mListInstanceState);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
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
        MainActivity activity = (MainActivity) getActivity();
        Location myLoc = (activity.getCurrentLocation() == null) ? activity.getLastLocation() : activity.getCurrentLocation();
        // If location info is available, load the data
        if (myLoc != null) {
            // Refreshes the list view with new data based
            // usually on updated location data.
            mFeedAdapter.loadObjects();
        }
    }
}
