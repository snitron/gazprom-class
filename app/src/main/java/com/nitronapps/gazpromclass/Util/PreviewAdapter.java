package com.nitronapps.gazpromclass.Util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PreviewAdapter extends FragmentPagerAdapter{

    private static int count = 1;

    public PreviewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewFragmentBuilder.newInstance(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    public void setCount(){
        count++;
        notifyDataSetChanged();
    }
}
