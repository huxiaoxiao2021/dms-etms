package com.jd.bluedragon.distribution.rollcontainer.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;

public interface ContainerRelationService {
	
    /**
     * 关系绑定
     * @param containerRelation
     * @return
     */
    public int addContainerRelation(ContainerRelation containerRelation);
    
    /**
     * 根据编号containerCode查询对应关系
     * @param containerCode
     * @return
     */
    public ContainerRelation getContainerRelation(String containerCode);
    
    public int updateContainerRelationByCode(ContainerRelation containerRelation);
    
    
    
    
}
