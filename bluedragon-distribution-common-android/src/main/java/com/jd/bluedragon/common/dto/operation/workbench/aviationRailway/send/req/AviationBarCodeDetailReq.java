package com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.send.req;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @author liwenji
 * @description 发货明细请求
 * @date 2023-08-21 10:37
 */
public class AviationBarCodeDetailReq implements Serializable {

    private static final long serialVersionUID = 4784612639942744950L;

    private User user;

    private CurrentOperate currentOperate;

    private Integer pageNumber;

    private Integer pageSize;

    /**
     * send_vehicle业务主键
     */
    private String bizId;

    /**
     * 包裹号箱号
     */
    private String barCode;

    /**
     * 已扫类型
     * com.jd.bluedragon.common.dto.operation.workbench.aviationRailway.enums.SendAbnormalTypeEnum
     */
    private Integer scanedType;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

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


    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getScanedType() {
        return scanedType;
    }

    public void setScanedType(Integer scanedType) {
        this.scanedType = scanedType;
    }
}
