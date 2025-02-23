package com.zerostic.goodmorning.Activities;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.TextUtils;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import java.util.HashMap;
import java.util.Objects;

public class ContactUsActivity extends AppCompatActivity {

    MaterialCardView faqCard,messageTypeCard;
    RelativeLayout rl1,rl2,rl3,rl4,rl5;
    String theme;
    boolean isSettingsAvailable;
    ImageView expandBtn,expand;
    TextView faq1,faq2,faq3,faq4;
    Dialog dialog;
    TextView title,message,messageTypeHeading;
    Button okay;
    Chip suggestion,query,complaint;
    EditText messageText;
    Animation animation;
    FloatingActionButton sendBtn;
    String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        readUserSettings();
        TextView contactEmail = findViewById(R.id.contactEmail);
        faqCard = findViewById(R.id.faqCard);
        messageTypeCard = findViewById(R.id.messageTypeCard);
        rl1 = findViewById(R.id.rl1);
        rl2 = findViewById(R.id.rl2);
        rl4 = findViewById(R.id.rl4);
        rl5 = findViewById(R.id.rl5);
        rl3 = findViewById(R.id.rl3);
        faq1 = findViewById(R.id.faq1);
        faq2 = findViewById(R.id.faq2);
        faq3 = findViewById(R.id.faq3);
        faq4 = findViewById(R.id.faq4);
        sendBtn = findViewById(R.id.sendBtn);
        messageText = findViewById(R.id.messageText);
        suggestion = findViewById(R.id.suggestion);
        query = findViewById(R.id.query);
        complaint = findViewById(R.id.complaint);
        messageTypeHeading = findViewById(R.id.messageTypeHeading);
        expandBtn = findViewById(R.id.expandBtn);
        expand = findViewById(R.id.expand);
        dialog = new Dialog(ContactUsActivity.this);
        dialog.setContentView(R.layout.permission_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
        okay = dialog.findViewById(R.id.okayBtn);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {40,80};
        if (!isNetworkConnected()){
            messageTypeCard.setEnabled(false);
            rl4.setEnabled(false);
        }
        String fName = getIntent().getStringExtra("name");
        String contactVal = fName +" you can contact us at support@zerostic.com we would love to hear from you.";
        contactEmail.setText(contactVal);
        rl2.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (rl3.getVisibility() == View.VISIBLE){
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                rl3.startAnimation(animation);
                new Handler().postDelayed(() -> rl3.setVisibility(View.GONE), 420);
                if (theme.equals("2")){
                    expandBtn.setImageResource(R.drawable.ic_baseline_expand_more_24light);
                }else {
                    expandBtn.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }
            }   else {
                rl3.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_down);
                rl3.startAnimation(animation);
                if (theme.equals("2")){
                    expandBtn.setImageResource(R.drawable.ic_baseline_expand_less_24light);
                }else {
                    expandBtn.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });

