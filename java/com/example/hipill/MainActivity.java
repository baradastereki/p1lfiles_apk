package com.example.hipill;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "HiPillChannel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText pillNameEditText = findViewById(R.id.etPillName);
        EditText pillTimeEditText = findViewById(R.id.etPillTime);
        Button addReminderButton = findViewById(R.id.btnAddReminder);

        // Create Notification Channel
        createNotificationChannel();

        // Add Reminder Logic
        addReminderButton.setOnClickListener(v -> {
            String pillName = pillNameEditText.getText().toString();
            String pillTime = pillTimeEditText.getText().toString();

            if (pillName.isEmpty() || pillTime.isEmpty()) {
                Toast.makeText(this, "Please enter the pill name and time.", Toast.LENGTH_SHORT).show();
                return;
            }

            scheduleNotification(pillName, pillTime);
            Toast.makeText(this, "Reminder added successfully!", Toast.LENGTH_SHORT).show();

            pillNameEditText.setText("");
            pillTimeEditText.setText("");
        });
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "HiPill Reminders",
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Channel for HiPill reminders");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(String pillName, String pillTime) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(timeFormat.parse(pillTime));
        } catch (Exception e) {
            Toast.makeText(this, "Invalid time format. Use hh:mm AM/PM.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("pillName", pillName);
        intent.putExtra("pillTime", pillTime);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.getTimeInMillis(),
            pendingIntent
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("HiPill Reminder")
            .setContentText("Hey. Ur ready? Itz " + pillTime + ". Eat ur " + pillName + " pill!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
