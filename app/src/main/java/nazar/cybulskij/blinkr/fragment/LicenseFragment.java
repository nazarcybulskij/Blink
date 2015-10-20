package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
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
    EditText mtvLicense;



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
        mtvLicense.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        init();
        return view;
    }

    private void init(){
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        String license = installation.getString("license");
        if (license!=null){
            mtvLicense.setText(license);

        }
    }


    @OnClick(R.id.left_icon)
    public void LeftIconClick() {
        ((MainActivity) getActivity()).openDrawer();
    }

    @OnClick(R.id.save_license)
    public void onSaveLicense(View v){
        String license = mtvLicense.getText().toString().replace("-","");

        if (license.length()<4){
            Toast.makeText(getActivity(),"Please Enter License Plate #",Toast.LENGTH_LONG).show();
        }else{
            ParseInstallation installation = ParseInstallation.getCurrentInstallation();
            installation.put("license",license);
            installation.saveInBackground();
            LeftIconClick();
        }


    }
}
