package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cocosw.bottomsheet.BottomSheet;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.CommentAdapter;
import nazar.cybulskij.blinkr.events.FeedEvent;
import nazar.cybulskij.blinkr.model.Comment;
import nazar.cybulskij.blinkr.model.Feed;

/**
 * Created by nazar on 28.09.15.
 */
public class MessageFragment extends Fragment {


    @Bind(R.id.list)
    ListView mCommentsList;

    Feed feed;

    CommentAdapter mCommentAdapter;
    @Bind(R.id.et_text_post)
    EditText mTextSend;


    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().registerSticky(this);
    }
    @Override
    public void onStop() {
        super.onStart();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.left_icon)
    public void LeftIconClick(){
        getActivity().onBackPressed();
    }


    View header;



    private void fullinfo(){

        if (mCommentsList.getHeaderViewsCount()==0) {
            header = View.inflate(getActivity(), R.layout.header_comment_fragment, null);
            mCommentsList.addHeaderView(header);
        }

            ViewHolder holder;
            holder = new ViewHolder();
            holder.ivShare = (ImageView) header.findViewById(R.id.share);
            holder.tvText= (TextView) header.findViewById(R.id.text);
            holder.tvName = (TextView) header.findViewById(R.id.name);
            holder.ratingBar = (RatingBar) header.findViewById(R.id.ratingBar);
            holder.tvTime = (TextView) header.findViewById(R.id.time);
            holder.tvName.setText(feed.getLicense());
            holder.ratingBar.setRating(feed.getRating().floatValue()*10);
            Date date = new Date();
            Date dateuser = feed.getCreatedAt();
            long diff = date.getTime() - dateuser.getTime();
            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

            if (diffDays==0){
                if (diffHours==0){
                    if(diffMinutes==0){
                        holder.tvTime.setText(diffSeconds+"s");
                    }else {
                        holder.tvTime.setText(diffMinutes+"m");
                    }
                }else{
                    holder.tvTime.setText(diffHours+"h");
                }
            }else{
                holder.tvTime.setText(diffDays + "d");
            }

            holder.tvText.setText(feed.getStatus());
            holder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(getActivity(), R.style.BottomSheet_Dialog)
                            .sheet(R.menu.menu_bottom_sheet)
                            .listener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    switch (which) {
                                        case R.id.Email:
                                           // shareEmail();
                                            break;
                                        case R.id.Facebook:
                                           // shareFacebook();
                                            break;
                                        case R.id.Twitter:
                                           // shareTwitter();

                                            break;
                                        case R.id.Whatsapp:
                                            //shareWhatsapp();
                                            break;


                                    }

                                }
                            }).show();

                }
            });






        if (mCommentAdapter == null){
            mCommentAdapter = new CommentAdapter(getActivity(), new ParseQueryAdapter.QueryFactory<Comment>() {
                @Override
                public ParseQuery<Comment> create() {
                    ParseQuery<Comment> query = Comment.getQuery();
                    query.whereEqualTo("Post", feed);
                    query.addAscendingOrder("createdAt");
                    return query;
                }
            });

            mCommentAdapter.setAutoload(true);
            // Disable pagination, we'll manage the query limit ourselves
            mCommentAdapter.setPaginationEnabled(false);

            mCommentsList.setAdapter(mCommentAdapter);
        }else{
            mCommentAdapter.loadObjects();
        }


    }



    @OnClick(R.id.btn_post)
    public void onPostComment(View v){
        String text = mTextSend.getText().toString();
        if (text.trim().equals("")){
            return;
        }
        final Comment comment = new Comment();
        comment.setUser(ParseUser.getCurrentUser());
        comment.setFeed(feed);
        comment.setCommentText(text);

        comment.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    ParseQuery query = ParseQuery.getQuery(Feed.class);
                    query.getInBackground(feed.getObjectId(), new GetCallback() {
                        @Override
                        public void done(ParseObject object, ParseException e) {

                        }

                        @Override
                        public void done(Object o, Throwable throwable) {
                            feed.increment("CommentsNumber");
                            feed.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException e) {
                                    if (e==null){
                                        ParseQuery query = ParseInstallation.getQuery();
                                        query.whereEqualTo("owner", comment.getUser().getObjectId());
                                        query.whereEqualTo("deviceType", "android");

                                        JSONObject data = new JSONObject();
                                        try {
                                            data.put("postid", comment.getObjectId());
                                            data.put("badge", "Increment");
                                            data.put("alert", "comment");
                                        } catch (JSONException e1) {
                                            e1.printStackTrace();
                                        }


                                        ParsePush push = new ParsePush();
                                        push.setQuery(query);
                                        push.setData(data);
                                        push.sendInBackground();
                                        mTextSend.setText("");

                                        fullinfo();
                                    }
                                }
                            });

                        }
                    });

                }
            }
        });


    }

    public void onEvent(FeedEvent event){
        feed = event.getFeed();
        fullinfo();
    }

    public static class ViewHolder {
        TextView tvText;
        TextView tvName;
        TextView tvTime;
        ImageView ivShare;
        RatingBar ratingBar;
    }



}
