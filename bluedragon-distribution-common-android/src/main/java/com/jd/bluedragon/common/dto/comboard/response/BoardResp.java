package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class BoardResp implements Serializable {
    private static final long serialVersionUID = -4292907209044485598L;
    /**
     * 小组下人员数量
     */
    private Integer groupUserCount;
    private Integer endSiteId;
    private String endSiteName;
    /**
     * 滑道编号
     */
    private String crossCode;
    /**
     * 笼车编号
     */
    private String tableTrolleyCode;
    /**
     * 该板已扫包裹数量
     */
    private Integer packageHaveScanCount;
    /**
     * 该板已扫箱子数量
     */
    private Integer boxHaveScanCount;
    /**
     * 大宗标识
     */
    private boolean bulkFlag;
    private List<GoodsCategoryDto> goodsCategoryList;

    private String boxCode;

    private String materialCode;

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getMaterialCode() {
        return materialCode;
    }

    public void setMaterialCode(String materialCode) {
        this.materialCode = materialCode;
    }

    public boolean isBulkFlag() {
        return bulkFlag;
    }

    public void setBulkFlag(boolean bulkFlag) {
        this.bulkFlag = bulkFlag;
    }
    
    public Integer getGroupUserCount() {
        return groupUserCount;
    }

    public void setGroupUserCount(Integer groupUserCount) {
        this.groupUserCount = groupUserCount;
    }

    public Integer getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Integer endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getCrossCode() {
        return crossCode;
    }

    public void setCrossCode(String crossCode) {
        this.crossCode = crossCode;
    }

    public String getTableTrolleyCode() {
        return tableTrolleyCode;
    }

    public void setTableTrolleyCode(String tableTrolleyCode) {
        this.tableTrolleyCode = tableTrolleyCode;
    }

    public Integer getPackageHaveScanCount() {
        return packageHaveScanCount;
    }

    public void setPackageHaveScanCount(Integer packageHaveScanCount) {
        this.packageHaveScanCount = packageHaveScanCount;
    }

    public Integer getBoxHaveScanCount() {
        return boxHaveScanCount;
    }

    public void setBoxHaveScanCount(Integer boxHaveScanCount) {
        this.boxHaveScanCount = boxHaveScanCount;
    }

    public List<GoodsCategoryDto> getGoodsCategoryList() {
        return goodsCategoryList;
    }

    public void setGoodsCategoryList(List<GoodsCategoryDto> goodsCategoryList) {
        this.goodsCategoryList = goodsCategoryList;
    }
}
