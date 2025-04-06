package com.zerostic.goodmorning.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.appcheck.FirebaseAppCheck;
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;

import java.util.Objects;


public class LoginActivity extends AppCompatActivity {

    TextInputEditText mEmail, mPassword;
    TextView noAccount,forgotPassword;
    MaterialCardView nextBtn, continueWithoutLogin;
    LottieAnimationView animationView;
    int currentNightMode;
    boolean isDarkTheme;
    FirebaseAuth mAuth;
    DatabaseReference reference;
    ImageView nextIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Utils.blackIconStatusBar(LoginActivity.this, R.color.background);
        FirebaseApp.initializeApp(this);
        FirebaseAppCheck firebaseAppCheck = FirebaseAppCheck.getInstance();
        firebaseAppCheck.installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
        );
        mEmail = findViewById(R.id.emailEditText);
        mPassword = findViewById(R.id.passwordEditText);
        continueWithoutLogin = findViewById(R.id.continueWithoutLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        noAccount = findViewById(R.id.noAccount);
        nextBtn = findViewById(R.id.nextBtn);
        animationView = findViewById(R.id.animationView);
        nextIcon = findViewById(R.id.nextIcon);
        currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        isDarkTheme = currentNightMode == Configuration.UI_MODE_NIGHT_YES;
        mAuth = FirebaseAuth.getInstance();
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10,20};
        noAccount.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            Intent regIntent = new Intent(LoginActivity.this, InformationActivity.class);
            regIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            regIntent.putExtra("act","login");
            startActivity(regIntent);
            overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
        });
        nextBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            String str_email = Objects.requireNonNull(mEmail.getText()).toString().trim();
            String str_password = Objects.requireNonNull(mPassword.getText()).toString().trim();
            nextIcon.setImageResource(R.drawable.no_background);
            nextBtn.setEnabled(false);
            animationView.setVisibility(View.VISIBLE);
            if (TextUtils.isEmpty(str_email)||TextUtils.isEmpty(str_password)){
                nextIcon.setImageResource(R.drawable.ic_next);
                animationView.setVisibility(View.GONE);
                nextBtn.setEnabled(true);
                Toast.makeText(LoginActivity.this,"All fields are required..",Toast.LENGTH_LONG).show();
            }else {
                nextBtn.setCardBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.cardColor));
                reference = FirebaseDatabase.getInstance().getReference();
                mAuth.signInWithEmailAndPassword(str_email,str_password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Users").child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Intent main = new Intent(LoginActivity.this, MainActivity.class);
                                main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(main);
                                overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
                                finish();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                    }else {
                        nextBtn.setCardBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.white));
                        nextIcon.setImageResource(R.drawable.ic_next);
                        animationView.setVisibility(View.GONE);
                        nextBtn.setEnabled(true);
                        Toast.makeText(LoginActivity.this,"Authentication Failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    nextBtn.setCardBackgroundColor(ContextCompat.getColor(LoginActivity.this,R.color.white));
                    nextIcon.setImageResource(R.drawable.ic_next);
                    animationView.setVisibility(View.GONE);
                    nextBtn.setEnabled(true);

                });
            }
        });

        forgotPassword.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            String email = Objects.requireNonNull(mEmail.getText()).toString().trim();
            if (TextUtils.isEmpty(email)){
                Toast.makeText(getApplicationContext(),"Enter your email in the email field",Toast.LENGTH_SHORT).show();
            }else {
                Dialog dialog = new Dialog(LoginActivity.this);
                dialog.setContentView(R.layout.theme_dialog);
                dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.picture_upload_bg));
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                dialog.setCancelable(false);
                TextView title = dialog.findViewById(R.id.headerText);
                TextView message = dialog.findViewById(R.id.confirmText);
                title.setVisibility(View.VISIBLE);
                message.setVisibility(View.VISIBLE);
                title.setText(R.string.resetPassword);
                message.setText("We are sending an email to "+email+" to reset your password.");
                LottieAnimationView animationView = dialog.findViewById(R.id.animationView1);
                dialog.show();
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                       // Toast.makeText(getApplicationContext(),"Password reset link is sent to your email\n"+email,Toast.LENGTH_SHORT).show();
                        animationView.setVisibility(View.GONE);
                        message.setText("Password reset link is sent to your email\n"+email+"\nPlease check your email and follow the instructions.");
                    }else {
                       // Toast.makeText(getApplicationContext(),"Failed to send password reset link",Toast.LENGTH_SHORT).show();
                        animationView.setVisibility(View.GONE);
                        message.setText("Failed to send password reset link to your email\n"+email+"\nPlease check your email and try again.");
                    }
                    dialog.setCancelable(true);
                });
            }
        });
        continueWithoutLogin.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            continueWithoutLogin();
        });
    }

    public void continueWithoutLogin() {
       /* mAuth.signInAnonymously().addOnCompleteListener(task -> {
           // Toast.makeText(LoginActivity.this, "You are now logged in as a guest "+ Objects.requireNonNull(mAuth.getCurrentUser()).getUid(), Toast.LENGTH_SHORT).show();
            if (task.isSuccessful()){

            }
        });*/
        Intent info = new Intent(LoginActivity.this, AnonymousUserInfoActivity.class);
        info.putExtra("act","login");
        info.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(info);
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }
}