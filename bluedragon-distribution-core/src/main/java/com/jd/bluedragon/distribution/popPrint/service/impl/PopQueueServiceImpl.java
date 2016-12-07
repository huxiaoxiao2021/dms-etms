package com.jd.bluedragon.distribution.popPrint.service.impl;

import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.dao.PopQueueDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class PopQueueServiceImpl implements PopQueueService {
	private static final Log log = LogFactory.getLog(PopQueueServiceImpl.class);
	@Autowired
	private PopQueueDao popQueueDao;
	@Override
	public int getCurrentWaitNo(Integer createSiteCode) {
		log.debug("分拣中心编号"+createSiteCode+"取当前最大排队号");
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
