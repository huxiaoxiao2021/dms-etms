package com.jd.bluedragon.distribution.abnormalDispose.domain;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 异常统计类
 * @date 2018年06月13日 16时:31分
 */
public class AbnormalDisposeMain {
    private String waveBusinessId;//班次业务主键：日期、班次编码计算出的哈希值
    private Date dateTime;//日期
    private Integer areaId;//区域id
    private String areaName;//区域名称
    private String siteCode;//站点编码 7位编码
    private String siteName;//机构名称
    private String transferNo;//中转班次
    private Date transferStartTime;//班次开始时间
    private Date transferEndTime;//班次结束时间

    private Integer notReceiveNum;//未收货数量
    private Integer notSendNum;//未发货数量

    private Integer notReceiveDisposeNum;//已处理未收货异常数
    private Integer notSendDisposeNum;//已处理未发货异常数

    private String notReceiveProgress;// 未收货异常处理进度
    private String notSendProgress;//未发货异常处理进度

    private String totalProgress;//总进度


    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(String siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getTransferNo() {
        return transferNo;
    }

    public void setTransferNo(String transferNo) {
        this.transferNo = transferNo;
    }

    public Date getTransferStartTime() {
        return transferStartTime;
    }

    public void setTransferStartTime(Date transferStartTime) {
        this.transferStartTime = transferStartTime;
    }

    public Date getTransferEndTime() {
        return transferEndTime;
    }

    public void setTransferEndTime(Date transferEndTime) {
        this.transferEndTime = transferEndTime;
    }

    public Integer getNotReceiveNum() {
        return notReceiveNum;
    }

    public void setNotReceiveNum(Integer notReceiveNum) {
        this.notReceiveNum = notReceiveNum;
    }

    public Integer getNotSendNum() {
        return notSendNum;
    }

    public void setNotSendNum(Integer notSendNum) {
        this.notSendNum = notSendNum;
    }

    public Integer getNotReceiveDisposeNum() {
        return notReceiveDisposeNum;
    }

    public void setNotReceiveDisposeNum(Integer notReceiveDisposeNum) {
        this.notReceiveDisposeNum = notReceiveDisposeNum;
    }

    public Integer getNotSendDisposeNum() {
        return notSendDisposeNum;
    }

    public void setNotSendDisposeNum(Integer notSendDisposeNum) {
        this.notSendDisposeNum = notSendDisposeNum;
    }

    public String getNotReceiveProgress() {
        return notReceiveProgress;
    }

    public void setNotReceiveProgress(String notReceiveProgress) {
        this.notReceiveProgress = notReceiveProgress;
    }

    public String getNotSendProgress() {
        return notSendProgress;
    }

    public void setNotSendProgress(String notSendProgress) {
        this.notSendProgress = notSendProgress;
    }

    public String getTotalProgress() {
        return totalProgress;
    }

    public void setTotalProgress(String totalProgress) {
        this.totalProgress = totalProgress;
    }
}
