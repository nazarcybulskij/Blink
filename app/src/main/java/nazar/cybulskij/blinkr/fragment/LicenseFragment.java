package nazar.cybulskij.blinkr.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import butterknife.ButterKnife;
import butterknife.Bind;
import butterknife.OnClick;

import android.support.annotation.Nullable;

import io.fabric.sdk.android.Fabric;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.interfaces.AutoCompleteCallBack;

/**
 * Created by nazar on 13.10.15.
 */
public class LicenseFragment extends AutoCompleteFragment {
    @Bind(R.id.license)
    public AutoCompleteTextView mtvLicense;
    private CharSequence currentString;
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "hluqFPKtfdEeE9cVoXZsksjdE";
    private static final String TWITTER_SECRET = "G5pIcD45KmDnoKbeOl2jA6qhyc4t77DZE5yPpHz81QmGDiygMq";
    private String license;
    private static final int STRING_LENGTH = 3;
    private static final int MIN_STRING_LENGTH = 4;
    private static final String LICENSE_KEY = "license";
    private static final String PHONE_NUMBER_KEY = "phoneNumber";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_license, container, false);
        ButterKnife.bind(this, view);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(getActivity(), new Crashlytics(), new TwitterCore(authConfig), new Digits());
        showCurrentLicense();
        String[] licenseStates = getResources().getStringArray(R.array.States_for_licelse);
        createAutoComplete(getActivity(), mtvLicense, createListOfStates(licenseStates), getAutoCompleteCallback(mtvLicense));
        mtvLicense.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        mtvLicense.setOnKeyListener(keyListener);
        mtvLicense.addTextChangedListener(watcher);
        return view;
    }

    private View.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (mtvLicense.length() == MIN_STRING_LENGTH && keyCode == KeyEvent.KEYCODE_DEL) {
                setFirstTwoChar(mtvLicense, currentString);
                return true;
            }
            return false;
        }
    };
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (mtvLicense.getText().toString().trim().length() >= MIN_STRING_LENGTH) {
                Drawable indicator = getResources().getDrawable(R.drawable.greencheck);
                mtvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, indicator, null);
            } else {
                mtvLicense.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
            }
            currentString = s;
            if (s.length() == STRING_LENGTH) {
                setTextWithDash(mtvLicense, currentString);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void showCurrentLicense() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String license = installation.getString(LICENSE_KEY);
        if (!TextUtils.isEmpty(license)) {
            mtvLicense.setText(license);
            mtvLicense.setSelection(mtvLicense.length());
        }
    }

    @OnClick(R.id.left_icon)
    public void showDrawerList() {
        ((MainActivity) getActivity()).openDrawer();
        hideKeyboard(getActivity());
    }

    @OnClick(R.id.save_license)
    public void onSaveLicense() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        if (TextUtils.isEmpty(installation.getString(PHONE_NUMBER_KEY))) {
            checkLicensePlate();
        } else {
            makeAlert();
        }
    }

    private void checkLicensePlate() {
        license = mtvLicense.getText().toString().replace(" ", "");
        if (license.length() < 4) {
            Toast.makeText(getActivity(), "Please Enter License Plate #", Toast.LENGTH_LONG).show();
        } else {
            logIn();
        }
    }

    public void logIn() {
        Digits.authenticate(authCallback, R.style.CustomDigitsTheme);
    }

    private AuthCallback authCallback = new AuthCallback() {
        @Override
        public void success(DigitsSession session, String phoneNumber) {
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put(PHONE_NUMBER_KEY, phoneNumber);
            installation.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    saveLicense(license);
                }
            });
        }

        @Override
        public void failure(DigitsException exception) {
            Log.i("LogIn", "failure: " + exception.toString());
        }
    };

    private void saveLicense(String license) {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put(LICENSE_KEY, license);
        installation.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                hideKeyboard(getActivity());
                makeAlert();
            }
        });
    }

    public void makeAlert() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Phone number: " + installation.getString(PHONE_NUMBER_KEY))
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
