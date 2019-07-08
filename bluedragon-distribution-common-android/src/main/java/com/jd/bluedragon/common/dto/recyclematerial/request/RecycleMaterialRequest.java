package com.jd.bluedragon.common.dto.recyclematerial.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;

import java.io.Serializable;

/**
 * @author : xumigen
 * @date : 2019/6/14
 */
public class RecycleMaterialRequest implements Serializable{
    private static final long serialVersionUID = -1L;

    private CurrentOperate currentOperate;
    /**
     * 物资编码
     */
    private String materialCode;

    /**
     * 实操类型 1130验货 1110收货 1300发货
     */
    private int operateType;

    /**
     * 业务类型:‘10’正向 "20' 逆向 '30' 三方
     */
    private int businessType;

    /**
     * 目的分拣中心编号
     */
    private Integer destSiteCode;

    /**
     * 目的分拣中心名称
     */
    private String destSiteName;

    /**
     * 操作人ERP
     */
    private String operatorErp;

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public int getOperateType() {
        return operateType;
    }

    public void setOperateType(int operateType) {
        this.operateType = operateType;
    }

    public int getBusinessType() {
        return businessType;
    }

    public void setBusinessType(int businessType) {
        this.businessType = businessType;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
    }

    public String getDestSiteName() {
        return destSiteName;
    }

    public void setDestSiteName(String destSiteName) {
        this.destSiteName = destSiteName;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }
}
