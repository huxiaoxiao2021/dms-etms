package com.jd.bluedragon.distribution.jy.evaluate;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 装车评价申诉更新对象
 * @date 2024/3/7
 */
public class JyEvaluateRecordAppealUpdateDto extends JyEvaluateRecordAppealEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * id集合
     */
    private List<Long> idList;
    /**
     * 不满意code集合
     */
    private List<Integer> dimensionCodeList;

    public List<Long> getIdList() {
        return idList;
    }

    public void setIdList(List<Long> idList) {
        this.idList = idList;
    }

    public List<Integer> getDimensionCodeList() {
        return dimensionCodeList;
    }

    public void setDimensionCodeList(List<Integer> dimensionCodeList) {
        this.dimensionCodeList = dimensionCodeList;
    }
}
