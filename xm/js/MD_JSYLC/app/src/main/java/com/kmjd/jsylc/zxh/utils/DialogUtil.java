package com.kmjd.jsylc.zxh.utils;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.kmjd.jsylc.zxh.R;

import java.util.IllegalFormatCodePointException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by androidshuai on 2017/11/29.
 */

public class DialogUtil {

    public static void showXunXiDialog(Context context, String title, String message){
        final Dialog messageDialog = new Dialog(context, R.style.MessageDialog);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.public_dialog3, null);
        messageDialog.setContentView(dialogView);
        messageDialog.setCanceledOnTouchOutside(false);
        View.OnClickListener onClickListener= new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.image_fork:
                    case R.id.button_confirm:
                        messageDialog.dismiss();
                        break;
                }
            }
        };
        ((TextView) dialogView.findViewById(R.id.tv_title)).setText(title);
        ((TextView) dialogView.findViewById(R.id.tv_message)).setText(Html.fromHtml(message));
        dialogView.findViewById(R.id.image_fork).setOnClickListener(onClickListener);
        dialogView.findViewById(R.id.button_confirm).setOnClickListener(onClickListener);
        messageDialog.show();
    }

    /**
     * 去掉Dialog文字里的空格
     * @param src
     * @return
     */
    public static String replaceBlank(String src) {
        String dest = "";
        if (src != null) {
            Pattern pattern = Pattern.compile("\t|\r|\n|\\s*");
            Matcher matcher = pattern.matcher(src);
            dest = matcher.replaceAll("");
            dest= Html.fromHtml(dest).toString().trim();
        }
        return dest;
    }
}
