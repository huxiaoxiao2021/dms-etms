package com.jd.bluedragon.distribution.test.inspection;

import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.Test;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;

/**
 * Created by shipeilin on 2017/8/4.
 */
public class CheckWayBillCodeTest {
    public static final int PACKAGE_TYPE = 0;
    public static final int Waybill_TYPE = 1;

    private static final String waybill_right_URL = "D:\\waybill_right.txt";
    private static final String waybill_error_URL = "D:\\waybill_error.txt";
    private static final String package_right_URL = "D:\\package_right.txt";
    private static final String package_error_URL = "D:\\package_error.txt";

    public static void main(String[] args) {
        readFileByLines(package_right_URL, PACKAGE_TYPE);
        readFileByLinesShowRight(package_right_URL, PACKAGE_TYPE);
        readFileByLines(package_error_URL, PACKAGE_TYPE);
        readFileByLinesShowRight(package_error_URL, PACKAGE_TYPE);
//        readFileByLines(waybill_right_URL, Waybill_TYPE);
//        readFileByLines(waybill_error_URL, Waybill_TYPE);
    }

    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLines(String fileName,int type) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String code = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((code = reader.readLine()) != null) {
                if(type == PACKAGE_TYPE && BusinessHelper.isPackageCode(code) && !SerialRuleUtil.isMatchCommonPackageCode(code)){
                    // 显示行号
                    System.out.println("line " + line + "error PackageCode: " + code);
                }
                if(type == Waybill_TYPE && !SerialRuleUtil.isMatchCommonWaybillCode(code)){
                    // 显示行号
                    System.out.println("line " + line + " error WaybillCode: " + code);
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
    /**
     * 以行为单位读取文件，常用于读面向行的格式化文件
     */
    public static void readFileByLinesShowRight(String fileName,int type) {
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            System.out.println("以行为单位读取文件内容，一次读一整行：");
            reader = new BufferedReader(new FileReader(file));
            String code = null;
            int line = 1;
            // 一次读入一行，直到读入null为文件结束
            while ((code = reader.readLine()) != null) {
                if(type == PACKAGE_TYPE && BusinessHelper.isPackageCode(code) && SerialRuleUtil.isMatchCommonPackageCode(code)){
                    // 显示行号
                    System.out.println("line " + line + " right PackageCode: " + code);
                }
                if(type == Waybill_TYPE && SerialRuleUtil.isMatchCommonWaybillCode(code)){
                    // 显示行号
                    System.out.println("line " + line + " right WaybillCode: " + code);
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

    @Test
    public void name() throws Exception {
        try{testForException();}catch(Exception e){
            System.out.println(e);
        }
    }

    @Test
    public void testForException() throws Exception {
        for(int i = 0;i<3;i++){
                try{
                    if(i==0){
                        throw new Exception("Exc" +i);
                    }
                    System.out.println("第"+i+"行执行");
                    break;
                }catch(Exception e)
                {
                    if(i==2){
                        throw new Exception("Game over~");
                    }
                }
        }
    }
}
