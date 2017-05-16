package com.jd.bluedragon.distribution.rollcontainer.service;

import java.util.List;
import java.util.Map;

import com.jd.bluedragon.distribution.gantry.domain.GantryDevice;
import com.jd.bluedragon.distribution.rollcontainer.domain.RollContainer;

public interface RollContainerService {
	
	/***
     * 根据条件查询符合条件的周转箱
     * @param param 条件
     * @return
     */
    public List<RollContainer> getRollContainer(Map<String, Object> param);
    
    /**
     * 添加周转箱信息
     * @param rollContainer
     * @return
     */
    public int addRollContainer(RollContainer rollContainer);
    
    /**
     * 更新周转箱信息
     * @param rollContainer
     * @return
     */
    public int updateRollContainerByCode(RollContainer rollContainer);
    
    /**
     * 根据周转箱编号查询信息
     * @return
     */
    public RollContainer getRollContainerByCode(String code);
    
}
