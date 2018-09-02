package com.kmjd.jsylc.zxh.utils;

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

    /*请将这段提示一定放入你的dimens文件的开头，它对你的组员很重要
    <!--重要提示：如需再自行添加复杂命名的dimen，请在name = "dp1"之前添加，否则下次调整范围，将丢失你的数据，后果自负，切记！-->*/
    private static String filePath = "E:\\MD\\AndroidStudioProjects\\oschina\\JSYLC\\app\\src\\main\\res\\values";
    private static String fileName = "dimens.xml";
    private static File dimensFile;
    private static int maxDp = 301;
    private static int maxSp = 25;
    private static int startIndex = 1;
    public static void main(String[] args) {

        dimensFile = new File(filePath, fileName);
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

    public static void createNewFile(){

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
