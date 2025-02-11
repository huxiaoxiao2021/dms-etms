package com.jd.bluedragon.distribution.material.domain;

import com.jd.bluedragon.distribution.api.response.material.MaterialSendDetailResponse;
import com.jd.ql.dms.common.web.mvc.api.DbEntity;

import java.io.Serializable;

public class DmsMaterialSend extends DbEntity implements Serializable {

    private static final long serialVersionUID = -1777325692406509781L;

    /**
    * 物资编号
    */
    private String materialCode;

    /**
    * 物资类型(1：保温箱；2：封签号)
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
     * 创建人用户ID
     */
    private Integer createUserId;

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

    public DmsMaterialSendFlow convert2SendFlow() {
        DmsMaterialSendFlow flow = new DmsMaterialSendFlow();
        flow.setCreateSiteCode(this.createSiteCode);
        flow.setCreateSiteType(this.createSiteType);
        flow.setCreateUserErp(this.createUserErp);
        flow.setCreateUserName(this.createUserName);
        flow.setMaterialCode(this.materialCode);
        flow.setMaterialType(this.materialType);
        flow.setReceiveSiteCode(this.receiveSiteCode);
        flow.setReceiveSiteType(this.receiveSiteType);
        flow.setSendCode(this.sendCode);
        flow.setSendType(this.sendType);
        flow.setSendNum(this.sendNum);
        flow.setUpdateUserErp(this.updateUserErp);
        flow.setUpdateUserName(this.updateUserName);
        flow.setYn(this.yn);
        return flow;
    }

    public DmsMaterialRelation convert2Relation() {
        DmsMaterialRelation relation = new DmsMaterialRelation();
        relation.setMaterialCode(this.materialCode);
        relation.setMaterialType(this.materialType);
        relation.setReceiveCode(this.sendCode);
        relation.setReceiveNum(this.sendNum);
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

    public MaterialSendDetailResponse convert2SendResponse() {
        MaterialSendDetailResponse detailResponse = new MaterialSendDetailResponse();
        detailResponse.setMaterialCode(this.materialCode);
        detailResponse.setMaterialType(this.materialType);
        detailResponse.setSendType(this.sendType);
        detailResponse.setSendCode(this.sendCode);
        detailResponse.setSendNum(this.sendNum);
        detailResponse.setCreateSiteCode(this.createSiteCode);
        detailResponse.setCreateSiteType(this.createSiteType);
        detailResponse.setReceiveSiteCode(this.receiveSiteCode);
        detailResponse.setReceiveSiteType(this.receiveSiteType);
        detailResponse.setOperateUserErp(this.updateUserErp);
        detailResponse.setOperateUserName(this.updateUserName);
        return detailResponse;
    }

}