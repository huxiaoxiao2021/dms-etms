package com.jd.bluedragon.distribution.print.domain.international;

import java.io.Serializable;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 3:53 PM
 */
public class RenderResultDTO implements Serializable {

    /**
     * 输出方式配置可选的值，0(Printer，输出到打印机)，1(File，输出到文件)
     */
    private Integer outputType;

    /**
     * 输出到本地文件格式，PDF-1，手持贴标机专用-2，蓝牙打印指令集-3。图片-4 当outputType=1时，此项必填。
     */
    private Integer fileFormat;

    /**
     * fileFormat=1时枚举值：1：URL
     * fileFormat=2时枚举值：2：base64字符串；
     * fileFormat=3时枚举值：3：ESC/POS；4：CPCL ；5：cpcl指令打印BMP；6：tspl；7：tspl指令打印BMP
     */
    private Integer dataFormat;

    /**
     * 成功的业务单号，全部成功时不返
     */
    private List<String> successfulOrderNumber;

    /**
     * 失败的业务单号，全部失败时不返。
     */
    private List<String> failedOrderNumber;

    /**
     * 文件下载链接，当fileFormat=1(PDF) 且 dataFormat=1 时，此项必返
     */
    private String url;

    /**
     * 返回唯一码（用来排查问题）
     */
    private String requestId;

    /**
     * 只有打印京东面单时，才会返回打印成功的包裹号。
     */
    private String successPackageCodes;

    /**
     * 错误单号、错误码、错误信息（部分失败、全部失败时返回）
     */
    private List<ErrorInfo> failedOrderInfo;

    public Integer getOutputType() {
        return outputType;
    }

    public void setOutputType(Integer outputType) {
        this.outputType = outputType;
    }

    public Integer getFileFormat() {
        return fileFormat;
    }

    public void setFileFormat(Integer fileFormat) {
        this.fileFormat = fileFormat;
    }

    public Integer getDataFormat() {
        return dataFormat;
    }

    public void setDataFormat(Integer dataFormat) {
        this.dataFormat = dataFormat;
    }

    public List<String> getSuccessfulOrderNumber() {
        return successfulOrderNumber;
    }

    public void setSuccessfulOrderNumber(List<String> successfulOrderNumber) {
        this.successfulOrderNumber = successfulOrderNumber;
    }

    public List<String> getFailedOrderNumber() {
        return failedOrderNumber;
    }

    public void setFailedOrderNumber(List<String> failedOrderNumber) {
        this.failedOrderNumber = failedOrderNumber;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSuccessPackageCodes() {
        return successPackageCodes;
    }

    public void setSuccessPackageCodes(String successPackageCodes) {
        this.successPackageCodes = successPackageCodes;
    }

    public List<ErrorInfo> getFailedOrderInfo() {
        return failedOrderInfo;
    }

    public void setFailedOrderInfo(List<ErrorInfo> failedOrderInfo) {
        this.failedOrderInfo = failedOrderInfo;
    }
}
