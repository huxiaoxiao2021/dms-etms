package com.jd.bluedragon.core.base;

import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;

/**
 * @author lijie
 * @date 2019/11/17 16:15
 */
public interface QuoteCustomerApiServiceManager {

    //根据青龙商家id查询陆运泡重比系数
    Integer queryVolumeRateByCustomerId(Integer customerId);

    //根据青龙商家id查询商家信息
    QuoteCustomerDto queryCustomerById(Integer customerId);
}
