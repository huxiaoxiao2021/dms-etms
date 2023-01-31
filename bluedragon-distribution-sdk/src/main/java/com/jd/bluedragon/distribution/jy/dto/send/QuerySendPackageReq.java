package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class QuerySendPackageReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = 1761459109148412628L;
    /**
     * 任务biz_id
     */
    private String sendVehicleBizId;
    private String waybillCode;
    /**
     * 货物分类
     * com.jd.bluedragon.distribution.jy.enums.GoodsTypeEnum
     */
    private String goodsType;
    /**
     *com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum
     */
    private Integer expType;
    private Integer pageNo;
    private Integer pageSize;


    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getGoodsType() {
        return goodsType;
    }

    public void setGoodsType(String goodsType) {
        this.goodsType = goodsType;
    }

    public Integer getExpType() {
        return expType;
    }

    public void setExpType(Integer expType) {
        this.expType = expType;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }
}
