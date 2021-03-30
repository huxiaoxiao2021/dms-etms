package com.jd.bluedragon.utils;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by xumigen on 2018/4/6.
 */
@SuppressWarnings("all")
public class CsvExporterUtils {
    private static final Logger logger = LoggerFactory.getLogger(CsvExporterUtils.class);

    public static void setResponseHeader(HttpServletResponse response, String fileName) throws Exception {
        response.setContentType("text/csv;charset=UTF-8");
        fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
        response.setHeader("Content-Disposition","attachment;filename=" + fileName);
        response.addHeader("Pargam", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
    }

    public static void writeTitleOfCsv(Map<String, String> map, BufferedWriter bfw, int length) throws IOException {
        int numtitle = 0;
        for (String value : map.values()) {
            numtitle++;
            StringBuffer sb = new StringBuffer();
            sb.append("\"").append(value).append("\"");
            bfw.write(sb.toString());
            if (numtitle <= length) {
                bfw.write(",");
            }
        }
        bfw.newLine();
    }

    public static void writeCsvByPage(BufferedWriter bfw, Map<String, String> map, List list) {
        try {
            for (Object obj : list) {
                int numObj = 0;

                for (Map.Entry<String, String> entry : map.entrySet()) {
                    numObj++;

                    StringBuffer sb = getThisFieldValue(obj, entry);

                    bfw.write(sb.toString());

                    if (numObj <= map.values().size()) {
                        bfw.write(",");
                    }
                }
                bfw.newLine();
            }
        } catch (Exception ex) {
            logger.error("写数据异常", ex);
        }
    }

    public static void writeCsvByPage(BufferedWriter bfw, Map<String, String> headerMap, Map<String, String> textMap, List list) {
        try {
            for (Object obj : list) {
                int numObj = 0;

                for (Map.Entry<String, String> entry : headerMap.entrySet()) {
                    numObj++;

                    StringBuffer sb = getThisFieldValue(obj, entry, textMap);

                    bfw.write(sb.toString());

                    if (numObj <= headerMap.values().size()) {
                        bfw.write(",");
                    }
                }
                bfw.newLine();
            }
        } catch (Exception ex) {
            logger.error("写数据异常", ex);
        }
    }

    private static StringBuffer getThisFieldValue(Object obj, Map.Entry<String, String> entry) {
        Object value = ReflectUtils.getFieldValueByName(entry.getKey(), obj);
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        if (value != null) {
            if (value instanceof Date) {
                String thisDate = DateHelper.formatDate((Date) value,DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2);
                sb.append(thisDate);
            } else {
                sb.append(String.valueOf(value));
            }
        }
        sb.append("\"");
        return sb;
    }

    private static StringBuffer getThisFieldValue(Object obj, Map.Entry<String, String> entry, Map<String, String> textMap) {
        Object value = ReflectUtils.getFieldValueByName(entry.getKey(), obj);
        StringBuffer sb = new StringBuffer();
        if (value != null) {
            if (value instanceof Date) {
                sb.append("\"");
                String thisDate = DateHelper.formatDate((Date) value,DateHelper.DATE_FORMAT_YYYYMMDDHHmmss2);
                sb.append(thisDate);
            } else {
                if(textMap.containsKey(entry.getKey())){
                    sb.append("=");
                }
                sb.append("\"");
                sb.append(String.valueOf(value));
            }
        }
        sb.append("\"");
        return sb;
    }
}
