package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
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

    private static final Logger logger = LoggerFactory.getLogger(EcpQueryWSManagerImpl.class);

    @Autowired
    private EcpQueryWS ecpQueryWS;

    @Override
    @JProfiler(jKey = "DMS.BASE.EcpQueryWSManagerImpl.getRailTrainListByCondition", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public BasicRailTrainDto getRailTrainListByCondition(String trainNumber,Integer beginCityId,Integer endCityId){
        BasicRailTrainDto basicRailTrainDto = new BasicRailTrainDto();
        basicRailTrainDto.setTrainNumber(trainNumber);
        basicRailTrainDto.setBeginCityId(beginCityId);
        basicRailTrainDto.setEndCityId(endCityId);
        CommonDto<List<BasicRailTrainDto>> commonDto = ecpQueryWS.getRailTrainListByCondition(basicRailTrainDto);
        if(commonDto == null || CommonDto.CODE_SUCCESS != commonDto.getCode() ){
            logger.warn("获取列车车次信息失败trainNumber[{}]beginCityId[{}]endCityId[{}]",trainNumber,beginCityId,endCityId);
            return null;
        }
        List<BasicRailTrainDto> list = commonDto.getData();
        if(CollectionUtils.isEmpty(list)){
            logger.warn("获取列车车次信息列表为空trainNumber[{}]beginCityId[{}]endCityId[{}]",trainNumber,beginCityId,endCityId);
            return null;
        }
        BasicRailTrainDto trainDto = list.get(0);
        logger.info("获取列车车次信息结果trainNumber[{}]beginCityId[{}]endCityId[{}]trainDto[{}]",trainNumber,beginCityId,endCityId,
                JsonHelper.toJson(trainDto));
        return trainDto;
    }
}
