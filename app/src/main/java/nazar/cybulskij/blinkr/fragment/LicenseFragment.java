package nazar.cybulskij.blinkr.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseInstallation;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;

/**
 * Created by nazar on 13.10.15.
 */
public class LicenseFragment extends Fragment {





    @Bind(R.id.license)
    public AutoCompleteTextView mtvLicense;
    private CharSequence tmp;
//    private final int maxInputLength = 19;

    public static LicenseFragment newInstance() {
        LicenseFragment fragment = new LicenseFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_license, container, false);
        ButterKnife.bind(this, view);

//        String[] licenceShtats = getResources().getStringArray(R.array.Shtats_for_licelce);
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,licenceShtats);
//        mtvLicense.setAdapter(adapter);
//        mtvLicense.setThreshold(0);

        mtvLicense.setFilters(new InputFilter[]{new InputFilter.AllCaps()});


        mtvLicense.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (mtvLicense.length() == 4) {
                    if (keyCode == KeyEvent.KEYCODE_DEL) {
                        setFirstTwoChar(mtvLicense, tmp);
                        return true;
                    }
                }
                return false;
            }
        });

        mtvLicense.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (mtvLicense.getText().toString().trim().length() >= 4) {
                    Drawable indiactor = getResources().getDrawable(R.drawable.greencheck);
                    mtvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, indiactor, null);
                } else {
                    mtvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
                }

                tmp = s;
                if (s.length() == 3) {
                    setTextWithDash(mtvLicense, tmp);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        init();
        return view;
    }

    private void init() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String license = installation.getString("license");
        if (license != null) {
            mtvLicense.setText(license);
        }
    }

    public static void setTextWithDash(EditText view, CharSequence text) {
        if(text.charAt(text.length()-1) != '-') {
            text = text.subSequence(0, 2) + "-" + text.subSequence(2, 3);
            view.setText(text);
            view.setSelection(view.length());
        }
    }

    public static void setFirstTwoChar(EditText view, CharSequence text) {

        view.setText(text.subSequence(0, 2));
        view.setSelection(view.length());
    }

    @OnClick(R.id.left_icon)
    public void LeftIconClick() {
        ((MainActivity) getActivity()).openDrawer();
        hideKeyboard(getActivity());
    }

    @OnClick(R.id.save_license)
    public void onSaveLicense() {
        String license = mtvLicense.getText().toString().replace(" ", "");

        if (license.length() < 4) {
            Toast.makeText(getActivity(), "Please Enter License Plate #", Toast.LENGTH_LONG).show();
        } else {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("license", license);
            installation.saveInBackground();
            LeftIconClick();
            hideKeyboard(getActivity());
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
