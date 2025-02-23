package com.zerostic.goodmorning.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Coupon;
import com.zerostic.goodmorning.Application.Users;
import com.zerostic.goodmorning.Application.Utils;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import nl.dionsegijn.konfetti.KonfettiView;
import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class PaymentActivity extends AppCompatActivity implements PaymentResultListener {
    private static final String TAG = PaymentActivity.class.getSimpleName();
    String uid,year,paid,name,email,image,currency,headingText,amount,month,date,nextPaymentDate,plan,currentSub,payDate,renewalDate;
    Button payBtn,okay;
    RadioButton plan1,plan2;
    TextView heading,title,message,couponBtn,refundBtn,planDetails,currentPlan,lastPayDate,nextPayDate,lifeTimeMembershipTitle,lifeTimeMembershipMessage;
    MaterialCardView card2;
    Dialog dialog, lifeTimeDialog,couponDialog;
    MaterialCardView lifeTimeMembershipCard;
    boolean isPaid,isDataAvailable,payEnabled,redeemed = false;
    Vibrator vibrator;
    Button accept, decline,dialogRedeemBtn;
    final long[] pattern = {10,20};
    KonfettiView konfettiView;
    EditText couponCode;
    LottieAnimationView lottieAnimationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        Utils.blackIconStatusBar(PaymentActivity.this, R.color.background);
        payBtn = findViewById(R.id.payBtn);
        heading = findViewById(R.id.heading);
        Checkout.preload(getApplicationContext());
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");
        currency = getIntent().getStringExtra("currency");
        headingText = name+" select the plan that suits you";
        lifeTimeMembershipCard = findViewById(R.id.lifeTimeMembershipCard);
        heading.setText(headingText);
        refundBtn = findViewById(R.id.refundBtn);
        plan1 = findViewById(R.id.plan1);
        plan2 = findViewById(R.id.plan2);
        konfettiView = findViewById(R.id.konfettiView);
        readPayDetails();
        readUserData();
        lifeTimeDialog = new Dialog(PaymentActivity.this);
        lifeTimeDialog.setContentView(R.layout.update_prompt_dialog);
        lifeTimeDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        lifeTimeDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lifeTimeDialog.setCancelable(false);
        accept = lifeTimeDialog.findViewById(R.id.okayBtn);
        lifeTimeMembershipTitle = lifeTimeDialog.findViewById(R.id.headerText);
        lifeTimeMembershipMessage = lifeTimeDialog.findViewById(R.id.confirmText);
        lifeTimeMembershipTitle.setVisibility(View.VISIBLE);
        lifeTimeMembershipMessage.setVisibility(View.VISIBLE);
        decline = lifeTimeDialog.findViewById(R.id.notNow);
        decline.setText(R.string.decline);
        accept.setText(R.string.accept);
        couponBtn = findViewById(R.id.couponBtn);
        lifeTimeMembershipTitle.setText("Life-Time Membership Terms");
        lifeTimeMembershipMessage.setText("Hey, "+name+" please read the entire terms for life-time membership.\n\nWhat does this mean?\n\nLife-Time membership means you have the license of Good Morning and you don't need to renew your license. \n\nLicense Suspension\n\nWell, your life-time membership can be suspended if you are found violating the Zest IT Terms of Service.\n\nThis Life-Time membership is valid only for Good Morning and other software's and services by Zest IT will be charged (if chargeable) as decided by Zest IT\n\nLife-Time Membership may take up to 72 hours to reflect in your account.");
        lifeTimeMembershipMessage.setMovementMethod(new ScrollingMovementMethod());
        card2 = findViewById(R.id.card2);
        planDetails = findViewById(R.id.planDetails);
        currentPlan = findViewById(R.id.currentPlan);
        lastPayDate = findViewById(R.id.lastPayDate);
        nextPayDate = findViewById(R.id.nextPayDate);
        if (isPaid){
            if (currentSub.equals("plan1")&&(currency.equals("INR"))){
                currentSub = "Plan Type -> Monthly plan";
            }else if (currentSub.equals("plan2")&&(currency.equals("INR"))){
                currentSub = "Plan Type -> Yearly Plan";
            }
            if (currentSub.equals("plan1")&&(currency.equals("USD"))){
                currentSub = "Plan Type -> Quarterly plan";
            }else if (currentSub.equals("plan2")&&(currency.equals("USD"))){
                currentSub = "Plan Type -> Yearly Plan";
            }
            if (currentSub.equals("free_trial")){
                currentSub = "Plan Type -> Free Trial";
                renewalDate = "Free trial will end after 30 days";
            }
            if (currentSub.equals("life_time")){
                lifeTimeMembershipCard.setVisibility(View.GONE);
            }
            String heading = name+" your current plan details";
            planDetails.setText(heading);
            currentPlan.setText(currentSub);
            lastPayDate.setText(payDate);
            nextPayDate.setText(renewalDate);
            if (payEnabled){
                plan1.setEnabled(false);
                plan2.setEnabled(false);
                payBtn.setEnabled(false);
                lifeTimeMembershipCard.setVisibility(View.GONE);
                couponBtn.setVisibility(View.GONE);
                couponBtn.setEnabled(false);
            }
            //Toast.makeText(getApplicationContext(),"Paid",Toast.LENGTH_SHORT).show();
        }else {
            card2.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(),"Unpaid",Toast.LENGTH_SHORT).show();
        }
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        Calendar rightNow = Calendar.getInstance();
        Date c = rightNow.getTime();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        SimpleDateFormat yf = new SimpleDateFormat("yyyy", Locale.getDefault());
        SimpleDateFormat mf = new SimpleDateFormat("MM", Locale.getDefault());
        SimpleDateFormat df = new SimpleDateFormat("dd", Locale.getDefault());
        year = yf.format(c);
        month = mf.format(c);
        date = df.format(c);
        dialog = new Dialog(PaymentActivity.this);
        dialog.setContentView(R.layout.payment_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        couponDialog = new Dialog(PaymentActivity.this);
        couponDialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        couponDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        couponDialog.setCancelable(true);
        lottieAnimationView = couponDialog.findViewById(R.id.animationView);
        couponCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (couponCode.getText().toString().equals(" ")){
                    couponCode.setError("Please enter a valid coupon code");
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        okay = dialog.findViewById(R.id.okayBtn);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        message.setVisibility(View.VISIBLE);
         //  Toast.makeText(getApplicationContext(),date,Toast.LENGTH_LONG).show();
        if (currency.equals("USD")){
            plan1.setText(R.string.usdplan1);
            plan2.setText(R.string.usdplan2);
        }
        plan1.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            plan = "plan1";
        });
        plan2.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            plan = "plan2";
        });
        payBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);

            if (currency.equals("USD")){
                if (plan1.isChecked()){
                    amount = "50";
                    startPayment(amount, currency);
                }
                else if (plan2.isChecked()){
                    amount = "164";
                    startPayment(amount, currency);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please select a plan",Toast.LENGTH_LONG).show();
                }
            }else {
                if (plan1.isChecked()){
                    amount = "1199";
                    startPayment(amount, currency);
                }else if (plan2.isChecked()){
                    amount = "11990";
                    startPayment(amount, currency);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Please select a plan",Toast.LENGTH_LONG).show();
                }
            }

        });
        lifeTimeMembershipCard.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            lifeTimeDialog.show();
        });
        accept.setOnClickListener(v -> {
           vibrator.vibrate(pattern, -1);
           lifeTimeDialog.dismiss();
            Intent lifeTime = new Intent(PaymentActivity.this, LifetimeMembershipActivity.class);
            lifeTime.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            lifeTime.putExtra("currency",currency);
            startActivity(lifeTime);
        });
        decline.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            lifeTimeDialog.dismiss();
        });
        refundBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
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
            title.setText(R.string.refundPolicy);
            message.setText(R.string.refund);
            dialog.show();
        });
        couponBtn.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            couponDialog.show();
        });
        dialogRedeemBtn.setOnClickListener(view -> {
            vibrator.vibrate(pattern, -1);
            dialogRedeemBtn.setVisibility(View.GONE);
            dialogRedeemBtn.setEnabled(false);
            lottieAnimationView.setVisibility(View.VISIBLE);
            redeemCoupon(couponCode.getText().toString().trim());
        //    redeemCoupon("JAYANT21ZEST");
        });

    }

    private void redeemCoupon(String code) {
        // check on firebase if the coupon is valid
        FirebaseDatabase.getInstance().getReference("coupons").child(code).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    /*couponDialog.dismiss();
                    couponBtn.setVisibility(View.GONE);
                    couponBtn.setEnabled(false);*/
                    Coupon coupon = dataSnapshot.getValue(Coupon.class);
                    if (coupon.getStatus().equals("redeemed")){
                        couponCode.setError("Coupon already redeemed");
                        dialogRedeemBtn.setVisibility(View.VISIBLE);
                        dialogRedeemBtn.setEnabled(true);
                        lottieAnimationView.setVisibility(View.GONE);
                    }else {
                        couponDialog.dismiss();
                        nextSubscriptionDate("11990",date,month,year);
                        String payDate = date+"/"+month+"/"+year;
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert firebaseUser != null;
                        String userid = firebaseUser.getUid();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Subscription").child(userid);
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("subscription", "plan2");
                        hashMap.put("payDate", payDate);
                        hashMap.put("nextPayDate", nextPaymentDate);
                        hashMap.put("currency", "INR");
                        reference.setValue(hashMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()){
                                UserDatabaseHelper databaseHelper = new UserDatabaseHelper(PaymentActivity.this);
                                boolean result;
                                if (isPaid){
                                    result = databaseHelper.updatePaymentDetails("1","plan2",payDate,nextPaymentDate,"INR");
                                }else {
                                    result = databaseHelper.insertPaymentDetails("1","plan2",payDate,nextPaymentDate,"INR");
                                }
                                if (result){
                                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                    reference1.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            if (getApplicationContext() == null){
                                                return;
                                            }
                                            Users user = snapshot.getValue(Users.class);
                                            assert user != null;
                                            updatePaymentStatus(user);
                                             HashMap<String, String> hashMap = new HashMap<>();
                                             hashMap.put("status", "redeemed");
                                             FirebaseDatabase.getInstance().getReference("coupons").child(code).setValue(hashMap);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                                }else {
                                    result = databaseHelper.updatePaymentDetails("1","plan2",payDate,nextPaymentDate,"INR");
                                    if (result){
                                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                                        reference1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                if (getApplicationContext() == null){
                                                    return;
                                                }
                                                Users user = snapshot.getValue(Users.class);
                                                assert user != null;
                                                updatePaymentStatus(user);
                                                 HashMap<String, String> hashMap = new HashMap<>();
                                                 hashMap.put("status", "redeemed");
                                                 FirebaseDatabase.getInstance().getReference("coupons").child(code).setValue(hashMap);
                                            }

                                            @Override
                                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                            }
                                        });
                                    }else {
                                        Toast.makeText(getApplicationContext(),"Failed, please mail us at cs@zestit.tech to get your refund",Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                    }
                }else {
                    couponCode.setError("Coupon not found");
                    dialogRedeemBtn.setVisibility(View.VISIBLE);
                    dialogRedeemBtn.setEnabled(true);
                    lottieAnimationView.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void startPayment(String amount, String currency){
        Checkout checkout = new Checkout();
        checkout.setKeyID("rzp_live_eI3adLlaFXLSoI");
        checkout.setImage(R.mipmap.ic_launcher);
        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();

            options.put("name", "ZestIT");
            options.put("description",  name+"'s Good Morning Subscription");
         /*  // options.put("image", image);
          //  options.put("order_id", uid+"_"+formattedDate);//from response of step 3.*/
            options.put("theme.color", "#3399cc");
            options.put("currency", currency);
            options.put("amount", amount);//pass amount in currency subunits
            options.put("prefill.email", email);
            options.put("prefill.contact","");
            JSONObject retryObj = new JSONObject();
            retryObj.put("enabled", true);
            retryObj.put("max_count", 4);
            options.put("retry", retryObj);

            checkout.open(activity, options);

        } catch(Exception e) {
            Log.e(TAG, "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String s) {
        nextSubscriptionDate(amount,date,month,year);
        String payDate = date+"/"+month+"/"+year;
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        assert firebaseUser != null;
        String userid = firebaseUser.getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users Subscription").child(userid);
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("subscription", plan);
        hashMap.put("payDate", payDate);
        hashMap.put("nextPayDate", nextPaymentDate);
        hashMap.put("currency", currency);
        reference.setValue(hashMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                UserDatabaseHelper databaseHelper = new UserDatabaseHelper(this);
                boolean result;
                if (isPaid){
                    result = databaseHelper.updatePaymentDetails("1",plan,payDate,nextPaymentDate,currency);
                }else {
                    result = databaseHelper.insertPaymentDetails("1",plan,payDate,nextPaymentDate,currency);
                }
                if (result){
                    DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                    reference1.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            if (getApplicationContext() == null){
                                return;
                            }
                            Users user = snapshot.getValue(Users.class);
                            assert user != null;
                            updatePaymentStatus(user);
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                        }
                    });
                }else {
                    result = databaseHelper.updatePaymentDetails("1",plan,payDate,nextPaymentDate,currency);
                    if (result){
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(userid);
                        reference1.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                if (getApplicationContext() == null){
                                    return;
                                }
                                Users user = snapshot.getValue(Users.class);
                                assert user != null;
                                updatePaymentStatus(user);
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError error) {

                            }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(),"Failed, please mail us at cs@zestit.tech to get your refund",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
       // Toast.makeText(getApplicationContext(),nextPaymentDate,Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPaymentError(int i, String s) {
        dialog.setContentView(R.layout.payment_dialog);
        dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);
        ImageView type = dialog.findViewById(R.id.type);
        okay = dialog.findViewById(R.id.okayBtn);
        title = dialog.findViewById(R.id.headerText);
        message = dialog.findViewById(R.id.confirmText);
        title.setVisibility(View.VISIBLE);
        type.setImageResource(R.drawable.wrong);
        message.setVisibility(View.VISIBLE);
        String titleHeading = "Payment Unsuccessful";
        String msg = name+" your payment was unsuccessful and any amount if deducted will be refunded soon, or you can contact ZestIT.";
        title.setText(titleHeading);
        message.setText(msg);
        dialog.show();
        okay.setOnClickListener(v -> {
            vibrator.vibrate(pattern, -1);
            deleteData();
            dialog.dismiss();
        });
    }

    private void nextSubscriptionDate(String amount, String date, String month, String year){
        switch (amount) {
            case "50": {
                int mon = Integer.parseInt(month);
                mon = mon + 3;
                if (mon > 12) {
                    mon = mon - 12;
                    int yr = Integer.parseInt(year);
                    yr = yr + 1;
                    year = String.valueOf(yr);
                    int dt = Integer.parseInt(date);
                    if (dt == 31) {
                        switch (mon) {
                            case 2:
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                mon++;
                                break;
                        }
                    } else if (dt == 30) {
                        switch (mon) {
                            case 2:
                                mon = mon + 1;
                                dt = 1;
                                break;
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                mon++;
                                break;
                        }
                    }else if (dt == 29){
                        switch (mon) {
                            case 2:
                                mon = mon + 1;
                                dt = 1;
                                break;
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                mon++;
                                break;
                        }
                    }
                    date = String.valueOf(dt);
                } else {
                    int yr = Integer.parseInt(year);
                    int dt = Integer.parseInt(date);
                    if (dt == 31) {
                        switch (mon) {
                            case 2:
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                mon++;
                                break;
                        }
                    } else if (dt == 30) {
                        switch (mon) {
                            case 1:
                                mon = mon + 2;
                                dt = 1;
                                break;
                            case 4:
                            case 6:
                            case 9:
                            case 11:
                                mon++;
                                break;
                        }
                    }
                    year = String.valueOf(yr);
                    date = String.valueOf(dt);
                }
                month = String.valueOf(mon);
                nextPaymentDate = date + "/" + month + "/" + year;
                break;
            }
            case "164": {
                int yr = Integer.parseInt(year);
                yr = yr + 1;
                int dt = Integer.parseInt(date);
                int mon = Integer.parseInt(month);
                if (dt == 29 && mon == 2) {
                    dt = 28;
                }
                date = String.valueOf(dt);
                month = String.valueOf(mon);
                year = String.valueOf(yr);
                nextPaymentDate = date + "/" + month + "/" + year;
                break;
            }
            case "1199": {
                int dt = Integer.parseInt(date);
                int mon = Integer.parseInt(month);
                int yr = Integer.parseInt(year);
                mon = mon + 1;
                if (mon > 12) {
                    mon = mon - 12;
                    yr = yr + 1;
                }
                if (dt == 31) {
                    switch (mon) {
                        case 2:
                            dt = 2;
                            mon = mon + 1;
                            break;
                        case 4:
                        case 6:
                        case 9:
                        case 11:
                            dt = 1;
                            mon = mon + 1;
                            break;
                    }
                } else if (dt == 30) {
                    if (mon == 2) {
                        dt = 1;
                        mon = mon + 1;
                    }
                } else if (dt == 29){
                    if (mon == 2) {
                        dt = 28;
                    }
                }
                date = String.valueOf(dt);
                month = String.valueOf(mon);
                year = String.valueOf(yr);
                nextPaymentDate = date + "/" + month + "/" + year;
                break;
            }
            case "11990": {
                int dt = Integer.parseInt(date);
                int mon = Integer.parseInt(month);
                int yr = Integer.parseInt(year);
                yr = yr + 1;
                if (dt == 29 && mon == 2) {
                    dt = 28;
                }
                date = String.valueOf(dt);
                month = String.valueOf(mon);
                year = String.valueOf(yr);
                nextPaymentDate = date + "/" + month + "/" + year;
                break;
            }
        }
    }

    private void updatePaymentStatus(Users users){
        if (users.getPayment_status().equals("n")){
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            String userId = firebaseUser.getUid();
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);
            HashMap<String, String> hashMap = new HashMap<>();
            hashMap.put("id", userId);
            hashMap.put("first_name", users.getFirst_name());
            hashMap.put("last_name", users.getLast_name());
            hashMap.put("email", users.getEmail());
            hashMap.put("phone_number", users.getPhone_number());
            hashMap.put("payment_status", "y");
            hashMap.put("country",users.getCountry());
            hashMap.put("account_open_date",users.getAccount_open_date());
            hashMap.put("password",users.getPassword());
            hashMap.put("imageUri",users.getImageUri());
            hashMap.put("terms_of_service","accepted");
            hashMap.put("verification",users.getVerification());
            reference.setValue(hashMap).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    dialog.setContentView(R.layout.payment_dialog);
                    dialog.getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this,R.drawable.dialog_background));
                    dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                    dialog.setCancelable(false);
                    ImageView type = dialog.findViewById(R.id.type);
                    okay = dialog.findViewById(R.id.okayBtn);
                    title = dialog.findViewById(R.id.headerText);
                    message = dialog.findViewById(R.id.confirmText);
                    title.setVisibility(View.VISIBLE);
                    type.setImageResource(R.drawable.correct);
                    message.setVisibility(View.VISIBLE);
                    String titleHeading = "Payment Successful";
                    String msg = name+" your payment is successful and your next payment date is "+nextPaymentDate+" we suggest you to restart the application.";
                    title.setText(titleHeading);
                    message.setText(msg);
                    dialog.show();
                    konfettiView.build()
                            .addColors(Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.RED, Color.CYAN)
                            .setDirection(0.0, 359.0)
                            .setSpeed(1f, 5f)
                            .setFadeOutEnabled(true)
                            .setTimeToLive(2000L)
                            .addShapes(Shape.Square.INSTANCE, Shape.Circle.INSTANCE)
                            .addSizes(new Size(12, 5f))
                            .setPosition(-50f, konfettiView.getWidth() + 50f, -50f, -50f)
                            // .burst(100);
                            .streamFor(500, 2000L);
                    okay.setOnClickListener(v -> {
                        vibrator.vibrate(pattern, -1);
                        deleteData();
                        dialog.dismiss();
                    });
                }
            });
        }
    }

    private void readPayDetails() {
        UserDatabaseHelper dataBaseHelper = new UserDatabaseHelper(this);
        SQLiteDatabase db = dataBaseHelper.getReadableDatabase();
        Cursor cursor = dataBaseHelper.readPaymentData("1", db);
        if (cursor.moveToFirst()) {
            isPaid = true;
            currentSub = cursor.getString(1);
            payDate = cursor.getString(2);
            renewalDate = cursor.getString(3);
            payDate = "Paid On -> "+payDate;
            if (currentSub.equals("free_plan")){
                currentSub = "Plan Type -> Free Plan";
            }else if (currentSub.equals("free_trial")){
                currentSub = "Plan Type -> Free Trial";
                renewalDate = "Free trial will end after 30 days";
            }
            if (renewalDate.equals("")){
                renewalDate = "You don't have an expiring plan";
            }else {
                renewalDate = "Next Payment date -> "+renewalDate;
            }

        }else {
           isPaid = false;
        }
    }

    private void readUserData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase;
        sqLiteDatabase = db.getReadableDatabase();
        Cursor cursor = db.readUserData("1",sqLiteDatabase);
        if (cursor.moveToFirst()){
            paid = cursor.getString(6);
            isDataAvailable = true;
            if (paid.equals("y")){
                payEnabled = true;
            }else if (paid.equals("n")){
                payEnabled = false;
            }
        }else {
            isDataAvailable = false;
        }
    }

    private void deleteData(){
        UserDatabaseHelper db = new UserDatabaseHelper(this);
        SQLiteDatabase sqLiteDatabase = db.getReadableDatabase();
        db.deleteData("1",sqLiteDatabase);
    }

}