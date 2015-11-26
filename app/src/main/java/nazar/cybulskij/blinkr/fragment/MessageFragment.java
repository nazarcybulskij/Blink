package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import java.io.File;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

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
import nazar.cybulskij.blinkr.util.Utils;

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

    Utils  utils = new Utils();



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
           // holder.ratingBar = (RatingBar) header.findViewById(R.id.ratingBar);
            holder.tvTime = (TextView) header.findViewById(R.id.time);
            holder.tvCountsComment = (TextView) header.findViewById(R.id.count_comment);
            holder.tvName.setText(feed.getLicense());
           // holder.ratingBar.setRating(feed.getRating().floatValue()*10);
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


        if (feed.getCommentsNumber().intValue()==0){
            holder.tvCountsComment.setVisibility(View.INVISIBLE);
        }else{
            holder.tvCountsComment.setVisibility(View.VISIBLE);
            holder.tvCountsComment.setText(feed.getCommentsNumber()+" Comments");
        }





            holder.tvText.setText(feed.getStatus());
            holder.ivShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new BottomSheet.Builder(getActivity(), R.style.BottomSheet_Dialog)
                            .sheet(R.menu.menu_bottom_sheet_report)
                            .listener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            utils.saveFile(utils.getBitmapFromView(mCommentsList));

                            switch (which) {
                                case R.id.Report:
                                    report();
                                    break;
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
            },R.layout.item_comment);

            mCommentAdapter.setAutoload(true);
            // Disable pagination, we'll manage the query limit ourselves
            mCommentAdapter.setPaginationEnabled(false);

            mCommentsList.setAdapter(mCommentAdapter);
        }else{
            mCommentAdapter.loadObjects();
        }


    }

    public void report(){

        Number number = feed.getReportsCount();


        feed.increment("Reports");
        feed.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e==null){
                    Toast.makeText(getActivity(),"Send Report",Toast.LENGTH_LONG).show();
                }

            }
        });

    }


    private  void shareEmail(){

        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath =  new File(filename);  //optional //internal storage
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

    private  void shareFacebook(){


        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath =  new File(filename);  //optional //internal storage
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);



        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, 0);
        for (final ResolveInfo app : activityList)
        {
            if ((app.activityInfo.name).contains("facebook"))
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




    private void shareTwitter(){
        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath =  new File(filename);  //optional //internal storage
        Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
        //shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "dvfgbf");
        //shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "chfh");
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(filePath));
        shareIntent.setType("image/jpeg");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PackageManager pm = getActivity().getPackageManager();
        List<ResolveInfo> activityList = pm.queryIntentActivities(shareIntent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        for (final ResolveInfo app : activityList)
        {
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

    private  void shareWhatsapp(){

        String filename = Environment.getExternalStorageDirectory().getAbsolutePath() + "/bitmap.png";
        File filePath =  new File(filename);  //optional //internal storage
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
       // RatingBar ratingBar;
        TextView tvCountsComment;
    }



}
