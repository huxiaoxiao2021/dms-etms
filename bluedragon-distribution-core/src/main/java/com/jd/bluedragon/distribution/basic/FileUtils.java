package com.jd.bluedragon.distribution.basic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName FileUtils
 * @date 2019/4/23
 */
public class FileUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    /**
     * 输入输出流写入
     *
     * @param is
     * @param os
     */
    public static void write(InputStream is, OutputStream os) {
        try {
            byte[] b = new byte[1024];
            int i;
            while ((i = is.read(b)) > 0) {
                os.write(b, 0, i);
            }
            os.flush();
        } catch (Exception e) {
            log.error("文件写入时发生异常", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.error("InputStream文件输入流关闭异常", e);
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                log.error("OutputStream文件输出流关闭异常", e);
            }
        }
    }

    /**
     * 根据文件全名获取文件扩展名
     *
     * @param fullFileName
     * @return
     */
    public static String getFileExtName(String fullFileName) {
        int index = fullFileName.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return fullFileName.substring(index + 1);
    }
}
