package com.jd.bluedragon.distribution.material.domain;

import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.io.Serializable;
import java.util.Date;

public class DmsMaterialReceive extends DbEntity implements Serializable {

    private static final long serialVersionUID = -7563647877217619207L;

    /**
    * 物资编号
    */
    private String materialCode;

    /**
    * 物资类型；1：保温箱
    */
    private Byte materialType;

    /**
    * 收货方式；1：按物资单个收货；2：按容器收货
    */
    private Byte receiveType;

    /**
    * 收货方式编号
    */
    private String receiveCode;

    /**
    * 收货数量
    */
    private Integer receiveNum;

    /**
    * 操作机构ID
    */
    private Long createSiteCode;

    /**
    * 操作机构类型
    */
    private Integer createSiteType;

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

    public Byte getReceiveType() {
        return receiveType;
    }

    public void setReceiveType(Byte receiveType) {
        this.receiveType = receiveType;
    }

    public String getReceiveCode() {
        return receiveCode;
    }

    public void setReceiveCode(String receiveCode) {
        this.receiveCode = receiveCode == null ? null : receiveCode.trim();
    }

    public Integer getReceiveNum() {
        return receiveNum;
    }

    public void setReceiveNum(Integer receiveNum) {
        this.receiveNum = receiveNum;
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

    public DmsMaterialReceiveFlow convert2ReceiveFlow() {
        DmsMaterialReceiveFlow flow = new DmsMaterialReceiveFlow();
        flow.setCreateSiteCode(this.createSiteCode);
        flow.setCreateSiteType(this.createSiteType);
        flow.setCreateUserErp(this.createUserErp);
        flow.setCreateUserName(this.createUserName);
        flow.setMaterialCode(this.materialCode);
        flow.setMaterialType(this.materialType);
        flow.setReceiveCode(this.receiveCode);
        flow.setReceiveType(this.receiveType);
        flow.setReceiveNum(this.receiveNum);
        flow.setUpdateUserErp(this.updateUserErp);
        flow.setUpdateUserName(this.updateUserName);
        flow.setYn(this.yn);
        return flow;
    }

    public DmsMaterialRelation convert2Relation() {
        DmsMaterialRelation relation = new DmsMaterialRelation();
        relation.setMaterialCode(this.materialCode);
        relation.setMaterialType(this.materialType);
        relation.setReceiveCode(this.receiveCode);
        relation.setReceiveNum(this.receiveNum);
        relation.setCreateSiteCode(this.createSiteCode);
        relation.setCreateSiteType(this.createSiteType);
        relation.setUpdateSiteCode(this.createSiteCode);
        relation.setUpdateSiteType(this.createSiteType);
        relation.setCreateUserErp(this.createUserErp);
        relation.setCreateUserName(this.createUserName);
        relation.setUpdateUserErp(this.updateUserErp);
        relation.setUpdateUserName(this.updateUserName);
        relation.setYn(this.yn);
        return relation;
    }

}