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
    private Integer siteCode;//站点编码 数字id
    private String siteName;//机构名称
    private String transferNo;//中转班次
    private Date transferStartTime;//班次开始时间
    private Date transferEndTime;//班次结束时间

    private Integer NotReceiveNum;//未收货数量
    private Integer NotSendNum;//未发货数量

    private Integer NotReceiveDisposeNum;//已处理未收货异常数
    private Integer NotSendDisposeNum;//已处理未发货异常数

    private Integer NotReceiveProcess;// 未收货异常处理进度
    private Integer NotSendProcess;//未发货异常处理进度

    private Integer totalProcess;//总进度


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

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
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
        return NotReceiveNum;
    }

    public void setNotReceiveNum(Integer notReceiveNum) {
        NotReceiveNum = notReceiveNum;
    }

    public Integer getNotSendNum() {
        return NotSendNum;
    }

    public void setNotSendNum(Integer notSendNum) {
        NotSendNum = notSendNum;
    }

    public Integer getNotReceiveDisposeNum() {
        return NotReceiveDisposeNum;
    }

    public void setNotReceiveDisposeNum(Integer notReceiveDisposeNum) {
        NotReceiveDisposeNum = notReceiveDisposeNum;
    }

    public Integer getNotSendDisposeNum() {
        return NotSendDisposeNum;
    }

    public void setNotSendDisposeNum(Integer notSendDisposeNum) {
        NotSendDisposeNum = notSendDisposeNum;
    }

    public Integer getNotReceiveProcess() {
        return NotReceiveProcess;
    }

    public void setNotReceiveProcess(Integer notReceiveProcess) {
        NotReceiveProcess = notReceiveProcess;
    }

    public Integer getNotSendProcess() {
        return NotSendProcess;
    }

    public void setNotSendProcess(Integer notSendProcess) {
        NotSendProcess = notSendProcess;
    }

    public Integer getTotalProcess() {
        return totalProcess;
    }

    public void setTotalProcess(Integer totalProcess) {
        this.totalProcess = totalProcess;
    }
}
