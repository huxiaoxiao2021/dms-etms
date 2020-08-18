package com.jd.bluedragon.distribution.ver.service;

import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.TaskRequest;
import com.jd.bluedragon.distribution.api.response.TaskResponse;
import com.jd.bluedragon.distribution.client.domain.*;

import com.jd.bluedragon.distribution.jsf.domain.BlockResponse;
import com.jd.bluedragon.distribution.ver.domain.CancelWaybillDto;
import com.jd.bluedragon.distribution.ver.domain.JdCancelWaybillResponse;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CancelWaybillService {

    CancelWaybill get(String waybillCode);

    Boolean hasCancel(String waybillCode);
    
    /**
     * 查询waybill_cancel表最近一段时间内是否有存活数据
     * @return
     */
	Integer countWaybillCancelLatest();
	
	/**
	 * 获得拦截结果
	 */
    JdCancelWaybillResponse dealCancelWaybill(String waybillCode);

    JdCancelWaybillResponse dealCancelWaybill(PdaOperateRequest pdaOperate);

    /**
     * 推送取消日志
     * @param pdaOperate
     * @param errorMsg
     */
    public void pushSortingInterceptRecord(PdaOperateRequest pdaOperate, String errorMsg);

    public TaskResponse postSortingCached(TaskRequest request);

    /**
     * 写入Waybill_Cancel数据
     * @param cancelWaybill
     */
    public void insertWaybillCancel(CancelWaybill cancelWaybill);

    public Integer getFeatureTypeByWaybillCode(String waybillCode);

    /**
     * 查找waybillCancel的所有数据
     * @param waybillCode
     * @return
     */
    List<CancelWaybill> getAllWaybillCancel(String waybillCode);

    /**
     * 查找waybillCancel的所有数据
     * @param waybillCode
     * @return
     */
    List<CancelWaybill> getAllWaybillCancel(String waybillCode, List<Integer> featureTypes);

    /**
     * 根据条件查找waybillCancel的所有数据
     * @param cancelWaybill
     * @return
     */
    List<CancelWaybill> getAllWaybillCancelByCondition(CancelWaybill cancelWaybill);

    /**
     * 更新或者是插入waybillCancel表
     * @param cancelWaybill
     * @return
     */
    Integer[] upsertWaybillCancel(CancelWaybillDto cancelWaybill);

    /**
     * 根据单号和featureType查询运单拦截消息
     * @param waybillCode
     * @param featureType
     * @return
     */
    CancelWaybill findWaybillCancelByWaybillCodeAndFeatureType(String waybillCode, Integer featureType);

    /**
     * 批量插入包裹号拦截记录
     *
     * @param cancelWaybill
     */
    boolean insertBatchPackageCodeLock(CancelWaybillDto cancelWaybill);

    /**
     * 查询运单是否拦截完成
     * @param waybillCode
     * @param featureType
     * @return
     */
    BlockResponse checkWaybillBlock(String waybillCode, Integer featureType);


    /**
     * 查询包裹是否拦截完成
     * @param packageCode
     * @param featureType
     * @return
     */
    BlockResponse checkPackageBlock(String packageCode, Integer featureType);
    /**
     * 根据运单号查询锁定状态为businessType的包裹号数量
     * @param waybillCode
     * @param featureType
     * @param businessType
     * @return
     */
    Long findPackageCodeCountByFeatureTypeAndWaybillCode(String waybillCode, Integer featureType, String businessType);

    Integer updatWaybillbusinessType(CancelWaybill cancelWaybill);


    List<CancelWaybill> findWaybillBlockByWaybillCode(@Param("waybillCode") String waybillCode,
                                                      @Param("businessType") String businessType);

    /**
     * 检查是否需要阻止的运单拦截消息
     * @param waybillCode 运单号
     * @param featureTypes 需要拦截的featureType
     * @return
     */
    JdResponse checkIfNeedBlock(String waybillCode, List<Integer> featureTypes);
    /**
     * 根据运单号查询指定拦截类型的拦截信息
     * @param waybillCode
     * @param businessType 拦截状态
     * @param featureTypes 拦截业务类型
     * @param interceptTypes 拦截的细分类型
     * @param interceptMode 拦截模式 强拦还是提示
     * @return
     */
    List<CancelWaybill> findWaybillBlockByFeatureTypeAndInterceptType(String waybillCode,
                                                                      String businessType,
                                                                      List<Integer> featureTypes,
                                                                      List<Integer> interceptTypes,
                                                                      Integer interceptMode);
    /**
     * 根据 feature_type 查询 无包裹拦截的运单拦截记录
     * @param waybillCode
     * @param businessType
     * @param featureTypes
     * @param packageCode
     * @return
     */
    List<CancelWaybill> selectWaybillBlockByFeatureTypesAndNoPackageCode(String waybillCode, String businessType,
                                                                         List<Integer> featureTypes,
                                                                         String packageCode);
    /**
     * 根据运单号 和FeatureTypes 把包裹号的拦截设置为无效
     * @return
     */
    Integer updateInvalidPackageCodeBlockByWaybillCodeAndFeatureTypes(String waybillCode,
                                                                      String businessType,
                                                                      List<Integer> featureTypes);
    /**
     * 如果按包裹维度拦截的业务类型，改包裹号拦截记录不存在，则重置包裹拦截记录
     * @param waybillCode
     * @param packageCode
     */
    void resetPackageBlockIfPakcageCodeNotExists(String waybillCode, String packageCode);
}
