package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class QuerySendWaybillReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -4012316247637888642L;
    /**
     * 任务biz_id
     */
    private String sendVehicleBizId;
    /**
     * 异常标识：false 待扫(未发生的) ,true(已发生的：已扫  拦截 强发等等 -各种异常情况)
     */
    private Boolean expFlag;

    /**
     * 货物分类
     * com.jd.bluedragon.distribution.jy.enums.GoodsTypeEnum
     */
    private String goodsType;
    /**
     * com.jd.bluedragon.distribution.jy.enums.ExcepScanTypeEnum
     */
    private Integer expType;
    private Integer pageNo;
    private Integer pageSize;

    public Boolean getExpFlag() {
        return expFlag;
    }

    public void setExpFlag(Boolean expFlag) {
        this.expFlag = expFlag;
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

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
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


}
