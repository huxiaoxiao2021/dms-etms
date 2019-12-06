package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.eclp.core.ApiResponse;
import com.jd.eclp.master.constant.ChannelEnum;
import com.jd.logistics.customer.center.domain.CustomerPinRel;
import com.jd.logistics.customer.customerPinRel.CustomerPinRelService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author lixin39
 * @Description ECLP开放平台 - 客户中心 - 客户跟pin关系接口
 * @ClassName EclpCustomerPinRelManagerImpl
 * @date 2019/9/25
 */
@Service
public class EclpCustomerPinRelManagerImpl implements EclpCustomerPinRelManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomerPinRelService customerPinRelService;

    @JProfiler(jKey = "DMS.BASE.EclpCustomerPinRelManagerImpl.getCustomerPinRelByPin", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public CustomerPinRel getCustomerPinRelByPin(String pin) {
        if (StringUtils.isBlank(pin)) {
            return null;
        }
        ApiResponse<CustomerPinRel> response = customerPinRelService.getCustomerNoByPinAndChannel(null, pin, ChannelEnum.BUSINESS_SYSTEM_BW.getCode());
        if (response.isSuccess()) {
            return response.getData();
        } else {
            log.error("[ECLP开放平台-客户中心-客户跟pin关系接口]查询失败，状态码：{}，信息：{}", response.getCode(), response.getMsg());
        }
        return null;
    }

}
