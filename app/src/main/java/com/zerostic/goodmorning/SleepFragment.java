package com.zerostic.goodmorning;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.Activities.MusicActivity;
import com.zerostic.goodmorning.Application.CircularSlider;


public class SleepFragment extends Fragment {
    private MaterialCardView musicCard;
    private Vibrator vibrator;
    final long[] pattern = {40, 80};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sleep, container, false);
        /*musicCard = view.findViewById(R.id.musicCard);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        musicCard.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent settingsIntent = new Intent(getContext(), MusicActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(settingsIntent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });*/
        CircularSlider slider = view.findViewById(R.id.circularSlider);

        return view;
    }
}