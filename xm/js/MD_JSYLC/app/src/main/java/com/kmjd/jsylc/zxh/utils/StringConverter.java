package com.kmjd.jsylc.zxh.utils;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Created by zym on 2016/12/19.
 */

public class StringConverter implements Converter<ResponseBody, String> {

    public static final StringConverter INSTANCE = new StringConverter();

    /*该方法为抽象方法,关键就在于我们怎么将传入的ResponseBody类型转换为想要的String类型*/
    @Override
    public String convert(ResponseBody value) throws IOException {
/*      这是ResponseBody类中的方法,该方法实现将ResponseBody转化为String
        public final String string ()throws IOException {
            return new String(bytes(), charset().name());
        }
*/
        return value.string();
    }
}
