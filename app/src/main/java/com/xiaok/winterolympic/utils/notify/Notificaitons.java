package com.xiaok.winterolympic.utils.notify;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.xiaok.winterolympic.R;
import com.xiaok.winterolympic.view.central.NavigationActivity;
import com.xiaok.winterolympic.view.notify.WatchLiveActivity;

/**
 *
 * @author peter
 * @date 2018/7/4
 */

public class Notificaitons {
    public final static int NOTIFICATION_SAMPLE = 0;
    public final static int NOTIFICATION_ACTION = 1;
    public final static int NOTIFICATION_REMOTE_INPUT = 2;
    public final static int NOTIFICATION_BIG_PICTURE_STYLE = 3;
    public final static int NOTIFICATION_BIG_TEXT_STYLE = 4;
    public final static int NOTIFICATION_INBOX_STYLE = 5;
    public final static int NOTIFICATION_MEDIA_STYLE = 6;
    public final static int NOTIFICATION_MESSAGING_STYLE = 7;
    public final static int NOTIFICATION_PROGRESS = 8;
    public final static int NOTIFICATION_CUSTOM_HEADS_UP = 9;
    public final static int NOTIFICATION_CUSTOM = 10;

    public final static String ACTION_SIMPLE = "com.android.peter.notificationdemo.ACTION_SIMPLE";
    public final static String ACTION_ACTION = "com.android.peter.notificationdemo.ACTION_ACTION";
    public final static String ACTION_REMOTE_INPUT = "com.android.peter.notificationdemo.ACTION_REMOTE_INPUT";
    public final static String ACTION_BIG_PICTURE_STYLE = "com.android.peter.notificationdemo.ACTION_BIG_PICTURE_STYLE";
    public final static String ACTION_BIG_TEXT_STYLE = "com.android.peter.notificationdemo.ACTION_BIG_TEXT_STYLE";
    public final static String ACTION_INBOX_STYLE = "com.android.peter.notificationdemo.ACTION_INBOX_STYLE";
    public final static String ACTION_MEDIA_STYLE = "com.android.peter.notificationdemo.ACTION_MEDIA_STYLE";
    public final static String ACTION_MESSAGING_STYLE = "com.android.peter.notificationdemo.ACTION_MESSAGING_STYLE";
    public final static String ACTION_PROGRESS = "com.android.peter.notificationdemo.ACTION_PROGRESS";
    public final static String ACTION_CUSTOM_HEADS_UP_VIEW = "com.android.peter.notificationdemo.ACTION_CUSTOM_HEADS_UP_VIEW";
    public final static String ACTION_CUSTOM_VIEW = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_LOVE = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_LOVE";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_PRE = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_PRE";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_PLAY_OR_PAUSE = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_PLAY_OR_PAUSE";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_NEXT = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_NEXT";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_LYRICS = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_LYRICS";
    public final static String ACTION_CUSTOM_VIEW_OPTIONS_CANCEL = "com.android.peter.notificationdemo.ACTION_CUSTOM_VIEW_OPTIONS_CANCEL";

    public final static String ACTION_YES = "com.android.peter.notificationdemo.ACTION_YES";
    public final static String ACTION_NO = "com.android.peter.notificationdemo.ACTION_NO";
    public final static String ACTION_DELETE = "com.android.peter.notificationdemo.ACTION_DELETE";
    public final static String ACTION_REPLY = "com.android.peter.notificationdemo.ACTION_REPLY";
    public final static String REMOTE_INPUT_RESULT_KEY = "remote_input_content";

    public final static String EXTRA_OPTIONS = "options";
    public final static String MEDIA_STYLE_ACTION_DELETE = "action_delete";
    public final static String MEDIA_STYLE_ACTION_PLAY = "action_play";
    public final static String MEDIA_STYLE_ACTION_PAUSE = "action_pause";
    public final static String MEDIA_STYLE_ACTION_NEXT = "action_next";
    public final static String ACTION_ANSWER = "action_answer";
    public final static String ACTION_REJECT = "action_reject";


    private PendingIntent yesPendingIntent;

    private static volatile Notificaitons sInstance = null;

    private Notificaitons() {
    }

    public static Notificaitons getInstance() {
        if(sInstance == null) {
            synchronized (Notificaitons.class) {
                if(sInstance == null) {
                    sInstance = new Notificaitons();
                }
            }
        }

        return sInstance;
    }

