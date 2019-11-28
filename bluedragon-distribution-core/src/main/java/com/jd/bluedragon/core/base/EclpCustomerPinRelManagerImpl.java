package com.jd.bluedragon.core.base;

import com.jd.eclp.core.ApiResponse;
import com.jd.eclp.master.constant.ChannelEnum;
import com.jd.logistics.customer.center.domain.CustomerPinRel;
import com.jd.logistics.customer.customerPinRel.CustomerPinRelService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private CustomerPinRelService customerPinRelService;

    @Override
    public CustomerPinRel getCustomerPinRelByPin(String pin) {
        if (StringUtils.isBlank(pin)) {
            return null;
        }
        ApiResponse<CustomerPinRel> response = customerPinRelService.getCustomerNoByPinAndChannel(null, pin, ChannelEnum.BUSINESS_SYSTEM_BW.getCode());
        if (response.isSuccess()) {
            return response.getData();
        } else {
            logger.error(String.format("[ECLP开放平台-客户中心-客户跟pin关系接口]查询失败，状态码：%s，信息：%s", response.getCode(), response.getMsg()));
        }
        return null;
    }

}
