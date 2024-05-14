package com.jd.bluedragon.distribution.station.entity;

import com.jdl.basic.api.domain.position.PositionDetailRecord;

import java.io.Serializable;

/**
 *  岗位网格工序&登陆签到未签退数
 */
public class PositionSignNumDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 岗位网格工序
     */
    private PositionDetailRecord positionDetailRecord;
    /**
     *登陆签到未签退数
     */
    private Integer num;

    public PositionDetailRecord getPositionDetailRecord() {
        return positionDetailRecord;
    }

    public void setPositionDetailRecord(PositionDetailRecord positionDetailRecord) {
        this.positionDetailRecord = positionDetailRecord;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }
}
