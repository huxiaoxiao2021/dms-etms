package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;

import java.io.Serializable;
import java.util.List;

public class PackageScanDto implements Serializable {
    private static final long serialVersionUID = -6303539269882493784L;
    private String packageCode;
    private List<LabelOption> labelOptionList;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public List<LabelOption> getLabelOptionList() {
        return labelOptionList;
    }

    public void setLabelOptionList(List<LabelOption> labelOptionList) {
        this.labelOptionList = labelOptionList;
    }
}
