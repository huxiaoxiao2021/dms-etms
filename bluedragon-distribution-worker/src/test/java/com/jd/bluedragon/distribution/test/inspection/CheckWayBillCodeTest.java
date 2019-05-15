package com.jd.bluedragon.distribution.test.inspection;

import com.jd.bluedragon.distribution.reverse.domain.ReverseSendWms;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.junit.Test;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringWriter;

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

    @Test
    public void testForChar() throws Exception {
        String s = "10000000003000000000000000000000012000000000000000";
        ReverseSendWms send = new ReverseSendWms();
//        send.setReverseWaybillType(Character.getNumericValue(s.charAt(33)));
        char b = s.charAt(33);
        System.out.println(toXml(send,ReverseSendWms.class));;
        System.out.println(s.length());
        System.out.println(b);
        System.out.println(b == '2');
        System.out.println(b == 48);
    }

    public static String toXml(Object request, Class<?> clazz) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //京东安全 修改：禁用DOCTYPE https://cf.jd.com/pages/viewpage.action?pageId=110849207
            dbf.setExpandEntityReferences(false);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setXIncludeAware(false);
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();

            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.marshal(request, doc);

            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (Exception e) {
            System.out.println(e);
        }

        return null;
    }

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
                if(type == PACKAGE_TYPE && WaybillUtil.isPackageCode(code) && !WaybillUtil.isPackageCode(code)){
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
                if(type == PACKAGE_TYPE && WaybillUtil.isPackageCode(code) && WaybillUtil.isPackageCode(code)){
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
