package com.jd.bluedragon.distribution.api.response.material;

import java.io.Serializable;

/**
 * @ClassName MaterialSendDetailResponse
 * @Description
 * @Author wyh
 * @Date 2020/3/18 18:08
 **/
public class MaterialSendDetailResponse implements Serializable {

    private static final long serialVersionUID = -2300426862571404366L;

    /**
     * 物资编号
     */
    private String materialCode;

    /**
     * 物资类型；1：保温箱
     */
    private Byte materialType;

    /**
     * 物资名称
     */
    private String materialName;

    /**
     * 发货方式；1：按物资单个发货；2：按容器发货；3：按批次号发货
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
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人
     */
    private String operateUserName;

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public Byte getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Byte materialType) {
        this.materialType = materialType;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
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
        this.sendCode = sendCode;
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

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }
}
