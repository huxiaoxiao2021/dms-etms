package com.jd.bluedragon.distribution.print.waybill.handler.complete;

import com.jd.bluedragon.distribution.handler.Context;
import com.jd.bluedragon.distribution.handler.InterceptResult;
import com.jd.bluedragon.distribution.print.request.PrintCompleteRequest;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wyh
 * @className WaybillPrintCompleteContext
 * @description
 * @date 2021/12/2 14:52
 **/
public class WaybillPrintCompleteContext implements Context, Serializable {

    private static final long serialVersionUID = 7154641119473260922L;

    /**
     * 系统编码标识 dms-分拣系统 wms-仓储
     */
    private String systemCode;

    /**
     *  请求应用程序类型-40-青龙打印客户端 50-jsf调用
     */
    protected Integer programType;

    /**
     * 业务类型
     */
    protected Integer businessType;

    /**
     * 业务操作类型
     */
    protected Integer operateType;

    /**
     * 返回结果
     */
    private InterceptResult<Boolean> result;

    /**
     * 请求体
     */
    private PrintCompleteRequest request;

    /**
     * 待处理的包裹号
     */
    private List<String> toDealPackageCodes;

    /**
     * 首次打印的包裹
     */
    private List<String> firstPrintPackages;

    /**
     * 补打的包裹
     */
    private List<String> reprintPackages;

    public PrintCompleteRequest getRequest() {
        return request;
    }

    public void setRequest(PrintCompleteRequest request) {
        this.request = request;
    }

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public Integer getProgramType() {
        return programType;
    }

    public void setProgramType(Integer programType) {
        this.programType = programType;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public List<String> getToDealPackageCodes() {
        return toDealPackageCodes;
    }

    public void setToDealPackageCodes(List<String> toDealPackageCodes) {
        this.toDealPackageCodes = toDealPackageCodes;
    }

    public InterceptResult<Boolean> getResult() {
        return result;
    }

    public void setResult(InterceptResult<Boolean> result) {
        this.result = result;
    }

    public List<String> getFirstPrintPackages() {
        return firstPrintPackages;
    }

    public void setFirstPrintPackages(List<String> firstPrintPackages) {
        this.firstPrintPackages = firstPrintPackages;
    }

    public List<String> getReprintPackages() {
        return reprintPackages;
    }

    public void setReprintPackages(List<String> reprintPackages) {
        this.reprintPackages = reprintPackages;
    }

    public void addToDealPackageCodes(String packageCode) {
        if (CollectionUtils.isEmpty(this.toDealPackageCodes)) {
            this.toDealPackageCodes = new ArrayList<>();
        }
        addToList(this.toDealPackageCodes, packageCode);
    }

    public void addToFirstPrintList(String packageCode) {
        if (CollectionUtils.isEmpty(this.firstPrintPackages)) {
            this.firstPrintPackages = new ArrayList<>();
        }
        addToList(this.firstPrintPackages, packageCode);
    }

    public void addToReprintPrintList(String packageCode) {
        if (CollectionUtils.isEmpty(this.reprintPackages)) {
            this.reprintPackages = new ArrayList<>();
        }
        addToList(this.reprintPackages, packageCode);
    }

    private <T> void addToList(List<T> list, T item) {
        if (StringUtils.isEmpty((String) item)) {
            return;
        }
        if (!list.contains(item)) {
            list.add(item);
        }
    }
}
