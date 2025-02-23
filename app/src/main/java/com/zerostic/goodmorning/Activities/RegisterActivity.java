package com.zerostic.goodmorning.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Users;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 Coded by iamjayantjha
 **/

public class RegisterActivity extends AppCompatActivity {
    Dialog dialog;
    StorageTask uploadTask;
    TextView title,message, heading,nextBtn;
    RadioButton rb1,rb2,rb3,rb4,rb5,rb6;
    Uri imageUri;
    String typeOfSleeper, voiceSettings, year, month, date, uName = "", uEmail, uPhoneNumber, uCountry, uPassword,account_open_date,lname,tos,activity,type, myUrl="";
    FirebaseUser mCurrentUser;
    StorageReference storageReference;
    Vibrator vibrator;
    CircleImageView profilePicture;
    int yr,updatedMon;
    final long[] pattern = {10,20};
    boolean isUserCreated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Utils.blackIconStatusBar(RegisterActivity.this, R.color.background);
        heading = findViewById(R.id.heading);
        storageReference = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        profilePicture = findViewById(R.id.profilePicture);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        nextBtn = findViewById(R.id.nextBtn);
        rb1 = findViewById(R.id.rb1);
        rb2 = findViewById(R.id.rb2);
        rb3 = findViewById(R.id.rb3);
        rb4 = findViewById(R.id.rb4);
        rb5 = findViewById(R.id.rb5);
        rb6 = findViewById(R.id.rb6);
        Calendar rightNow = Calendar.getInstance();
        Date c = rightNow.getTime();
        SimpleDateFormat yf = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat mf = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
        year = yf.format(c);
        month = mf.format(c);
        date = df.format(c);
        activity = getIntent().getStringExtra("activity");
        if (activity.equals("information")) {
            uName = getIntent().getStringExtra("name");
            lname = getIntent().getStringExtra("lname");
            uEmail = getIntent().getStringExtra("email");
            uPhoneNumber = getIntent().getStringExtra("phone");
            uCountry = getIntent().getStringExtra("country");
            tos = getIntent().getStringExtra("tos");
            uPassword = getIntent().getStringExtra("password");
            account_open_date = getIntent().getStringExtra("account_open_date");
            type = getIntent().getStringExtra("type");
        } else if (activity.equals("main")) {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                    if (getApplicationContext() == null){
                        return;
                    }
                    Users user = snapshot.getValue(Users.class);
                    if(user == null){
                        Intent info = new Intent(RegisterActivity.this, InformationActivity.class);
                        info.putExtra("act","main");
                        info.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(info);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        finish();
                    }else{
                        uName = user.getFirst_name();
                        lname = user.getLast_name();
                        uEmail = user.getEmail();
                        uPhoneNumber = user.getPhone_number();
                        uCountry = user.getCountry();
                        tos = user.getTerms_of_service();
                        uPassword = user.getPassword();
                        account_open_date = user.getAccount_open_date();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                }
            });
        }
        String headingText = "Just few more things "+uName+",";
        heading.setText(headingText);
        profilePicture.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (isUserCreated) {
                choosePicture();
            }else{
                typeOfSleeper = "2";
                voiceSettings = "2";
                Dialog registrationDialog = new Dialog(RegisterActivity.this);
                registrationDialog.setContentView(R.layout.theme_dialog);
                Objects.requireNonNull(registrationDialog.getWindow()).setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
                registrationDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                TextView title = registrationDialog.findViewById(R.id.headerText);
                TextView message = registrationDialog.findViewById(R.id.confirmText);
                title.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                title.setText(R.string.registering_user);
                String text = "Hold on "+uName+" while we create an account for you";
                message.setText(text);
                registrationDialog.show();
                registrationDialog.setCancelable(false);
                createUser(registrationDialog);
            }
        });
        nextBtn.setOnClickListener(v -> {
        if (rb1.isChecked()){
            typeOfSleeper = "3";
        }else if (rb2.isChecked()){
            typeOfSleeper = "2";
        }else if (rb3.isChecked()){
            typeOfSleeper = "1";
        }

        if (rb4.isChecked()){
            voiceSettings = "3";
        }else if (rb5.isChecked()){
            voiceSettings = "2";
        }else if (rb6.isChecked()){
            voiceSettings = "1";
        }

        if (TextUtils.isEmpty(typeOfSleeper)||TextUtils.isEmpty(voiceSettings)){
            vibrator.vibrate(pattern,-1);
            Toast.makeText(getApplicationContext(),"It would be better if you could give us these information's",Toast.LENGTH_SHORT).show();
        }else {
            vibrator.vibrate(pattern,-1);
            if (type.equals("email")){
                saveDataForEmailIdandPassword();
            }else if (type.equals("anonymous")){
                saveDataForAnonymous();
            }
        }

        });
    }

    private void createUser(Dialog registrationDialog) {
        vibrator.vibrate(pattern,-1);
        if (type.equals("email")){
            UserDatabaseHelper db = new UserDatabaseHelper(this);
            boolean result = db.insertUserSettings("1","1",voiceSettings,"1",typeOfSleeper,"1","yes");
            if (result){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mAuth.createUserWithEmailAndPassword(uEmail,uPassword).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        isUserCreated = true;
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", userid);
                        hashMap.put("first_name", uName);
                        hashMap.put("last_name", lname);
                        hashMap.put("email", uEmail);
                        hashMap.put("phone_number", uPhoneNumber);
                        hashMap.put("payment_status", "n");
                        hashMap.put("country",uCountry);
                        hashMap.put("account_open_date",account_open_date);
                        hashMap.put("password",uPassword);
                        hashMap.put("imageUri",myUrl);
                        hashMap.put("terms_of_service","accepted");
                        hashMap.put("verification","Unverified");
                        reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()){
                                saveUserData(registrationDialog);
                            }else {
                                FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task2 -> {
                                    if (task2.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                        finish();
                                    }

                                });
                                Toast.makeText(RegisterActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(RegisterActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                        registrationDialog.dismiss();
                    }
                });
            }
        }else if (type.equals("anonymous")){
            Toast.makeText(RegisterActivity.this,"Anonymous users can change image from profile page.",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveUserData(Dialog registrationDialog) {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        boolean result = db.insertUserData("1",uName,lname,uPhoneNumber,uEmail,uCountry,"no","",account_open_date,"Unverified");
        if (result) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            String userid = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(userid);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", userid);
            hashMap.put("theme", "1");
            hashMap.put("voice_settings", voiceSettings);
            hashMap.put("weather_settings", "1");
            hashMap.put("type_of_sleeper", typeOfSleeper);
            hashMap.put("weather", "1");
            hashMap.put("update_prompt", "yes");
            reference.setValue(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String currency;
                    if (uCountry.equals("INDIA")) {
                        currency = "INR";
                    } else {
                        currency = "USD";
                    }
                    updatedMon = Integer.parseInt(month);
                    yr = Integer.parseInt(year);
                    updatedMon = updatedMon + 1;
                    if (updatedMon > 12) {
                        updatedMon = 1;
                        yr = yr + 1;
                    }
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users Subscription").child(userid);
                    HashMap<String, String> hashMap1 = new HashMap<>();
                    hashMap1.put("subscription", "free_trial");
                    hashMap1.put("payDate", date + "/" + month + "/" + year);
                    hashMap1.put("nextPayDate", date + "/" + updatedMon + "/" + yr);
                    hashMap1.put("currency", currency);
                    reference1.setValue(hashMap1).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            registrationDialog.dismiss();
                            choosePicture();
                        }
                    });
                }
            });
        }
    }

    private void saveDataForAnonymous() {
        dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.theme_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.registering_user);
        String text = "Hold on "+uName+" while we create an account for you";
        message.setText(text);
        dialog.show();
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        boolean result = db.insertUserSettings("1","1",voiceSettings,"1",typeOfSleeper,"1","yes");
        if (result){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.signInAnonymously().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                    assert firebaseUser != null;
                    String userid = firebaseUser.getUid();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", userid);
                    hashMap.put("first_name", uName);
                    hashMap.put("last_name", lname);
                    hashMap.put("email", uEmail);
                    hashMap.put("phone_number", uPhoneNumber);
                    hashMap.put("payment_status", "n");
                    hashMap.put("country",uCountry);
                    hashMap.put("account_open_date",account_open_date);
                    hashMap.put("password",uPassword);
                    hashMap.put("imageUri",myUrl);
                    hashMap.put("terms_of_service","accepted");
                    hashMap.put("verification","Unverified");
                    reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                        if(task1.isSuccessful()){
                            saveUserData();
                        }else
                        {
                            FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()){
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                    finish();
                                }

                            });
                            Toast.makeText(RegisterActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    private void saveDataForEmailIdandPassword() {
        dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.theme_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        TextView title = dialog.findViewById(R.id.headerText);
        TextView message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.registering_user);
        String text = "Hold on "+uName+" while we create an account for you";
        message.setText(text);
        dialog.show();
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        boolean result = db.insertUserSettings("1","1",voiceSettings,"1",typeOfSleeper,"1","yes");
        if (result){
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(uEmail,uPassword).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                isUserCreated = true;
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                assert firebaseUser != null;
                String userid = firebaseUser.getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", userid);
                hashMap.put("first_name", uName);
                hashMap.put("last_name", lname);
                hashMap.put("email", uEmail);
                hashMap.put("phone_number", uPhoneNumber);
                hashMap.put("payment_status", "n");
                hashMap.put("country",uCountry);
                hashMap.put("account_open_date",account_open_date);
                hashMap.put("password",uPassword);
                hashMap.put("imageUri",myUrl);
                hashMap.put("terms_of_service","accepted");
                hashMap.put("verification","Unverified");
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if(task1.isSuccessful()){
                        saveUserData();
                    }else
                    {
                        FirebaseAuth.getInstance().getCurrentUser().delete().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()){
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                                finish();
                            }

                        });
                        Toast.makeText(RegisterActivity.this,"Registration Failed"+"\n"+"Please retry after sometime...", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            });
        }
    }

    private void saveUserData() {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        boolean result = db.insertUserData("1",uName,lname,uPhoneNumber,uEmail,uCountry,"no","",account_open_date,"Unverified");
        if (result){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            String userid = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Settings").child(userid);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", userid);
            hashMap.put("theme", "1");
            hashMap.put("voice_settings", voiceSettings);
            hashMap.put("weather_settings", "1");
            hashMap.put("type_of_sleeper", typeOfSleeper);
            hashMap.put("weather", "1");
            hashMap.put("update_prompt", "yes");
            reference.setValue(hashMap).addOnCompleteListener(task -> {
              if (task.isSuccessful()){
                  String currency;
                  if (uCountry.equals("INDIA")){
                      currency = "INR";
                  }else {
                      currency = "USD";
                  }
                  updatedMon = Integer.parseInt(month);
                  yr = Integer.parseInt(year);
                  updatedMon = updatedMon+1;
                  if (updatedMon>12){
                      updatedMon = 1;
                      yr = yr+1;
                  }
                  DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users Subscription").child(userid);
                  HashMap<String, String> hashMap1 = new HashMap<>();
                  hashMap1.put("subscription","free_trial");
                  hashMap1.put("payDate", date+"/"+month+"/"+year);
                  hashMap1.put("nextPayDate", date+"/"+updatedMon+"/"+yr);
                  hashMap1.put("currency", currency);
                  reference1.setValue(hashMap1).addOnCompleteListener(task1 -> {
                      if (task1.isSuccessful()){
                          dialog.dismiss();
                          Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                          mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          startActivity(mainIntent);
                          overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                          finish();
                      }else {
                          Toast.makeText(getApplicationContext(),uName+" we were not able to start your free trial please mail us at cs@zestit.tech and restart the application to continue further",Toast.LENGTH_LONG).show();
                      }
                  });
              }else {
                  dialog.dismiss();
                  Toast.makeText(getApplicationContext(),"Something went wrong please check your internet connection",Toast.LENGTH_SHORT).show();
              }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        RegisterActivity.this.finish();
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(Intent.createChooser(intent,"Select Profile Photo"), 1);
        startActivityResultLauncher.launch(intent);
    }

    ActivityResultLauncher<Intent> startActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // There are no request codes
                    Intent data = result.getData();
                    assert data != null;
                    imageUri = data.getData();
                    profilePicture.setImageURI(imageUri);
                    uploadPicture();
                }
            });


    private void uploadPicture() {
        dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.theme_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.image_upload_title);
        message.setText(R.string.image_upload_message);
        dialog.show();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        StorageReference riversRef = storageReference.child("Profile Pictures").child(mCurrentUser.getUid()+".jpg");

        uploadTask = riversRef.putFile(imageUri);
        uploadTask.continueWithTask(task -> {
            if (!task.isSuccessful()){
                throw Objects.requireNonNull(task.getException());
            }
            return riversRef.getDownloadUrl();
        }).addOnCompleteListener(task -> {
        }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
            if (task.isSuccessful()){
                Uri downloadUri = task.getResult();
                myUrl = downloadUri.toString();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
                reference.child("imageUri").setValue(myUrl);
                Glide.with(RegisterActivity.this).load(imageUri).into(profilePicture);
                updateData();
            }else {
                Toast.makeText(RegisterActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(RegisterActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show());
    }

    private void updateData() {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        String id = "1";
        boolean result = db.updateUserData(id,uName,lname,uPhoneNumber,uEmail,uCountry,"n", myUrl,account_open_date,"Unverified");
        if (result){
            if (dialog.isShowing()){
                dialog.dismiss();
            }
            Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
            finish();
        }else {
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }


}