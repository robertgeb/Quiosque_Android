package ufrrj.com.quiosque;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by israel on 10/06/17.
 */

public class QuiosqueSyncAlarm extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        // Checa novidades quando alarm é chamado
        QuiosqueService.startActionCheckNovidades(context);
    }

    public static void setAlarm(Context context)
    {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, QuiosqueSyncAlarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        // Configurando para cada 10 min chamar esse BroadcastReceiver
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 10, pi); // Millisec * Second * Minute
    }

    public static void cancelAlarm(Context context)
    {
        Intent intent = new Intent(context, QuiosqueSyncAlarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }


}