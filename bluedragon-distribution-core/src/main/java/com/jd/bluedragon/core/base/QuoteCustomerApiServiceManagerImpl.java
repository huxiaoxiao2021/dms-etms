package com.jd.bluedragon.core.base;

import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;
import com.jd.bluedragon.dms.receive.quote.dto.ResponseDTO;
import com.jd.etms.quote.api.QuoteCustomerApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * @author lijie
 * @date 2019/11/17 16:18
 */
@Service("quoteCustomerApiServiceManager")
public class QuoteCustomerApiServiceManagerImpl implements QuoteCustomerApiServiceManager{

    private static final Logger logger = LoggerFactory.getLogger(QuoteCustomerApiServiceManagerImpl.class);

    @Autowired
    private QuoteCustomerApi quoteCustomerApiService;

    @Override
    public Integer queryVolumeRateByCustomerId(Integer customerId) {
        ResponseDTO<QuoteCustomerDto> response = quoteCustomerApiService.queryCustomerById(customerId);
        if(response !=null && response.getData() != null){
            return response.getData().getVolumeRate();
        }
        logger.error("根据商家ID：{}",customerId + "获取重泡比失败！");
        return null;
    }
}
