package com.jd.bluedragon.distribution.notice.domain;

import java.io.InputStream;

/**
 * @author lixin39
 * @Description TODO
 * @ClassName AttachmentDownloadDto
 * @date 2019/4/23
 */
public class AttachmentDownloadDto {

    /**
     * 文件输入流
     */
    private InputStream inputStream;

    /**
     * 文件名称
     */
    private String fileName;

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
