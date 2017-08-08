package com.jd.bluedragon.distribution.test.inspection;

import com.jd.bluedragon.utils.SerialRuleUtil;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;

/**
 * Created by shipeilin on 2017/8/4.
 */
public class CheckWayBillCodeTest {
    private static final String rightURL = "D:\\right.txt";
    private static final String errorURL = "D:\\error.txt";

    public static void main(String[] args) {
        readFileByLines(rightURL);
        readFileByLines(errorURL);
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String waybillCode = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((waybillCode = reader.readLine()) != null) {
                if(!SerialRuleUtil.isMatchCommonWaybillCode(waybillCode)){
                    // 显示行号
                    System.out.println("line " + line + " error: " + waybillCode);
                }
                line++;
            }
            System.out.println("共计行数："+line);
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
