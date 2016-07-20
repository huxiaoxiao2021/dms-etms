package com.jd.bluedragon.distribution.popPrint.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.PopQueueQuery;
import com.jd.bluedragon.distribution.popPrint.domain.PopQueue;
/**
 * 
* 类描述： POP收货排队号管理DAO
* 创建者： libin
* 项目名称： bluedragon-distribution-core
* 创建时间： 2013-1-14 下午3:04:49
* 版本号： v1.0
 */
public class PopQueueDao extends BaseDao<PopQueue> {
	public static final String namespace = PopQueueDao.class.getName();
/**
 * 
* 方法描述 : 根据分拣中心获取当天的最大排序号
* 创建者：libin 
* 创建时间： 2013-1-14 下午3:05:10
* @param createSiteCode
* @return Integer
 */
	public int getCurrentWaitNo(Integer createSiteCode) {
		Integer count = (Integer) this.getSqlSession().selectOne(namespace + ".getCurrentWaitNo", createSiteCode);
		if(count==null){
			count=0;	
		}
		return count.intValue();
	}
	
	public int insertPopQueue(PopQueue popQueue){
		int m =this.getSqlSession().insert(namespace+".insertPopQueue", popQueue);
		return m;
		
	}
	
	public int updatePopQueue(PopQueue popQueue){
		int m =this.getSqlSession().update(namespace+".updatePopQueue", popQueue);
		return m;
		
	}
	
	public PopQueue  getPopQueueByQueueNo(String queueNo){
		PopQueue popQueue=(PopQueue)this.getSqlSession().selectOne(namespace+".getPopQueueByQueueNo", queueNo);
		return popQueue;
		
	}
	
	public List<PopQueue> getPopQueueList(PopQueueQuery popQueueQuery){
		@SuppressWarnings("unchecked")
		List<PopQueue> list = this.getSqlSession().selectList(namespace+".getPopQueueList",popQueueQuery);
		return list; 
	}
	
	public int getCount(PopQueueQuery popQueueQuery){
		 Integer count = (Integer) this.getSqlSession().selectOne(namespace+".getCount", popQueueQuery);
	               
		return count;
	}

}
