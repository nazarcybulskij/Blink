package nazar.cybulskij.blinkr.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.ToxicBakery.viewpager.transforms.BackgroundToForegroundTransformer;
import com.ToxicBakery.viewpager.transforms.DefaultTransformer;
import com.ToxicBakery.viewpager.transforms.DepthPageTransformer;

import nazar.cybulskij.blinkr.R;
import nazar.cybulskij.blinkr.adapter.PageAdapter;
import nazar.cybulskij.blinkr.view.ViewPagerCustomDuration;


/**
 * Created by nazar on 09.10.15.
 */
public class ContentFragment  extends Fragment {

    private static final String KEY_SELECTED_PAGE = "KEY_SELECTED_PAGE";
    private ViewPager mPager;
    private PageAdapter mAdapter;



    public ContentFragment() {
        // Empty constructor required for fragment subclasses
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_content, container, false);
        int selectedPage = 0;
        if (savedInstanceState != null) {
            selectedPage = savedInstanceState.getInt(KEY_SELECTED_PAGE);
        }

        mAdapter = new PageAdapter(getFragmentManager());
        mPager = (ViewPager) rootView.findViewById(R.id.container_view_pager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(selectedPage);

        mPager.setPageTransformer(true, new DefaultTransformer());



        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mPager.onSaveInstanceState();
    }
}
