package com.zerostic.goodmorning.AlarmsList;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.zerostic.goodmorning.Data.UserDatabaseHelper;
import com.zerostic.goodmorning.R;
import com.zerostic.goodmorning.Activities.AlarmEditActivity;
import com.zerostic.goodmorning.Activities.CreateAlarmActivity;
import com.zerostic.goodmorning.Activities.SettingsActivity;
import com.zerostic.goodmorning.Activities.WeatherActivity;
import com.zerostic.goodmorning.Data.Alarm;
import com.zerostic.goodmorning.Data.DataBaseHelper;

import java.util.Calendar;
import java.util.Locale;

/**
 Coded by iamjayantjha
 **/

public class AlarmsListFragment extends Fragment implements OnToggleAlarmListener, onDeleteAlarmListener, alarmUpdateListener{
    private AlarmRecyclerViewAdapter alarmRecyclerViewAdapter;
    private AlarmsListViewModel alarmsListViewModel;
    ImageView settings;
    ImageView weather;
    private Vibrator vibrator;
    final long[] pattern = {10, 20};
    final long[] pattern2 = {1, 2};
    private boolean isWeather, toSpeak;
    DataBaseHelper dataBaseHelper;
    private TextToSpeech tts;
    private UserDatabaseHelper userDatabaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dataBaseHelper = new DataBaseHelper(getContext());
        alarmRecyclerViewAdapter = new AlarmRecyclerViewAdapter(this,this,this);
        alarmsListViewModel = new ViewModelProvider(this).get(AlarmsListViewModel.class);
        alarmsListViewModel.getAlarmsLiveData().observe(this, alarms -> {
            if (alarms != null) {
                alarmRecyclerViewAdapter.setAlarms(alarms);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        tts = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS){
                int result = tts.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                    Toast.makeText(requireContext(),"Language is not supported",Toast.LENGTH_SHORT).show();
                    toSpeak = false;
                }else {
                    toSpeak = true;
                }
            }
        });
        userDatabaseHelper = new UserDatabaseHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_listalarms, container, false);
        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        RecyclerView alarmsRecyclerView = view.findViewById(R.id.fragment_listalarms_recylerView);
        alarmsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        alarmsRecyclerView.setAdapter(alarmRecyclerViewAdapter);
        SharedPreferences preferences = requireContext().getSharedPreferences("PREFS", MODE_PRIVATE);
        isWeather = preferences.getBoolean("weatherEnabled",true);
        MaterialCardView addAlarm = view.findViewById(R.id.fragment_listalarms_addAlarm);
        settings = view.findViewById(R.id.settings);
        weather = view.findViewById(R.id.weatherData);
        weather.setEnabled(isWeather);
        weather.setOnClickListener(view12 -> {
            vibrator.vibrate(pattern, -1);
            Intent settingsIntent = new Intent(getContext(), WeatherActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(settingsIntent);
            getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });
        Calendar rightNow = Calendar.getInstance();
        int hours = rightNow.get(Calendar.HOUR_OF_DAY);
        if ((hours>=5)&&(hours<=18)){
            weather.setImageResource(R.drawable.ic_sunmain);
        }else {
            weather.setImageResource(R.drawable.ic_moonmain);
        }
        settings.setOnClickListener(view1 -> {
            vibrator.vibrate(pattern, -1);
            Intent settingsIntent = new Intent(getContext(), SettingsActivity.class);
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(settingsIntent);
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        addAlarm.setOnClickListener(view13 -> {
            vibrator.vibrate(pattern2, -1);
            Intent open = new Intent(getContext(), CreateAlarmActivity.class);
            open.putExtra("method","Default");
            open.putExtra("tone","0");
            open.putExtra("wuc","Off");
            open.putExtra("act","main");
            startActivity(open);
            SharedPreferences preferences1 = getActivity().getSharedPreferences("PREFS",MODE_PRIVATE);
            SharedPreferences.Editor editor=preferences1.edit();
            editor.putString("snooze","0");
            editor.putString("title","Alarm");
            editor.putString("ringsIn","Rings in 24 hours");
            editor.apply();
            getActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        // swipe to delete
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                Alarm alarm = alarmRecyclerViewAdapter.getAlarmAt(viewHolder.getAdapterPosition());
                if (!dataBaseHelper.isInWUC(alarm.getAlarmId())){
                    alarmsListViewModel.remove(alarm);
                    alarmRecyclerViewAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                    Handler handler = new Handler();
                    Runnable runnable = () -> {
                    onToggle(alarm);
                    onDelete(alarm);
                    dataBaseHelper.deleteData(String.valueOf(alarm.getAlarmId()));
                    };
                    Snackbar.make(alarmsRecyclerView, "Alarm deleted", 10000)
                            .setAction("Undo", v -> {
                                handler.removeCallbacks(runnable);
                                alarmsListViewModel.insert(alarm);
                                alarmRecyclerViewAdapter.notifyDataSetChanged();
                            }).show();
                    handler.postDelayed(runnable, 10000);
                }else {
                    String name = userDatabaseHelper.getFirstName();
                    String speech = name+",. you cannot delete this at the moment";
                    Toast.makeText(getContext(), speech,Toast.LENGTH_SHORT).show();
                    say(speech);
                    //refresh the adapter
                    alarmRecyclerViewAdapter.notifyDataSetChanged();
                }

            }
        }).attachToRecyclerView(alarmsRecyclerView);

        return view;
    }

    @Override
    public void onDestroy() {
        if (tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onToggle(Alarm alarm) {
        if (alarm.isStarted()) {
            alarm.cancelAlarm(requireContext());
            alarmsListViewModel.update(alarm);
        } else {
            alarm.schedule(requireContext());
            alarmsListViewModel.update(alarm);
        }
        vibrator.vibrate(pattern, -1);
    }
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDelete(Alarm alarm){
        vibrator.vibrate(pattern, -1);
        if (!dataBaseHelper.isInWUC(alarm.getAlarmId())){
            alarmsListViewModel.delete(alarm);
            alarmsListViewModel.update(alarm);
            dataBaseHelper.deleteData(String.valueOf(alarm.getAlarmId()));
        }else {
            String name = userDatabaseHelper.getFirstName();
            String speech = name+",. you cannot delete this at the moment";
            Toast.makeText(getContext(), speech,Toast.LENGTH_SHORT).show();
            say(speech);
            //refresh the adapter
            alarmRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onUpdate(Alarm alarm) {
        vibrator.vibrate(pattern2, -1);
        String id = String.valueOf(alarm.getAlarmId());
        Intent editIntent = new Intent(getContext(), AlarmEditActivity.class);
        editIntent.putExtra("id",id);
        editIntent.putExtra("act","main");
        editIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(editIntent);
        requireActivity().overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left);
    }

    private void say(String speakText) {
        tts.setPitch(1.00f);
        tts.setSpeechRate(1);
        tts.speak(speakText, TextToSpeech.QUEUE_ADD, null);
    }

}