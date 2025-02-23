package com.zerostic.goodmorning.Application;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.zerostic.goodmorning.R;

import static android.content.Context.MODE_PRIVATE;

/**
 Coded by iamjayantjha
 **/

public class SnoozeBottomSheet extends BottomSheetDialogFragment {
    private snoozeBottomSheetListener mListener;

    String snoozeTime,time;
    private Vibrator vibrator;
    Chip twoMin,threeMin, fourMin, fiveMin, sixMin, sevenMin, eightMin,nineMin, tenMin;
    MaterialButton removeSnooze;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.snooze_bottom_sheet,container,false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        SharedPreferences preferences= getActivity().getSharedPreferences("snooze", MODE_PRIVATE);
        snoozeTime  = preferences.getString("snooze", "0");
        twoMin = view.findViewById(R.id.twoMin);
        threeMin = view.findViewById(R.id.threeMin);
        fourMin = view.findViewById(R.id.fourMin);
        fiveMin = view.findViewById(R.id.fiveMin);
        sixMin = view.findViewById(R.id.sixMin);
        sevenMin = view.findViewById(R.id.sevenMin);
        eightMin = view.findViewById(R.id.eightMin);
        nineMin = view.findViewById(R.id.nineMin);
        tenMin = view.findViewById(R.id.tenMin);
        removeSnooze = view.findViewById(R.id.removeBtn);
        switch (snoozeTime) {
            case "2":
                twoMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "3":
                threeMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "4":
                fourMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "5":
                fiveMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "6":
                sixMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "7":
                sevenMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "8":
                eightMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "9":
                nineMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
            case "10":
                tenMin.setChecked(true);
                removeSnooze.setVisibility(View.VISIBLE);
                break;
        }
        twoMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (twoMin.isChecked()) {
                time = "2";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });
        threeMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (threeMin.isChecked()) {
                time = "3";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        fourMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (fourMin.isChecked()) {
                time = "4";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        fiveMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (fiveMin.isChecked()) {
                time = "5";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        sixMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (sixMin.isChecked()) {
                time = "6";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        sevenMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (sevenMin.isChecked()) {
                time = "7";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        eightMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (eightMin.isChecked()) {
                time = "8";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        nineMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (nineMin.isChecked()) {
                time = "9";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });

        tenMin.setOnCheckedChangeListener((buttonView, isChecked) -> {
            vibrator.vibrate(pattern, -1);
            if (tenMin.isChecked()) {
                time = "10";
            }else {
                time = "";
            }
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });
        removeSnooze.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            time = "";
            if (time.equals("")){
                mListener.onSaveClick("0");
            }else {
                mListener.onSaveClick(time);
            }
            SharedPreferences preferences2 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences2.edit();
            editor.putString("snooze",time);
            editor.apply();
            dismiss();
        });
        return view;
    }

    public interface snoozeBottomSheetListener{
        void onSaveClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (snoozeBottomSheetListener) context;
        }
        catch (ClassCastException classCastException){
            throw new ClassCastException(context.toString()+" must implement Bottom Sheet Listener");
        }

    }
}
