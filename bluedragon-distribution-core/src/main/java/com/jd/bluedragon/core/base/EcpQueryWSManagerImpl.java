package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.ecp.dto.AirBillCondtionDto;
import com.jd.tms.ecp.dto.AirPortDto;
import com.jd.tms.ecp.dto.AirTplBillDto;
import com.jd.tms.ecp.dto.BasicRailTrainDto;
import com.jd.tms.ecp.dto.CommonDto;
import com.jd.tms.ecp.ws.EcpQueryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author : xumigen
 * @date : 2019/9/13
 */
@Service("ecpQueryWSManager")
public class EcpQueryWSManagerImpl implements EcpQueryWSManager {

    private static final Logger log = LoggerFactory.getLogger(EcpQueryWSManagerImpl.class);

    @Autowired
    private EcpQueryWS ecpQueryWS;

    /**
     * 承运商账号;分拣使用时传固定值：(0000000)
     */
    private final static String carrierCode = "0000000";
    /**
     * 承运商类型;分拣使用时传固定值：(1)
     */
    private final static int carrierType = 1;

    @Override
    @JProfiler(jKey = "DMS.BASE.EcpQueryWSManagerImpl.getRailTrainListByCondition", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public BasicRailTrainDto getRailTrainListByCondition(String trainNumber,Integer beginCityId,Integer endCityId){
        BasicRailTrainDto basicRailTrainDto = new BasicRailTrainDto();
        basicRailTrainDto.setTrainNumber(trainNumber);
        basicRailTrainDto.setBeginCityId(beginCityId);
        basicRailTrainDto.setEndCityId(endCityId);
        CommonDto<List<BasicRailTrainDto>> commonDto = ecpQueryWS.getRailTrainListByCondition(basicRailTrainDto);
        if(commonDto == null || CommonDto.CODE_SUCCESS != commonDto.getCode() ){
            log.warn("获取列车车次信息失败trainNumber[{}]beginCityId[{}]endCityId[{}]",trainNumber,beginCityId,endCityId);
            return null;
        }
        List<BasicRailTrainDto> list = commonDto.getData();
        if(CollectionUtils.isEmpty(list)){
            log.warn("获取列车车次信息列表为空trainNumber[{}]beginCityId[{}]endCityId[{}]",trainNumber,beginCityId,endCityId);
            return null;
        }
        BasicRailTrainDto trainDto = list.get(0);
        if(log.isInfoEnabled()){
            log.info("获取列车车次信息结果trainNumber[{}]beginCityId[{}]endCityId[{}]trainDto[{}]",trainNumber,beginCityId,endCityId,
                    JsonHelper.toJson(trainDto));
        }
        return trainDto;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.EcpQueryWSManagerImpl.getRailTrainListByCondition", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public List<AirPortDto> getAirPortListByFlightNumber(String flightNumber){
        CommonDto<List<AirPortDto>> commonDto = ecpQueryWS.getAirPortListByFlightNumber(flightNumber);
        if(commonDto == null || CommonDto.CODE_SUCCESS != commonDto.getCode() ){
            log.warn("根据航班号查起末机场列表失败flightNumber[{}]beginCityId[{}]endCityId[{}]",flightNumber,JsonHelper.toJson(commonDto));
            return null;
        }
        return commonDto.getData();
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.EcpQueryWSManagerImpl.getAirTplBillDetailInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public AirTplBillDto getAirTplBillDetailInfo(String billCode){
        AirBillCondtionDto param = new AirBillCondtionDto();
        param.setBillCode(billCode);
        param.setCarrierCode(carrierCode);
        param.setCarrierType(carrierType);
        CommonDto<AirTplBillDto> commonDto = ecpQueryWS.getAirTplBillDetailInfo(param);
        if(commonDto == null || CommonDto.CODE_SUCCESS != commonDto.getCode() ){
            log.warn("根据主运单号查询主运单详情失败billCode[{}]endCityId[{}]",billCode,JsonHelper.toJson(commonDto));
            return null;
        }
        return commonDto.getData();
    }
}
