package com.jd.bluedragon.distribution.ver.domain;


import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;

/**
 * @Description 更新waybillCancel的dto
 * @author jinjingcheng
 * @date 2019/4/27.
 */
public class CancelWaybillDto extends CancelWaybill {

    private Integer[] featureTypes;
    /*是否是解锁动作比如 打印*/

    public Integer[] getFeatureTypes() {
        return featureTypes;
    }

    public void setFeatureTypes(Integer[] featureTypes) {
        this.featureTypes = featureTypes;
    }
}
