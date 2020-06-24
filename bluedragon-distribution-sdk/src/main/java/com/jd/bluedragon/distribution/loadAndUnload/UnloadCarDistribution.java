package com.jd.bluedragon.distribution.loadAndUnload;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

/**
 * 卸车任务与卸车人关系DTO
 *
 * @author: hujiping
 * @date: 2020/6/23 11:07
 */
public class UnloadCarDistribution extends DbEntity {

    /**
     * 卸车任务人关系主键ID
     * */
    private Long unloadDistributeId;
    /**
     * 封车编码
     * */
    private String sealCarCode;
    /**
     * 卸车人ERP
     * */
    private String unloadUserErp;
    /**
     * 卸车人名称
     * */
    private String unloadUserName;
    /**
     * 卸车人类型
     *  0：负责人 1：协助人
     * */
    private Integer unloadUserType;
    /**
     * 是否有效
     * */
    private Integer yn;

    public Long getUnloadDistributeId() {
        return unloadDistributeId;
    }

    public void setUnloadDistributeId(Long unloadDistributeId) {
        this.unloadDistributeId = unloadDistributeId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getUnloadUserErp() {
        return unloadUserErp;
    }

    public void setUnloadUserErp(String unloadUserErp) {
        this.unloadUserErp = unloadUserErp;
    }

    public String getUnloadUserName() {
        return unloadUserName;
    }

    public void setUnloadUserName(String unloadUserName) {
        this.unloadUserName = unloadUserName;
    }

    public Integer getUnloadUserType() {
        return unloadUserType;
    }

    public void setUnloadUserType(Integer unloadUserType) {
        this.unloadUserType = unloadUserType;
    }

    public Integer getYn() {
        return yn;
    }

    public void setYn(Integer yn) {
        this.yn = yn;
    }
}
