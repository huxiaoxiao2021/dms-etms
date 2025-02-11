package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.tms.basic.dto.*;
import com.jd.tms.basic.ws.CarrierQueryWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/9 14:05
 */
@Service("carrierQueryWSManger")
public class CarrierQueryWSMangerImpl implements CarrierQueryWSManager{

    private static final Logger logger = LoggerFactory.getLogger(CarrierQueryWSMangerImpl.class);

    @Value("${carrier.fuzzyQuery.num:20}")
    private int fuzzyQueryResultNum;

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

    @Override
    public List<SimpleCarrierDto> queryCarrierByLikeCondition(CarrierDto condition){
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CarrierQueryWSMangerImpl.queryCarrierByLikeCondition", false, true);
        try {
            CommonDto<List<SimpleCarrierDto>> commonDto = carrierQueryWS.queryCarrierByLikeCondition(condition, fuzzyQueryResultNum);
            if(commonDto == null || CollectionUtils.isEmpty(commonDto.getData())){
                logger.warn("根据条件{}模糊查询承运商数据为空!", JsonHelper.toJson(condition));
                return null;
            }
            return commonDto.getData();
        }catch (Exception e){
            logger.error("根据条件{}模糊查询承运商异常!", JsonHelper.toJson(condition), e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return  null;
    }

    /**
     * 根据条件获取承运商司机
     * @param paramDto 入参
     * @return 结果
     * @author fanggang7
     * @time 2021-11-16 13:40:28 周二
     */
    @Override
    public CommonDto<CarrierDriverDto> getCarrierDriverByParam(CarrierDriverParamDto paramDto) {
        CallerInfo info = Profiler.registerInfo("DMS.BASE.CarrierQueryWSMangerImpl.getCarrierDriverByParam", false, true);
        try {
            CommonDto<CarrierDriverDto> commonDto = carrierQueryWS.getCarrierDriverByParam(paramDto);
            if(commonDto == null){
                logger.warn("根据条件{}模糊查询承运商数据为空!", JsonHelper.toJson(paramDto));
                return null;
            }
            return commonDto;
        }catch (Exception e){
            logger.error("根据条件{}模糊查询承运商异常!", JsonHelper.toJson(paramDto), e);
            Profiler.functionError(info);
        }finally {
            Profiler.registerInfoEnd(info);
        }
        return null;
    }
}
