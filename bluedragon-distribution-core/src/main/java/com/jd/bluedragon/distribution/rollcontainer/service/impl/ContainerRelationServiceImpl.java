package com.jd.bluedragon.distribution.rollcontainer.service.impl;


import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.rollcontainer.dao.ContainerRelationDao;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("containerRelationService")
public class ContainerRelationServiceImpl implements ContainerRelationService{
	
	@Autowired
    private ContainerRelationDao containerRelationDao;
	
	@Autowired
    private KvIndexDao kvIndexDao;

	@Override
	public int addContainerRelation(ContainerRelation containerRelation) {
		return containerRelationDao.addContainerRelation(containerRelation);
	}
	
	public ContainerRelation getContainerRelation(String containerCode, Integer dmsId){
		Map<String,Object> param = new HashMap<String, Object>(2);
		param.put("containerCode", containerCode);
		param.put("dmsId", dmsId);
		return containerRelationDao.getContainerRelation(param);
	}
	
	public int updateContainerRelationByCode(ContainerRelation containerRelation){
		return containerRelationDao.updateContainerRelationByCode(containerRelation);
	}
	
	@Override
	public String getBoxCodeByContainerCode(String containerCode) {
		return kvIndexDao.queryRecentOneByKeyword(containerCode);
	}

	@Override
	public Pager<List<ContainerRelation>> getContainerRelationPager(String boxCode,
																	  String siteCode,
																	  Integer dmsId,
																	  Integer sendStatus,
																	  String startTime,
																	  String endTime,
																	  Integer startIndex,
																	  Integer pageSize) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("boxCode", boxCode);
		param.put("siteCode", siteCode);
		param.put("dmsId", dmsId);
		param.put("sendStatus", sendStatus);
		param.put("startTime", startTime);
		param.put("endTime", endTime);
		param.put("startIndex", startIndex);
		param.put("pageSize", pageSize);
		List<ContainerRelation> containerRelationList = containerRelationDao.getContainerRelationByModel(param);
		Integer count = containerRelationDao.getContainerRelationCountByModel(param);
		Pager<List<ContainerRelation>> pager = new Pager<List<ContainerRelation>>();
		pager.setData(containerRelationList);
		pager.setTotalSize(count);
		pager.setPageSize(pageSize);
		return pager;
	}

	/**
	 * 根据箱号获取ContainerRelation
	 * @param boxCode
	 * @return
	 */
	@Override
	public List<ContainerRelation> getContainerRelationByBoxCode(String boxCode){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("boxCode", boxCode);
		return containerRelationDao.getContainerRelationByModel(param);
	}


}
