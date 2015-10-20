package nazar.cybulskij.blinkr.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.parse.ParseQueryAdapter;

import java.util.Date;

import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.model.Comment;

/**
 * Created by nazar on 04.10.15.
 */
public class CommentAdapter extends ParseQueryAdapter<Comment> {

    LayoutInflater mInflater;

    public CommentAdapter(Context context, QueryFactory<Comment> queryFactory) {
        super(context, queryFactory);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public View getItemView(Comment comment, View view, ViewGroup parent) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.item_comment, parent, false);
            holder = new ViewHolder();
            holder.tvText = (TextView) view.findViewById(R.id.text);
            holder.tvTime = (TextView) view.findViewById(R.id.time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.tvText.setText(comment.getCommentText());
        Date date = new Date();
        Date datecomment = comment.getCreatedAt();
        long diff = date.getTime() - datecomment.getTime();
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
        
        return view;
    }


    public static class ViewHolder {
        TextView tvText;
        TextView tvTime;
    }

}
