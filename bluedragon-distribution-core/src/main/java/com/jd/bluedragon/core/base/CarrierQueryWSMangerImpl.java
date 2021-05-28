package com.jd.bluedragon.core.base;

import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.tms.basic.dto.CommonDto;
import com.jd.tms.basic.dto.TransportResourceDto;
import com.jd.tms.basic.ws.CarrierQueryWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/9 14:05
 */
@Service("carrierQueryWSManger")
public class CarrierQueryWSMangerImpl implements CarrierQueryWSManager{
    private static final Logger logger = LoggerFactory.getLogger(CarrierQueryWSMangerImpl.class);

    @Autowired
    private CarrierQueryWS carrierQueryWS;

    @Override
    public CommonDto<TransportResourceDto> getTransportResourceByTransCode(String capacityCode){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CarrierQueryWSMangerImpl.getTransportResourceByTransCode", false, true);
        try {
            CommonDto<TransportResourceDto>  result =  carrierQueryWS.getTransportResourceByTransCode(capacityCode);
            if(logger.isDebugEnabled()) {
            	logger.debug("调用运力查询信息接口结果:{} 入参capacityCode:{}",JsonHelper.toJson(result),capacityCode);
            }
            if(result == null || result.getData()==null ){
                logger.error("调用运力查询信息接口结果为空 入参capacityCode:{}",capacityCode);
                return null;
            }
            return result;
        }catch (Exception e){
            logger.error("调用运力查询信息接口异常 入参capacityCode:{}",capacityCode,e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
       return  null;
    }


}
    
