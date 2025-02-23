package com.zerostic.goodmorning.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import com.zerostic.goodmorning.R;

/**
 Coded by iamjayantjha
 **/

public class SliderAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;
    }

    public int[] slide_images = {
            R.mipmap.ic_launcher_foreground,
            R.mipmap.ic_launcher_background,
            R.mipmap.ic_launcher
    };

    public String[] slide_headings = {
            "First Image",
            "Second Image",
            "Third Image"
    };

    public String[] slide_dscs = {
            "First Image Description",
            "Second Image Description",
            "Third Image Description"
    };


    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (RelativeLayout) object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        ImageView slideImage = (ImageView) view.findViewById(R.id.image1);
        TextView slideHeading = (TextView) view.findViewById(R.id.heading);
        TextView slideDscs = (TextView) view.findViewById(R.id.subHeading);
        slideImage.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDscs.setText(slide_dscs[position]);
        container.addView(view);
        return  view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
