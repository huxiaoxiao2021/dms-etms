package com.jd.bluedragon.distribution.work.domain;

import java.io.Serializable;

/**
 * 暴力分拣任务责任信息
 */
public class ResponsibleInfoVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 工种：1-正式工 2-外包工 3-临时工
     */
    private String workTypeName;
    /**
     * 责任人类型：网格签到人 外包商 网格组长
     */
    private String responsibleTypeName;
    /**
     * 责任人编码 erp或外包商code
     */
    private String responsibleCode;

    /**
     * 责任人名称 姓名或外包商名称
     */
    private String responsibleName;
    

    /**
     * 责任人身份证号，work_type为2或3时
     */
    private String idCard;
    /**
     * 外包人员姓名
     */
    private String outerName;

    public String getWorkTypeName() {
        return workTypeName;
    }

    public void setWorkTypeName(String workTypeName) {
        this.workTypeName = workTypeName;
    }

    public String getResponsibleTypeName() {
        return responsibleTypeName;
    }

    public void setResponsibleTypeName(String responsibleTypeName) {
        this.responsibleTypeName = responsibleTypeName;
    }

    public String getResponsibleCode() {
        return responsibleCode;
    }

    public void setResponsibleCode(String responsibleCode) {
        this.responsibleCode = responsibleCode;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }

    public String getOuterName() {
        return outerName;
    }

    public void setOuterName(String outerName) {
        this.outerName = outerName;
    }
}
