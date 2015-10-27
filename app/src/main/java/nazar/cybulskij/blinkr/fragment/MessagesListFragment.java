package nazar.cybulskij.blinkr.fragment;

/**
 * Created by nazar on 26.09.15.
 */

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.location.Location;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.ViewPager;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import info.hoang8f.android.segmented.SegmentedGroup;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.FeedAdapter;
import nazar.cybulskij.blinkr.events.FeedEvent;
import nazar.cybulskij.blinkr.events.ReloadEvent;
import nazar.cybulskij.blinkr.listener.OnChangedLocationListener;
import nazar.cybulskij.blinkr.model.Feed;
import nazar.cybulskij.blinkr.model.MessagesEnum;

/**
 * A placeholder fragment containing a simple view.
 */
public  class MessagesListFragment extends Fragment implements OnChangedLocationListener {

    @Bind(R.id.list)
    ListView mListview;

     SearchView mSearchView;
    String searchstr="";

    FeedAdapter mFeedAdapterNerby;

    FeedAdapter mFeedAdapterRecent;



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
                    mListview.setAdapter(mFeedAdapterNerby);
                } else {
                    state = MessagesEnum.RECENT;
                    mListview.setAdapter(mFeedAdapterRecent);
                }
                doListQuery();
            }
        });
        ((MainActivity)getActivity()).setListener(this);



        return rootView;
    }

    @OnClick(R.id.left_icon)
    public void LeftIconClick(){
        ((MainActivity)getActivity()).openDrawer();
    }

    @OnClick(R.id.rigth_icon)
    public void RigthIconClick(){
        ViewPager vp=(ViewPager) getActivity().findViewById(R.id.container_view_pager);
        vp.setCurrentItem(vp.getCurrentItem()+1);
    }



    @Override
    public void onPause() {
        // Save ListView state @ onPause
        mListInstanceState = mListview.onSaveInstanceState();
        super.onPause();
    }

    boolean first=true;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        if (mListview.getHeaderViewsCount() == 0) {
            View header = View.inflate(getActivity(), R.layout.header_layout, null);
            mSearchView = (SearchView)header.findViewById(R.id.searchView);



            int searchPlateId = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
            EditText searchPlate = (EditText) mSearchView.findViewById(searchPlateId);
            searchPlate.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
            searchPlate.setTextColor(getResources().getColor(R.color.orange));

            //searchPlate.setTextColor(getResources().getColor(R.color.novoda_blue));

            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchstr = query;
                    mFeedAdapterNerby.loadObjects();
                    mFeedAdapterRecent.loadObjects();
                    //Toast.makeText(getActivity(),query,Toast.LENGTH_SHORT).show();

                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if(newText.equals("")){
                        searchstr = "";
                        mFeedAdapterNerby.loadObjects();
                        mFeedAdapterRecent.loadObjects();
                    }
                    return false;
                }
            });
            mListview.addHeaderView(header);
        }





        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Feed> factorynerby =
                new ParseQueryAdapter.QueryFactory<Feed>() {
                    public ParseQuery<Feed> create() {
                        MainActivity activity = (MainActivity) getActivity();
                        Location myLoc = (activity.getCurrentLocation() == null) ? activity.getLastLocation() : activity.getCurrentLocation();
                        // If location info isn't available, clean up any existing markers
                        final ParseGeoPoint myPoint = MainActivity.geoPointFromLocation(myLoc);
                        // Create the map Parse query
                        ParseQuery<Feed> query = Feed.getQuery();
                        if (!searchstr.trim().equals("")) {
                            query.whereContains("License",searchstr);
                        }
                        // Set up additional query filters
                         query.whereWithinMiles("location", myPoint, 15.0);
                        query.whereLessThanOrEqualTo("Reports", 2);
                        query.orderByDescending("createdAt");
                        query.setLimit(MainActivity.MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };

        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Feed> factoryrecent =
                new ParseQueryAdapter.QueryFactory<Feed>() {
                    public ParseQuery<Feed> create() {
                        MainActivity activity = (MainActivity) getActivity();
                        Location myLoc = (activity.getCurrentLocation() == null) ? activity.getLastLocation() : activity.getCurrentLocation();
                        // If location info isn't available, clean up any existing markers
                        final ParseGeoPoint myPoint = MainActivity.geoPointFromLocation(myLoc);
                        // Create the map Parse query
                        ParseQuery<Feed> query = Feed.getQuery();
                        // Set up additional query filters
                        if (!searchstr.trim().equals("")) {
                            query.whereContains("License",searchstr);
                        }
                        query.whereLessThanOrEqualTo("Reports", 2);
                        query.orderByDescending("createdAt");
                        query.setLimit(MainActivity.MAX_POST_SEARCH_RESULTS);
                        return query;
                    }
                };


        if (mFeedAdapterNerby == null || mFeedAdapterRecent==null) {
            mFeedAdapterNerby = new FeedAdapter(getActivity(), factorynerby);
            // Disable automatic loading when the adapter is attached to a view.
            mFeedAdapterNerby.setAutoload(false);

            // Disable pagination, we'll manage the query limit ourselves
            mFeedAdapterNerby.setPaginationEnabled(false);

            mFeedAdapterRecent = new FeedAdapter(getActivity(), factoryrecent);
            // Disable automatic loading when the adapter is attached to a view.
            mFeedAdapterRecent.setAutoload(false);

            // Disable pagination, we'll manage the query limit ourselves
            mFeedAdapterRecent.setPaginationEnabled(false);

            if (state==MessagesEnum.NEABY )
                mListview.setAdapter(mFeedAdapterNerby);
            else
                mListview.setAdapter(mFeedAdapterRecent);

        } else {
            if (state==MessagesEnum.NEABY ) {
                mListview.setAdapter(mFeedAdapterNerby);
            }
            else{
                mListview.setAdapter(mFeedAdapterRecent);

            }


        }

        doListQuery();


        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                FragmentManager fragmentManager = getActivity().getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container, MessageFragment.newInstance(), "message");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                if (state == MessagesEnum.NEABY)
                    EventBus.getDefault().postSticky(new FeedEvent(mFeedAdapterNerby.getItem(position - 1)));
                else
                    EventBus.getDefault().postSticky(new FeedEvent(mFeedAdapterRecent.getItem(position - 1)));


            }
        });


        if (mListInstanceState != null) {
            mListview.onRestoreInstanceState(mListInstanceState);
        }


    }


    @Override
    public void onStart() {
        super.onStart();
        mFeedAdapterRecent.notifyDataSetChanged();
        mFeedAdapterNerby.notifyDataSetChanged();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);


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
            if (state==MessagesEnum.NEABY )
                mFeedAdapterNerby.loadObjects();
            else
                mFeedAdapterRecent.loadObjects();

        }
    }






    @Override
    public void onChange() {
        doListQuery();
    }

    public  void onEvent(ReloadEvent event){
        onChange();
    }
}
