package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author pengchong28
 * @description 装车评价记录申诉审核request对象
 * @date 2024/3/6
 */
public class JyEvaluateRecordAppealRes implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 申诉的评价维度id和审核操作，opinion中，1是通过，2是驳回
     */
    List<Map<String, Integer>> appealList;
    /**
     * 修改人ERP
     */
    private String updateUserErp;
    /**
     * 更新人姓名
     */
    private String updateUserName;

    /**
     * 历史的不满意明细
     */
    private List<AppealDimensionReq> dimensionList;

    /**
     * 被评价目标业务主键
     */
    private String targetBizId;
    /**
     * 评价来源业务主键
     */
    private String sourceBizId;

    /**
     * 被评价目标场地编码（装车场地编码）
     */
    private Long targetSiteCode;
    /**
     * 评价来源场地编码
     */
    private Long sourceSiteCode;

    public List<Map<String, Integer>> getAppealList() {
        return appealList;
    }

    public void setAppealList(List<Map<String, Integer>> appealList) {
        this.appealList = appealList;
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp;
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName;
    }

    public List<AppealDimensionReq> getDimensionList() {
        return dimensionList;
    }

    public void setDimensionList(List<AppealDimensionReq> dimensionList) {
        this.dimensionList = dimensionList;
    }

    public String getTargetBizId() {
        return targetBizId;
    }

    public void setTargetBizId(String targetBizId) {
        this.targetBizId = targetBizId;
    }

    public String getSourceBizId() {
        return sourceBizId;
    }

    public void setSourceBizId(String sourceBizId) {
        this.sourceBizId = sourceBizId;
    }

    public Long getTargetSiteCode() {
        return targetSiteCode;
    }

    public void setTargetSiteCode(Long targetSiteCode) {
        this.targetSiteCode = targetSiteCode;
    }

    public Long getSourceSiteCode() {
        return sourceSiteCode;
    }

    public void setSourceSiteCode(Long sourceSiteCode) {
        this.sourceSiteCode = sourceSiteCode;
    }
}
