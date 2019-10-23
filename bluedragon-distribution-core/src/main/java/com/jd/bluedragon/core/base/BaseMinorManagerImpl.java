package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import com.jd.ldop.basic.api.BasicTraderAPI;
import com.jd.ldop.basic.api.BasicTraderReturnAPI;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.basic.dto.BasicTraderReturnDTO;
import com.jd.ldop.basic.dto.PageDTO;
import com.jd.ldop.basic.dto.ResponseDTO;
import com.jd.ql.basic.domain.AirTransport;
import com.jd.ql.basic.domain.BaseDmsStore;
import com.jd.ql.basic.domain.BaseResult;
import com.jd.ql.basic.domain.BaseSiteGoods;
import com.jd.ql.basic.domain.CrossPackageTagNew;
import com.jd.ql.basic.domain.ReverseCrossPackageTag;
import com.jd.ql.basic.dto.BaseGoodsPositionDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BaseCrossPackageTagWS;
import com.jd.ql.basic.ws.BasicAirConfigWS;
import com.jd.ql.basic.ws.BasicSecondaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Service("baseMinorManager")
public class BaseMinorManagerImpl implements BaseMinorManager {
	private Log log = LogFactory.getLog(BaseMinorManagerImpl.class);
	
	public static final String SEPARATOR_HYPHEN = "-";

	@Autowired
	@Qualifier("basicTraderAPI")
	private BasicTraderAPI basicTraderAPI;

	@Autowired
	@Qualifier("basicTraderReturnAPI")
	private BasicTraderReturnAPI basicTraderReturnAPI;

	@Autowired
	@Qualifier("basicSecondaryWS")
	private BasicSecondaryWS basicSecondaryWS;

	@Autowired
	@Qualifier("baseCrossPackageTagWS")
	private BaseCrossPackageTagWS baseCrossPackageTagWS;
    /**
     * 是否使用基础资料新接口，默认启用
     */
	@Value("${beans.BaseMinorManagerImpl.useNewCrossPackageTagWS:true}")
	private boolean useNewCrossPackageTagWS;

