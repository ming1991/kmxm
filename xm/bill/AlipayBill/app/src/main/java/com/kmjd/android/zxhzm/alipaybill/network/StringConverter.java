package com.kmjd.android.zxhzm.alipaybill.network;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by zym on 2016/12/19.
 */

public class StringConverter implements Converter<ResponseBody, String> {

    private static final StringConverter INSTANCE = new StringConverter();
    public static StringConverter create() {
        return INSTANCE;
    }
    /*该方法为抽象方法,关键就在于我们怎么将传入的ResponseBody类型转换为想要的String类型*/
    @Override
    public String convert(@NonNull ResponseBody value) throws IOException {
        return value.string();
    }
}
