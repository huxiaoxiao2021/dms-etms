package com.jd.bluedragon.core.jsf.address;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.jd.addresstranslation.api.address.GBDistrictJDDistrictMapService;
import com.jd.addresstranslation.api.base.BaseResponse;
import com.jd.addresstranslation.api.base.GBAddressLevelsResponse;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ql.dms.print.utils.StringHelper;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

/**
 * 
 * @ClassName: GbDistrictJDDistrictMapServiceManagerImpl
 * @link https://lbsapi.jd.com/iframe.html?nav=2&childNav=1-7&childURL=/doc/guide/addressAnalyze/nationalStandardData/
 * @Description: 京标国标映射服务实现
 * @author: wuyoude
 * @date: 2022年12月1日 下午2:37:26
 *
 */
@Service("gbDistrictJDDistrictMapServiceManager")
public class GbDistrictJDDistrictMapServiceManagerImpl implements GbDistrictJDDistrictMapServiceManager{
    private static final Logger log = LoggerFactory.getLogger(GbDistrictJDDistrictMapServiceManagerImpl.class);
    private static final String UMP_KEY_PREFIX = "dmsWeb.jsf.client.address.gbDistrictJDDistrictMapService.";
    private static final Integer SUC_CODE = 200;
    private static final String SUFF_CODE = "000000000000000000000000000000000000";
    
    @Autowired
    @Qualifier("gbDistrictJDDistrictMapService")
    private GBDistrictJDDistrictMapService gbDistrictJDDistrictMapService;
    /**
     * 秘钥
     */
    @Value("${beans.gbDistrictJDDistrictMapServiceManager.appKey}")
    private String appKey;
    
    @Cache(key = "addressGbMapService.getGBDistrictByJDCode@args0", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = false)
	@Override
	public JdResult<DmsGbAddressLevelsResponse> getGBDistrictByJDCode(Integer jdCode){
    	CallerInfo callerInfo = ProfilerHelper.registerInfo(UMP_KEY_PREFIX + "partnerReceiptState");
		JdResult<DmsGbAddressLevelsResponse> result = new JdResult<DmsGbAddressLevelsResponse>();
		if(jdCode == null || jdCode <= 0) {
			result.toFail("输入的jdCode无效！");
			return result;
		}
		try {
			if(log.isInfoEnabled()){
				log.info("京标国标映射服务-request：{},{}",appKey,jdCode);
			}
			BaseResponse<List<GBAddressLevelsResponse>>  rpcResult = gbDistrictJDDistrictMapService.getGBDistrictByJDCode(appKey, jdCode);
			if(log.isInfoEnabled()){
				log.info("京标国标映射服务-response：{}",JsonHelper.toJson(rpcResult));
			}
			if(rpcResult != null
					&& SUC_CODE.equals(rpcResult.getStatus())
					&& !CollectionUtils.isEmpty(rpcResult.getResult())){
				DmsGbAddressLevelsResponse dmsData = toDmsGbAddressLevelsResponse(rpcResult.getResult().get(0));
				dmsData.setJdCode(jdCode.toString());
				result.setData(dmsData);
				result.toSuccess();
			}else{
				log.warn("京标国标映射服务-查询失败！return:{}",JsonHelper.toJson(rpcResult));
				result.toFail("京标国标映射服务-查询失败！");
			}
		} catch (Exception e) {
			log.error("京标国标映射服务-查询异常！",e);
			result.toError("京标国标映射服务-查询异常！");
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}

	private DmsGbAddressLevelsResponse toDmsGbAddressLevelsResponse(GBAddressLevelsResponse gbAddressLevelsResponse) {
		DmsGbAddressLevelsResponse dmsData = new DmsGbAddressLevelsResponse();
		if(StringHelper.isNotEmpty(gbAddressLevelsResponse.getTownCode())) {
			dmsData.setGbCode(toCode12(gbAddressLevelsResponse.getTownCode()));
			dmsData.setName(gbAddressLevelsResponse.getTownName());
			dmsData.setAbbreviationName(gbAddressLevelsResponse.getTownAbbreviationName());
			return dmsData;
		}
		if(StringHelper.isNotEmpty(gbAddressLevelsResponse.getDistrictCode())) {
			dmsData.setGbCode(toCode12(gbAddressLevelsResponse.getDistrictCode()));
			dmsData.setName(gbAddressLevelsResponse.getDistrictName());
			dmsData.setAbbreviationName(gbAddressLevelsResponse.getDistrictAbbreviationName());
			return dmsData;
		}
		if(StringHelper.isNotEmpty(gbAddressLevelsResponse.getCityCode())) {
			dmsData.setGbCode(toCode12(gbAddressLevelsResponse.getCityCode()));
			dmsData.setName(gbAddressLevelsResponse.getCityName());
			dmsData.setAbbreviationName(gbAddressLevelsResponse.getCityAbbreviationName());
			return dmsData;
		}
		if(StringHelper.isNotEmpty(gbAddressLevelsResponse.getProvinceCode())) {
			dmsData.setGbCode(toCode12(gbAddressLevelsResponse.getProvinceCode()));
			dmsData.setName(gbAddressLevelsResponse.getProvinceName());
			dmsData.setAbbreviationName(gbAddressLevelsResponse.getProvAbbreviationName());
			return dmsData;
		}		
		return dmsData;
	}
	private String toCode12(String code) {
		if(code != null && code.length() < 12) {
			return code.concat(SUFF_CODE.substring(0, 12 - code.length()));
		}
		return code;
	}
}
