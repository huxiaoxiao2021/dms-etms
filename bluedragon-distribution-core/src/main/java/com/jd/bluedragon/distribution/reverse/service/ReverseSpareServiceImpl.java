package com.jd.bluedragon.distribution.reverse.service;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.dao.ReverseSpareDao;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.sorting.service.SortingService;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-28 下午05:21:37
 * 
 *             逆向备件库按商品退货分拣处理服务实现
 */
@Service("reverseSpareService")
public class ReverseSpareServiceImpl implements ReverseSpareService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SortingService sortingService;

	@Autowired
	private ReverseSpareDao reverseSpareDao;

	@Override
	@Profiled(tag = "ReverseSpareServiceImpl.batchAddSorting")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int batchAddSorting(List<Sorting> sortings,
			List<ReverseSpare> reverseSpares) {
		if (sortings == null || sortings.size() <= 0) {
			this.logger
					.info("ReverseSpareServiceImpl batchAddSorting --> 传入参数不合法");
			return Constants.RESULT_FAIL;
		}
		 
		Collections.sort(sortings);
		this.sortingService.taskToSorting(sortings);

		this.batchAddOrUpdate(reverseSpares);

		return Constants.RESULT_SUCCESS;
	}

	@Override
	@Profiled(tag = "ReverseSpareServiceImpl.batchAddOrUpdate")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int batchAddOrUpdate(List<ReverseSpare> reverseSpares) {
		if (reverseSpares == null || reverseSpares.size() <= 0) {
			this.logger
					.info("ReverseSpareServiceImpl batchAddOrUpdate --> 传入参数不合法");
			return Constants.RESULT_FAIL;
		}
		for (ReverseSpare reverseSpare : reverseSpares) {
			if (Constants.NO_MATCH_DATA == this.reverseSpareDao.update(
					ReverseSpareDao.namespace, reverseSpare).intValue()) {
				this.reverseSpareDao.add(ReverseSpareDao.namespace,
						reverseSpare);
			}
		}

		return Constants.RESULT_SUCCESS;
	}

	@Override
	public Sorting querySortingBySpareCode(Sorting sorting) {
		List<Sorting> sortings = this.sortingService.queryByCode(sorting);
		if (sortings != null && !sortings.isEmpty()) {
			return sortings.get(0);
		}
		return null;
	}

	@Override
	public ReverseSpare queryBySpareCode(String spwareCode) {
		return this.reverseSpareDao.queryBySpareCode(spwareCode);
	}
	
	@Override
	public List<ReverseSpare> queryByWayBillCode(String waybillCode,String sendCode) {
		ReverseSpare rs = new ReverseSpare();
		rs.setSendCode(sendCode);
		rs.setWaybillCode(waybillCode);
		return this.reverseSpareDao.queryByWayBillCode(rs);
	}
	
	@Override
	public List<ReverseSpare> queryBySpareTranCode(String spareTranCode) {
		return this.reverseSpareDao.queryBySpareTranCode(spareTranCode);
	}


}
