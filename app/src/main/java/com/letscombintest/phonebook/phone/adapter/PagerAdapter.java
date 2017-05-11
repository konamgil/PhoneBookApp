package com.letscombintest.phonebook.phone.adapter;

/**
 * Created by konamgil on 2017-05-10.
 */

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.letscombintest.phonebook.phone.fragment.AllFragment;
import com.letscombintest.phonebook.phone.fragment.FavoriteFragment;

import static com.letscombintest.phonebook.phone.fragment.AllFragment.newInstance;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int _numOfTabs = 0;
    Context context;

    public PagerAdapter(FragmentManager fm, int numOfTabs, Context context) {
        super(fm);
        this._numOfTabs = numOfTabs;
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                FavoriteFragment favoriteFragment = FavoriteFragment.newInstance(context);
                return favoriteFragment;
            case 1:
                AllFragment allFragment = newInstance(context);
                return allFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return _numOfTabs;
    }
}
