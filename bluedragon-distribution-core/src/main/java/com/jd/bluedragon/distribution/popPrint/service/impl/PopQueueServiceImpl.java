package com.jd.bluedragon.distribution.popPrint.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.dao.PopQueueDao;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
import com.jd.bluedragon.distribution.popPrint.service.PopQueueService;
@Service
public class PopQueueServiceImpl implements PopQueueService {
	private static final Log log = LogFactory.getLog(PopQueueServiceImpl.class);
	@Autowired
	private PopQueueDao popQueueDao;
	@Override
    @Profiled(tag = "PopQueueService.getCurrentWaitNo")
	public int getCurrentWaitNo(Integer createSiteCode) {
		log.debug("分拣中心编号"+createSiteCode+"取当前最大排队号");
		return this.popQueueDao.getCurrentWaitNo(createSiteCode);
	}
	@Override
	@Profiled(tag = "PopQueueService.insertPopQueue")
	public int insertPopQueue(PopQueue popQueue) {
		
		return this.popQueueDao.insertPopQueue(popQueue);
	}
	@Override
	@Profiled(tag = "PopQueueService.updatePopQueue")
	public int updatePopQueue(PopQueue popQueue) {
		return this.popQueueDao.updatePopQueue(popQueue);
	}
	@Override
	@Profiled(tag = "PopQueueService.getPopQueueByQueueNo")
	public PopQueue getPopQueueByQueueNo(String queueNo) {
		return this.popQueueDao.getPopQueueByQueueNo(queueNo);
	}
	@Override
	@Profiled(tag = "PopQueueService.getPopQueueList")
	public List<PopQueue> getPopQueueList(PopQueueQuery popQueueQuery) {
		
		return this.popQueueDao.getPopQueueList(popQueueQuery);
	}
	@Override
	@Profiled(tag = "PopQueueService.getCount")
	public int getCount(PopQueueQuery popQueueQuery) {
		return this.popQueueDao.getCount(popQueueQuery);
	}

}
