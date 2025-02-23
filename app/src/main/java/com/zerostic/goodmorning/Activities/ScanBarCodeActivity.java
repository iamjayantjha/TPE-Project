package com.zerostic.goodmorning.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.budiyev.android.codescanner.AutoFocusMode;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Application.Utils;
import java.io.IOException;

/**
 Coded by iamjayantjha
 **/

public class ScanBarCodeActivity extends AppCompatActivity {
   String tone,method,wuc,act,id,payStat, codeText="";
    CodeScanner codeScanner;
    CodeScannerView scannerView;
    TextView code;
    MaterialButton saveBtn;
    ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_bar_code);
        Utils.blackIconStatusBar(ScanBarCodeActivity.this, R.color.background);
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        final long[] pattern = {10, 20};
        tone = getIntent().getStringExtra("tone");
        wuc = getIntent().getStringExtra("wuc");
        method = getIntent().getStringExtra("method");
        act = getIntent().getStringExtra("act");
        id = getIntent().getStringExtra("id");
        payStat = getIntent().getStringExtra("payStat");
        scannerView = findViewById(R.id.scanner_view);
        codeScanner = new CodeScanner(this, scannerView);
        code = findViewById(R.id.code);
        saveBtn = findViewById(R.id.saveBtn);
        refresh = findViewById(R.id.refresh);
        codeScanner.setCamera(CodeScanner.CAMERA_BACK);
        codeScanner.setFormats(CodeScanner.ALL_FORMATS);
        codeScanner.setAutoFocusMode(AutoFocusMode.SAFE);
        codeScanner.setAutoFocusEnabled(true);
        codeScanner.setFlashEnabled(false);
        codeScanner.setDecodeCallback(result -> runOnUiThread(() -> {
            vibrator.vibrate(pattern,-1);
            scannerView.setVisibility(View.GONE);
            code.setVisibility(View.VISIBLE);
            code.setText("Scanned Code Contains\n"+result.getText());
            codeScanner.stopPreview();
            codeScanner.releaseResources();
            saveBtn.setVisibility(View.VISIBLE);
            codeText = result.getText();
            refresh.setVisibility(View.VISIBLE);
        }));
        codeScanner.setErrorCallback(error -> runOnUiThread(() -> {
            Toast.makeText(ScanBarCodeActivity.this, "Camera initialization error: " + error.getMessage(), Toast.LENGTH_LONG).show();
        }));
        refresh.setOnClickListener(v -> {
            scannerView.setVisibility(View.VISIBLE);
            code.setVisibility(View.GONE);
            saveBtn.setVisibility(View.GONE);
            refresh.setVisibility(View.GONE);
            codeScanner.startPreview();
        });
        saveBtn.setOnClickListener(v -> {
            vibrator.vibrate(pattern,-1);
            Intent methodIntent = new Intent(ScanBarCodeActivity.this, MethodFunctionActivity.class);
            methodIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            methodIntent.putExtra("code",codeText);
            methodIntent.putExtra("heading","Bar Code");
            methodIntent.putExtra("tone",tone);
            methodIntent.putExtra("wuc",wuc);
            methodIntent.putExtra("act",act);
            methodIntent.putExtra("id",id);
            methodIntent.putExtra("payStat",payStat);
            startActivity(methodIntent);
            finish();
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        codeScanner.releaseResources();
        super.onPause();
    }
}