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
import nazar.cybulskij.blinkr.interfaces.AutoCompleteCallBack;

/**
 * Created by Tarasik on 11.11.2015.
 */
public class AutoCompleteAdapter extends ArrayAdapter<String> {

    private AutoCompleteCallBack callBack;


    public AutoCompleteAdapter(Context context, int resource, ArrayList<String> items, AutoCompleteCallBack callBack) {
        super(context, resource, items);
        this.callBack = callBack;
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
            final TextView textView = (TextView) view.findViewById(R.id.auto_complete_item);
            textView.setText(autoCompleteItem);
            final View finalView = view;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String str = (String) textView.getText();
                    int strLengthWithOutTwoChars = str.length() - 2;
                    callBack.onAutoComplete(str.subSequence(strLengthWithOutTwoChars, str.length()).toString());
                    finalView.forceLayout();
                }
            });
        }
        return view;
    }
}