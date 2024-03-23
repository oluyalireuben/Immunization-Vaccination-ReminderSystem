package com.example.vaccinationremindersystem;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vaccinationremindersystem.databinding.ActivityNotificationBinding;
import com.example.vaccinationremindersystem.model.Profile;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {
    ActivityNotificationBinding binding;
    String title, message;
    Long time;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        createChannel();

        FirebaseDatabase.getInstance().getReference().child(
                Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid()
        ).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                title = snapshot.child("title").getValue(String.class);
                message = snapshot.child("message").getValue(String.class);
                time = snapshot.child("time").getValue(Long.class);

                long trigger = System.currentTimeMillis();
                scheduleNotification(trigger + 60000);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        FirebaseDatabase.getInstance().getReference("Profile/" + FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String dob = Objects.requireNonNull(snapshot.getValue(Profile.class)).getChildDOB();
                        LocalDate birthDate = parseDate(dob);

                        int[] intervals = {1, 2, 4, 6, 8, 9, 10, 13, 15};
                        List<LocalDate> vaccinationDates = calculateVaccinationDates(birthDate, intervals);

                        updateTextViews(vaccinationDates);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateTextViews(List<LocalDate> vaccinationDates) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        TextView[] textViews = new TextView[]{
                binding.vaccine1,
                binding.vaccine2,
                binding.vaccine3,
                binding.vaccine4,
                binding.vaccine5,
                binding.vaccine6,
                binding.vaccine7,
                binding.vaccine8,
                binding.vaccine9,
        };
        for (int i = 0; i < vaccinationDates.size(); i++) {
            textViews[i].setText(vaccinationDates.get(i).format(formatter));
            textViews[i].setTextColor(Color.GREEN);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private List<LocalDate> calculateVaccinationDates(LocalDate birthDate, int[] intervals) {
        List<LocalDate> vaccinationDates = new ArrayList<>();
        LocalDate currentDate;

        for (int interval : intervals) {
            currentDate = birthDate.plusMonths(interval);
            vaccinationDates.add(currentDate);
        }
        return vaccinationDates;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private LocalDate parseDate(String date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
            return LocalDate.parse(date, formatter);
        } catch (DateTimeParseException exception) {
            exception.printStackTrace();
            return null;
        }

    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNotification(long trigger) {
        Intent intent = new Intent(NotificationActivity.this, Notification.class);
        intent.putExtra("titleExtra", title);
        intent.putExtra("messageExtra", message);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                NotificationActivity.this,
                1,
                intent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                trigger,
                60 * 1000,
                pendingIntent
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        String name = "Vaccination";
        String desc = "Channel description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("notificationChannel", name, importance);
        channel.setDescription(desc);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
