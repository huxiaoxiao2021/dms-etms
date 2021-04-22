package com.jd.bluedragon.common.dto.unloadCar;

import java.io.Serializable;
import java.util.List;

/**
 * 卸车任务扫描结果
 * @author lvyuan21
 * @date 2020-12-24 15:34
 */
public class UnloadScanDetailDto implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 总单数
     */
    private Integer totalWaybillNum;

    /**
     * 总件数
     */
    private Integer totalPackageNum;

    /**
     * 已卸单数
     */
    private Integer loadWaybillAmount;

    /**
     * 已卸件数
     */
    private Integer loadPackageAmount;

    /**
     * 未卸单数
     */
    private Integer unloadWaybillAmount;

    /**
     * 未卸件数
     */
    private Integer unloadPackageAmount;

    /**
     * 包裹号转运单 权限标识
     */
    private Integer waybillAuthority;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 下一流向名称
     */
    private String receiveSiteName;

    /**
     * 下一流向编码
     */
    private Integer receiveSiteCode;

    /**
     * 未卸件数
     */
    private List<UnloadScanDto> unloadScanDtoList;

    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 板箱绑定数量
     */
    private Integer boardBoxCount;
    /**
     * 板单绑定数量
     */
    private Integer boardWaybillCount;


    public Integer getTotalWaybillNum() {
        return totalWaybillNum;
    }

    public void setTotalWaybillNum(Integer totalWaybillNum) {
        this.totalWaybillNum = totalWaybillNum;
    }

    public Integer getTotalPackageNum() {
        return totalPackageNum;
    }

    public void setTotalPackageNum(Integer totalPackageNum) {
        this.totalPackageNum = totalPackageNum;
    }

    public Integer getLoadWaybillAmount() {
        return loadWaybillAmount;
    }

    public void setLoadWaybillAmount(Integer loadWaybillAmount) {
        this.loadWaybillAmount = loadWaybillAmount;
    }

    public Integer getLoadPackageAmount() {
        return loadPackageAmount;
    }

    public void setLoadPackageAmount(Integer loadPackageAmount) {
        this.loadPackageAmount = loadPackageAmount;
    }

    public Integer getUnloadWaybillAmount() {
        return unloadWaybillAmount;
    }

    public void setUnloadWaybillAmount(Integer unloadWaybillAmount) {
        this.unloadWaybillAmount = unloadWaybillAmount;
    }

    public Integer getUnloadPackageAmount() {
        return unloadPackageAmount;
    }

    public void setUnloadPackageAmount(Integer unloadPackageAmount) {
        this.unloadPackageAmount = unloadPackageAmount;
    }

    public Integer getWaybillAuthority() {
        return waybillAuthority;
    }

    public void setWaybillAuthority(Integer waybillAuthority) {
        this.waybillAuthority = waybillAuthority;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public List<UnloadScanDto> getUnloadScanDtoList() {
        return unloadScanDtoList;
    }

    public void setUnloadScanDtoList(List<UnloadScanDto> unloadScanDtoList) {
        this.unloadScanDtoList = unloadScanDtoList;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public Integer getBoardBoxCount() {
        return boardBoxCount;
    }

    public void setBoardBoxCount(Integer boardBoxCount) {
        this.boardBoxCount = boardBoxCount;
    }

    public Integer getBoardWaybillCount() {
        return boardWaybillCount;
    }

    public void setBoardWaybillCount(Integer boardWaybillCount) {
        this.boardWaybillCount = boardWaybillCount;
    }
}
