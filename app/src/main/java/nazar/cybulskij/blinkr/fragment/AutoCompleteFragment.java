package nazar.cybulskij.blinkr.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import java.util.ArrayList;

import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.AutoCompleteAdapter;
import nazar.cybulskij.blinkr.interfaces.AutoCompleteCallBack;

/**
 * Created by Tarasik on 04.12.2015.
 */
public class AutoCompleteFragment extends Fragment {

    private int firstChar = 0, thirdChar = 2, fourthChar = 3;
    private String dash = "-";

    public void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public  void setTextWithDash(EditText view, CharSequence text) {
        if (text.charAt(text.length() - 1) != '-') {
            text = text.subSequence(firstChar, thirdChar) + dash + text.subSequence(thirdChar, fourthChar);
            view.setText(text);
            view.setSelection(view.length());
        }
    }

    public  void setFirstTwoChar(EditText view, CharSequence text) {
        view.setText(text.subSequence(firstChar, thirdChar));
        view.setSelection(view.length());
    }

    public  ArrayList<String> createListOfStates(String[] strArray) {
        ArrayList<String> list = new ArrayList<>();
        for (String str : strArray) {
            list.add(str);
        }
        return list;
    }

    public  void createAutoComplete(Context context, AutoCompleteTextView autoCompleteView, ArrayList<String> strList, AutoCompleteCallBack callBack) {
        AutoCompleteAdapter customAdapter = new AutoCompleteAdapter(context, R.layout.item_autocomplete, strList, callBack);
        autoCompleteView.setAdapter(customAdapter);
        autoCompleteView.setThreshold(0);
    }

    public AutoCompleteCallBack getAutoCompleteCallback (final AutoCompleteTextView view){
        AutoCompleteCallBack callBack = new AutoCompleteCallBack() {
            @Override
            public void onAutoComplete(String autoCompleteText) {
                view.setText(autoCompleteText);
                view.setSelection(view.length());
            }
        };
        return callBack;
    }
}
