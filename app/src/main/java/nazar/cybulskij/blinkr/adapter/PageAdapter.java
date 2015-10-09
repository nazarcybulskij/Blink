package nazar.cybulskij.blinkr.adapter;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;

;import nazar.cybulskij.blinkr.fragment.MessagesListFragment;


/**
 * Created by nazar on 07.10.15.
 */
public class PageAdapter extends FragmentStatePagerAdapter {

    public PageAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        Fragment fragment;

        fragment = new MessagesListFragment();
        fragment.setArguments(bundle);



        return fragment;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
