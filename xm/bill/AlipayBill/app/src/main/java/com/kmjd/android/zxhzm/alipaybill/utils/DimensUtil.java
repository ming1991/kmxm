package com.kmjd.android.zxhzm.alipaybill.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by KMJD on 2017/1/18.
 */

public class DimensUtil {

    private static File dimensFile;
    private static int startIndex = 1;
    public static void main(String[] args) {

        String filePath = "../AlipayBill/app/src/main/res/values/";
        String fileName = "dimens.xml";
        dimensFile = new File(filePath, fileName);
        int maxDp = 301;
        int maxSp = 25;
        if (dimensFile.exists()) {
            writeToFile(maxDp, maxSp);
        } else {
            createNewFile();
            writeToFile(maxDp, maxSp);
        }

    }

    private static void writeToFile(int maxDp, int maxSp) {
        FileWriter fileWriter;
        StringBuilder sb;
        BufferedWriter bufferedWriter = null;
        String needTempDimens = readFromFile();
        try {
            //创建一个新的空文件直接覆盖以前的文件（不用担心内容丢失，因为明知道dimens文件不可能很大，已缓存）
            fileWriter = new FileWriter(dimensFile, false);
            sb = new StringBuilder();
            System.out.print(needTempDimens);
            sb.append(needTempDimens);
            for (int i = startIndex; i < maxDp; i++) {
                sb.append("\n    <dimen name = \"dp").append(i).append("\"").append(">").append(i).append("dp</dimen>");
            }
            sb.append("\n<!--————————————————————dp、sp分割线————————————————————-->");
            for (int i = startIndex; i < maxSp; i++) {
                sb.append("\n    <dimen name = \"sp").append(i).append("\"").append(">").append(i).append("sp</dimen>");
            }
            startIndex = 1;
            sb.append("\n</resources>");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String readFromFile(){
        FileReader fileReader;
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        String needTempDimens = "";
        try {

            fileReader = new FileReader(dimensFile);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
            String total = sb.toString();
            needTempDimens = total.split("</resources>")[0].replace("<resources>","\n<resources>").replace("    ","\n    ").
                    split("<dimen name = \"dp0\">1dp</dimen>")[0];

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return needTempDimens;
    }

    private static void createNewFile(){

        StringBuilder sb;
        BufferedWriter bufferedWriter = null;
        FileWriter fileWriter;
        try {
            //false,从头写
            fileWriter = new FileWriter(dimensFile, false);
            sb = new StringBuilder();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
            sb.append("\n<resources>");
            sb.append("\n</resources>");
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(sb.toString());
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (bufferedWriter != null){
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
