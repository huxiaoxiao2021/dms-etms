package com.jd.bluedragon.distribution.batch.service;

import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;
import com.jd.bluedragon.distribution.batch.domain.BatchSendResponse;

import java.util.Date;
import java.util.List;

/**
 * 波次发货状态服务
 * Created by wangtingwei on 2014/10/20.
 */
public interface BatchSendService {

    /**
     * 生成波次对应的发货批次号
     * @param batchInfo 波次信息
     * @param siteCode  收货站点
     * @return
     */
    public boolean generate(BatchInfo batchInfo,Integer siteCode);

    /**
     * 生成的发货批次号
     * @param batchSend 波次信息
     * @return
     */
    public Integer add(BatchSend batchSend);

    /**
     * 获取当前发货批次
     * @param sortingCenterId   分拣中心ID
     * @param operateTime       分拣时间
     * @param siteCode          目的站点编号
     * @return
     */
    public BatchSend readBySortinCenterIdAndOperateTimeAndSiteCode(Integer sortingCenterId,Date operateTime,Integer siteCode );

    /**
     * 获取当前发货批次，如果不存在则即时生成并返回
     * @param sortingCenterId    分拣中心ID
     * @param operateTime        分拣时间
     * @param siteCode           目的站点编号
     * @return
     */
    public BatchSend readAndGenerateIfNotExist(Integer sortingCenterId,Date operateTime,Integer siteCode );
    
    /**
     * 发货读取波次发货批次信息
     * @param BatchSend     
     * @return
     */
    public BatchSendResponse findBatchSend(BatchSendRequest request);
    
    /**
     * 发货更新状态
     * @param BatchSend     
     * @return
     */
    public Integer batchUpdateStatus(BatchSend batchSend);


    /**
     * 读波次发货批次信息
     * @param batchCode     波次号
     * @param siteCode      收货站点
     * @return
     */
    public BatchSend read(String batchCode,Integer siteCode);

    public BatchSend readFromCache(String batchCode,Integer siteCode);

    /**
     * 发车
     * @param sendCode 发货批次号
     * @return
     */
    public boolean sendCar(String sendCode,Date operateTime);

    /**
     * 取消发车
     * @param sendCode 发货批次号
     * @return
     */
    public boolean cancelSendCar(String sendCode,Date operateTime);

    /**
     * 读取发车状态
     * @param sendCode 发货批次号
     * @return  BatchSend
     */
    public BatchSend readBySendCode(String sendCode);
}
