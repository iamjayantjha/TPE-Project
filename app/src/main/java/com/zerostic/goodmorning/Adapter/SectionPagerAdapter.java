package com.zerostic.goodmorning.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.zerostic.goodmorning.TrackerFragment;
import com.zerostic.goodmorning.SleepFragment;
import com.zerostic.goodmorning.AlarmsList.AlarmsListFragment;

import org.jetbrains.annotations.NotNull;

public class SectionPagerAdapter extends FragmentPagerAdapter {
    public SectionPagerAdapter(@NonNull @NotNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @NotNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                AlarmsListFragment alarmsListFragment =new AlarmsListFragment();
                return  alarmsListFragment;

            case 1:
                TrackerFragment trackerFragment =new TrackerFragment();
                return trackerFragment;

            case 2:
                SleepFragment thirdFragment = new SleepFragment();
                return thirdFragment;

            default:
                AlarmsListFragment alFragment =new AlarmsListFragment();
                return  alFragment;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
