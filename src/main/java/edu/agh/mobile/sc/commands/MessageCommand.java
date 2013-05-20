package edu.agh.mobile.sc.commands;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import edu.agh.mobile.sc.R;

/**
 * @author Przemyslaw Dadel
 */
public class MessageCommand extends AbstractExtrasCommand {

    @Override
    public boolean accepts(Bundle extras) {
        return hasExtrasValue(extras, "message");
    }

    @Override
    public void execute(Context context, Bundle extras) {
        final String message = getExtrasValue(extras, "message", String.class);
        final String title = getExtrasValue(extras, "title", String.class);
        handleNotification(context, message, title);
    }

    private void handleNotification(Context context, String message, String title) {
        final int NoOTIFICATION_ID = 1314151912;
        final PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(), 0);
        final NotificationManager notifManager = getNotificationService(context);
        final Notification note = new Notification(R.drawable.sc, title, System.currentTimeMillis());
        note.flags |= Notification.FLAG_AUTO_CANCEL | Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_ONLY_ALERT_ONCE;
        note.setLatestEventInfo(context, title, message, pendingIntent);
        notifManager.notify(NoOTIFICATION_ID, note);
    }

    private NotificationManager getNotificationService(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


}
