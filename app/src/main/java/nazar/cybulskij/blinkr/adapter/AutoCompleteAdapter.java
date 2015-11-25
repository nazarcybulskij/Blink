package nazar.cybulskij.blinkr.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import nazar.cybulskij.blinkr.R;

/**
 * Created by Tarasik on 11.11.2015.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> {


    public AutoCompleteAdapter(Context context, int resource, ArrayList<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater vi;
        if (view == null) {
            vi = LayoutInflater.from(getContext());
            view = vi.inflate(R.layout.item_autocomplete, null);
        }
        String autoCompleteItem = getItem(position);
        if (!TextUtils.isEmpty(autoCompleteItem)) {
            TextView textView = (TextView) view.findViewById(R.id.auto_complete_item);
            textView.setText(autoCompleteItem);
        }
        return view;
    }
}