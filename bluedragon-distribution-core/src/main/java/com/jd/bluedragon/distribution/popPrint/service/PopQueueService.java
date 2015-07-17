package com.jd.bluedragon.distribution.popPrint.service;

import java.util.List;

import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;

public interface PopQueueService {
	public int getCurrentWaitNo(Integer createSiteCode);
	public int insertPopQueue(PopQueue popQueue);
	public int updatePopQueue(PopQueue popQueue);
	public PopQueue  getPopQueueByQueueNo(String flowNumber);
	public List<PopQueue> getPopQueueList(PopQueueQuery popQueueQuery );
	public int getCount(PopQueueQuery popQueueQuery);
}
