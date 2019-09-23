package com.xiaok.winterolympic.utils.notify;

import android.graphics.Bitmap;

/**
 *
 * @author peter
 * @date 2018/7/4
 */

public class NotificationContentWrapper {
    public Bitmap bitmap;
    public String title;
    public String summery;

    public NotificationContentWrapper(Bitmap bitmap, String title, String summery) {
        this.bitmap = bitmap;
        this.title = title;
        this.summery = summery;
    }
}
