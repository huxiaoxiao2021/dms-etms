package com.jd.bluedragon.distribution.rollcontainer.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.rollcontainer.dao.ContainerRelationDao;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;

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
	
	public ContainerRelation getContainerRelation(String containerCode){
		return containerRelationDao.getContainerRelation(containerCode);
	}
	
	public int updateContainerRelationByCode(ContainerRelation containerRelation){
		return containerRelationDao.updateContainerRelationByCode(containerRelation);
	}
	
	@Override
	public String getBoxCodeByContainerCode(String containerCode) {
		return kvIndexDao.queryRecentOneByKeyword(containerCode);
	}
	
}