	/**
	 * 
	 * 获取航空标示信息接口
	 */
	@Autowired
	private BasicAirConfigWS basicAirConfigWS;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Cache(key = "TbaseMinorManagerImpl.getBaseTraderById@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
	redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderById", mState = {JProEnum.TP, JProEnum.FunctionError})
	public BasicTraderInfoDTO getBaseTraderById(Integer paramInteger) {
		ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
		responseDTO = basicTraderAPI.getBasicTraderById(paramInteger);
		if(responseDTO != null && responseDTO.getResult() != null ){
			responseDTO.getResult().setAllAddress(null);
			return responseDTO.getResult();
		}
		return null;
	}

	@Cache(key = "baseMinorManagerImpl.getAirConfig@args0@args1@args2@args3", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getAirConfig", mState = {JProEnum.TP, JProEnum.FunctionError})
	public AirTransport getAirConfig(Integer originalProvinceId,
			Integer originalCityId, Integer destinationProvinceId,
			Integer destinationCityId) {

		return basicAirConfigWS
				.getAirConfig(originalProvinceId, originalCityId,
						destinationProvinceId, destinationCityId);
	}
	
	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionDmsCodeSiteCode", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionDmsCodeSiteCode(Integer dmsID,String flage, Integer siteCode) {
		return ((List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionDmsCodeSiteCode(dmsID, siteCode));
		 
	}

	@Cache(key = "baseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId@args0@args1@args2", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000, 
			redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseGoodsPositionTaskAreaNoDmsId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BaseGoodsPositionDto> getBaseGoodsPositionTaskAreaNoDmsId(
			Integer dmsID,String flage, Integer taskAreaNo) {
		return (List<BaseGoodsPositionDto>) basicSecondaryWS.getBaseGoodsPositionTaskAreaNoDmsId(String.valueOf(taskAreaNo), dmsID);
	}

	@Cache(key = "TbaseMinorManagerImpl.getBaseTraderByName@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderByName", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BasicTraderInfoDTO> getBaseTraderByName(String name) {
		ResponseDTO<List<BasicTraderInfoDTO>> responseDTO =  basicTraderAPI.getBaseTraderByName(name);
		if(responseDTO == null || responseDTO.getResult() == null){
			return Collections.emptyList();
		}
        resetBasicTraderInfoDTOs(responseDTO.getResult());
		return responseDTO.getResult();
	}

    /**
     * 获取签约商家ID列表
     * @return
     */
    @Cache(key = "baseMinorManagerImpl.getTraderInfoPopCodeAll", memoryEnable = true, memoryExpiredTime = 30 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getSignCustomer", mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<String> getSignCustomer(){
		ResponseDTO<List<String>> responseDTO = null;
         responseDTO = basicTraderAPI.getTraderInfoPopCodeAll();
         if(responseDTO == null){
         	return null;
		 }
		return responseDTO.getResult();
    }

	@Cache(key = "TbaseMinorManagerImpl.getBaseAllTrader", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseAllTrader", mState = {JProEnum.TP, JProEnum.FunctionError})
	public List<BasicTraderInfoDTO> getBaseAllTrader() {
		log.info("基础资料客户端--getBaseAllTrader获取所有商家，开始调用分页接口获取数据");
		List<BasicTraderInfoDTO> traderList = new ArrayList();
		int count = 0;
		long startTime = System.currentTimeMillis();
		ResponseDTO<PageDTO<BasicTraderInfoDTO>> resPageDto = this.basicTraderAPI.getTraderListByPage(1);
		if(null != resPageDto && null != resPageDto.getResult() && null != resPageDto.getResult().getData()
				&& (resPageDto.getResult().getData()).size() > 0) {
			PageDTO<BasicTraderInfoDTO> pageDTO = resPageDto.getResult();
			traderList.addAll((Collection)pageDTO.getData());
			count = pageDTO.getTotalRow();
			int totalPage = pageDTO.getTotalPage();

			for(int i = 2; i <= totalPage; ++i) {
				traderList.addAll((Collection)this.basicTraderAPI.getTraderListByPage(i).getResult().getData());
			}
		} else {
			log.error("getBaseAllTrader获取数据为空");
		}

		log.info("getBaseAllTrader获取数据count[" + count + "]");
		log.info("getBaseAllTrader获取数据耗时[" + (System.currentTimeMillis() - startTime) + "]");
        resetBasicTraderInfoDTOs(traderList);
		return traderList;
	}

	@Cache(key = "DMS.BASE.BaseMinorManagerImpl.getGoodsVolumeLimitBySiteCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
			redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	public BaseSiteGoods getGoodsVolumeLimitBySiteCode(Integer siteCode) {
		CallerInfo info = Profiler.registerInfo("DMS.BASE.BaseMinorManagerImpl.getGoodsVolumeLimitBySiteCode", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try {
			BaseResult<BaseSiteGoods> baseResult = basicSecondaryWS.getGoodsVolumeLimitBySiteCode(siteCode);
			if (baseResult != null && baseResult.getResultCode() == BaseResult.RESULT_SUCCESS) {
				return baseResult.getData();
			}else{
				log.warn("获取三方站点超限配置为空，siteCode："+siteCode + ",返回结果："+ JsonHelper.toJson(baseResult));
			}
		} catch (Exception e) {
            Profiler.functionError(info);
            log.error("获取三方站点超限配置异常，siteCode："+siteCode, e);
		}finally {
            Profiler.registerInfoEnd(info);
        }
		return null;
	}


	@Override
    @Cache(key = "DMS.BASE.BaseMinorManagerImpl.getTraderInfoByPopCode@args0", memoryEnable = true, memoryExpiredTime = 10 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 20 * 60 * 1000)
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getTraderInfoByPopCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public BasicTraderInfoDTO getTraderInfoByPopCode(String popCode){
		ResponseDTO<BasicTraderInfoDTO> responseDTO = null;
		responseDTO = basicTraderAPI.getBasicTraderInfoByPopId(popCode);
		if(responseDTO != null && responseDTO.getResult() != null){
		    //设置商家地址列表为空,这个属性太大，并且我们用不到
            responseDTO.getResult().setAllAddress(null);
			return responseDTO.getResult();
		}
		return null;
	}

    /**
     * 精简商家基础资料，过滤掉不用的大属性，避免内存缓存存储大量无用数据
     * 不用的大属性：商家地址列表
     * @param basicTraderInfoDTOList
     */
	private void resetBasicTraderInfoDTOs(List<BasicTraderInfoDTO> basicTraderInfoDTOList){
	    if(basicTraderInfoDTOList == null || basicTraderInfoDTOList.isEmpty()){
	        return;
        }
        for (BasicTraderInfoDTO dto:basicTraderInfoDTOList){
            dto.setAllAddress(null);
        }
    }
	/**
	 * 逆向-根据始发分拣中心和目的站点获取目的分拣中心、道口号、笼车号信息
	 * @param originalDmsId
	 * @param targetSiteId
	 * @return
	 */
	@Override
	public JdResult<ReverseCrossPackageTag> getReverseCrossPackageTag(
			Integer originalDmsId, Integer targetSiteId) {
		String params = "{" + originalDmsId + "," + targetSiteId +"}";
		JdResult<ReverseCrossPackageTag> result = new JdResult<ReverseCrossPackageTag>();
		CallerInfo callerInfo = ProfilerHelper.registerInfo("DMSWEB.jsf.out.basicSecondaryWS.getReverseCrossPackageTag");
		try{
			BaseResult<ReverseCrossPackageTag> reverseResult= basicSecondaryWS.getReverseCrossPackageTag(originalDmsId,targetSiteId);
			if(null != reverseResult 
					&& BaseResult.RESULT_SUCCESS==reverseResult.getResultCode()){
                result.setData(reverseResult.getData());
                result.toSuccess(reverseResult.getMessage());
			}else if(reverseResult != null){
				result.toFail(reverseResult.getMessage());
				log.warn("jsf-fail:basicSecondaryWS.getReverseCrossPackageTag!params:"+params+",msg:返回结果"+JsonHelper.toJson(reverseResult));
            }else{
            	result.toFail("jsf-fail:basicSecondaryWS.getReverseCrossPackageTag!params:"+params+",msg:返回结果为null");
            	log.warn(result.getMessage());
            }
		}catch(Exception e){
			result.toError("jsf-exception:basicSecondaryWS.getReverseCrossPackageTag!params:"+params+",msg:"+e.getMessage());
			log.error(result.getMessage(), e);
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
	/**
	  * jsf-调用：根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
	  * @param baseDmsStore  库房
	  * @param targetSiteId  目的站点ID -- 必填
	  * @param originalDmsId 始发分拣中心ID
	  * @return
	  */
	private JdResult<CrossPackageTagNew> getCrossPackageTagByPara(
			BaseDmsStore baseDmsStore, Integer targetSiteId,
			Integer originalDmsId) {
		JdResult<CrossPackageTagNew> result = new JdResult<CrossPackageTagNew>();
		String params = "{" + originalDmsId + "," + targetSiteId +"}";
		CallerInfo callerInfo = ProfilerHelper.registerInfo("DMSWEB.jsf.out.basicSecondaryWS.getCrossPackageTagByPara");
		try{
			BaseResult<CrossPackageTagNew> crossPackageTagResult= basicSecondaryWS.getCrossPackageTagByPara(baseDmsStore, targetSiteId, originalDmsId);
			if(null != crossPackageTagResult
					&& BaseResult.SUCCESS==crossPackageTagResult.getResultCode()){
               result.setData(crossPackageTagResult.getData());
               result.toSuccess(crossPackageTagResult.getMessage());
			}else if(crossPackageTagResult != null){
				result.toFail(crossPackageTagResult.getMessage());
				log.warn("jsf-fail:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:返回结果"+JsonHelper.toJson(crossPackageTagResult));
           }else{
           		result.toFail("jsf-fail:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:返回结果为null");
           		log.warn(result.getMessage());
           }
		}catch(Exception e){
			result.toError("jsf-exception:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:"+e.getMessage());
			log.error(result.getMessage(), e);
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
	/**
	  * jsf-调用：根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
	  * @param baseDmsStore  库房
	  * @param targetSiteId  目的站点ID -- 必填
	  * @param originalDmsId 始发分拣中心ID
	  * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
	  * @return
	  */
	private JdResult<CrossPackageTagNew> queryCrossPackageTagByParam(
			BaseDmsStore baseDmsStore, Integer targetSiteId,
			Integer originalDmsId, Integer originalCrossType) {
		JdResult<CrossPackageTagNew> result = new JdResult<CrossPackageTagNew>();
		String params = "{" + originalDmsId + "," + targetSiteId + "," + originalCrossType +"}";
		CallerInfo callerInfo = ProfilerHelper.registerInfo("DMSWEB.jsf.out.baseCrossPackageTagWS.queryCrossPackageTagByParam");
		try{
			BaseResult<CrossPackageTagNew> crossPackageTagResult= baseCrossPackageTagWS.queryCrossPackageTagByParam(baseDmsStore, targetSiteId, originalDmsId, originalCrossType);
			if(null != crossPackageTagResult 
					&& BaseResult.SUCCESS==crossPackageTagResult.getResultCode()){
                result.setData(crossPackageTagResult.getData());
                result.toSuccess(crossPackageTagResult.getMessage());
			}else if(crossPackageTagResult != null){
				result.toFail(crossPackageTagResult.getMessage());
				log.warn("jsf-fail:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:返回结果"+JsonHelper.toJson(crossPackageTagResult));
            }else{
            	result.toFail("jsf-fail:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:返回结果为null");
            	log.warn(result.getMessage());
            }
		}catch(Exception e){
			result.toError("jsf-exception:baseCrossPackageTagWS.queryCrossPackageTagByParam!params:"+params+",msg:"+e.getMessage());
			log.error(result.getMessage(), e);
			Profiler.functionError(callerInfo);
		}finally{
			Profiler.registerInfoEnd(callerInfo);
		}
		return result;
	}
	/**
	  * 根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
	  * @param baseDmsStore  库房
	  * @param targetSiteId  目的站点ID -- 必填
	  * @param originalDmsId 始发分拣中心ID
	  * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
	  * @return
	  */
	@Override
	public JdResult<CrossPackageTagNew> queryCrossPackageTag(
			BaseDmsStore baseDmsStore, Integer targetSiteId,
			Integer originalDmsId, Integer originalCrossType) {
		if(!useNewCrossPackageTagWS){
			return this.getCrossPackageTagByPara(baseDmsStore, targetSiteId, originalDmsId);
		}
		//航空或者航填，调用新接口
		if(Constants.ORIGINAL_CROSS_TYPE_AIR.equals(originalCrossType)
				|| Constants.ORIGINAL_CROSS_TYPE_FILL.equals(originalCrossType)){
			JdResult<CrossPackageTagNew> result = this.queryCrossPackageTagByParam(baseDmsStore, targetSiteId, originalDmsId, originalCrossType);
			//如果新的大全表没有数据 则 继续查询老大全表
			if(result.isSucceed() && result.getData()!= null && result.getData().getOriginalDmsId()!=null){
				return result;
			}else{
				JdResult<CrossPackageTagNew> normalResult = this.getCrossPackageTagByPara(baseDmsStore, targetSiteId, originalDmsId);
				if(normalResult.isSucceed() && normalResult.getData()!=null
						&& normalResult.getData().getOriginalDmsId()!=null && normalResult.getData().getDestinationDmsId()!=null){
					//始发和目的相等维护了道口 可以返回陆运大全表
					if(normalResult.getData().getOriginalDmsId().equals(normalResult.getData().getDestinationDmsId())){
						return normalResult;
					}
				}
				return result;
			}
		}else{
			return this.getCrossPackageTagByPara(baseDmsStore, targetSiteId, originalDmsId);
		}
	}
	/**
	  * 打印业务-根据库房、目的站点ID、始发分拣中心ID、始发道口类型，获取取包裹标签打印信息
	  * 先调用正向业务，获取不到数据会调用逆向接口
	  * @param baseDmsStore  库房
	  * @param targetSiteId  目的站点ID -- 必填
	  * @param originalDmsId 始发分拣中心ID
	  * @param originalCrossType 始发道口类型  1 -- 普通 2 -- 航空 3 -- 填仓
	  * @return
	  */
	@Override
	public JdResult<CrossPackageTagNew> queryCrossPackageTagForPrint(
			BaseDmsStore baseDmsStore, Integer targetSiteId,
			Integer originalDmsId, Integer originalCrossType) {
		//1、先获取正向
		JdResult<CrossPackageTagNew> result = this.queryCrossPackageTag(baseDmsStore, targetSiteId, originalDmsId, originalCrossType);
		//2、正向调用失败,或者返回数据为空，调用一次逆向接口
		if(!result.isSucceed() || result.getData() == null){
			JdResult<ReverseCrossPackageTag> reverseResult = this.getReverseCrossPackageTag(originalDmsId, targetSiteId);
			if(reverseResult.isSucceed() && reverseResult.getData() != null){
				CrossPackageTagNew tag=new CrossPackageTagNew();
	            tag.setTargetSiteName(reverseResult.getData().getTargetStoreName());
	            tag.setTargetSiteId(reverseResult.getData().getTargetStoreId());
	            tag.setOriginalCrossCode(reverseResult.getData().getOriginalCrossCode());
	            tag.setOriginalDmsName(reverseResult.getData().getOriginalDmsName());
	            tag.setOriginalTabletrolleyCode(reverseResult.getData().getOriginalTabletrolleyCode());
	            tag.setOriginalDmsId(reverseResult.getData().getOriginalDmsId());
	            tag.setDestinationCrossCode(reverseResult.getData().getDestinationCrossCode());
	            tag.setDestinationDmsName(reverseResult.getData().getDestinationDmsName());
	            tag.setDestinationTabletrolleyCode(reverseResult.getData().getDestinationTabletrolleyCode());
	            tag.setDestinationDmsId(reverseResult.getData().getDestinationDmsId());
	            result.toSuccess();
	            result.setData(tag);
			}else{
				result.setCode(reverseResult.getCode());
				result.setMessageCode(reverseResult.getMessageCode());
				result.setMessage(reverseResult.getMessage());
			}
        }
		return result;
	}

	/**
	 * 根据商家id获得返单信息
	 * @param busiId
	 * @return
	 */
	@Override
	@JProfiler(jKey = "DMS.BASE.BaseMinorManagerImpl.getBaseTraderReturnListByTraderId", mState = {JProEnum.TP, JProEnum.FunctionError})
	public ResponseDTO<List<BasicTraderReturnDTO>> getBaseTraderReturnListByTraderId(Integer busiId){
		return basicTraderReturnAPI.getBaseTraderReturnListByTraderId(busiId);
	}

	@Override
	public CrossPackageTagNew queryNonDmsSiteCrossPackageTagForPrint(Integer targetSiteId, Integer originalDmsId) {
		try {
			if (targetSiteId != null && originalDmsId != null) {
				BaseStaffSiteOrgDto receiveSiteDto = baseMajorManager.getBaseSiteBySiteId(targetSiteId);
				if(receiveSiteDto == null){
					log.error("[箱号/批次号打印]获取基础资料信息,根据targetSiteId+"+targetSiteId+"未获得到站点信息");
					return null;
				}
				if (!Constants.DMS_SITE_TYPE.equals(receiveSiteDto.getSiteType()) && !Constants.FINANCIAL_SPECIAL_SITE_TYPE.equals(receiveSiteDto.getSiteType())) {
					BaseDmsStore baseDmsStore = new BaseDmsStore();
					JdResult<CrossPackageTagNew> result = queryCrossPackageTagForPrint(baseDmsStore, targetSiteId, originalDmsId, Constants.ORIGINAL_CROSS_TYPE_GENERAL);
					if (result.isSucceed()) {
						return result.getData();
					} else {
						log.warn("[箱号/批次号打印]获取基础资料信息！Message:" + result.getMessage());
					}
				}
			}

		} catch (Exception e) {
			log.error("[箱号/批次号打印]获取基础资料信息", e);
		}
		return null;
	}


}
