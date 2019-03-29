package com.corwin.learncards;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;

public class PeriodLearningManager {

    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    public void onActivityStarted(Context context) {
//        alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        Intent intent = new Intent(context, AlarmsReceiver.class);
//        intent.setAction("com.corwin.learncards");
//        alarmIntent = PendingIntent.getBroadcast(context, 666, intent, PendingIntent.FLAG_CANCEL_CURRENT);
//        AlarmManager.AlarmClockInfo info = alarmMgr.getNextAlarmClock();
//        alarmMgr.cancel(alarmIntent);
//        long when = SystemClock.elapsedRealtime() + 1 * 60 * 1000;
//        int SDK_INT = Build.VERSION.SDK_INT;
//        if (SDK_INT < Build.VERSION_CODES.KITKAT)
//            alarmMgr.set(AlarmManager.RTC_WAKEUP, when, alarmIntent);
//        else if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M)
//            alarmMgr.setExact(AlarmManager.RTC_WAKEUP, when, alarmIntent);
//        else if (SDK_INT >= Build.VERSION_CODES.M) {
////            alarmMgr.set(AlarmManager.RTC_WAKEUP, when, alarmIntent);
//            alarmMgr.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 1 * 60 * 1000, alarmIntent);
//        }
//        Log.d("Corwin", "start timer");
    }


}
