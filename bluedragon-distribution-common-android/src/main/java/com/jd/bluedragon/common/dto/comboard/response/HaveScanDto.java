package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.operation.workbench.unload.response.LabelOption;
import java.io.Serializable;
import java.util.List;

public class HaveScanDto implements Serializable {
    private static final long serialVersionUID = 3605310597726635355L;
    private String barCode;
    /**
     * 类型 1箱号 2包裹号 3运单号
     */
    private Integer type;
    /**
     * 包含包裹数量
     */
    private String packageCount;

    /**
     * 包裹标签
     */
    private List<LabelOption> labelOptionList;

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getPackageCount() {
        return packageCount;
    }

    public void setPackageCount(String packageCount) {
        this.packageCount = packageCount;
    }

    public List<LabelOption> getLabelOptionList() {
        return labelOptionList;
    }

    public void setLabelOptionList(
        List<LabelOption> labelOptionList) {
        this.labelOptionList = labelOptionList;
    }
}
