package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;

/**
 * 卸车扫描请求对象
 *
 * @author: hujiping
 * @date: 2020/6/23 15:25
 */
public class UnloadCarScanRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     * */
    private String sealCarCode;
    /**
     * 板号
     * */
    private String boardCode;
    /**
     * 单号
     * */
    private String barCode;
    /**
     * 目的编码
     * */
    private Integer receiveSiteCode;
    /**
     * 目的名称
     * */
    private String receiveSiteName;
    /**
     * 操作人编码
     * */
    private Integer operateUserCode;
    /**
     * 操作人ERP
     * */
    private String operateUserErp;
    /**
     * 操作人名称
     * */
    private String operateUserName;
    /**
     * 操作人所属站点
     * */
    private Integer operateSiteCode;
    /**
     * 操作人所属站点名称
     * */
    private String operateSiteName;
    /**
     * 操作时间 13位时间戳
     * */
    private Long operateTime;
    /**
     * 操作类型
     *  0：组板 1：取消组板
     * */
    private Integer businessType;
    /**
     * 是否强制组板
     * **/
    private boolean isForceCombination = false;

    /**
     * 1:组板转移标识
     */
    private Integer isCombinationTransfer;

    /**
     * 包裹号转运单号标识
     */
    private Integer transfer;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 卸车模式: 1-人工, 0-流水线
     */
    private Integer type;

    /**
     * 组板来源 1：pda 2:分拣机
     */
    private Integer bizSource;


    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
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

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public boolean getIsForceCombination() {
        return isForceCombination;
    }

    public void setIsForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }

    public Integer getIsCombinationTransfer() {
        return isCombinationTransfer;
    }

    public void setIsCombinationTransfer(Integer isCombinationTransfer) {
        this.isCombinationTransfer = isCombinationTransfer;
    }

    public Integer getTransfer() {
        return transfer;
    }

    public void setTransfer(Integer transfer) {
        this.transfer = transfer;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public boolean isForceCombination() {
        return isForceCombination;
    }

    public void setForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public void setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
    }
}
