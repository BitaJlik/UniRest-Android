package com.unirest;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.gson.Gson;
import com.unirest.data.models.CookerRemind;
import com.unirest.data.models.WasherRemind;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Data", Context.MODE_PRIVATE);
        String jsonStringCooker = sharedPreferences.getString("CookerReminder", null);
        String jsonStringWasher = sharedPreferences.getString("WasherReminder", null);

        Gson gson = new Gson();
        if (jsonStringCooker != null) {
            try {
                CookerRemind remind = gson.fromJson(jsonStringCooker, CookerRemind.class);
                if (remind.getTimeToRemind() < System.currentTimeMillis()) {
                    sendNotification(getApplicationContext().getString(R.string.notification_remind_cooker), true);
                    sharedPreferences.edit().putString("CookerReminder", null).apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.failure();
            }
        }
        if (jsonStringWasher != null) {
            try {
                WasherRemind remind = gson.fromJson(jsonStringWasher, WasherRemind.class);
                if (remind.getTimeToRemind() < System.currentTimeMillis()) {
                    sendNotification(getApplicationContext().getString(R.string.notification_remind_washer), false);
                    sharedPreferences.edit().putString("WasherReminder", null).apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.failure();
            }
        }

        return Result.success();
    }

    private void sendNotification(String message, boolean isCooker) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "data_check_channel";

        NotificationChannel channel = new NotificationChannel(channelId, "Data Check Notifications", NotificationManager.IMPORTANCE_DEFAULT);
        notificationManager.createNotificationChannel(channel);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(isCooker ? R.drawable.ic_cooker : R.drawable.ic_washer)
                .setContentTitle(getApplicationContext().getString(R.string.reminder))
                .setContentText(message)
                .setAutoCancel(true);

        notificationManager.notify(1, notificationBuilder.build());
    }
}
