package com.jd.bluedragon.distribution.busineCode.sendCode.jsf;

import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jsf.domain.InvokeResult;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.busineCode.sendCode.service.SendCodeService;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import com.jd.bluedragon.distribution.sendCode.DMSSendCodeJSFService;
import com.jd.bluedragon.distribution.sendCode.domain.HugeSendCodeEntity;
import com.jd.bluedragon.distribution.ver.domain.Site;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.SiteHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ql.basic.dto.BaseSiteInfoDto;
import com.jd.ql.dms.report.WeightVolSendCodeJSFService;
import com.jd.ql.dms.report.domain.BaseEntity;
import com.jd.ql.dms.report.domain.WeightVolSendCodeSumVo;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;

import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jd.bluedragon.distribution.busineCode.sendCode.contant.HugeSendCodeResponseEnum.NOT_DMS_POSITIVE;
import static com.jd.bluedragon.distribution.busineCode.sendCode.contant.HugeSendCodeResponseEnum.SEND_PROCESSING;

/**
 * <p>
 *     分拣批次号jsf服务实现类
 *
 * @author wuzuxiang
 * @since 2020/2/25
 **/
@Service("dmsSendCodeJSFService")
public class DMSSendCodeJSFServiceImpl implements DMSSendCodeJSFService {

    private static final Logger logger = LoggerFactory.getLogger(DMSSendCodeJSFServiceImpl.class);

    @Autowired
    private SendCodeService sendCodeService;

    @Autowired
    private WeightVolSendCodeJSFService weightVolSendCodeJSFService;

    @Autowired
    private SiteService siteService;

    @Autowired
    private SendDetailService sendDetailService;

    @Override
    public InvokeResult<HugeSendCodeEntity> queryBigInfoBySendCode(String sendCode) {
        InvokeResult<HugeSendCodeEntity> result = new InvokeResult<>();
        result.success();
        CallerInfo callerInfo = ProfilerHelper.registerInfo( "DMSSendCodeJSFService.queryBigInfoBySendCode.check.fail");
        Pair<InvokeResult<HugeSendCodeEntity>, Integer> checkResult = checkSendCode(sendCode);
        if(!checkResult.getKey().codeSuccess()){
            Profiler.registerInfoEnd(callerInfo);
            return checkResult.getKey();
        }

        HugeSendCodeEntity entity = new HugeSendCodeEntity();
        entity.setSendCode(sendCode);

        /* 1. 查询批次号重量体积信息 */
        BaseEntity<WeightVolSendCodeSumVo> weightVolSendCodeSumVoBaseEntity =
                weightVolSendCodeJSFService.sumWeightAndVolumeBySendCode(sendCode);
        if (weightVolSendCodeSumVoBaseEntity != null && weightVolSendCodeSumVoBaseEntity.isSuccess()
                && weightVolSendCodeSumVoBaseEntity.getData() != null) {
            WeightVolSendCodeSumVo sumVo = weightVolSendCodeSumVoBaseEntity.getData();
            entity.setVolume(weightVolSendCodeSumVoBaseEntity.getData().getPackageVolumeSum());
            entity.setWeight(weightVolSendCodeSumVoBaseEntity.getData().getPackageWeightSum());
            if(sumVo.getSendDetailCount() < checkResult.getValue()){
                result.customMessage(SEND_PROCESSING.getCode(), SEND_PROCESSING.getDesc());
                logger.info("根据批次号查询体积重量es有积压，表数量:{}大于es中发货数量:{},sendCode:", checkResult.getValue(),
                        sumVo.getSendDetailCount(), sendCode);
            }else {
                logger.info("根据批次号查询体积重量发货数量正常，表数量:{}与es中发货数量:{}对比通过,sendCode:", checkResult.getValue(),
                        sumVo.getSendDetailCount(), sendCode);
            }
        } else {
            logger.error("根据批次号查询批次下重量体积失败：{}", sendCode);
        }
        
        

        /* 2. 查询批次号的生鲜属性 */
        Boolean isFreshCode = sendCodeService.isFreshSendCode(sendCode);
        entity.setFreshCode(Boolean.TRUE.equals(isFreshCode));

        result.setData(entity);
        return result;
    }

