package com.zerostic.goodmorning.Activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
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
import com.zerostic.goodmorning.Application.ChangeImageBottomSheet;
import com.zerostic.goodmorning.Application.Users;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import org.jetbrains.annotations.NotNull;
import java.util.HashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements ChangeImageBottomSheet.bottomSheetListener {
    CircleImageView profilePicture;
    TextInputEditText nameEditText, emailEditText;
    String name,email,imageURI,fName,verification,lName,param2;
    ImageView verified;
    FirebaseAuth mAuth;
    FirebaseUser mCurrentUser;
    Dialog dialog, dialog2;
    Button okay;
    TextView saveBtn,title,message, userSince;
    String fn="",ln="";
    Vibrator vibrator;
    DatabaseReference reference;
    String myUrl,phone,country,payStat,accountOpenDate,password = "";
    StorageReference storageReference;
    StorageTask uploadTask;
    MaterialCardView profilePictureCard;
    final long[] pattern = {10,20};
    Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Utils.blackIconStatusBar(ProfileActivity.this, R.color.background);
        imageURI = getIntent().getStringExtra("imageURI");
        userSince = findViewById(R.id.userSince);
        readUserData();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference();
        profilePicture = findViewById(R.id.profilePicture);
        saveBtn = findViewById(R.id.saveBtn);
        nameEditText = findViewById(R.id.nameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        verified = findViewById(R.id.verified);
        profilePictureCard = findViewById(R.id.profilePictureCard);
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        nameEditText.setText(name);
        dialog = new Dialog(ProfileActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        okay = dialog.findViewById(R.id.okayBtn);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.confirmation);
        message.setText(R.string.logout);
        emailEditText.setText(email);
        if (imageURI.equals("")||imageURI.isEmpty()){
            Glide.with(ProfileActivity.this).load(R.mipmap.ic_profilepicture).into(profilePicture);
        }else {
            Glide.with(ProfileActivity.this).load(imageURI).into(profilePicture);
        }
        if (isNetworkConnected()){
            profilePicture.setOnLongClickListener(v -> {
                Toast.makeText(getApplicationContext(),"Click your profile picture to change it",Toast.LENGTH_SHORT).show();
                return true;
            });
            nameEditText.setEnabled(true);
            if (verification.equals("Unverified")){
                verified.setImageResource(R.drawable.ic_unverified);
                verified.setOnClickListener(v -> {
                    vibrator.vibrate(pattern,-1);
                    dialog.show();
                    okay.setOnClickListener(v1 -> {
                        dialog.dismiss();
                        mCurrentUser.sendEmailVerification().addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                vibrator.vibrate(pattern, -1);
                                Toast.makeText(getApplicationContext(),"Email verification link has been sent to\n"+email,Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                                Intent login = new Intent(getApplicationContext(), LoginActivity.class);
                                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(login);
                                overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
                                finish();
                            }
                        });
                    });
                });
            }else if (verification.equals("Verified")){
                verified.setImageResource(R.drawable.ic_verified);
                verified.setOnClickListener(v -> {
                    vibrator.vibrate(pattern,-1);
                    title.setText(R.string.verifiedUser);
                    String msg = "Hey "+fName+" you are now a verified user.";
                    message.setText(msg);
                    dialog.show();
                    okay.setOnClickListener(v12 -> {
                        vibrator.vibrate(pattern, -1);
                        dialog.dismiss();
                    });
                });
            }
        }
        profilePicture.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (isNetworkConnected()){
                readDataFromServer();
                ChangeImageBottomSheet changeImageBottomSheet = new ChangeImageBottomSheet();
                changeImageBottomSheet.show(getSupportFragmentManager(),"changeImageBottomSheet");
            }else {
                Toast.makeText(getApplicationContext(),"Sorry you are not connected to the internet, please connect and try again.",Toast.LENGTH_LONG).show();
            }
        });

        nameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!nameEditText.getText().toString().equals(name)){
                    saveBtn.setTextColor(getResources().getColor(R.color.selected));
                    saveBtn.setOnClickListener(v -> {
                        fn="";
                        ln="";
                        vibrator.vibrate(pattern, -1);
                        String a = nameEditText.getText().toString().trim();
                        for (String val: a.split(" ")){
                            if (fn.equals("")){
                                fn=val;
                            }else if (ln.equals("")){
                                ln=val;
                            }else {
                                ln=ln+" "+val;
                            }
                        }
                        reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (getApplicationContext() == null){
                                    return;
                                }
                                Users user = snapshot.getValue(Users.class);
                                assert user != null;
                                updateData(user,fn,ln);

                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                        readUserData();
                    });
                }else {
                    saveBtn.setTextColor(getResources().getColor(R.color.defaultText));
                    saveBtn.setOnClickListener(v -> {
                        vibrator.vibrate(pattern, -1);
                        Toast.makeText(getApplicationContext(),"Nothing to change "+fName,Toast.LENGTH_SHORT).show();
                        ProfileActivity.this.finish();
                    });
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void readDataFromServer(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (getApplicationContext() == null){
                    return;
                }
                Users user = snapshot.getValue(Users.class);
                assert user != null;
                password = user.getPassword();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //startActivityForResult(intent, 1);
        startActivityForResult(Intent.createChooser(intent,"Select Profile Photo"), 1);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
            uploadPicture();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void uploadPicture() {
        dialog2 = new Dialog(ProfileActivity.this);
        dialog2.setContentView(R.layout.theme_dialog);
        dialog2.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
        dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog2.setCancelable(false);
        TextView title = dialog2.findViewById(R.id.headerText);
        TextView message = dialog2.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        title.setText(R.string.image_upload_title);
        message.setText(R.string.image_upload_message);
        dialog2.show();

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
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("id", mCurrentUser.getUid());
                hashMap.put("first_name", fName);
                hashMap.put("last_name", lName);
                hashMap.put("email", email);
                hashMap.put("phone_number", phone);
                hashMap.put("country",country);
                hashMap.put("password",password);
                hashMap.put("account_open_date",accountOpenDate);
                hashMap.put("imageUri",myUrl);
                hashMap.put("payment_status",payStat);
                hashMap.put("terms_of_service","accepted");
                hashMap.put("verification",verification);
                reference.setValue(hashMap);
                Glide.with(ProfileActivity.this).load(imageUri).into(profilePicture);
                updateDataInLocalStorage();
            }else {
                Toast.makeText(ProfileActivity.this,"Failed to upload image",Toast.LENGTH_SHORT).show();
                dialog2.dismiss();
            }
        }).addOnFailureListener(e -> Toast.makeText(ProfileActivity.this,""+e.getMessage(),Toast.LENGTH_SHORT).show());
    }

    private void updateDataInLocalStorage() {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        String id = "1";
        boolean result = db.updateUserData(id,fName,lName,phone,email,country,payStat, myUrl,accountOpenDate,verification);
        if (result){
            dialog2.dismiss();
            imageURI = myUrl;
        }else {
            Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
        }
    }

    private void updateData(Users user,String first, String last) {
                HashMap<String, String> hashMap = new HashMap<>();
                hashMap.put("id", user.getId());
                hashMap.put("first_name", first);
                hashMap.put("last_name", last);
                hashMap.put("email", user.getEmail());
                hashMap.put("phone_number", user.getPhone_number());
                hashMap.put("payment_status", user.getPayment_status());
                hashMap.put("country",user.getCountry());
                hashMap.put("account_open_date",user.getAccount_open_date());
                hashMap.put("password",user.getPassword());
                hashMap.put("imageUri",user.getImageUri());
                hashMap.put("terms_of_service",user.getTerms_of_service());
                hashMap.put("verification",user.getVerification());
                reference.setValue(hashMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                         updateUserData(user,first,last);
                         dialog.setCancelable(false);
                         title.setText(R.string.information);
                         message.setText(first+" we have updated your data if you want to update again please restart the application.");
                         dialog.show();
                         okay.setOnClickListener(v -> {
                             vibrator.vibrate(pattern, -1);
                             dialog.dismiss();
                             ProfileActivity.this.finish();
                         });
                    }
                });
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            param2 = cursor.getString(0);
            fName = cursor.getString(1);
            lName = cursor.getString(2);
            phone = cursor.getString(3);
            email = cursor.getString(4);
            country = cursor.getString(5);
            payStat = cursor.getString(6);
            accountOpenDate = cursor.getString(8);
            verification = cursor.getString(9);
            name = fName+" "+lName;
            userSince.setText("User since "+accountOpenDate);
        }
    }

    private void updateUserData(Users users,String first,String last) {
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        String id = "1";
        db.updateUserData(id, first, last, users.getPhone_number(), users.getEmail(), users.getCountry(), users.getPayment_status(), users.getImageUri(), users.getAccount_open_date(), users.getVerification());
    }

    @Override
    public void onClick(String text) {
     //   Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
        if (isNetworkConnected()){
            if (text.equals("upload")){
                choosePicture();
            }else if (text.equals("remove")){
                if (imageURI.equals("")){
                    Toast.makeText(getApplicationContext(),"Sorry you don't have an image to remove.",Toast.LENGTH_SHORT).show();
                }else {
                    profilePicture.setImageResource(R.mipmap.ic_profilepicture);
                    myUrl = "";
                    dialog2 = new Dialog(ProfileActivity.this);
                    dialog2.setContentView(R.layout.theme_dialog);
                    dialog2.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
                    dialog2.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog2.setCancelable(false);
                    TextView title = dialog2.findViewById(R.id.headerText);
                    TextView message = dialog2.findViewById(R.id.confirmText);
                    title.setVisibility(View.VISIBLE);
                    message.setVisibility(View.VISIBLE);
                    title.setText(R.string.image_upload_title);
                    message.setText(R.string.image_upload_message);
                    dialog2.show();
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(mCurrentUser.getUid());
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("id", mCurrentUser.getUid());
                    hashMap.put("first_name", fName);
                    hashMap.put("last_name", lName);
                    hashMap.put("email", email);
                    hashMap.put("phone_number", phone);
                    hashMap.put("country",country);
                    hashMap.put("password",password);
                    hashMap.put("account_open_date",accountOpenDate);
                    hashMap.put("imageUri",myUrl);
                    hashMap.put("payment_status",payStat);
                    hashMap.put("terms_of_service","accepted");
                    hashMap.put("verification",verification);
                    reference.setValue(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            removePictureFromStorage();
                            updateDataInLocalStorage();
                        }else {
                            Toast.makeText(getApplicationContext(),"Sorry your picture is removed yet.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }else {
            Toast.makeText(getApplicationContext(),"Cannot find an active internet connection.",Toast.LENGTH_SHORT).show();
        }
    }

    private void removePictureFromStorage() {
        StorageReference riversRef = storageReference.child("Profile Pictures").child(mCurrentUser.getUid()+".jpg");
        riversRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(),"Profile Picture removed successfully.",Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Uh-oh, an error occurred!
                Toast.makeText(getApplicationContext(),"Something went wrong, please try again later.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void signOut(View view) {
        vibrator.vibrate(pattern,-1);
        FirebaseAuth.getInstance().signOut();
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        db.deleteAllUserData("1",db.getWritableDatabase());
        Intent intent = new Intent(ProfileActivity.this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left,R.anim.slide_out_right);
        ProfileActivity.this.finish();
        //finish all activities

    }
}