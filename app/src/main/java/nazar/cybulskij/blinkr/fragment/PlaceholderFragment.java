package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import nazar.cybulskij.blinkr.R;


/**
 * Created by nazar on 07.10.15.
 */
public class PlaceholderFragment extends Fragment {

    public static final String EXTRA_POSITION = "EXTRA_POSITION";
    private static final int[] COLORS = new int[] { 0xFF33B5E5, 0xFFAA66CC, 0xFF99CC00, 0xFFFFBB33, 0xFFFF4444 };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final int position = getArguments().getInt(EXTRA_POSITION);
        final View view = (View) inflater.inflate(R.layout.fragment_main, container, false);

        view.setBackgroundColor(COLORS[position - 1]);

        return view;
    }

}