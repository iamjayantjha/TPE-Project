package com.zerostic.goodmorning.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class InformationActivity extends AppCompatActivity {
    TextInputEditText uFName, uEmail, uPhoneNumber, uCountry, uPassword,uLName;
    MaterialCardView nextBtn;
    String str_username,str_email,str_password,str_country, str_phoneNumber,formattedDate,act;
    FirebaseAuth mAuth;
    LottieAnimationView loading;
    int currentNightMode;
    boolean isDarkTheme,isPasswordOK;
    Chip tos;
    Dialog dialog;
    TextView title,message;
    Button okay;
    ImageView nextIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information);
        uFName = findViewById(R.id.fnameEditText);
        uLName = findViewById(R.id.lnameEditText);
        uEmail = findViewById(R.id.emailEditText);
        uPassword = findViewById(R.id.passwordEditText);
        uPhoneNumber = findViewById(R.id.phoneEditText);
        uCountry = findViewById(R.id.countryEditText);
        nextBtn = findViewById(R.id.nextBtn);
        TextView terms = findViewById(R.id.termsBtn);
        TextView privacy = findViewById(R.id.privacyBtn);
        tos = findViewById(R.id.tos);
        dialog = new Dialog(InformationActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        nextIcon = findViewById(R.id.nextIcon);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        okay = dialog.findViewById(R.id.okayBtn);
        message.setMovementMethod(new ScrollingMovementMethod());
        mAuth = FirebaseAuth.getInstance();
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
     //   Toast.makeText(getApplicationContext(),""+isDarkTheme,Toast.LENGTH_SHORT).show();
        loading = findViewById(R.id.animationView);
        act = getIntent().getStringExtra("act");
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        nextBtn.setOnClickListener(v -> {
           vibrator.vibrate(pattern, -1);
           if (isEmpty()){
               Toast.makeText(getApplicationContext(),"All fields are required",Toast.LENGTH_SHORT).show();
           }
           else {
               if (tos.isChecked()){
                  if (isPasswordOK){
                      nextIcon.setImageResource(R.drawable.no_background);
                      nextBtn.setCardBackgroundColor(ContextCompat.getColor(InformationActivity.this,R.color.cardColor));
                      nextBtn.setEnabled(false);
                      loading.setVisibility(View.VISIBLE);
                      String lastname = Objects.requireNonNull(uLName.getText()).toString().trim();
                      Calendar rightNow = Calendar.getInstance();
                      Date c = rightNow.getTime();
                      SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                      formattedDate = df.format(c);
                      if (act.equals("login")){
                          registerUser(str_username,lastname,str_email, str_phoneNumber,str_country,str_password);
                      }else {
                          if (act.equals("main")){
                              updateData(str_username,lastname,str_email, str_phoneNumber,str_country,str_password);
                          }
                      }
                  }else {
                      Toast.makeText(getApplicationContext(),"Password length must be at least 6 characters long.",Toast.LENGTH_SHORT).show();
                  }
               }else {
                   Toast.makeText(getApplicationContext(),"Please accept the terms of service and continue.",Toast.LENGTH_LONG).show();
               }
               //saveData();
           }
        });
        tos.setOnClickListener(v -> vibrator.vibrate(pattern,-1));
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

    private void updateData(String str_username, String lastname, String str_email, String str_phonenumber, String str_country, String str_password) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userid);
        hashMap.put("first_name", str_username);
        hashMap.put("last_name", lastname);
        hashMap.put("email", str_email);
        hashMap.put("phone_number", str_phonenumber);
        hashMap.put("payment_status", "n");
        hashMap.put("country",str_country);
        hashMap.put("account_open_date",formattedDate);
        hashMap.put("password",str_password);
        hashMap.put("imageUri","");
        hashMap.put("terms_of_service","accepted");
        hashMap.put("verification","Unverified");
        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
            if(task1.isSuccessful()){
                saveData();
            }else
            {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getApplicationContext(),"Your data is not inserted properly, Please try again later.",Toast.LENGTH_SHORT).show();
                nextIcon.setImageResource(R.drawable.ic_next);
                nextBtn.setCardBackgroundColor(ContextCompat.getColor(InformationActivity.this,R.color.white));
                loading.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
                Toast.makeText(InformationActivity.this,"You need to re login to the application", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
            }
        });
    }

    private boolean isEmpty(){
        str_username = Objects.requireNonNull(uFName.getText()).toString();
        str_email = Objects.requireNonNull(uEmail.getText()).toString().trim();
        str_password = Objects.requireNonNull(uPassword.getText()).toString().trim();
        str_country = Objects.requireNonNull(uCountry.getText()).toString().trim().toUpperCase();
        str_phoneNumber = Objects.requireNonNull(uPhoneNumber.getText()).toString().trim();
        isPasswordOK = str_password.length() >= 6;
        return TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password) || TextUtils.isEmpty(str_country) || TextUtils.isEmpty(str_phoneNumber);
    }

    private void saveData(){
        String lastname;
        boolean result;
        try (UserDatabaseHelper db = new UserDatabaseHelper(this)) {
            String id = "1";
            lastname = Objects.requireNonNull(uLName.getText()).toString().trim();
            Calendar rightNow = Calendar.getInstance();
            Date c = rightNow.getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            formattedDate = df.format(c);
            result = db.insertUserData(id, str_username, lastname, str_phoneNumber, str_email, str_country, "no", "", formattedDate, "Unverified");
        }
        if (result){
            Intent registerIntent = new Intent(InformationActivity.this, RegisterActivity.class);
            registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            registerIntent.putExtra("activity","information");
            registerIntent.putExtra("name",str_username);
            registerIntent.putExtra("lname",lastname);
            registerIntent.putExtra("email",str_email);
            registerIntent.putExtra("phone", str_phoneNumber);
            registerIntent.putExtra("payment_status", "n");
            registerIntent.putExtra("country",str_country);
            registerIntent.putExtra("account_open_date",formattedDate);
            registerIntent.putExtra("password",str_password);
            registerIntent.putExtra("tos","accepted");
            registerIntent.putExtra("verification","Unverified");
            startActivity(registerIntent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();
        }else {
            nextIcon.setImageResource(R.drawable.ic_next);
            nextBtn.setCardBackgroundColor(ContextCompat.getColor(InformationActivity.this,R.color.white));
            loading.setVisibility(View.GONE);
            nextBtn.setEnabled(true);
            Toast.makeText(getApplicationContext(),"Data Upload Failed",Toast.LENGTH_SHORT).show();
        }
    }

    private void registerUser(final String fname,final String lname, final String email, final String phoneNumber, final String country, final String password) {
        String lastname = Objects.requireNonNull(uLName.getText()).toString().trim();
        Calendar rightNow = Calendar.getInstance();
        Date c = rightNow.getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
        formattedDate = df.format(c);
        Intent registerIntent = getRegisterIntent(lastname);
        startActivity(registerIntent);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        finish();
       /* mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                String userid = firebaseUser.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userid);
                hashMap.put("first_name", fname);
                hashMap.put("last_name", lname);
                hashMap.put("email", email);
                hashMap.put("phone_number", phoneNumber);
                hashMap.put("payment_status", "n");
                hashMap.put("country",country);
                hashMap.put("account_open_date",formattedDate);
                hashMap.put("password",password);
                hashMap.put("imageUri","");
                hashMap.put("terms_of_service","accepted");
                hashMap.put("verification","Unverified");
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        saveData();
                    }else
                    {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(getApplicationContext(),"Your data is not inserted properly, Please try again later.",Toast.LENGTH_SHORT).show();
                        if (isDarkTheme){
                            nextBtn.setImageResource(R.drawable.ic_nextlight);
                        }else {
                            nextBtn.setImageResource(R.drawable.ic_next);
                        }
                        loading.setVisibility(View.GONE);
                        nextBtn.setEnabled(true);
                        Toast.makeText(InformationActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                if (isDarkTheme){
                    nextBtn.setImageResource(R.drawable.ic_nextlight);
                }else {
                    nextBtn.setImageResource(R.drawable.ic_next);
                }
                loading.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
                Toast.makeText(InformationActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
            }
               });*/
    }

    private @NonNull Intent getRegisterIntent(String lastname) {
        Intent registerIntent = new Intent(InformationActivity.this, RegisterActivity.class);
        registerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
        registerIntent.putExtra("activity","information");
        registerIntent.putExtra("name",str_username);
        registerIntent.putExtra("lname", lastname);
        registerIntent.putExtra("email",str_email);
        registerIntent.putExtra("phone", str_phoneNumber);
        registerIntent.putExtra("payment_status", "n");
        registerIntent.putExtra("country",str_country);
        registerIntent.putExtra("account_open_date",formattedDate);
        registerIntent.putExtra("password",str_password);
        registerIntent.putExtra("tos","accepted");
        registerIntent.putExtra("verification","Unverified");
        registerIntent.putExtra("type","email");
        return registerIntent;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        InformationActivity.this.finish();
    }
}