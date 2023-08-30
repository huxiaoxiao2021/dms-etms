package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 异常包裹查询请求
 * @date 2023-08-18 14:47
 */
public class AviationSendAbnormalPackReq  extends BaseReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * send_vehicle业务主键
     */
    private String bizId;

    /**
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendAbnormalTypeEnum
     */
    private Integer queryBarCodeFlag;

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getQueryBarCodeFlag() {
        return queryBarCodeFlag;
    }

    public void setQueryBarCodeFlag(Integer queryBarCodeFlag) {
        this.queryBarCodeFlag = queryBarCodeFlag;
    }
}
