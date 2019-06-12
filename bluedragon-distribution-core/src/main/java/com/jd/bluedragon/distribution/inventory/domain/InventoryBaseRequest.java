package com.jd.bluedragon.distribution.inventory.domain;

import java.io.Serializable;
import java.util.List;

public class InventoryBaseRequest implements Serializable {

    /*
    * 盘点任务编号
    * */
    private String inventoryTaskId;

    /*
     * 始发编号
     * */
    private Integer createSiteCode;

    /*
     * 始发名称
     * */
    private String createSiteName;

    /*
     * 流向编号列表
     * */
    private List<Integer> directionCodeList;

    /*
     * 类型标记
     * 值为1：查询信息时表示【初始化】，查询明细时标识【未盘】
     * 值为2：查询信息时表示【刷新数据】，查询明细时标识【已盘】
     * */
    private Integer type;

    /*
     * 盘点范围
     * 1：自定义 2：全场 3：异常区
     * */
    private Integer inventoryScope;

    /*
     * 运单号或包裹号
     * */
    private String barCode;

    /*
     * 操作人编号
     * */
    private Integer operateUserCode;

    /*
     * 操作人姓名
     * */
    private String operateUserName;

    /*
     * 操作人erp
     * */
    private String operateUserErp;

    public String getInventoryTaskId() {
        return inventoryTaskId;
    }

    public void setInventoryTaskId(String inventoryTaskId) {
        this.inventoryTaskId = inventoryTaskId;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }

    public String getCreateSiteName() {
        return createSiteName;
    }

    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }

    public List<Integer> getDirectionCodeList() {
        return directionCodeList;
    }

    public void setDirectionCodeList(List<Integer> directionCodeList) {
        this.directionCodeList = directionCodeList;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getInventoryScope() {
        return inventoryScope;
    }

    public void setInventoryScope(Integer inventoryScope) {
        this.inventoryScope = inventoryScope;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }
}
