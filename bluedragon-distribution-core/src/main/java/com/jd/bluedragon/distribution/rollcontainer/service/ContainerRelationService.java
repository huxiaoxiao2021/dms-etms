package com.jd.bluedragon.distribution.rollcontainer.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.Pager;
import com.jd.bluedragon.distribution.api.response.ContainerRelationResponse;
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
    
    /**
     * 根据rfid获取对应的箱号
     * @param containerCode
     * @return
     */
    public String getBoxCodeByContainerCode(String containerCode);


    /**
     * 根据 箱号 发货状态 站点编码查询 分页ContainerRelation
     * @param boxCode
     * @param siteCode
     * @param dmsId
     * @param sendStatus
     * @param startTime
     * @param endTime
     * @param pageNo
     * @param pageSize
     * @return
     */
    Pager<List<ContainerRelation>> getContainerRelationPager(String boxCode,
                                                               String siteCode,
                                                               Integer dmsId,
                                                               Integer sendStatus,
                                                               String startTime,
                                                               String endTime,
                                                               Integer pageNo,
                                                               Integer pageSize);

}
