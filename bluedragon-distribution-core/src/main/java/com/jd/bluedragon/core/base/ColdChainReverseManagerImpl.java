package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coldchain.distribution.api.ColdDmsPackingConsumableApi;
import com.jd.coldchain.distribution.dto.BaseResponse;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseRequest;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseResult;
import com.jd.coldchain.fulfillment.ot.api.service.waybill.ColdChainReverseService;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.jd.coldchain.fulfillment.common.api.dto.ResponseDTO;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author : caozhixing
 * @version V1.0
 * @Project: bluedragon-distribution
 * @Package com.jd.bluedragon.core.base
 * @Description: 冷链逆向单接口
 * @date Date : 2021年12月22日 14:33
 */
@Service("coldChainReverseManager")
public class ColdChainReverseManagerImpl implements ColdChainReverseManager {
    private final static Logger logger = LoggerFactory.getLogger(ChuguanExportManagerImpl.class);

    @Autowired
    private ColdChainReverseService coldChainReverseService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    @Qualifier("obcsManager")
    private OBCSManager obcsManager;
    @Autowired
    private ColdDmsPackingConsumableApi coldDmsPackingConsumableApi;

    /**
     * 检查是否是需要调eclp逆向换单的冷链产品
     * 冷链卡班|冷链小票|医药大票
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.ColdChainReverseManager.checkColdReverseProductType", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public boolean checkColdReverseProductType(String waybillCode) {
        if(StringUtils.isBlank(waybillCode)){
            return false;
        }
        Waybill waybill = null;
        try {
            waybill = waybillQueryManager.getWaybillByWayCode(waybillCode);
        }catch (Exception e){
            logger.warn("ColdChainReverseManagerImpl-checkColdReverseProductType查询运单详情异常，运单号{}",waybillCode);
            return false;
        }
        if(waybill == null || waybill.getWaybillExt() == null || StringUtils.isBlank(waybill.getWaybillExt().getProductType())){
            return false;
        }
        List<String> productTypes = Arrays.asList(waybill.getWaybillExt().getProductType().split(Constants.SEPARATOR_COMMA));
        //
        return productTypes.contains(DmsConstants.PRODUCT_TYPE_COLD_CHAIN_KB)
                || productTypes.contains(Constants.PRODUCT_TYPE_MEDICINE_DP)
                || productTypes.contains(Constants.PRODUCT_TYPE_COLD_CHAIN_XP)
                || productTypes.contains(Constants.PRODUCT_TYPE_MEDICINE_COLD)
                || productTypes.contains(Constants.PRODUCT_TYPE_MEDICAL_PART_BILL);
    }

    /**
     *
     * 构建调用eclp逆向换单的入参
     * @param exchangeWaybillDto
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.ColdChainReverseManager.makeColdChainReverseRequest", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public ColdChainReverseRequest makeColdChainReverseRequest(ExchangeWaybillDto exchangeWaybillDto) {

        String waybillCode = exchangeWaybillDto.getWaybillCode();
        ColdChainReverseRequest requestDto = new ColdChainReverseRequest();
        requestDto.setWaybillCode(waybillCode);
        requestDto.setOperateUserId(exchangeWaybillDto.getOperatorId());
        requestDto.setOperateUser(exchangeWaybillDto.getOperatorName());
        requestDto.setOperateTime(StringUtils.isBlank(exchangeWaybillDto.getOperateTime())?new Date():DateHelper.parseDateTime(exchangeWaybillDto.getOperateTime()));
        if(exchangeWaybillDto.getIsTotalout()){
            requestDto.setReverseType(1);// 整单拒收
        }else{
            requestDto.setReverseType(2);// 包裹拒收
        }
        requestDto.setReverseSource(2);//分拣中心
        requestDto.setSortCenterId(exchangeWaybillDto.getCreateSiteCode());
        requestDto.setReturnType(LDOPManagerImpl.RETURN_TYPE_0);//默认
        if(exchangeWaybillDto.getReturnType()!=null){
            requestDto.setReturnType(exchangeWaybillDto.getReturnType());
        }
        if(!new Integer(0).equals(exchangeWaybillDto.getPackageCount())){
            requestDto.setPackageCount(exchangeWaybillDto.getPackageCount());
        }
        //系统来源:  1:城配，2:一体机，3:冷链调度，4:分拣
        requestDto.setSystemSource(LDOPManagerImpl.RETURN_TYPE_4);
        return requestDto;
    }

    /**
     * 调用eclp接口逆向换单
     * https://cf.jd.com/pages/viewpage.action?pageId=638147267
     * @param coldChainReverseRequest
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.ColdChainReverseManager.createReverseWbOrder", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public DmsWaybillReverseResult createReverseWbOrder(ColdChainReverseRequest coldChainReverseRequest,StringBuilder errorMessage) {
        CallerInfo info = null;
        try{
            info = Profiler.registerInfo( "DMSWEB.ColdChainReverseManagerImpl.createReverseWbOrder",false, true);

            ResponseDTO<ColdChainReverseResult> responseDTO = coldChainReverseService.createReverseWbOrder(coldChainReverseRequest);
            if(!ResponseDTO.SUCCESS_CODE.equals(responseDTO.getStatusCode())){
                //失败
                errorMessage.append("eclp自动换单接口失败 " + responseDTO.getStatusMessage());
                logger.warn("触发eclp逆向换单失败,入参：{}  失败原因：{}",JsonHelper.toJson(coldChainReverseRequest),responseDTO.getStatusMessage());
                return null;
            }
            ColdChainReverseResult coldChainReverseResult = responseDTO.getData();
            if(coldChainReverseResult == null || StringUtils.isBlank(coldChainReverseResult.getWaybillCode())) {
                //逆向换单后返回的运单号为空
                errorMessage.append("eclp自动换单接口失败 " + responseDTO.getStatusMessage());
                logger.warn("触发eclp逆向换单返回的运单号为空,入参：{}  返回值：{}",JsonHelper.toJson(coldChainReverseRequest),JsonHelper.toJson(responseDTO));
                return null;
            }
            DmsWaybillReverseResult result = new DmsWaybillReverseResult();
            result.setWaybillCode(coldChainReverseResult.getWaybillCode());
            return result;
        }catch (Exception e){
            logger.error("触发eclp逆向换单发生异常,入参：" + JsonHelper.toJson(coldChainReverseRequest),e);
            Profiler.functionError(info);
            return null;
        }
    }

    /**
     * 检查需不需要确认包装耗材
     * @param waybillCode
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.WEB.ColdChainReverseManager.checkIsNeedConfirmed", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean checkIsNeedConfirmed(String waybillCode) {
        if(StringUtils.isBlank(waybillCode)){
            return false;
        }
        CallerInfo info = null;
        boolean flag = false;
        try{
            info = Profiler.registerInfo( "DMSWEB.ColdChainReverseManagerImpl.checkIsNeedConfirmed",false, true);
            BaseResponse<Boolean>  baseResponse = coldDmsPackingConsumableApi.checkIsNeedConfirmed(waybillCode);
            logger.warn("checkIsNeedConfirmed检查需不需要确认冷链包装耗材,入参：{}  结果：{}",waybillCode,JsonHelper.toJson(baseResponse));
            if(Objects.nonNull(baseResponse) && baseResponse.getCode() == BaseResponse.OK_CODE){
                flag = baseResponse.getData();
            }
        }catch (Exception e){
            logger.error("checkIsNeedConfirmed检查需不需要确认冷链包装耗材发生异常,入参：" + waybillCode,e);
            Profiler.functionError(info);
        }
        return flag;
    }
}
