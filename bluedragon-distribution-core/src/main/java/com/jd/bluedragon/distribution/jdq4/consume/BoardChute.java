package com.jd.bluedragon.distribution.jdq4.consume;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 分拣机格口绑定板信息
 * @date 2021-11-30
 */
public class BoardChute implements Serializable {
    /**
     * 主键
     */
    private Long id;

    /**
     * 版号
     */
    private String boardCode;

    /**
     * 组板分拣中心id
     */
    private Integer createSiteCode;

    /**
     * 组板分拣中心名称
     */
    private String createSiteName;

    /**
     * 目的分拣中心id
     */
    private Integer receiveSiteCode;

    /**
     * 目的分拣中心名称
     */
    private String receiveSiteName;

    /**
     * 分拣机编码
     */
    private String machineCode;

    /**
     * 格口号
     */
    private String chuteCode;

    /**
     * 板内包裹或箱总数
     */
    private Integer packageCount;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 1 现场扫最后一件 2 板号已发货
     */
    private Short endType;

    /**
     * 创建人erp
     */
    private String createErp;

    /**
     * 结束人erp
     */
    private String endErp;

    /**
     * 最后一件条码
     */
    private String lastBarcode;

    /**
     * 最后一件条码
     */
    private String firstBarcode;

    /**
     * 0无效,有效
     */
    private Byte status;

    private String sendCode;

    /**
     * 时间戳
     */
    private Date ts;
    /**
     * 批量更新条件
     */
    private List<Long> ids;

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    private static final long serialVersionUID = 1L;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getChuteCode() {
        return chuteCode;
    }

    public void setChuteCode(String chuteCode) {
        this.chuteCode = chuteCode;
    }

    public Integer getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(Integer packageCount) {
        this.packageCount = packageCount;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Short getEndType() {
        return endType;
    }

    public void setEndType(Short endType) {
        this.endType = endType;
    }

    public String getCreateErp() {
        return createErp;
    }

    public void setCreateErp(String createErp) {
        this.createErp = createErp;
    }

    public String getEndErp() {
        return endErp;
    }

    public void setEndErp(String endErp) {
        this.endErp = endErp;
    }

    public String getLastBarcode() {
        return lastBarcode;
    }

    public void setLastBarcode(String lastBarcode) {
        this.lastBarcode = lastBarcode;
    }

    public String getFirstBarcode() {
        return firstBarcode;
    }

    public void setFirstBarcode(String firstBarcode) {
        this.firstBarcode = firstBarcode;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public Date getTs() {
        return ts;
    }

    public void setTs(Date ts) {
        this.ts = ts;
    }

    public List<Long> getIds() {
        return ids;
    }

    public void setIds(List<Long> ids) {
        this.ids = ids;
    }
}