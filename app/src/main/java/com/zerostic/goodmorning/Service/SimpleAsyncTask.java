package com.zerostic.goodmorning.Service;

import android.os.AsyncTask;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;

public class SimpleAsyncTask extends AsyncTask<Void, Void, String> {
    private WeakReference<TextView> mTextView;
    private static final String TEXT_STATE = "currentText";

    @Override
    protected String doInBackground(Void... voids) {
        Random r = new Random();
        int n = r.nextInt(11);

        int s = 5000;

        try {
            Thread.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Awake at last after sleeping for " + s + " milliseconds!";

    }

    public SimpleAsyncTask(TextView tv) {
        mTextView = new WeakReference<>(tv);
    }

    protected void onPostExecute(String result) {
        mTextView.get().setText(result);
    }
}
