package com.jd.bluedragon.distribution.transport.domain;

import java.math.BigDecimal;

/**
 * <p>
 * Created by lixin39 on 2018/1/3.
 */
public class ArPdaSendRegister {

    private Integer taskType;

    /**
     * 航空单号
     */
    private String airNo;

    /**
     * 运力名称
     */
    private String transName;

    /**
     * 铁路站序
     */
    private String railwayNo;

    /**
     * 发货件数
     */
    private Integer num;

    /**
     * 重量
     */
    private BigDecimal Weight;

    /**
     * 备注
     */
    private String demo;

    /**
     * 车型
     */
    private Integer OperateType;

    /**
     * 发批次
     */
    private String BatchNo;

    public String getAirNo() {
        return airNo;
    }

    public void setAirNo(String airNo) {
        this.airNo = airNo;
    }

    public String getTransName() {
        return transName;
    }

    public void setTransName(String transName) {
        this.transName = transName;
    }

    public String getRailwayNo() {
        return railwayNo;
    }

    public void setRailwayNo(String railwayNo) {
        this.railwayNo = railwayNo;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public BigDecimal getWeight() {
        return Weight;
    }

    public void setWeight(BigDecimal weight) {
        Weight = weight;
    }

    public String getDemo() {
        return demo;
    }

    public void setDemo(String demo) {
        this.demo = demo;
    }

    public Integer getOperateType() {
        return OperateType;
    }

    public void setOperateType(Integer operateType) {
        OperateType = operateType;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }
}
