package com.jd.bluedragon.common.dto.jyexpection.response;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 拦截任务详情实体
 * @author fanggang7
 * @time 2024-01-14 17:37:50 周日
 */
public class JyExceptionInterceptDetailDto implements Serializable {
    private static final long serialVersionUID = -1094345976242701327L;

    private String bizId;

    /**
     * 任务提报人ID
     */
    private Integer createUserId;

    /**
     * 任务提报人erp
     */
    private String createUserErp;

    /**
     * 任务提报人名称
     */
    private String createUserName;

    /**
     * 拦截类型
     */
    private Integer interceptType;

    /**
     * 拦截类型名称
     */
    private String interceptTypeName;

    /**
     * 任务类型
     */
    private Integer taskType;

    /**
     * 任务类型名称
     */
    private String taskTypeName;

    /**
     * 处理方式
     */
    private List<Integer> disposeNode;

    /**
     * 处理方式名称
     */
    private List<String> disposeNodeName;

    /**
     * 录入重量
     */
    private BigDecimal inputWeight;

    /**
     * 录入体积
     */
    private BigDecimal inputVolume;

    /**
     * 录入长度
     */
    private BigDecimal inputLength;
    /**
     * 录入宽度
     */
    private BigDecimal inputWidth;

    /**
     * 录入高度
     */
    private BigDecimal inputHeight;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(Integer createUserId) {
        this.createUserId = createUserId;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp;
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
    }

    public Integer getInterceptType() {
        return interceptType;
    }

    public void setInterceptType(Integer interceptType) {
        this.interceptType = interceptType;
    }

    public String getInterceptTypeName() {
        return interceptTypeName;
    }

    public void setInterceptTypeName(String interceptTypeName) {
        this.interceptTypeName = interceptTypeName;
    }

    public Integer getTaskType() {
        return taskType;
    }

    public void setTaskType(Integer taskType) {
        this.taskType = taskType;
    }

    public String getTaskTypeName() {
        return taskTypeName;
    }

    public void setTaskTypeName(String taskTypeName) {
        this.taskTypeName = taskTypeName;
    }

    public List<Integer> getDisposeNode() {
        return disposeNode;
    }

    public void setDisposeNode(List<Integer> disposeNode) {
        this.disposeNode = disposeNode;
    }

    public List<String> getDisposeNodeName() {
        return disposeNodeName;
    }

    public void setDisposeNodeName(List<String> disposeNodeName) {
        this.disposeNodeName = disposeNodeName;
    }

    public BigDecimal getInputWeight() {
        return inputWeight;
    }

    public void setInputWeight(BigDecimal inputWeight) {
        this.inputWeight = inputWeight;
    }

    public BigDecimal getInputVolume() {
        return inputVolume;
    }

    public void setInputVolume(BigDecimal inputVolume) {
        this.inputVolume = inputVolume;
    }

    public BigDecimal getInputLength() {
        return inputLength;
    }

    public void setInputLength(BigDecimal inputLength) {
        this.inputLength = inputLength;
    }

    public BigDecimal getInputWidth() {
        return inputWidth;
    }

    public void setInputWidth(BigDecimal inputWidth) {
        this.inputWidth = inputWidth;
    }

    public BigDecimal getInputHeight() {
        return inputHeight;
    }

    public void setInputHeight(BigDecimal inputHeight) {
        this.inputHeight = inputHeight;
    }
}
