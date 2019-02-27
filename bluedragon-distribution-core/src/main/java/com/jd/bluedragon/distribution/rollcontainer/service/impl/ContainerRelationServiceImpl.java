package com.jd.bluedragon.distribution.rollcontainer.service.impl;


import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelationCondition;
import com.jd.bluedragon.utils.ObjectMapHelper;
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
	public Pager<List<ContainerRelation>> getContainerRelationPager(ContainerRelationCondition condition,
																	Pager<List<ContainerRelation>> pager) {
		if(pager == null){
            pager = new Pager<List<ContainerRelation>>(condition.getPage(), condition.getPageSize());
        }
		Map<String, Object> param = convertContainerRelationCondition2Map(condition, pager);
		List<ContainerRelation> containerRelationList = containerRelationDao.getContainerRelationByModel(param);
		Integer count = containerRelationDao.getContainerRelationCountByModel(param);
		pager.setData(containerRelationList);
		pager.setTotalSize(count);
		return pager;
	}

	@Override
	public List<ContainerRelation> getContainerRelationByModel(ContainerRelationCondition condition){
		Map<String, Object> param = convertContainerRelationCondition2Map(condition, null);
		List<ContainerRelation> containerRelationList = containerRelationDao.getContainerRelationByModel(param);
		return containerRelationList;
	}
	private Map<String, Object> convertContainerRelationCondition2Map(ContainerRelationCondition condition,
																	  Pager<List<ContainerRelation>> pager){

		Map<String, Object> param = ObjectMapHelper.convertObject2Map(condition);
		if(pager != null){
			param.put("startIndex", pager.getStartIndex());
			param.put("pageSize", pager.getPageSize());
		}
		return param;
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
