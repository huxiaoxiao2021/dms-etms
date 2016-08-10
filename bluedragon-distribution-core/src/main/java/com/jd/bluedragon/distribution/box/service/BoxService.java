package com.jd.bluedragon.distribution.box.service;

import java.util.List;

import com.jd.bluedragon.distribution.box.domain.Box;

public interface BoxService {

    Integer add(Box box);

    /** 批量生成箱子信息 */
    List<Box> batchAdd(Box box);
    
    /**重打*/
    Integer reprint(Box box);
    
    List<Box> findBoxes(Box box);

    Box findBoxByCode(String boxCode);
    
    Box findBoxByBoxCode(Box box);

    List<Box> findBoxesBySite(Box box);

    Integer updateStatusByCodes(Box box);

    Integer updateVolumeByCode(Box box);
    /**  
     * 查询缓存箱号
     */
    Box findBoxCacheByCode(String boxCode);
    
    /**  
     * 删除缓存箱号
     */
    Long delboxCodeCache(String boxCode);

    /***
     * 批量更新箱号的状态
     * @param boxCodes 箱号列表
     * @param boxStatus 箱号状态
     * @return
     */
    Integer batchUpdateStatus(List<String> boxCodes, Integer boxStatus);
}
