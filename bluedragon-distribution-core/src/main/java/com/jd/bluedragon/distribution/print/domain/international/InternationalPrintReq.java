package com.jd.bluedragon.distribution.print.domain.international;


import com.jd.bluedragon.Constants;

import java.io.Serializable;
import java.util.List;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2023/7/19 3:36 PM
 */
public class InternationalPrintReq implements Serializable {

    /**
     * 调用方系统标识
     */
    private final String callsystem = Constants.SYSTEM_CODE_OWON;

    /**
     * 模板尺寸
     */
    private String templateSize;

    /**
     * 操作打单人账号:京东PIN/ERP
     */
    private String operator;

    /**
     * 操作打印类型:逆向换单打印-1，包裹标签补打-2
     */
    private Integer operateType;

    /**
     * 场地ID
     */
    private String operatorSiteId;

    /**
     * 单次请求任务唯一识别码
     */
    private String requestId;

    /**
     * 打印的输出方式
     */
    private List<OutputConfigDTO> outputConfig;

    public String getCallsystem() {
        return callsystem;
    }

    public String getTemplateSize() {
        return templateSize;
    }

    public void setTemplateSize(String templateSize) {
        this.templateSize = templateSize;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public String getOperatorSiteId() {
        return operatorSiteId;
    }

    public void setOperatorSiteId(String operatorSiteId) {
        this.operatorSiteId = operatorSiteId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public List<OutputConfigDTO> getOutputConfig() {
        return outputConfig;
    }

    public void setOutputConfig(List<OutputConfigDTO> outputConfig) {
        this.outputConfig = outputConfig;
    }
}
