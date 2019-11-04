package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.ecp.dto.AirDepartInfoDto;
import com.jd.tms.ecp.dto.CommonDto;
import com.jd.tms.ecp.ws.EcpAirWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : xumigen
 * @date : 2019/11/4
 */
@Service("ecpAirWSManager")
public class EcpAirWSManagerImpl implements EcpAirWSManager {

    private static final Logger logger = LoggerFactory.getLogger(EcpAirWSManagerImpl.class);

    @Autowired
    private EcpAirWS ecpAirWS;

    @Override
    @JProfiler(jKey = "DMS.BASE.EcpAirWSManagerImpl.submitSortAirDepartInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public boolean submitSortAirDepartInfo(AirDepartInfoDto param) {
        CommonDto<String> commonDto = ecpAirWS.submitSortAirDepartInfo(param);
        logger.info("分拣发货登记提交调用运输接口param[{}]commonDto[{}]", JsonHelper.toJson(param),JsonHelper.toJson(commonDto));
        if(commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS){
            return false;
        }
        return true;
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.EcpAirWSManagerImpl.supplementSortAirDepartInfo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWEB)
    public boolean supplementSortAirDepartInfo(AirDepartInfoDto param) {
        CommonDto<String> commonDto = ecpAirWS.supplementSortAirDepartInfo(param);
        logger.info("分拣发货登记提交调用运输接口param[{}]commonDto[{}]", JsonHelper.toJson(param),JsonHelper.toJson(commonDto));
        if(commonDto == null || commonDto.getCode() != CommonDto.CODE_SUCCESS){
            return false;
        }
        return true;
    }


}
