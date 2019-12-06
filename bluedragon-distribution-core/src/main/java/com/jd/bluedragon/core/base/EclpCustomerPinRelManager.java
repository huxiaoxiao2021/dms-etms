package com.jd.bluedragon.core.base;

import com.jd.logistics.customer.center.domain.CustomerPinRel;

/**
 * @author lixin39
 * @Description ECLP开放平台 - 客户中心 - 客户跟pin关系接口
 * @ClassName EclpCustomerPinRelManager
 * @date 2019/9/25
 */
public interface EclpCustomerPinRelManager {

    /**
     * 根据pin码查询客户BW编码
     *
     * @param pin
     * @return
     */
    CustomerPinRel getCustomerPinRelByPin(String pin);

}
