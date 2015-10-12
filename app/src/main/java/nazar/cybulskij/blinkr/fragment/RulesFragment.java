package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import nazar.cybulskij.blinkr.MainActivity;
import nazar.cybulskij.blinkr.R;

/**
 * Created by nazar on 13.10.15.
 */
public class RulesFragment extends Fragment {



    public static RulesFragment newInstance() {
        RulesFragment fragment = new RulesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_rules, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @OnClick(R.id.left_icon)
    public void LeftIconClick(){
        ((MainActivity)getActivity()).openDrawer();
    }
}

