package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdObject;

/**
 * @ClassName: ExchangePrintRequest
 * @Description: 换单打印请求对象
 * @author: hujiping
 * @date: 2019/7/23 15:19
 */
public class ExchangePrintRequest extends JdObject {

    private static final long serialVersionUID = 1L;

    /**
     * 换单前老单号
     * */
    private String oldWaybillCode;

    /**
     * 包裹数量
     * */
    private Integer packageNumber;

    public String getOldWaybillCode() {
        return oldWaybillCode;
    }

    public void setOldWaybillCode(String oldWaybillCode) {
        this.oldWaybillCode = oldWaybillCode;
    }

    public Integer getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(Integer packageNumber) {
        this.packageNumber = packageNumber;
    }
}
