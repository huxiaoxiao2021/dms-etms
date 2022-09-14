package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.UmpConstants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.tms.jdi.dto.*;
import com.jd.tms.jdi.ws.JdiQueryWS;
import com.jd.tms.jdi.ws.JdiTransWorkWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 类的描述
 *
 * @author hujiping
 * @date 2021/10/8 2:18 下午
 */
@Service("jdiQueryWSManager")
public class JdiQueryWSManagerImpl implements JdiQueryWSManager {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JdiQueryWS jdiQueryWS;

    @Qualifier("jdiTransWorkWS")
    @Autowired
    private JdiTransWorkWS jdiTransWorkWS;

    @Override
    public CommonDto<TransWorkItemDto> queryTransWorkItemBySimpleCode(String simpleCode) {
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.JdiQueryWSManager.queryTransWorkItemBySimpleCode",
                Constants.UMP_APP_NAME_DMSWEB,false,true);
        try {
            if(StringUtils.isEmpty(simpleCode)){
                return null;
            }
            return jdiQueryWS.queryTransWorkItemBySimpleCode(simpleCode);
        }catch (Exception e){
            logger.warn("根据派车任务明细简码:{}获取派车任务明细异常!", simpleCode);
            Profiler.functionError(callerInfo);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return null;
    }

    /**
     * 根据派车明细编码获取派车任务明细
     *
     * @param itemCode
     * @return
     */
    @Override
    public TransWorkItemDto getTransWorkItemsDtoByItemCode(String itemCode) {
        CommonDto<List<TransWorkItemDto>> resp = null;
        try {
            if(StringUtils.isEmpty(itemCode)){
                return null;
            }
            resp = jdiQueryWS.getTransWorkItemsDtoByItemCode(itemCode);
            if(resp.isSuccess() && !CollectionUtils.isEmpty(resp.getData())){
                for(TransWorkItemDto dto : resp.getData()){
                    if(itemCode.equals(dto.getTransWorkItemCode())){
                        return dto;
                    }
                }
            }
            return null;
        }catch (Exception e){
            logger.error("getTransWorkItemsDtoByItemCode:{}获取派车任务明细异常!",itemCode,e.getMessage(),e);
        }finally {
            logger.info("JdiQueryWS.getTransWorkItemsDtoByItemCode,req:{},resp:{}",itemCode, JsonHelper.toJson(resp));
        }
        return null;
    }

    @Override
    public BigTransWorkDto queryTransWorkByChoice(String transWorkCode, BigQueryOption option) {
        if (StringUtils.isBlank(transWorkCode)) {
            return null;
        }

        CallerInfo callerInfo = ProfilerHelper.registerInfo("dmsWeb.jsf.JdiQueryWSManager.queryTransWorkByChoice");
        try {
            CommonDto<BigTransWorkDto> commonDto = jdiTransWorkWS.queryDataByOption(transWorkCode, option);
            if (commonDto.getCode() != CommonDto.CODE_SUCCESS) {
                logger.warn("根据派车单号查询多表数据失败. {}, {}, resp:{}",
                        JsonHelper.toJson(transWorkCode), JsonHelper.toJson(option), JsonHelper.toJson(commonDto));
                return null;
            }
            if (commonDto.getData() == null) {
                logger.warn("根据派车单号查询多表数据成功，返回数据为空. {}, {}, resp:{}",
                        JsonHelper.toJson(transWorkCode), JsonHelper.toJson(option), JsonHelper.toJson(commonDto));
                return null;
            }

            return commonDto.getData();
        }
        catch (Exception ex) {
            logger.warn("根据派车单号查询多表数据异常! {}, {}", JsonHelper.toJson(transWorkCode), JsonHelper.toJson(option), ex);
            Profiler.functionError(callerInfo);
        }
        finally {
            Profiler.registerInfoEnd(callerInfo);
        }

        return null;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JdiQueryWSManager.queryTransWork", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public TransWorkBillDto queryTransWork(String transWorkCode) {
        BigQueryOption option = new BigQueryOption();
        option.setQueryTransWorkBillDto(true);
        BigTransWorkDto transWorkDto = queryTransWorkByChoice(transWorkCode, option);
        if (transWorkDto == null || transWorkDto.getTransWorkBillDto() == null) {
            Profiler.businessAlarm("dms.web.JdiQueryWSManager.queryTransWork", "查询运输派车单数据为空:" + transWorkCode);
            logger.warn("根据派车单号查询派车单返回数据为空. {}", transWorkCode);
            return null;
        }
        return transWorkDto.getTransWorkBillDto();
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JdiQueryWSManager.queryTransWorkAndAllItem", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public BigTransWorkDto queryTransWorkAndAllItem(String transWorkCode) {
        BigQueryOption option = new BigQueryOption();
        option.setQueryTransWorkBillDto(true);
        option.setQueryTransWorkItemDtoList(true);
        BigTransWorkDto transWorkDto = queryTransWorkByChoice(transWorkCode, option);
        if (transWorkDto == null || transWorkDto.getTransWorkBillDto() == null) {
            Profiler.businessAlarm("dms.web.JdiQueryWSManager.queryTransWork", "查询运输派车单数据为空:" + transWorkCode);
            logger.warn("根据派车单号查询派车单返回数据为空. {}", transWorkCode);
            return null;
        }
        return transWorkDto;
    }

    @Override
    @JProfiler(jKey = UmpConstants.UMP_KEY_BASE + "JdiQueryWSManager.listTranWorkCodesByVehicleFuzzy", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> listTranWorkCodesByVehicleFuzzy(TransWorkFuzzyQueryParam param) {
        if (logger.isInfoEnabled()){
            logger.info("invoke queryTransWorkByVehicleFuzzy req：{}",JsonHelper.toJson(param));
        }
        CommonDto<List<TransWorkBillDto>> rs = null;
        try {
            rs = jdiTransWorkWS.queryTransWorkByVehicleFuzzy(param);
            if (logger.isInfoEnabled()){
                logger.info(param.getBeginNodeCode()+"|"+param.getVehicleNumber()+" invoke queryTransWorkByVehicleFuzzy resp：{}",JsonHelper.toJson(rs));
            }
        } catch (Exception e) {
            logger.error("jy 根据车牌号后四位模糊检索派车单异常",e);
        }
        if (ObjectHelper.isNotNull(rs) && Constants.RESULT_SUCCESS ==rs.getCode()){
            List<TransWorkBillDto> transWorkBillDtoList =rs.getData();
            if (ObjectHelper.isNotNull(rs.getData()) && rs.getData().size()>0){
                List<String> transWorkCodeList =new ArrayList<>();
                for (TransWorkBillDto transWorkBillDto:transWorkBillDtoList){
                    transWorkCodeList.add(transWorkBillDto.getTransWorkCode());
                }
                return transWorkCodeList;
            }
        }
        return null;
    }
}
