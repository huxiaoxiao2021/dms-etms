package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @author: 刘铎（liuduo8）
 * @date: 2024/4/17
 * @description: 云打印本地打印组件需要的打印数据实体
 */
public class CloudPrintDocument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 明文常量
     */
    public final static String DATA_TYPE_PLAIN = "plain";

    /**
     * 文档ID
     */
    private String documentId;
    
    /**
     * 模板代码
     */
    private String templateCode;
    
    /**
     * 打印数据
     */
    private String printData;
    
    /**
     * 数据类型（加密）
     */
    private String dataType;
    
    /**
     * 自定义打印数据
     */
    private String customizedPrintData;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getPrintData() {
        return printData;
    }

    public void setPrintData(String printData) {
        this.printData = printData;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCustomizedPrintData() {
        return customizedPrintData;
    }

    public void setCustomizedPrintData(String customizedPrintData) {
        this.customizedPrintData = customizedPrintData;
    }
}