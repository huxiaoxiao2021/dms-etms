package com.jd.bluedragon.distribution.rollcontainer.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.gantry.dao.GantryDeviceDao;
import com.jd.bluedragon.distribution.rollcontainer.dao.RollContainerDao;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;
import com.jd.bluedragon.distribution.rollcontainer.service.RollContainerService;

@Service("rollContainerService")
public class RollContainerServiceImpl implements RollContainerService{
	
	@Autowired
    private RollContainerDao rollContainerDao;
	
	@Override
	public List<RollContainer> getRollContainer(Map<String, Object> param) {
		// TODO Auto-generated method stub
		return rollContainerDao.getRollContainerPage(param);
	}
	
	public int addRollContainer(RollContainer rollContainer){
		return rollContainerDao.addRollContainer(rollContainer);
	}
	
	public int updateRollContainerByCode(RollContainer rollContainer){
		return rollContainerDao.updateRollContainerByCode(rollContainer);
	}

	@Override
	public RollContainer getRollContainerByCode(String code) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
