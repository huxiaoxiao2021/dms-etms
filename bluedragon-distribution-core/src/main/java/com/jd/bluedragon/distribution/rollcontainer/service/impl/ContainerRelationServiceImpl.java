package com.jd.bluedragon.distribution.rollcontainer.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.rollcontainer.dao.ContainerRelationDao;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.service.ContainerRelationService;

@Service("containerRelationService")
public class ContainerRelationServiceImpl implements ContainerRelationService{
	
	@Autowired
    private ContainerRelationDao containerRelationDao;

	@Override
	public int addContainerRelation(ContainerRelation containerRelation) {
		// TODO Auto-generated method stub
		return containerRelationDao.addContainerRelation(containerRelation);
	}
	
	public ContainerRelation getContainerRelation(String containerCode){
		return containerRelationDao.getContainerRelation(containerCode);
	}
	
	public int updateContainerRelationByCode(ContainerRelation containerRelation){
		return containerRelationDao.updateContainerRelationByCode(containerRelation);
	}
	
	
}
