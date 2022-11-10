package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.comboard.request.ExcepScanDto;

import java.io.Serializable;
import java.util.List;

public class BoardExcepStatisticsResp implements Serializable {
    private static final long serialVersionUID = -5234283242615422754L;
    private List<ExcepScanDto> excepScanDtoList;
    private List<PackageScanDto> packageCodeList;

    public List<ExcepScanDto> getExcepScanDtoList() {
        return excepScanDtoList;
    }

    public void setExcepScanDtoList(List<ExcepScanDto> excepScanDtoList) {
        this.excepScanDtoList = excepScanDtoList;
    }

    public List<PackageScanDto> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<PackageScanDto> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }
}
