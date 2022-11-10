package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class PackageDetailResp implements Serializable {
    private static final long serialVersionUID = -9151315421097125474L;
    private List<PackageScanDto> packageCodeList;
    private Integer totalPage;

    public List<PackageScanDto> getPackageCodeList() {
        return packageCodeList;
    }

    public void setPackageCodeList(List<PackageScanDto> packageCodeList) {
        this.packageCodeList = packageCodeList;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
