package com.jd.bluedragon.distribution.box.service;

import com.jd.bluedragon.distribution.api.request.BoxRequest;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.api.request.box.BoxTypeReq;
import com.jd.bluedragon.distribution.api.response.BoxResponse;
import com.jd.bluedragon.distribution.api.response.box.BoxTypeDto;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.domain.UpdateBoxReq;
import com.jd.bluedragon.dms.utils.RecycleBasketTypeEnum;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;
import java.util.Map;

public interface BoxService {

    Integer add(Box box);

    long newBoxId();

    /** 批量生成箱子信息 */
    List<Box> batchAdd(Box box);

    /** 批量生成箱子信息 */
    List<Box> batchAddNew(Box box, String systemType);

    /**重打*/
    Integer reprint(Box box);

    Box findBoxByCode(String boxCode);
    
    Box findBoxByBoxCode(Box box);

    List<String> generateRecycleBasketCode(int quantity, RecycleBasketTypeEnum typeEnum);

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
    Boolean updateBoxStatusRedis(String boxCode, Integer operateSiteCode, Integer boxStatus, String userErp);

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


    /**
     * 更新箱状态
     * @param boxReq
     * @return
     */
    Boolean updateBoxStatus(BoxReq boxReq);

    /**
     * 箱号生成服务
     * @param request
     * @param systemType
     * @param isNew
     * @return
     */
    BoxResponse commonGenBox(BoxRequest request, String systemType, boolean isNew);


    /**
     * 生成没有 始发目的场地信息的箱号
     * @param request
     * @return
     */
    BoxResponse genBoxWithoutSiteInfo(BoxRequest request);

    /**
     * 变更箱号信息
     * @param request
     * @return
     */
    BoxResponse updateBox(UpdateBoxReq request);

    void computeRouter(List<Map.Entry<Integer, String>> router);

    /**
     * 判断小件集包功能是否已经推广给当前场地
     * @param siteCode
     * @param orgId
     * @return
     */
    boolean checkCollectPackageIfReleasedForSite(Integer orgId,Integer siteCode);

    /**
     * 查询箱类型
     * @param boxTypeReq 查询箱类型入参
     * @return 箱号类型列表
     * @author fanggang7
     * @time 2023-10-24 14:14:24 周二
     */
    Result<List<BoxTypeDto>> getBoxTypeList(BoxTypeReq boxTypeReq);
}
