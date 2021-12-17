package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.tms.jdi.dto.*;
import com.jd.tms.jdi.ws.JdiSelectWS;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 2:17 下午
 */
@Service("jdiSelectWSManager")
public class JdiSelectWSManagerImpl implements JdiSelectWSManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdiSelectWS jdiSelectWS;

    @Override
    public CommonDto<String> checkTransportCode(String simpleCode, String transportCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiSelectWSManager.checkTransportCode",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(StringUtils.isEmpty(simpleCode)){
                return null;
            }
            return jdiSelectWS.checkTransportCode(simpleCode, transportCode);
        }catch (Exception e){
            logger.warn("根据派车任务明细简码:{}获取派车任务明细接口返回状态异常!", simpleCode);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public CommonDto<PageDto<TransBookBillResultDto>> getTransBookBill(TransBookBillQueryDto transBookBillQueryDto, PageDto<TransBookBillQueryDto> pageDto) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiSelectWSManager.getTransBookBill",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return jdiSelectWS.getTransBookBill(transBookBillQueryDto, pageDto);
        }catch (Exception e){
            logger.warn("根据条件:{},{}获取委托书列表异常!", JsonHelper.toJson(transBookBillQueryDto), JsonHelper.toJson(pageDto), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    @Override
    public CommonDto<TransWorkItemWsDto> getVehicleNumberOrItemCodeByParam(TransWorkItemWsDto transWorkItemWsDto) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiSelectWSManager.getVehicleNumberOrItemCodeByParam",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            return jdiSelectWS.getVehicleNumberOrItemCodeByParam(transWorkItemWsDto);
        }catch (Exception e){
            logger.warn("根据条件:{}获取派车明细异常!", JsonHelper.toJson(transWorkItemWsDto), e);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

}
