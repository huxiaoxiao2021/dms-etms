package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: xumigen
 * @create: 2023-03-01 13:52
 **/
public class FileRequest implements Serializable {

    private String fileNamePrefix;

    private String fileName;

    public String getFileNamePrefix() {
        return fileNamePrefix;
    }

    public void setFileNamePrefix(String fileNamePrefix) {
        this.fileNamePrefix = fileNamePrefix;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
