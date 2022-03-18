package com.jd.bluedragon.distribution.ministore.dto;

public class DeviceDto {
    private String miniStoreCode;//微仓码
    private String iceBoardCode;//冰板码
    private String boxCode;//集包码
    private Byte state;//状态
    private Boolean occupiedFlag;//占用标识

    public String getMiniStoreCode() {
        return miniStoreCode;
    }

    public void setMiniStoreCode(String miniStoreCode) {
        this.miniStoreCode = miniStoreCode;
    }

    public String getIceBoardCode() {
        return iceBoardCode;
    }

    public void setIceBoardCode(String iceBoardCode) {
        this.iceBoardCode = iceBoardCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }
}
