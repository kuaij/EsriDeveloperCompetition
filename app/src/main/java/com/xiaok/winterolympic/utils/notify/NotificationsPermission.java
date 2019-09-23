package com.xiaok.winterolympic.utils.notify;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NotificationsPermission {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    @SuppressLint("NewApi")
    public static boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            return isEnableV26(context);
        }else {
            AppOpsManager mAppOps =
                    (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

            ApplicationInfo appInfo = context.getApplicationInfo();
            String pkg = context.getApplicationContext().getPackageName();
            int uid = appInfo.uid;
            Class appOpsClass = null;

            /* Context.APP_OPS_MANAGER */
            try {
                appOpsClass = Class.forName(AppOpsManager.class.getName());

                Method checkOpNoThrowMethod =
                        appOpsClass.getMethod(CHECK_OP_NO_THROW,
                                Integer.TYPE, Integer.TYPE, String.class);

                Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
                int value = (Integer) opPostNotificationValue.get(Integer.class);

                return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) ==
                        AppOpsManager.MODE_ALLOWED);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }

    }


    /**
     * 8.0以上获取t通知栏状态
     * @param context
     * @return
     */
    public static boolean isEnableV26(Context context) {
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        try {
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            Method sServiceField = notificationManager.getClass().getDeclaredMethod("getService");
            sServiceField.setAccessible(true);
            Object sService = sServiceField.invoke(notificationManager);

            Method method = sService.getClass().getDeclaredMethod("areNotificationsEnabledForPackage"
                    , String.class, Integer.TYPE);
            method.setAccessible(true);
            return (boolean) method.invoke(sService, pkg, uid);
        } catch (Exception e) {
            return true;
        }
    }
}
