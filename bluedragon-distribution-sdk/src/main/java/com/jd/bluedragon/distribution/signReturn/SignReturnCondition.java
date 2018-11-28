package com.jd.bluedragon.distribution.signReturn;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * @ClassName: SignReturnCondition
 * @Description: 签单返回合单打印查询条件
 * @author: hujiping
 * @date: 2018/11/23 16:39
 */
public class SignReturnCondition extends BasePagerCondition {

    /**
     * 合单运单号/包裹号
     * */
    private String waybillCode;
    /**
     * 合单内运单号/包裹号
     * */
    private String waybillCodeInMerged;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getWaybillCodeInMerged() {
        return waybillCodeInMerged;
    }

    public void setWaybillCodeInMerged(String waybillCodeInMerged) {
        this.waybillCodeInMerged = waybillCodeInMerged;
    }
}
