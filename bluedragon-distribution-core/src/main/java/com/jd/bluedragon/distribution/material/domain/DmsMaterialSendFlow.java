package com.jd.bluedragon.distribution.material.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.io.Serializable;
import java.util.Date;

public class DmsMaterialSendFlow extends DbEntity implements Serializable {

    private static final long serialVersionUID = -174702515105996059L;

    /**
    * 物资编号
    */
    private String materialCode;

    /**
    * 物资类型；1：保温箱
    */
    private Byte materialType;

    /**
    * 发货方式；1：按物资单个发货；2：按容器发货
    */
    private Byte sendType;

    /**
    * 发货编号
    */
    private String sendCode;

    /**
    * 发货数量
    */
    private Integer sendNum;

    /**
    * 操作机构
    */
    private Long createSiteCode;

    /**
    * 操作机构类型
    */
    private Integer createSiteType;

    /**
    * 收货机构
    */
    private Long receiveSiteCode;

    /**
    * 收货机构类型
    */
    private Integer receiveSiteType;

    /**
    * 创建人ERP
    */
    private String createUserErp;

    /**
    * 创建人名称
    */
    private String createUserName;

    /**
    * 更新人ERP
    */
    private String updateUserErp;

    /**
    * 修改人名称
    */
    private String updateUserName;

    /**
    * 删除标识，1:未删除；0：已删除
    */
    private Boolean yn;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode == null ? null : materialCode.trim();
    }

    public Byte getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Byte materialType) {
        this.materialType = materialType;
    }

    public Byte getSendType() {
        return sendType;
    }

    public void setSendType(Byte sendType) {
        this.sendType = sendType;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode == null ? null : sendCode.trim();
    }

    public Integer getSendNum() {
        return sendNum;
    }

    public void setSendNum(Integer sendNum) {
        this.sendNum = sendNum;
    }

    public Long getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Long createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public Integer getCreateSiteType() {
        return createSiteType;
    }

    public void setCreateSiteType(Integer createSiteType) {
        this.createSiteType = createSiteType;
    }

    public Long getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Long receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getReceiveSiteType() {
        return receiveSiteType;
    }

    public void setReceiveSiteType(Integer receiveSiteType) {
        this.receiveSiteType = receiveSiteType;
    }

    public String getCreateUserErp() {
        return createUserErp;
    }

    public void setCreateUserErp(String createUserErp) {
        this.createUserErp = createUserErp == null ? null : createUserErp.trim();
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName == null ? null : createUserName.trim();
    }

    public String getUpdateUserErp() {
        return updateUserErp;
    }

    public void setUpdateUserErp(String updateUserErp) {
        this.updateUserErp = updateUserErp == null ? null : updateUserErp.trim();
    }

    public String getUpdateUserName() {
        return updateUserName;
    }

    public void setUpdateUserName(String updateUserName) {
        this.updateUserName = updateUserName == null ? null : updateUserName.trim();
    }

    public Boolean getYn() {
        return yn;
    }

    public void setYn(Boolean yn) {
        this.yn = yn;
    }
}