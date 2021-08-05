package com.jd.bluedragon.distribution.stock.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 库存盘点查询条件
 *
 * @author hujiping
 * @date 2021/6/4 2:12 下午
 */
public class InventoryQuery implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 包裹号集合
     */
    private List<String> packList = new ArrayList<String>();

    /**
     * 波次编码
     */
    private String waveCode;

    /**
     * 波次开始时间
     */
    private Date waveBeginTime;

    /**
     * 波次结束时间
     */
    private Date waveEndTime;

    /**
     * 操作人分拣中心ID
     */
    private Integer operateSiteCode;

    public List<String> getPackList() {
        return packList;
    }

    public void setPackList(List<String> packList) {
        this.packList = packList;
    }

    public String getWaveCode() {
        return waveCode;
    }

    public void setWaveCode(String waveCode) {
        this.waveCode = waveCode;
    }

    public Date getWaveBeginTime() {
        return waveBeginTime;
    }

    public void setWaveBeginTime(Date waveBeginTime) {
        this.waveBeginTime = waveBeginTime;
    }

    public Date getWaveEndTime() {
        return waveEndTime;
    }

    public void setWaveEndTime(Date waveEndTime) {
        this.waveEndTime = waveEndTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }
}
