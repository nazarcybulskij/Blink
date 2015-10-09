package nazar.cybulskij.blinkr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;

import java.util.Date;

import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.model.Feed;


/**
 * Created by nazar on 03.10.15.
 */
public class FeedAdapter extends ParseQueryAdapter<Feed> {

    LayoutInflater  inflater ;

    public FeedAdapter(Context context, QueryFactory<Feed> queryFactory) {
        super(context, queryFactory);
        inflater = LayoutInflater.from(context);

    }

    @Override
    public View getItemView(Feed post, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_message, parent, false);
            holder = new ViewHolder();
            holder.tvText= (TextView) view.findViewById(R.id.text);
            holder.tvName = (TextView) view.findViewById(R.id.name);
            holder.ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);
            holder.tvCountsComment = (TextView) view.findViewById(R.id.count_comment);
            holder.tvTime = (TextView) view.findViewById(R.id.time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }



        holder.tvName.setText(post.getLicense());
        holder.ratingBar.setRating(post.getRating().floatValue()*10);



        Date date = new Date();
        Date dateuser = post.getCreatedAt();

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
            holder.tvTime.setText(diffDays+"d");
        }

        holder.tvText.setText(post.getStatus());

        if (post.getCommentsNumber()==0){
           holder.tvCountsComment.setVisibility(View.INVISIBLE);
        }else{
            holder.tvCountsComment.setVisibility(View.VISIBLE);
            holder.tvCountsComment.setText(post.getCommentsNumber()+" Comments");
        }


        return view;
    }


    public static class ViewHolder {
        TextView tvText;
        TextView tvName;
        TextView tvTime;
        TextView tvCountsComment;
        RatingBar ratingBar;
    }


}
