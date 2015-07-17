package com.jd.bluedragon.distribution.reverse.service;

import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.reverse.dao.LossOrderDao;
import com.jd.bluedragon.distribution.reverse.domain.LossOrder;

@Service("lossOrderService")
public class LossOrderServiceImpl implements LossOrderService {

    @Autowired
	private LossOrderDao lossOrderDao;

	@Profiled(tag = "LossOrderService.add")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(LossOrder lossOrder) {
		return this.lossOrderDao.add(LossOrderDao.namespace, lossOrder);
    }

}
