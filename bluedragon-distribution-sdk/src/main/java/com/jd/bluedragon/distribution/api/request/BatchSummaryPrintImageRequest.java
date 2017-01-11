package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;

/**
 * Created by wuzuxiang on 2017/1/4.
 */
public class BatchSummaryPrintImageRequest implements Serializable{

    private static final long serialVersionUID = -1776791802433960641L;

    /**
     * 始发地
     */
    private Integer createSiteNo;

    /**
     * 始发地名称
     */
    private String createSiteName;

    /**
     * 目的地
     */
    private Integer receiveSiteNo;

    /**
     * 目的地名称
     */
    private String receiveSiteName;

    /**
     * 打印时间
     */
    private String printTime;

    /**
     * 批次号
     */
    private String sendCode;

    /**
     * 发货时间
     */
    private String sendTime;

    /**
     * 周转箱号数
     */
    private Integer totalBoxNum;

    /**
     * 原包个数
     */
    private Integer totalPackageBarNum;

    /**
     * 合计
     */
    private Integer totalNum;

    /**
     * 应发包裹数量
     */
    private Integer packageBarRecNum;

    /**
     * 实发包裹数量
     */
    private Integer packageBarNum;

    /**
     * 体积
     */
    private Double volume;

    /**
     * 横向dpi,纸张打印精细度
     */
    private Integer xdpi = 200;

    /**
     * 纵向dpi,纸张打印精细度
     */
    private Integer ydpi = 200;

    public Integer getCreateSiteNo() {
        return createSiteNo;
    }

    public void setCreateSiteNo(Integer createSiteNo) {
        this.createSiteNo = createSiteNo;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public Integer getReceiveSiteNo() {
        return receiveSiteNo;
    }

    public void setReceiveSiteNo(Integer receiveSiteNo) {
        this.receiveSiteNo = receiveSiteNo;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getPrintTime() {
        return printTime;
    }

    public void setPrintTime(String printTime) {
        this.printTime = printTime;
    }

    public String getSendCode() {
        return sendCode;
    }

    public void setSendCode(String sendCode) {
        this.sendCode = sendCode;
    }

    public String getSendTime() {
        return sendTime;
    }

    public void setSendTime(String sendTime) {
        this.sendTime = sendTime;
    }

    public Integer getTotalBoxNum() {
        return totalBoxNum;
    }

    public void setTotalBoxNum(Integer totalBoxNum) {
        this.totalBoxNum = totalBoxNum;
    }

    public Integer getTotalPackageBarNum() {
        return totalPackageBarNum;
    }

    public void setTotalPackageBarNum(Integer totalPackageBarNum) {
        this.totalPackageBarNum = totalPackageBarNum;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getPackageBarRecNum() {
        return packageBarRecNum;
    }

    public void setPackageBarRecNum(Integer packageBarRecNum) {
        this.packageBarRecNum = packageBarRecNum;
    }

    public Integer getPackageBarNum() {
        return packageBarNum;
    }

    public void setPackageBarNum(Integer packageBarNum) {
        this.packageBarNum = packageBarNum;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getXdpi() {
        return xdpi;
    }

    public void setXdpi(Integer xdpi) {
        this.xdpi = xdpi;
    }

    public Integer getYdpi() {
        return ydpi;
    }

    public void setYdpi(Integer ydpi) {
        this.ydpi = ydpi;
    }

    @Override
    public String toString(){
        return "BatchSendPrintImageRequest-->sendCode:" + this.sendCode + ",createSiteName:" + this.createSiteName + ",receiveSiteName:"
                + this.receiveSiteName + ",printTime:" + this.printTime + ",sendTime:" + this.sendTime + ",totalBoxNum:" + this.totalBoxNum
                + ",totalPackageBarNum:" + this.totalPackageBarNum + ",totalNum:" + this.totalNum + ",packageBarRecNum:" + this.packageBarRecNum
                + ",packageBarNum:" + this.packageBarNum + ",volume:" + this.volume;
    }
}
