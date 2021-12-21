package com.jd.bluedragon.distribution.arAbnormal;

import java.util.List;
import java.util.concurrent.ExecutorService;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.dms.job.ConcurrentJobHandler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.etms.waybill.domain.Waybill;

public class CheckCanAirToRoadJobHandler extends ConcurrentJobHandler<List<String>, ArAbnormalResponse>{

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
	public CheckCanAirToRoadJobHandler(ExecutorService executorService) {
		super(executorService);
	}
	
    @Value("${beans.ArAbnormalServiceImpl.maxJobThreadNum:5}")
    private int maxJobThreadNum;
    
    @Value("${beans.ArAbnormalServiceImpl.minPerJobWaybillNum:100}")
    private int minPerJobWaybillNum;
    
	@Override
	protected List<List<String>> split(List<String> job) {
		if(job != null && job.size() > minPerJobWaybillNum) {
			return CollectionHelper.splitList(job, maxJobThreadNum,minPerJobWaybillNum);
		}
		List<List<String>> jobList = Lists.newArrayList();
		if(job != null) {
			jobList.add(job);
		}
		return jobList;
	}

	@Override
	protected ArAbnormalResponse doJob(List<String> waybillCodes) {
		ArAbnormalResponse result = new ArAbnormalResponse();
		for(String waybillCode : waybillCodes) {
			Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
			if(waybill == null) {
				result.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
				result.setMessage("单号"+waybillCode+"在运单系统不存在！");
				return result;
			}
			if(!BusinessUtil.checkCanAirToRoad(waybill.getWaybillSign(), waybill.getSendPay())) {
				result.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
				result.setMessage(HintService.getHint(HintCodeConstants.AIR_TO_ROAD_NOT_ALLOWD));
				return result;
			}
		}
		result.setCode(ArAbnormalResponse.CODE_OK);
		result.setMessage(ArAbnormalResponse.MESSAGE_OK);
		return result;
	}
	/**
	 * 有一个失败，整个结果就认为是失败
	 */
	@Override
	protected ArAbnormalResponse merge(List<ArAbnormalResponse> resultList) {
		ArAbnormalResponse result = new ArAbnormalResponse();
		if(CollectionUtils.isEmpty(resultList)) {
			result.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
			result.setMessage("校验操作航空转陆运执行异常，请稍后重试！");
			return result;
		}
		for(ArAbnormalResponse tmp : resultList) {
			if(!ArAbnormalResponse.CODE_OK.equals(tmp.getCode())) {
				return tmp;
			}
		}
		result.setCode(ArAbnormalResponse.CODE_OK);
		result.setMessage(ArAbnormalResponse.MESSAGE_OK);  
		return result;
	}

}
