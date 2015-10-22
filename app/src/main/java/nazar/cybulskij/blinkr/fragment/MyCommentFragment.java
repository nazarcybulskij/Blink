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

import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;

import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.CommentAdapter;
import nazar.cybulskij.blinkr.adapter.FeedAdapter;
import nazar.cybulskij.blinkr.events.FeedEvent;
import nazar.cybulskij.blinkr.model.Comment;
import nazar.cybulskij.blinkr.model.Feed;
import nazar.cybulskij.blinkr.model.MessagesEnum;

/**
 * Created by nazar on 11.10.15.
 */
public class MyCommentFragment extends ListFragment {



    ListView mListview;
    CommentAdapter mCommentsAdapter;
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
    public static MyCommentFragment newInstance(int userid) {
        MyCommentFragment fragment = new MyCommentFragment();
        Bundle args = new Bundle();


        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setRetainInstance(true);
        View rootView = inflater.inflate(R.layout.fragment_my_comments, container, false);
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


        // Set up a customized query
        ParseQueryAdapter.QueryFactory<Comment> factory =
                new ParseQueryAdapter.QueryFactory<Comment>() {
                    public ParseQuery<Comment> create() {
                        ParseQuery<Comment> query = Comment.getQuery();
                        ParseUser user = ParseUser.getCurrentUser();
                        query.whereEqualTo("fromUser", user);
                        query.addAscendingOrder("createdAt");
                        return query;
                    }
                };

        if (mCommentsAdapter == null) {
            mCommentsAdapter = new CommentAdapter(getActivity(), factory,R.layout.item_comment_fragment);
            // Disable automatic loading when the adapter is attached to a view.
            mCommentsAdapter.setAutoload(true);

            // Disable pagination, we'll manage the query limit ourselves
            mCommentsAdapter.setPaginationEnabled(false);
            mListview.setAdapter(mCommentsAdapter);
        } else {
            mListview.setAdapter(mCommentsAdapter);
        }

    }

    @OnClick(R.id.left_icon)
    public void LeftIconClick(){
        ((MainActivity)getActivity()).openDrawer();
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
            mCommentsAdapter.loadObjects();
    }





}

