package com.zerostic.goodmorning.Application;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.R;

public class ChangeImageBottomSheet extends BottomSheetDialogFragment {
    private bottomSheetListener mListener;

    MaterialCardView upload, removeImg;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.change_image_bottom_sheet,container,false);
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        upload = view.findViewById(R.id.upload);
        removeImg = view.findViewById(R.id.removeImg);
        final long[] pattern = {40, 80};
        upload.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            mListener.onClick("upload");
            dismiss();
        });
        upload.setOnLongClickListener(v -> {
            Toast.makeText(getContext(),"Upload profile picture.",Toast.LENGTH_SHORT).show();
            return true;
        });
        removeImg.setOnLongClickListener(v -> {
            Toast.makeText(getContext(),"Remove profile picture.",Toast.LENGTH_SHORT).show();
            return true;
        });
        removeImg.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            mListener.onClick("remove");
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
            mListener = (ChangeImageBottomSheet.bottomSheetListener) context;
        }
        catch (ClassCastException classCastException){
            throw new ClassCastException(context.toString()+" must implement Bottom Sheet Listener");
        }

    }
}

