package nazar.cybulskij.blinkr.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseInstallation;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterCore;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;
import nazar.cybulskij.blinkr.App;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.AutoCompleteAdapter;

/**
 * Created by nazar on 13.10.15.
 */
public class LicenseFragment extends Fragment {
    public void setMtvLicense(AutoCompleteTextView mtvLicense) {
        this.mtvLicense = mtvLicense;
    }

    @Bind(R.id.license)
    public AutoCompleteTextView mtvLicense;
    private CharSequence tmp;

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
        init();
        String[] licenceShtats = getResources().getStringArray(R.array.Shtats_for_licelce);
        autoCompleteCreator(getActivity(), mtvLicense, licenceShtats);
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

        return view;
    }

    private void init() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String license = installation.getString("license");
        if (App.isLogIn) {
            if (license != null) {
                mtvLicense.setText(license);
            }
            App.phoneNumberId = installation.getString("phoneNumber");
        }
    }

    public static void setTextWithDash(EditText view, CharSequence text) {
        if (text.charAt(text.length() - 1) != '-') {
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
        if (App.phoneNumberId.equals("")) {
            final String license = mtvLicense.getText().toString().replace(" ", "");
            if (license.length() < 4) {
                Toast.makeText(getActivity(), "Please Enter License Plate #", Toast.LENGTH_LONG).show();
            } else {
                Digits.authenticate(App.getAuthCallback(), R.style.CustomDigitsTheme);
                ParseInstallation installation = ParseInstallation.getCurrentInstallation();
                installation.put("license", license);
                installation.saveInBackground();
                hideKeyboard(getActivity());
                alert();
            }
        } else {
            alert();
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

    public static View autocompleteTextTransformer(final AutoCompleteTextView textView) {
        textView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = (String) parent.getItemAtPosition(position);
                textView.setText(str.subSequence(str.length() - 2, str.length()));
                textView.setSelection(textView.length());
                view.setBackgroundColor(Color.GRAY);
            }
        });
        return textView;
    }

    public static View autoCompleteCreator(Context context, AutoCompleteTextView view, String[] strArray) {
        ArrayList<String> list = new ArrayList<>();
        for (String str : strArray) {
            list.add(str);
        }
        AutoCompleteAdapter customAdapter = new AutoCompleteAdapter(context, R.layout.item_autocomplete, list);
        view.setAdapter(customAdapter);
        autocompleteTextTransformer(view);
        view.setThreshold(0);
        return view;
    }

    public void alert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Phone number: " + App.phoneNumberId)
                .setMessage("You have successfully added your phone number and license plate #.")
                .setCancelable(false)
                .setNegativeButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

}