    public void sendSimpleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_SIMPLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建删除通知时发送的广播
        Intent deleteIntent = new Intent(context,NotificationService.class);
        deleteIntent.setAction(ACTION_DELETE);
        PendingIntent deletePendingIntent = PendingIntent.getService(context,0,deleteIntent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.LOW)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle(context.getString(R.string.notify_title))
                //设置通知内容
                .setContentText(context.getString(R.string.notify_content_athletic))
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置删除通知时的响应事件
                .setDeleteIntent(deletePendingIntent);
        //发送通知
        nm.notify(NOTIFICATION_SAMPLE,nb.build());
    }

    public void sendActionNotification(Context context, NotificationManager nm, String notifyTitle, String notifyContent, String buttonText) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_ACTION);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.notify_watch_live)
                //设置通知标题
                .setContentTitle(notifyTitle)
                //设置通知内容
                .setContentText(notifyContent)
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi);
        //创建点击通知 YES 按钮时发送的广播
        Intent yesIntent = new Intent(context, WatchLiveActivity.class);
        yesIntent.setAction(ACTION_YES);

        //一万个不想适配Android8.0
        yesPendingIntent = PendingIntent.getActivity(context,0,yesIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Action yesActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource("", R.mipmap.ic_yes),
                buttonText,
                yesPendingIntent)
                .build();
        //创建点击通知 NO 按钮时发送的广播
        Intent noIntent = new Intent(context,NotificationService.class);
        noIntent.setAction(ACTION_NO);
        PendingIntent noPendingIntent = PendingIntent.getService(context,0,noIntent,0);
        Notification.Action noActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource("", R.mipmap.ic_no),
                context.getString(R.string.notify_action_no),
                noPendingIntent)
                .build();
        //为通知添加按钮
        nb.setActions(yesActionBuilder,noActionBuilder);
        //发送通知
        nm.notify(NOTIFICATION_ACTION,nb.build());
    }

    public void sendVolunteerActionNotification(Context context, NotificationManager nm, String notifyTitle, String notifyContent, String buttonText) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_ACTION);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.notify_watch_live)
                //设置通知标题
                .setContentTitle(notifyTitle)
                //设置通知内容
                .setContentText(notifyContent)
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi);
        //创建点击通知 YES 按钮时发送的广播
        Intent yesIntent = new Intent(context, NavigationActivity.class);
        yesIntent.setAction(ACTION_YES);

        //一万个不想适配Android8.0
        yesPendingIntent = PendingIntent.getActivity(context,0,yesIntent,PendingIntent.FLAG_CANCEL_CURRENT);
        Notification.Action yesActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource("", R.mipmap.ic_yes),
                buttonText,
                yesPendingIntent)
                .build();
        //创建点击通知 NO 按钮时发送的广播
        Intent noIntent = new Intent(context,NotificationService.class);
        noIntent.setAction(ACTION_NO);
        PendingIntent noPendingIntent = PendingIntent.getService(context,0,noIntent,0);
        Notification.Action noActionBuilder = new Notification.Action.Builder(
                Icon.createWithResource("", R.mipmap.ic_no),
                context.getString(R.string.notify_action_no),
                noPendingIntent)
                .build();
        //为通知添加按钮
        nb.setActions(yesActionBuilder,noActionBuilder);
        //发送通知
        nm.notify(NOTIFICATION_ACTION,nb.build());
    }

    public void sendRemoteInputNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_REMOTE_INPUT);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.IMPORTANCE)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Remote input notification")
                //设置通知内容
                .setContentText("Demo for remote input notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi);
        //创建带输入框的按钮
        RemoteInput remoteInput = new RemoteInput.Builder(REMOTE_INPUT_RESULT_KEY)
                .setLabel("Reply").build();
        Intent remoteInputIntent = new Intent(context,NotificationService.class);
        remoteInputIntent.setAction(ACTION_REPLY);
        PendingIntent replyPendingIntent = PendingIntent.getService(context,2,remoteInputIntent,0);
        Notification.Action replyAction = new Notification.Action.Builder(
                Icon.createWithResource("",R.mipmap.ic_reply),
                "Reply",
                replyPendingIntent)
                .addRemoteInput(remoteInput)
                .build();
        //为通知添加按钮
        nb.setActions(replyAction);
        //发送通知
        nm.notify(NOTIFICATION_REMOTE_INPUT,nb.build());
    }

    public void sendBigPictureStyleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_BIG_PICTURE_STYLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建大视图样式
        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle()
                .setBigContentTitle("Big picture style notification ~ Expand title")
                .setSummaryText("Demo for big picture style notification ! ~ Expand summery")
                .bigPicture(BitmapFactory.decodeResource(context.getResources(),R.mipmap.big_style_picture));
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Big picture style notification")
                //设置通知内容
                .setContentText("Demo for big picture style notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置大视图样式通知
                .setStyle(bigPictureStyle);
        //发送通知
        nm.notify(NOTIFICATION_BIG_PICTURE_STYLE,nb.build());
    }

    public void sendBigTextStyleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_BIG_TEXT_STYLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建大文字样式
        Notification.BigTextStyle bigTextStyle = new Notification.BigTextStyle()
                .setBigContentTitle("Big text style notification ~ Expand title")
                .setSummaryText("Demo for big text style notification ! ~ Expand summery")
                .bigText("We are the champions   \n" +
                        "We are the champions   \n" +
                        "No time for losers   \n" +
                        "Cause we are the champions of the World");
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Big text style notification")
                //设置通知内容
                .setContentText("Demo for big text style notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置大文字样式通知
                .setStyle(bigTextStyle);
        //发送通知
        nm.notify(NOTIFICATION_BIG_TEXT_STYLE,nb.build());
    }

    public void sendInboxStyleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_INBOX_STYLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建信箱样式
        Notification.InboxStyle inboxStyle = new Notification.InboxStyle()
                .setBigContentTitle("Inbox style notification ~ Expand title")
                .setSummaryText("Demo for inbox style notification ! ~ Expand summery")
                //最多六行
                .addLine("1. I am email content.")
                .addLine("2. I am email content.")
                .addLine("3. I am email content.")
                .addLine("4. I am email content.")
                .addLine("5. I am email content.")
                .addLine("6. I am email content.");
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Inbox style notification")
                //设置通知内容
                .setContentText("Demo for inbox style notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置信箱样式通知
                .setStyle(inboxStyle);
        //发送通知
        nm.notify(NOTIFICATION_INBOX_STYLE,nb.build());
    }

    public void sendMediaStyleNotification(Context context, NotificationManager nm, boolean isPlaying) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_MEDIA_STYLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建Action按钮
        Intent playOrPauseIntent = new Intent(context,NotificationService.class);
        playOrPauseIntent.setAction(ACTION_MEDIA_STYLE);
        playOrPauseIntent.putExtra(EXTRA_OPTIONS, isPlaying ? MEDIA_STYLE_ACTION_PAUSE : MEDIA_STYLE_ACTION_PLAY);
        PendingIntent playOrPausePendingIntent = PendingIntent.getService(context,0, playOrPauseIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action playOrPauseAction = new Notification.Action.Builder(
                Icon.createWithResource(context,isPlaying ? R.mipmap.ic_pause : R.mipmap.ic_play),
                isPlaying ? "PAUSE" : "PLAY",
                playOrPausePendingIntent)
                .build();
        Intent nextIntent = new Intent(context,NotificationService.class);
        nextIntent.setAction(ACTION_MEDIA_STYLE);
        nextIntent.putExtra(EXTRA_OPTIONS, MEDIA_STYLE_ACTION_NEXT);
        PendingIntent nextPendingIntent = PendingIntent.getService(context,1, nextIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action nextAction = new Notification.Action.Builder(
                Icon.createWithResource(context,R.mipmap.ic_next),
                "Next",
                nextPendingIntent)
                .build();
        Intent deleteIntent = new Intent(context,NotificationService.class);
        deleteIntent.setAction(ACTION_MEDIA_STYLE);
        deleteIntent.putExtra(EXTRA_OPTIONS,MEDIA_STYLE_ACTION_DELETE);
        PendingIntent deletePendingIntent = PendingIntent.getService(context,2, deleteIntent,PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Action deleteAction = new Notification.Action.Builder(
                Icon.createWithResource(context,R.mipmap.ic_delete),
                "Delete",
                deletePendingIntent)
                .build();
        //创建媒体样式
        Notification.MediaStyle mediaStyle = new Notification.MediaStyle()
                //最多三个Action
                .setShowActionsInCompactView(0,1,2);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.MEDIA)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Media style notification")
                //设置通知内容
                .setContentText("Demo for media style notification !")
                //设置通知不可删除
                .setOngoing(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置Action按钮
                .setActions(playOrPauseAction,nextAction,deleteAction)
                //设置信箱样式通知
                .setStyle(mediaStyle);

        //发送通知
        nm.notify(NOTIFICATION_MEDIA_STYLE,nb.build());
    }

    public void sendMessagingStyleNotification(Context context, NotificationManager nm) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_MESSAGING_STYLE);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建信息样式
        Notification.MessagingStyle messagingStyle = new Notification.MessagingStyle("peter")
                .setConversationTitle("Messaging style notification")
                .addMessage("This is a message for you", System.currentTimeMillis(),"peter");
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.DEFAULT)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Messaging style notification")
                //设置通知内容
                .setContentText("Demo for messaging style notification !")
                //设置点击通知后自动删除通知
                .setAutoCancel(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                //设置信箱样式通知
                .setStyle(messagingStyle);
        //发送通知
        nm.notify(NOTIFICATION_MESSAGING_STYLE,nb.build());
    }

    public void sendProgressViewNotification(Context context, NotificationManager nm, int progress) {
        //创建点击通知时发送的广播
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_PROGRESS);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        //创建通知
        Notification.Builder nb = new Notification.Builder(context,NotificationChannels.LOW)
                //设置通知左侧的小图标
                .setSmallIcon(R.mipmap.ic_notification)
                //设置通知标题
                .setContentTitle("Downloading...")
                //设置通知内容
                .setContentText(String.valueOf(progress) + "%")
                //设置通知不可删除
                .setOngoing(true)
                //设置显示通知时间
                .setShowWhen(true)
                //设置点击通知时的响应事件
                .setContentIntent(pi)
                .setProgress(100,progress,false);
        //发送通知
        nm.notify(NOTIFICATION_PROGRESS,nb.build());
    }





    public void clearAllNotification(NotificationManager nm) {
        nm.cancelAll();
    }
}
