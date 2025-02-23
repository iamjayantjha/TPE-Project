package com.zerostic.goodmorning.Application;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.zerostic.goodmorning.R;

import static android.content.Context.MODE_PRIVATE;

/**
 Coded by iamjayantjha
 **/

public class TitleBottomSheet extends BottomSheetDialogFragment {
    private bottomSheetListener mListener;

    MaterialButton saveBtn;
    EditText alarmTitleText;
    String title;
    private Vibrator vibrator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.title_bottom_sheet,container,false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        SharedPreferences preferences= getActivity().getSharedPreferences("title", MODE_PRIVATE);
        title  = preferences.getString("title", null);
        saveBtn = view.findViewById(R.id.saveBtn);
        alarmTitleText = view.findViewById(R.id.alarmTitleText);
        if (title.equals("")){
            alarmTitleText.setText("");
        }else {
            alarmTitleText.setText(title);
        }
        saveBtn.setOnClickListener(view1 -> {
            vibrator.vibrate(pattern, -1);
            String title =  alarmTitleText.getText().toString();
            if (title.equals("")){
                mListener.onClick("Alarm");
            }else {
                mListener.onClick(title);
            }
            SharedPreferences preferences1=getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putString("title",title);
            editor.apply();
            dismiss();
        });
        return view;
    }

    public interface bottomSheetListener{
        void onClick(String text);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (bottomSheetListener) context;
        }
        catch (ClassCastException classCastException){
            throw new ClassCastException(context.toString()+" must implement Bottom Sheet Listener");
        }

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        String title =  alarmTitleText.getText().toString();
        if (title.equals("")){
            mListener.onClick("Alarm");
        }else {
            mListener.onClick(title);
        }
        SharedPreferences preferences1=requireActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences1.edit();
        editor.putString("title",title);
        editor.apply();
    }
}
