package com.jd.bluedragon.distribution.open.entity;

import java.util.List;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.open.entity
 * @ClassName: BatchSortingPageRequest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2022/12/2 15:01
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class BatchSortingPageRequest extends BatchPageRequest{

    /**
     * 货物明细信息
     */
    private List<CargoOperateInfo> cargoNoList;

    /**
     * 操作类型
     */
    private String operateType;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 箱号路由
     */
    private String boxRouteCode;

    /**
     * 箱号路由名称
     */
    private String boxRouteName;

    /**
     * 操作站点编码
     */
    private String operateSiteCode;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    public List<CargoOperateInfo> getCargoNoList() {
        return cargoNoList;
    }

    public void setCargoNoList(List<CargoOperateInfo> cargoNoList) {
        this.cargoNoList = cargoNoList;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getBoxRouteCode() {
        return boxRouteCode;
    }

    public void setBoxRouteCode(String boxRouteCode) {
        this.boxRouteCode = boxRouteCode;
    }

    public String getBoxRouteName() {
        return boxRouteName;
    }

    public void setBoxRouteName(String boxRouteName) {
        this.boxRouteName = boxRouteName;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }
}
