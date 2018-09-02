package com.kmjd.jsylc.zxh.module.card.utils;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Base64Util {

    /**
     * <p>gcBitmap</p>
     * @param bitmap
     * @Description 图片回收
     */
    public static void gcBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle(); // 回收图片所占的内存
            bitmap = null;
            System.gc(); // 提醒系统及时回收
        }
    }

    /**
     * <p>bitmapToBase64</p>
     *
     * @param @param  bitmap
     * @Description: (Bitmap 转换为字符串)
     */

    @SuppressLint("NewApi")
    public static String bitmapToBase64(Bitmap bitmap) {
        // 要返回的字符串
        String reslut = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                // 压缩只对保存有效果bitmap还是原来的大小
                bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
                baos.flush();
                baos.close();
                // 转换为字节数组
                byte[] byteArray = baos.toByteArray();
                // 转换为字符串
                reslut = android.util.Base64.encodeToString(byteArray, android.util.Base64.DEFAULT);
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return reslut;

    }

    /**
     * <p>base64ToBitmap</p>
     * @param  base64String
     * @return Bitmap    返回Bitmap图片
     * @Description: (base64l转换为Bitmap)
     */
    public static Bitmap base64ToBitmap(String base64String) {
        byte[] decode = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        return bitmap;
    }

}
