package com.jd.bluedragon.distribution.abnormalDispose.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Date;

/**
 * @author tangchunqing
 * @Description: 异常统计查询条件
 * @date 2018年06月13日 20时:01分
 */
public class AbnormalDisposeCondition  extends BasePagerCondition {
    private Date startTime;
    private Date endTime;
    private Integer areaId;
    private Integer provinceId;
    private Integer cityId;
    private String dmsSiteCode;
    private String waveBusinessId;
    private String isDispose;//是否提报异常

    public String getIsDispose() {
        return isDispose;
    }

    public void setIsDispose(String isDispose) {
        this.isDispose = isDispose;
    }

    public String getWaveBusinessId() {
        return waveBusinessId;
    }

    public void setWaveBusinessId(String waveBusinessId) {
        this.waveBusinessId = waveBusinessId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getAreaId() {
        return areaId;
    }

    public void setAreaId(Integer areaId) {
        this.areaId = areaId;
    }

    public Integer getProvinceId() {
        return provinceId;
    }

    public void setProvinceId(Integer provinceId) {
        this.provinceId = provinceId;
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getDmsSiteCode() {
        return dmsSiteCode;
    }

    public void setDmsSiteCode(String dmsSiteCode) {
        this.dmsSiteCode = dmsSiteCode;
    }
}
