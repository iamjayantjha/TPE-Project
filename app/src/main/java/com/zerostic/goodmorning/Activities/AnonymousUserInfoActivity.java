package com.zerostic.goodmorning.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AnonymousUserInfoActivity extends AppCompatActivity {
    Dialog dialog;
    TextView title,message;
    Button okay;
    String act,formattedDate,fName,lName,country;
    TextInputEditText uFName, uCountry, uLName;
    MaterialCardView nextBtn;
    ImageView nextIcon;
    Chip tos;
    LottieAnimationView loading;
    FirebaseAuth mAuth;
    int currentNightMode;
    boolean isDarkTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anonymous_user_info);
        act = getIntent().getStringExtra("act");
        uFName = findViewById(R.id.fnameEditText);
        uLName = findViewById(R.id.lnameEditText);
        uCountry = findViewById(R.id.countryEditText);
        nextBtn = findViewById(R.id.nextBtn);
        nextIcon = findViewById(R.id.nextIcon);
        tos = findViewById(R.id.tos);
        loading = findViewById(R.id.animationView);
        dialog = new Dialog(AnonymousUserInfoActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        mAuth = FirebaseAuth.getInstance();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        okay = dialog.findViewById(R.id.okayBtn);
        message.setMovementMethod(new ScrollingMovementMethod());
        TextView terms = findViewById(R.id.termsBtn);
        TextView privacy = findViewById(R.id.privacyBtn);
        nextBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            nextIcon.setImageResource(R.drawable.no_background);
            nextBtn.setEnabled(false);
            loading.setVisibility(View.VISIBLE);
            Calendar rightNow = Calendar.getInstance();
            Date c = rightNow.getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            formattedDate = df.format(c);
            if (uFName.getText().toString().isEmpty() || uLName.getText().toString().isEmpty() || uCountry.getText().toString().isEmpty()) {
                uFName.setError("Please enter your first name");
                Toast.makeText(AnonymousUserInfoActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                uLName.setError("Please enter your last name");
                uCountry.setError("Please enter your country");
                if (isDarkTheme){
                    nextIcon.setImageResource(R.drawable.ic_next_grey);
                }else {
                    nextIcon.setImageResource(R.drawable.ic_next);
                }
                loading.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
            } else if (!tos.isChecked()) {
                tos.setError("Please accept the terms and conditions");
                Toast.makeText(AnonymousUserInfoActivity.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                if (isDarkTheme){
                    nextIcon.setImageResource(R.drawable.ic_next_grey);
                }else {
                    nextIcon.setImageResource(R.drawable.ic_next);
                }
                loading.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
            } else {
                 fName = uFName.getText().toString();
                 lName = uLName.getText().toString();
                 country = uCountry.getText().toString();
                if (act.equals("login")) {
                    Toast.makeText(AnonymousUserInfoActivity.this, "1", Toast.LENGTH_SHORT).show();
                    saveData();
                }else {
                    updateAnonymousUserData(fName,lName,country);
                }
            }
        });
        tos.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (tos.isChecked()) {
                tos.setError(null);
            } else {
                tos.setError("Please accept the terms and conditions");
            }
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

    private void updateAnonymousUserData(String fName, String lName, String country) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userid);
        hashMap.put("first_name", fName);
        hashMap.put("last_name", lName);
        hashMap.put("email", "");
        hashMap.put("phone_number", "");
        hashMap.put("payment_status", "n");
        hashMap.put("country",country);
        hashMap.put("account_open_date",formattedDate);
        hashMap.put("password","");
        hashMap.put("imageUri","");
        hashMap.put("terms_of_service","accepted");
        hashMap.put("verification","Unverified");
        Toast.makeText(AnonymousUserInfoActivity.this, "3", Toast.LENGTH_SHORT).show();
        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                saveData();
                Toast.makeText(AnonymousUserInfoActivity.this, "4", Toast.LENGTH_SHORT).show();
            }else
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Your data is not inserted properly, Please try again later.",Toast.LENGTH_SHORT).show();
                if (isDarkTheme){
                    nextIcon.setImageResource(R.drawable.ic_next_grey);
                }else {
                    nextIcon.setImageResource(R.drawable.ic_next);
                }
                loading.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
                Toast.makeText(AnonymousUserInfoActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerAnonymousUser(String fName, String lName, String country) {
        mAuth.signInAnonymously().addOnCompleteListener(task -> {
            Toast.makeText(AnonymousUserInfoActivity.this, "2", Toast.LENGTH_SHORT).show();
            if (task.isSuccessful()){
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                String userid = firebaseUser.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userid);
                hashMap.put("first_name", fName);
                hashMap.put("last_name", lName);
                hashMap.put("email", "");
                hashMap.put("phone_number", "");
                hashMap.put("payment_status", "n");
                hashMap.put("country",country);
                hashMap.put("account_open_date",formattedDate);
                hashMap.put("password","");
                hashMap.put("imageUri","");
                hashMap.put("terms_of_service","accepted");
                hashMap.put("verification","Unverified");
                Toast.makeText(AnonymousUserInfoActivity.this, "3", Toast.LENGTH_SHORT).show();
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        saveData();
                        Toast.makeText(AnonymousUserInfoActivity.this, "4", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Your data is not inserted properly, Please try again later.",Toast.LENGTH_SHORT).show();
                        if (isDarkTheme){
                            nextIcon.setImageResource(R.drawable.ic_next_grey);
                        }else {
                            nextIcon.setImageResource(R.drawable.ic_next);
                        }
                        loading.setVisibility(View.GONE);
                        nextBtn.setEnabled(true);
                        Toast.makeText(AnonymousUserInfoActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void saveData(){
        Calendar rightNow = Calendar.getInstance();
        Date c = rightNow.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        Intent registerIntent = new Intent(AnonymousUserInfoActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        registerIntent.putExtra("activity","information");
        registerIntent.putExtra("name",fName);
        registerIntent.putExtra("lname",lName);
        registerIntent.putExtra("email","");
        registerIntent.putExtra("phone","");
        registerIntent.putExtra("country",country);
        registerIntent.putExtra("password","");
        registerIntent.putExtra("account_open_date",formattedDate);
        registerIntent.putExtra("tos","accepted");
        registerIntent.putExtra("verification","Unverified");
        registerIntent.putExtra("type","anonymous");
        startActivity(registerIntent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
    }
}