package com.jd.bluedragon.common.dto.comboard.response;


import java.io.Serializable;
import java.util.List;

public class HaveScanStatisticsResp implements Serializable {
    private static final long serialVersionUID = 6211348983763778535L;
    List<BoxScanDto> boxScanDtoList;
    List<PackageScanDto> packageScanDtoList;

    public List<BoxScanDto> getBoxScanDtoList() {
        return boxScanDtoList;
    }

    public void setBoxScanDtoList(List<BoxScanDto> boxScanDtoList) {
        this.boxScanDtoList = boxScanDtoList;
    }

    public List<PackageScanDto> getPackageScanDtoList() {
        return packageScanDtoList;
    }

    public void setPackageScanDtoList(List<PackageScanDto> packageScanDtoList) {
        this.packageScanDtoList = packageScanDtoList;
    }
}
