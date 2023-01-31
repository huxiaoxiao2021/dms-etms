package com.jd.bluedragon.distribution.abnormal.service.impl;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.dms.job.ConcurrentJobHandler;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.etms.waybill.domain.Waybill;
/**
 * 滞留上报-并发校验
 * @author wuyoude
 *
 */
public class CheckStrandReportJobHandler extends ConcurrentJobHandler<List<String>, List<String>>{
	private final Logger log = LoggerFactory.getLogger(CheckStrandReportJobHandler.class);

    @Autowired
    private WaybillQueryManager waybillQueryManager;
    
	public CheckStrandReportJobHandler(ExecutorService executorService) {
		super(executorService);
	}
	
    @Value("${beans.CheckStrandReportJobHandler.maxJobThreadNum:10}")
    private int maxJobThreadNum;
    
    @Value("${beans.CheckStrandReportJobHandler.minPerJobWaybillNum:200}")
    private int minPerJobWaybillNum;

	@Autowired
	private WaybillService waybillService;
    
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
	 * 校验是否特快送，特快送包裹加入列表中
	 */
	@Override
	protected List<String> doJob(List<String> packageCodes) {
		if(log.isInfoEnabled()) {
			log.info("checkStrandReportJobHandler-校验开始");
		}
		List<String> result = Lists.newArrayList();
		if(CollectionUtils.isEmpty(packageCodes)) {
			return result;
		}
		for(String packageCode : packageCodes) {
			String waybillCode = WaybillUtil.getWaybillCode(packageCode);
			Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
			if(waybill != null 
					&& BusinessUtil.isVasWaybill(waybill.getWaybillSign()) &&  waybillService.isLuxurySecurityVosWaybill(waybillCode)) {
				result.add(packageCode);
				break;
			}
		}
		if(log.isInfoEnabled()) {
			log.info("checkStrandReportJobHandler-校验结束！");
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
