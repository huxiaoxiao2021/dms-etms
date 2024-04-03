package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.domain.DmsWaybillReverseResult;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coldchain.distribution.dto.BaseResponse;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseRequest;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseResult;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.WaybillAddress;
import com.jd.coldchain.fulfillment.ot.api.service.waybill.ColdChainReverseService;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.tp.common.utils.Objects;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    private final static Logger logger = LoggerFactory.getLogger(ColdChainReverseManagerImpl.class);

    @Autowired
    private ColdChainReverseService coldChainReverseService;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    /**
     * 二次换单限制次数
     */
    @Value("${beans.WaybillReverseManagerImpl.twiceExchangeMaxTimes:3}")
    private int twiceExchangeMaxTimes;

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
        requestDto.setLimitReverseFlag(Boolean.FALSE);
        requestDto.setWaybillCode(waybillCode);
        requestDto.setOperateUserId(exchangeWaybillDto.getOperatorId());
        requestDto.setOperateUser(exchangeWaybillDto.getOperatorName());
        requestDto.setOperateTime(StringUtils.isBlank(exchangeWaybillDto.getOperateTime())?new Date():DateHelper.parseDateTime(exchangeWaybillDto.getOperateTime()));
        if(exchangeWaybillDto.getIsTotalout()){
            requestDto.setReverseType(1);// 整单拒收
        }else{
            requestDto.setReverseType(2);// 包裹拒收
        }
        //二次换单时设置换单次数限制
        if(Boolean.TRUE.equals(exchangeWaybillDto.getTwiceExchangeFlag())){
            requestDto.setLimitReverseFlag(Boolean.TRUE);
            requestDto.setAllowReverseCount(twiceExchangeMaxTimes);
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
        //自定义地址
        if(StringUtils.isNotBlank(exchangeWaybillDto.getAddress())){
            WaybillAddress waybillAddress = new WaybillAddress();
            waybillAddress.setAddress(exchangeWaybillDto.getAddress());
            waybillAddress.setContact(exchangeWaybillDto.getContact());
            waybillAddress.setPhone(exchangeWaybillDto.getPhone());
            requestDto.setWaybillAddress(waybillAddress);
        }
        //系统来源:  1:城配，2:一体机，3:冷链调度，4:分拣
        requestDto.setSystemSource(LDOPManagerImpl.SYSTEM_SOURCE_4);
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
        try{

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
            return null;
        }
    }


}
