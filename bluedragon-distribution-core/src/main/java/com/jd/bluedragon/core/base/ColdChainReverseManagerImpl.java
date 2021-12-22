package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.domain.ExchangeWaybillDto;
import com.jd.bluedragon.distribution.reverse.domain.LocalClaimInfoRespDTO;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseRequest;
import com.jd.coldchain.fulfillment.ot.api.dto.waybill.ColdChainReverseResult;
import com.jd.coldchain.fulfillment.ot.api.service.waybill.ColdChainReverseService;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ldop.center.api.reverse.dto.WaybillReverseResult;
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

    /**
     * 检查是否是需要调eclp逆向换单的冷链产品
     * 冷链卡班|冷链小票|医药大票
     * @param waybillCode
     * @return
     */
    @Override
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
        List<String> productTypes = Arrays.asList(waybill.getWaybillExt().getProductType().split(","));
        return productTypes.contains(DmsConstants.PRODUCT_TYPE_COLD_CHAIN_KB) || productTypes.contains(Constants.PRODUCT_TYPE_MEDICINE_DP) || productTypes.contains(Constants.PRODUCT_TYPE_COLD_CHAIN_XP);
    }

    /**
     *
     * 构建调用eclp逆向换单的入参
     * @param exchangeWaybillDto
     * @return
     */
    @Override
    public ColdChainReverseRequest makeColdChainReverseRequest(ExchangeWaybillDto exchangeWaybillDto) {

        String waybillCode = exchangeWaybillDto.getWaybillCode();
        ColdChainReverseRequest requestDto = new ColdChainReverseRequest();
        requestDto.setLwbNo(waybillCode);
        requestDto.setOperateUserId(exchangeWaybillDto.getOperatorId());
        requestDto.setOperateUser(exchangeWaybillDto.getOperatorName());
        requestDto.setOperateTime(StringUtils.isBlank(exchangeWaybillDto.getOperateTime())?new Date():DateHelper.parseDateTime(exchangeWaybillDto.getOperateTime()));
        if(exchangeWaybillDto.getIsTotalout()){
            requestDto.setReverseType((byte)1);// 整单拒收
        }else{
            requestDto.setReverseType((byte)2);// 包裹拒收
        }
        requestDto.setReverseSource((byte)2);//分拣中心
        requestDto.setSortCenterId(exchangeWaybillDto.getCreateSiteCode());
        requestDto.setReturnType(LDOPManagerImpl.RETURN_TYPE_0);//默认
        if(exchangeWaybillDto.getReturnType()!=null){
            requestDto.setReturnType(exchangeWaybillDto.getReturnType());
        }
        if(!new Integer(0).equals(exchangeWaybillDto.getPackageCount())){
            requestDto.setPackageCount(exchangeWaybillDto.getPackageCount());
        }
        if(this.checkIsPureMatch(waybillCode,null)){
            //是纯配一次换单  理赔状态满足  一定退备件库
            requestDto.setReturnType(LDOPManagerImpl.RETURN_TYPE_4);
        }
        return requestDto;
    }

    /**
     * 调用eclp接口逆向换单
     * @param coldChainReverseRequest
     * @return
     */
    @Override
    public WaybillReverseResult createReverseWbOrder(ColdChainReverseRequest coldChainReverseRequest,StringBuilder errorMessage) {
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
            if(coldChainReverseResult == null || StringUtils.isBlank(coldChainReverseResult.getLwb())) {
                //逆向换单后返回的运单号为空
                errorMessage.append("eclp自动换单接口失败 " + responseDTO.getStatusMessage());
                logger.warn("触发eclp逆向换单返回的运单号为空,入参：{}  返回值：{}",JsonHelper.toJson(coldChainReverseRequest),JsonHelper.toJson(responseDTO));
                return null;
            }
            WaybillReverseResult result = new WaybillReverseResult();
            result.setWaybillCode(coldChainReverseResult.getLwb());
            return result;
        }catch (Exception e){
            logger.error("触发eclp逆向换单发生异常,入参：" + JsonHelper.toJson(coldChainReverseRequest),e);
            Profiler.functionError(info);
            return null;
        }
    }

    /**
     * 纯配一次换单判断
     * @param waybillCode
     * @param waybillSign
     * @return
     */
    private Boolean checkIsPureMatch(String waybillCode,String waybillSign){

        if(StringUtils.isBlank(waybillSign)){
            //外部未传入waybillSign 自己再去调用一次
            BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode,true,true,true,false);
            if(baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null && StringUtils.isNotBlank(baseEntity.getData().getWaybill().getWaybillSign())){
                waybillSign = baseEntity.getData().getWaybill().getWaybillSign();
            }
        }
        //纯配外单一次换单理赔完成且物权归京东-退备件库
        if(BusinessUtil.isPurematch(waybillSign)){
            LocalClaimInfoRespDTO claimInfoRespDTO = obcsManager.getClaimListByClueInfo(1,waybillCode);
            if(claimInfoRespDTO != null){
                if(LocalClaimInfoRespDTO.LP_STATUS_DONE.equals(claimInfoRespDTO.getStatusDesc())
                        && LocalClaimInfoRespDTO.GOOD_OWNER_JD.equals(claimInfoRespDTO.getGoodOwner())){
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }
}
