package com.jd.bluedragon.distribution.basic;

import org.apache.log4j.Logger;

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

    private static final Logger logger = Logger.getLogger(FileUtils.class);

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
            logger.error("文件写入时发生异常", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("InputStream文件输入流关闭异常", e);
            }

            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                logger.error("OutputStream文件输出流关闭异常", e);
            }
        }
    }
}
