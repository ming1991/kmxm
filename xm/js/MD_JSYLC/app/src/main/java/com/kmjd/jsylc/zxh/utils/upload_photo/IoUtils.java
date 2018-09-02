package com.kmjd.jsylc.zxh.utils.upload_photo;

import java.io.Closeable;

/**
 * Created by http://teachcourse.cn on 2018/1/11.
 */

class IoUtils implements Cloneable {

    public static void closeSilently(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }
}