    private Pair<InvokeResult<HugeSendCodeEntity>, Integer> checkSendCode(String sendCode){
        InvokeResult<HugeSendCodeEntity> result = new InvokeResult<>();
        if(StringUtils.isBlank(sendCode)){
            result.customMessage(400, "查询参数sendCode为空");
            return Pair.of(result, null);
        }
        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> validate =  sendCodeService.validateSendCodeEffective(sendCode);
        //服务执行异常
        if(validate.getCode() == com.jd.bluedragon.distribution.base.domain.InvokeResult.SERVER_ERROR_CODE){
            result.customMessage(validate.getCode(), validate.getMessage());
            return Pair.of(result, null);
        }
        //非200不存在（不符合正则，或在库里不存在）
        if(!validate.codeSuccess()){
            logger.error("根据批次号查询批次内的体积重量，批次号不存在。sendcode:{},code:{},message:{}", sendCode, validate.getCode(),
                    validate.getMessage());
            result.customMessage(NOT_DMS_POSITIVE.getCode(), NOT_DMS_POSITIVE.getDesc());
            return Pair.of(result, null);
        }

        Integer createSiteCode = BusinessUtil.getCreateSiteCodeFromSendCode(sendCode);
        //批次号始发站点为空
        if(createSiteCode == null){
            logger.error("根据批次号查询批次内的体积重量，批次号校验，根据批次号未获取到是始发站点。sendcode:{}", sendCode);
            result.customMessage(NOT_DMS_POSITIVE.getCode(), NOT_DMS_POSITIVE.getDesc());
            return Pair.of(result, null);
        }
        Site curSite = siteService.get(createSiteCode);
        //批次号始发站点信息不存在
        if(curSite == null){
            logger.error("根据批次号查询批次内的体积重量,批次号校验批次号创建站点信息不存在，sendcode:{},createSiteCode:{}",
                    sendCode, createSiteCode);
            result.customMessage(NOT_DMS_POSITIVE.getCode(), NOT_DMS_POSITIVE.getDesc());
            return Pair.of(result, null);
        }
        //终端创建的批次号
        if(curSite.getType() == 4 || curSite.getType() == 8 || curSite.getType() == 16 ){
            logger.error("根据批次号查询批次内的体积重量,批次号校验，创建站点未终端站点，sendcode:{},createSiteCode:{}",
                    sendCode, createSiteCode);
            result.customMessage(NOT_DMS_POSITIVE.getCode(), NOT_DMS_POSITIVE.getDesc());
            return Pair.of(result, null);
        }

        Integer sendDetailCount = sendDetailService.querySendDCountBySendCode(sendCode);
        if(sendDetailCount == 0){
            logger.error("根据批次号查询批次内的体积重量,批次号无发货数据，sendcode:{},sendDetailCount:{}",
                    sendCode, sendDetailCount);
            result.customMessage(NOT_DMS_POSITIVE.getCode(), NOT_DMS_POSITIVE.getDesc());
            return Pair.of(result, 0);
        }
        
        return Pair.of(result, sendDetailCount);

    }
	@Override
	@JProfiler(jKey = "DMS.CORE.DMSSendCodeJSFServiceImpl.queryWeightAndVolumeInfoBySendCodes", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
	public InvokeResult<Map<String, HugeSendCodeEntity>> queryWeightAndVolumeInfoBySendCodes(List<String> sendCodes) {
        InvokeResult<Map<String, HugeSendCodeEntity>> result = new InvokeResult<>();
        result.success();
        result.setData(new HashMap<String, HugeSendCodeEntity>());
        if(CollectionUtils.isEmpty(sendCodes)) {
        	return result;
        }
        List<WeightVolSendCodeSumVo> weightVolumeList = null;
        /* 1. 查询批次号重量体积信息 */
        try {
			BaseEntity<List<WeightVolSendCodeSumVo>> weightVolSendCodeSumVoBaseEntity = weightVolSendCodeJSFService.sumWeightAndVolumeBySendCodes(sendCodes);
			if (weightVolSendCodeSumVoBaseEntity != null
					&& weightVolSendCodeSumVoBaseEntity.isSuccess()
			        && weightVolSendCodeSumVoBaseEntity.getData() != null) {
			    weightVolumeList = weightVolSendCodeSumVoBaseEntity.getData();
			} else {
			    logger.warn("根据批次号列表查询批次下重量体积失败：{},查询结果{}", sendCodes,JsonHelper.toJson(weightVolSendCodeSumVoBaseEntity));
			}
		} catch (Exception e) {
			logger.error("根据批次号列表查询批次下重量体积失败：{}", JsonHelper.toJson(sendCodes),e);
		}
        Map<String,WeightVolSendCodeSumVo> weightMap = new HashMap<String,WeightVolSendCodeSumVo>();
        if(!CollectionUtils.isEmpty(weightVolumeList)) {
        	for(WeightVolSendCodeSumVo weightVo: weightVolumeList) {
        		weightMap.put(weightVo.getSendCode(), weightVo);
        	}
    	}
        for(int  i=0; i<sendCodes.size();i++) {
        	String sendCode = sendCodes.get(i);
        	HugeSendCodeEntity data = new HugeSendCodeEntity();
        	data.setSendCode(sendCode);
        	if(weightMap.containsKey(sendCode)) {
            	WeightVolSendCodeSumVo weightVo = weightMap.get(sendCode);
            	data.setVolume(weightVo.getPackageVolumeSum());
            	data.setWeight(weightVo.getPackageWeightSum());
        	}
        	result.getData().put(sendCode, data);
        }
		return result;
	}
    @Override
    public InvokeResult<String> createSendCode(Map<BusinessCodeAttributeKey.SendCodeAttributeKeyEnum, String> attributeKeyEnumObjectMap, BusinessCodeFromSourceEnum fromSourceEnum, String createUser) {
        InvokeResult<String> result = new InvokeResult<>();
        result.success();

        result.setData(sendCodeService.createSendCode(attributeKeyEnumObjectMap,fromSourceEnum, createUser));
        if (StringHelper.isEmpty(result.getData())) {
            result.customMessage(400,"创建批次失败");
        }

        return result;
    }

    @Override
    public InvokeResult<Boolean> validateSendCodeEffective(String sendCode) {
        InvokeResult<Boolean> result = new InvokeResult<>();
        result.success();

        com.jd.bluedragon.distribution.base.domain.InvokeResult<Boolean> invokeResult = sendCodeService.validateSendCodeEffective(sendCode);
        result.setCode(invokeResult.getCode());
        result.setData(invokeResult.getData());
        result.setMessage(invokeResult.getMessage());

        return result;
    }
}
