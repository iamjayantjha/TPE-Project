package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

public class AboutZerosticActivity extends AppCompatActivity {
    Dialog dialog;
    TextView title,message;
    Button okay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_zerostic);
        Utils.blackIconStatusBar(AboutZerosticActivity.this, R.color.background);
        TextView licenses = findViewById(R.id.licensesBtn);
        TextView credits1 = findViewById(R.id.creditsBtn);
        TextView terms = findViewById(R.id.termsBtn);
        TextView privacy = findViewById(R.id.privacyBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        dialog = new Dialog(AboutZerosticActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        okay = dialog.findViewById(R.id.okayBtn);
        message.setMovementMethod(new ScrollingMovementMethod());
        final long[] pattern = {10,20};
        licenses.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent license = new Intent(AboutZerosticActivity.this, LicensesActivity.class);
            startActivity(license);
        });
        credits1.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            Intent credits = new Intent(AboutZerosticActivity.this, CreditsActivity.class);
            startActivity(credits);
        });
        terms.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(R.string.termsncondition);
            message.setText(R.string.terms);
            dialog.show();
        });
        okay.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            dialog.dismiss();
        });
        privacy.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(R.string.privacyPolicy);
            message.setText(R.string.privacy);
            dialog.show();
        });
    }
}