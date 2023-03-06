package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * @program: ql-dms-distribution
 * @description:
 * @author: xumigen
 * @create: 2023-03-01 13:52
 **/
public class FileRequest implements Serializable {

    /**
     * 查看文件列表是需要传
     */
    private String fileNamePrefix;

    private String fileName;

    private String folder;

    /**
     * 删除和上传时需要传入
     */
    private String secretKey;


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

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }
}
