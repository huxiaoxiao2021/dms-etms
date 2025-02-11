package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.dms.receive.quote.dto.QuoteCustomerDto;
import com.jd.bluedragon.dms.receive.quote.dto.ResponseDTO;
import com.jd.etms.quote.api.QuoteCustomerApi;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
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
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.quoteCustomerApiServiceManager.queryVolumeRateByCustomerId",mState={JProEnum.TP,JProEnum.FunctionError})
    public Integer queryVolumeRateByCustomerId(Integer customerId) {
        try {
            ResponseDTO<QuoteCustomerDto> response = quoteCustomerApiService.queryCustomerById(customerId);
            if(ResponseDTO.SUCCESS_CODE.equals(response.getStatusCode()) && response.getData() != null){
                return response.getData().getVolumeRate();
            }
            logger.warn("根据商家ID：{},获取重泡比失败,使用默认的泡重比8000",customerId);
        }catch(Exception e){
            logger.error("根据商家ID：{},获取重泡比失败,使用默认的泡重比8000",customerId);
        }
        return null;
    }

    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.quoteCustomerApiServiceManager.queryCustomerById",mState={JProEnum.TP,JProEnum.FunctionError})
    public QuoteCustomerDto queryCustomerById(Integer customerId) {

        try{
            ResponseDTO<QuoteCustomerDto> response = quoteCustomerApiService.queryCustomerById(customerId);
            if(ResponseDTO.SUCCESS_CODE.equals(response.getStatusCode()) && response.getData() != null){
                return  response.getData();
            }
            logger.warn("根据商家ID：{}获取商家信息失败，失败原因{}",customerId,response.getStatusMessage());
        }catch (Exception e){
            logger.error("根据商家ID：{}获取商家信息失败,获取一个异常",customerId,e);
        }
        return null;
    }
}
