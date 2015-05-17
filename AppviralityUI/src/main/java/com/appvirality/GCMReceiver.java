package com.appvirality;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;

public class GCMReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        if ("com.google.android.c2dm.intent.RECEIVE".equals(action)) {
            handleNotificationIntent(context, intent);
        }
    }

    static class NotificationData {
        private NotificationData(int anIcon, CharSequence aTitle, String aMessage, Intent anIntent) {
            icon = anIcon;
            title = aTitle;
            message = aMessage;
            intent = anIntent;
        }

        public final int icon;
        public final CharSequence title;
        public final String message;
        public final Intent intent;
    }

    Intent getDefaultIntent(Context context) {
        final PackageManager manager = context.getPackageManager();
        return manager.getLaunchIntentForPackage(context.getPackageName());
    }

    NotificationData readInboundIntent(Context context, Intent intent) {
        final PackageManager manager = context.getPackageManager();

        final String message = intent.getStringExtra("message");
        final String uriString = intent.getStringExtra("deeplink");
        CharSequence notificationTitle = intent.getStringExtra("title");

        if (message == null) {
            return null;
        }

        ApplicationInfo appInfo;
        try {
            appInfo = manager.getApplicationInfo(context.getPackageName(), 0);
        } catch (final NameNotFoundException e) {
            appInfo = null;
        }

        int notificationIcon = -1;
		if (null != appInfo) {
            notificationIcon = appInfo.icon;
        }

        if (notificationIcon == -1) {
            notificationIcon = android.R.drawable.sym_def_app_icon;
        }

        if (null == notificationTitle && null != appInfo) {
            notificationTitle = manager.getApplicationLabel(appInfo);
        }

        if (null == notificationTitle) {
            return null;
        }

        final Intent notificationIntent = buildNotificationIntent(context, uriString);

        return new NotificationData(notificationIcon, notificationTitle, message, notificationIntent);
    }

    private Intent buildNotificationIntent(Context context, String uriString) {
        Uri uri = null;
        if (null != uriString) {
            uri = Uri.parse(uriString);
        }

        final Intent ret;
        if (null == uri) {
            ret = getDefaultIntent(context);
        } else {
            ret = new Intent(Intent.ACTION_VIEW, uri);
        }

        return ret;
    }

    private Notification buildNotification(Context context, Intent intent) {
        final NotificationData notificationData = readInboundIntent(context, intent);
        if (null == notificationData) {
            return null;
        }

        final PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                0,
                notificationData.intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        final Notification notification;
        if (Build.VERSION.SDK_INT >= 16) {
            notification = makeNotificationSDK16OrHigher(context, contentIntent, notificationData);
        } else if (Build.VERSION.SDK_INT >= 11) {
            notification = makeNotificationSDK11OrHigher(context, contentIntent, notificationData);
        } else {
            notification = makeNotificationSDKLessThan11(context, contentIntent, notificationData);
        }

        return notification;
    }

    private void handleNotificationIntent(Context context, Intent intent) {
        final Context applicationContext = context.getApplicationContext();
        final Notification notification = buildNotification(applicationContext, intent);

        if (null != notification) {
            final NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }
    }

    @SuppressWarnings("deprecation")
    @TargetApi(9)
    private Notification makeNotificationSDKLessThan11(Context context, PendingIntent intent, NotificationData notificationData) {
        final Notification n = new Notification(notificationData.icon, notificationData.message, System.currentTimeMillis());
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        n.setLatestEventInfo(context, notificationData.title, notificationData.message, intent);
        return n;
    }

    @SuppressWarnings("deprecation")
    @TargetApi(11)
    private Notification makeNotificationSDK11OrHigher(Context context, PendingIntent intent, NotificationData notificationData) {
        final Notification.Builder builder = new Notification.Builder(context).
                setSmallIcon(notificationData.icon).
                setTicker(notificationData.message).
                setWhen(System.currentTimeMillis()).
                setContentTitle(notificationData.title).
                setContentText(notificationData.message).
                setContentIntent(intent);

        final Notification n = builder.getNotification();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        return n;
    }

    @SuppressLint("NewApi")
    @TargetApi(16)
    private Notification makeNotificationSDK16OrHigher(Context context, PendingIntent intent, NotificationData notificationData) {
        final Notification.Builder builder = new Notification.Builder(context).
                setSmallIcon(notificationData.icon).
                setTicker(notificationData.message).
                setWhen(System.currentTimeMillis()).
                setContentTitle(notificationData.title).
                setContentText(notificationData.message).
                setContentIntent(intent).
                setStyle(new Notification.BigTextStyle().bigText(notificationData.message));

        final Notification n = builder.build();
        n.flags |= Notification.FLAG_AUTO_CANCEL;
        return n;
    }

}
