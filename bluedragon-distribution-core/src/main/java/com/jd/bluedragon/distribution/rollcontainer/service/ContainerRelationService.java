package com.jd.bluedragon.distribution.rollcontainer.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelation;
import com.jd.bluedragon.distribution.rollcontainer.domain.ContainerRelationCondition;
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
    public ContainerRelation getContainerRelation(String containerCode, Integer dmsId);
    
    public int updateContainerRelationByCode(ContainerRelation containerRelation);
    
    /**
     * 根据rfid获取对应的箱号
     * @param containerCode
     * @return
     */
    public String getBoxCodeByContainerCode(String containerCode);


    /***
     * 根据 ContainerRelationCondition查询 分页ContainerRelation
     * @param condition
     * @param pager
     * @return
     */
    Pager<List<ContainerRelation>> getContainerRelationPager(ContainerRelationCondition condition,
                                                             Pager<List<ContainerRelation>> pager);
    /***
     * 根据 ContainerRelationCondition查询ContainerRelation list
     * @param condition
     * @return
     */
    List<ContainerRelation> getContainerRelationByModel(ContainerRelationCondition condition);

    List<ContainerRelation> getContainerRelationByBoxCode(String boxCode);
}