        rl4.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (rl5.getVisibility() == View.VISIBLE){
                TransitionManager.beginDelayedTransition(messageTypeCard,
                        new AutoTransition());
                rl5.setVisibility(View.GONE);
                messageTypeHeading.setText(R.string.message_us);
                if (suggestion.isChecked() || query.isChecked() || complaint.isChecked()){
                    suggestion.setChecked(false);
                    query.setChecked(false);
                    complaint.setChecked(false);
                    messageText.setText("");
                }
                if (theme.equals("2")){
                    expand.setImageResource(R.drawable.ic_baseline_expand_more_24light);
                }else {
                    expand.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }
                if (rl1.getVisibility() == View.VISIBLE){
                    rl1.setVisibility(View.GONE);
                    animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                    rl1.startAnimation(animation);
                }
            }   else {

                TransitionManager.beginDelayedTransition(messageTypeCard,
                        new AutoTransition());
                rl5.setVisibility(View.VISIBLE);
                messageTypeHeading.setText(R.string.message_type);
                if (theme.equals("2")){
                    expand.setImageResource(R.drawable.ic_baseline_expand_less_24light);
                }else {
                    expand.setImageResource(R.drawable.ic_baseline_expand_less_24);
                }
            }
        });
        suggestion.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (suggestion.isChecked()){
                rl1.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_down);
                messageTypeHeading.setText(R.string.suggestion);
                type = "suggestion";
            }else {
                rl1.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                messageText.setText("");
            }
            rl1.startAnimation(animation);
        });

        sendBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            String msg = messageText.getText().toString().trim();
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            if (TextUtils.isEmpty(msg)){
                Toast.makeText(getApplicationContext(),"Please type your "+type+" in the box above",Toast.LENGTH_SHORT).show();
            }else {
                if (isNetworkConnected()){
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Message").child(type).child(uid);
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", uid);
                    hashMap.put("name", fName);
                    hashMap.put("message", msg);
                    reference.setValue(hashMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            title.setText(type.toUpperCase());
                            message.setText(fName+" your "+type+" has been sent and we may reach out to you for the same in few days.\nThanks");
                            dialog.show();
                            rl1.setVisibility(View.GONE);
                            animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                            messageText.setText("");
                            messageTypeHeading.setText(R.string.message_type);
                            rl1.startAnimation(animation);
                            if (suggestion.isChecked()){
                                suggestion.setChecked(false);
                            }else if (query.isChecked()){
                                query.setChecked(false);
                            }else if (complaint.isChecked()){
                                complaint.setChecked(false);
                            }
                        }else {
                            title.setText(type.toUpperCase());
                            message.setText(fName+" your "+type+" is not yet sent something went wrong please try again later.");
                            dialog.show();
                        }
                    });
                }else {
                    Toast.makeText(getApplicationContext(),"Ooops you are not connected.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        query.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (query.isChecked()){
                rl1.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_down);
                messageTypeHeading.setText(R.string.query);
                type = "query";
            }else {
                rl1.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                messageText.setText("");
            }
            rl1.startAnimation(animation);
        });

        complaint.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            if (complaint.isChecked()){
                rl1.setVisibility(View.VISIBLE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_down);
                messageTypeHeading.setText(R.string.complaint);
                type = "complaint";
            }else {
                rl1.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                messageText.setText("");
            }
            rl1.startAnimation(animation);
        });

        faq1.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(faq1.getText().toString());
            message.setText(R.string.why_user_needs_to_signIn);
            dialog.show();
        });
        faq2.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(faq2.getText().toString());
            message.setText(R.string.why_user_has_to_watch_ads);
            dialog.show();
        });
        faq3.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(faq3.getText().toString());
            message.setText(R.string.how_to_contact);
            dialog.show();
        });
        faq4.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            title.setText(faq4.getText().toString());
            message.setText(R.string.bluetooth_problem);
            dialog.show();
        });
        okay.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            dialog.dismiss();
        });
    }

    private void readUserSettings(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserSettings("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            theme = cursor.getString(1);
            isSettingsAvailable = true;
        }
        else {
            isSettingsAvailable = false;
            theme = "";
        }
    }

    @Override
    public void onBackPressed() {
        if (rl3.getVisibility() == View.VISIBLE){
            animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
            rl3.startAnimation(animation);
            new Handler().postDelayed(() -> rl3.setVisibility(View.GONE), 420);
            if (theme.equals("2")){
                expandBtn.setImageResource(R.drawable.ic_baseline_expand_more_24light);
            }else {
                expandBtn.setImageResource(R.drawable.ic_baseline_expand_more_24);
            }
        }else if (rl5.getVisibility() == View.VISIBLE){
            if (rl1.getVisibility() == View.VISIBLE){
                rl1.setVisibility(View.GONE);
                animation = AnimationUtils.loadAnimation(this,R.anim.slide_up);
                rl1.startAnimation(animation);
                if (suggestion.isChecked() || query.isChecked() || complaint.isChecked()){
                    suggestion.setChecked(false);
                    query.setChecked(false);
                    complaint.setChecked(false);
                    messageText.setText("");
                }
            }else {
                TransitionManager.beginDelayedTransition(messageTypeCard,
                        new AutoTransition());
                rl5.setVisibility(View.GONE);
                messageTypeHeading.setText(R.string.message_us);
                if (theme.equals("2")){
                    expand.setImageResource(R.drawable.ic_baseline_expand_more_24light);
                }else {
                    expand.setImageResource(R.drawable.ic_baseline_expand_more_24);
                }

            }
        }
        else {
            super.onBackPressed();
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }
}