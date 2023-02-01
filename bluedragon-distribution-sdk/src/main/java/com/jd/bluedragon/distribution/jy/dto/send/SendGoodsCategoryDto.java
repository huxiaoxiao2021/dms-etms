package com.jd.bluedragon.distribution.jy.dto.send;


import java.io.Serializable;

public class SendGoodsCategoryDto implements Serializable {

    private static final long serialVersionUID = -1367376394354226288L;
    private String type;
    private String name;
    private Integer shouldScanCount;
    private Integer haveScanCount;
    private Integer waitScanCount;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getShouldScanCount() {
        return shouldScanCount;
    }

    public void setShouldScanCount(Integer shouldScanCount) {
        this.shouldScanCount = shouldScanCount;
    }

    public Integer getHaveScanCount() {
        return haveScanCount;
    }

    public void setHaveScanCount(Integer haveScanCount) {
        this.haveScanCount = haveScanCount;
    }

    public Integer getWaitScanCount() {
        return waitScanCount;
    }

    public void setWaitScanCount(Integer waitScanCount) {
        this.waitScanCount = waitScanCount;
    }
}
