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
/**
 * 校验能否航空转陆运，返回非航空运单列表
 * @author wuyoude
 *
 */
public class CheckCanAirToRoadJobHandler extends ConcurrentJobHandler<List<String>, List<String>>{

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
	public CheckCanAirToRoadJobHandler(ExecutorService executorService) {
		super(executorService);
	}
	
    @Value("${beans.ArAbnormalServiceImpl.maxJobThreadNum:10}")
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
	/**
	 * 校验是否航空单，非航空单加入列表中
	 */
	@Override
	protected List<String> doJob(List<String> waybillCodes) {
		List<String> result = Lists.newArrayList();
		if(CollectionUtils.isEmpty(waybillCodes)) {
			return result;
		}
		for(String waybillCode : waybillCodes) {
			Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
			if(waybill != null 
					&& !BusinessUtil.checkCanAirToRoad(waybill.getWaybillSign(), waybill.getSendPay())) {
				result.add(waybillCode);
			}
		}
		return result;
	}
	/**
	 * 将多个列表合并成一个
	 */
	@Override
	protected List<String> merge(List<List<String>> resultList) {
		List<String> result = Lists.newArrayList();
		if(CollectionUtils.isEmpty(resultList)) {
			return result;
		}
		for(List<String> tmp : resultList) {
			if(CollectionUtils.isNotEmpty(tmp)) {
				result.addAll(tmp);
			}
		}
		return result;
	}

}
