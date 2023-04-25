package com.jd.bluedragon.distribution.recycle.material.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 周转筐作废请求体
 *
 * @author hujiping
 * @date 2023/4/12 6:15 PM
 */
public class RecycleBasketAbolishRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 批次标识
     */
    private String batchFlag;

    /**
     * 周转筐集合
     */
    private List<String> recycleBasketList;

    /**
     * 操作人ERP
     */
    private String operateUserErp;

    /**
     * 操作人场地ID
     */
    private Integer operateSiteCode;

    public String getBatchFlag() {
        return batchFlag;
    }

    public void setBatchFlag(String batchFlag) {
        this.batchFlag = batchFlag;
    }

    public List<String> getRecycleBasketList() {
        return recycleBasketList;
    }

    public void setRecycleBasketList(List<String> recycleBasketList) {
        this.recycleBasketList = recycleBasketList;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }
}
