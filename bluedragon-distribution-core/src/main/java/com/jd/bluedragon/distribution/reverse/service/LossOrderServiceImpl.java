package com.jd.bluedragon.distribution.reverse.service;

import com.jd.bluedragon.distribution.reverse.dao.LossOrderDao;
import com.jd.bluedragon.distribution.reverse.domain.LossOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service("lossOrderService")
public class LossOrderServiceImpl implements LossOrderService {

    @Autowired
	private LossOrderDao lossOrderDao;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(LossOrder lossOrder) {
		return this.lossOrderDao.add(LossOrderDao.namespace, lossOrder);
    }

}
