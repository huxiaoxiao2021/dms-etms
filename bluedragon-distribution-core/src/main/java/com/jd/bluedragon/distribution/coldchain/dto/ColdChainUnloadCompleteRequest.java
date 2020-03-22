package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * 冷链卸货完成请求
 */
public class ColdChainUnloadCompleteRequest {

    /**
     * 操作人ERP
     */
    private String operateERP;

    /**
     * 任务编号
     */
    private String taskNo;

    public String getOperateERP() {
        return operateERP;
    }

    public void setOperateERP(String operateERP) {
        this.operateERP = operateERP;
    }

    public String getTaskNo() {
        return taskNo;
    }

    public void setTaskNo(String taskNo) {
        this.taskNo = taskNo;
    }
}
