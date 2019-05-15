package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.BoxSystemTypeEnum;

import java.util.List;

public interface BoxService {

    Integer add(Box box);

    /** 批量生成箱子信息 */
    List<Box> batchAdd(Box box);

    /** 批量生成箱子信息 */
    List<Box> batchAddNew(Box box, BoxSystemTypeEnum systemType);

    /**重打*/
    Integer reprint(Box box);
    
//    List<Box> findBoxes(Box box);

    Box findBoxByCode(String boxCode);
    
    Box findBoxByBoxCode(Box box);

    List<Box> findBoxesBySite(Box box);

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
     * 更新箱号状态的缓存
     * @param boxCode 箱号
     * @param operateSiteCode 操作箱号的单位，用于区分中转发货的箱号状态
     * @param boxStatus 箱号状态
     * @return
     */
    Boolean updateBoxStatusRedis(String boxCode, Integer operateSiteCode, Integer boxStatus);

    /***
     * 获取箱号状态的缓存
     * @param boxCode 箱号
     * @param operateSiteCode 操作箱号的单位，用于区分中转发货的箱号状态
     * @return
     */
    Integer getBoxStatusFromRedis(String boxCode, Integer operateSiteCode);

    /***
     * 判断箱号是否已经发货
     * @param boxCode 箱号
     * @param operateSiteCode 操作箱号的单位，用于区分中转发货的箱号状态
     * @return
     */
    Boolean checkBoxIsSent(String boxCode, Integer operateSiteCode);
}
