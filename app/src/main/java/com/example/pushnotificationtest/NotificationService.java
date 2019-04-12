package com.example.pushnotificationtest;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.util.Log;
import com.google.firebase.database.*;

public class NotificationService extends Service {

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FirebaseDatabase.getInstance().getReference("Uber").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getChildrenCount() != 0) {
                    if (dataSnapshot.getChildrenCount() != pref.getInt("count", 0)) {
                        editor.putInt("count", (int) dataSnapshot.getChildrenCount());
                        editor.apply();
                        Log.i("dxdiag", "New Message");
                        Log.i("dxdiag", "Firebase Count " + String.valueOf(dataSnapshot.getChildrenCount()));
                        Log.i("dxdiag", "Pref Count " + String.valueOf(pref.getInt("count", 0)));
                    }
                } else {
                    editor.putInt("count", 0);
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("dxdiag", "created");

        pref = getSharedPreferences("message_count", Context.MODE_PRIVATE);
        editor = pref.edit();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("dxdiag", "Notifications", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
            Notification notification = new Notification.Builder(this, "dxdiag").setSmallIcon(R.mipmap.ic_launcher)
                    .setContentText("Receiving").setAutoCancel(true)
                    .setContentIntent(pendingIntent).build();
            startForeground(69, notification);
        }

    }

    @Override
    public void onDestroy() {
        Log.i("dxdiag", "destroyed");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
