package com.jd.bluedragon.common.dto.queueManagement.response;

import java.io.Serializable;
import java.util.List;

public class PdaPlatformInfoDto implements Serializable {
    private static final long serialVersionUID = -1L;

    private String platformCode;
    private String platformName;
    private Integer loadType;
    private List<FLowInfoDto> flowInfoList;
    private List<CarTypeInfoDto> carTypeInfoList;

    public String getPlatformCode() {
        return platformCode;
    }

    public void setPlatformCode(String platformCode) {
        this.platformCode = platformCode;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public Integer getLoadType() {
        return loadType;
    }

    public void setLoadType(Integer loadType) {
        this.loadType = loadType;
    }

    public List<FLowInfoDto> getFlowInfoList() {
        return flowInfoList;
    }

    public void setFlowInfoList(List<FLowInfoDto> flowInfoList) {
        this.flowInfoList = flowInfoList;
    }

    public List<CarTypeInfoDto> getCarTypeInfoList() {
        return carTypeInfoList;
    }

    public void setCarTypeInfoList(List<CarTypeInfoDto> carTypeInfoList) {
        this.carTypeInfoList = carTypeInfoList;
    }
}
