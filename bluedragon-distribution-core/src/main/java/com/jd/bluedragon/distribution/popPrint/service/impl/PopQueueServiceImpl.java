package com.jd.bluedragon.distribution.popPrint.service.impl;

import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.dao.PopQueueDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PopQueueServiceImpl implements PopQueueService {
	private static final Logger log = LoggerFactory.getLogger(PopQueueServiceImpl.class);
	@Autowired
	private PopQueueDao popQueueDao;
	@Override
	public int getCurrentWaitNo(Integer createSiteCode) {
		if(log.isDebugEnabled()){
			log.debug("分拣中心编号{}取当前最大排队号",createSiteCode);
		}
		return this.popQueueDao.getCurrentWaitNo(createSiteCode);
	}
	@Override
	public int insertPopQueue(PopQueue popQueue) {
		
		return this.popQueueDao.insertPopQueue(popQueue);
	}
	@Override
	public int updatePopQueue(PopQueue popQueue) {
		return this.popQueueDao.updatePopQueue(popQueue);
	}
	@Override
	public PopQueue getPopQueueByQueueNo(String queueNo) {
		return this.popQueueDao.getPopQueueByQueueNo(queueNo);
	}
	@Override
	public List<PopQueue> getPopQueueList(PopQueueQuery popQueueQuery) {

		return this.popQueueDao.getPopQueueList(popQueueQuery);
	}
	@Override
	public int getCount(PopQueueQuery popQueueQuery) {
		return this.popQueueDao.getCount(popQueueQuery);
	}

}
