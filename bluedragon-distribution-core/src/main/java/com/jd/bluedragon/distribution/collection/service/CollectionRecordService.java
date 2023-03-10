package com.jd.bluedragon.distribution.collection.service;

import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.dms.java.utils.sdk.base.Result;

import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.service
 * @ClassName: CollectionRecordService
 * @Description: 待集齐集合相应的操作Service类
 * @Author： wuzuxiang
 * @CreateDate 2023/3/1 14:56
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface CollectionRecordService {

    /**
     * 查询或者创建一个待集齐集合ID
     * @param collectionCodeEntity
     * @param userErp
     * @return
     */
    String getJQCodeByBusinessType(CollectionCodeEntity collectionCodeEntity, String userErp);

    /**
     * 根据当前的扫描要素查询所有可能得待集齐集合ID
     */
    List<CollectionCodeEntity> queryAllCollectionCodesByElement(Map<CollectionConditionKeyEnum, Object> elements, CollectionBusinessTypeEnum businessTypeEnum);

    /**
     * 初始化待集齐集合对象，包括collection_record统计主表和collection_record_detail统计明细表
     * 全量模式，按照整个待集齐集合进行初始化
     * @param collectionCreatorEntity 待集齐集合的创建对象
     * @return 返回是否创建成功
     */
    boolean initFullCollection(CollectionCreatorEntity collectionCreatorEntity, Result<Boolean> result);


    /**
     * 初始化待集齐集合对象，包括collection_record统计主表和collection_record_detail统计明细表
     * 增量模式，按照待集齐集合下某个aggCode进行增量初始化
     * 重要约束，为了保证增量模式下的操作效率，一次增量初始化时，只能保证一个aggCodeType下，最多只有一个aggCode。
     * 简而言之，如果按照运单(aggCodeType==运单号)，那么最多只能包含一个运单(aggCode==&{waybillCode0})
     * 不满足重要约束的直接返回失败
     * @param collectionCreatorEntity 待集齐集合的创建对象
     * @return 返回是否创建成功
     */
    boolean initPartCollection(CollectionCreatorEntity collectionCreatorEntity, Result<Boolean> result);

    /**
     * 增量初始化待集齐集合对象，并将该待集齐单号置为已集齐的状态
     * @param collectionCreatorEntity
     * @param result
     * @return
     */
    boolean initAndCollectedPartCollection(CollectionCreatorEntity collectionCreatorEntity, Result<Boolean> result);

    /**
     * 根据操作的单号去消除在待集齐集合中的单号状态
     * @param collectionCollectorEntity 待集齐单号对象
     * @param result 处理结果
     * @return 返回成功或者失败
     */
    boolean collectTheScanCode(CollectionCollectorEntity collectionCollectorEntity, Result<Boolean> result);

    /**
     * 查询某一个待集齐集合下的aggCode的集齐情况
     * @param collectionCode
     * @param aggCode
     * @param aggCodeTypeEnum
     */
    CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCode(String collectionCode, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum);

    /**
     * 查询某一个待集齐集合下的aggCode的集齐情况，以及collectedMark相同的数量
     * @param collectionCode 集合号
     * @param aggCode 聚合统计号
     * @param aggCodeTypeEnum 聚合统计类型
     */
    CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(String collectionCode, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark);

    /**
     * 通过查询元素定位到所有的满足该元素下的所有的集合的未集齐aggCode数量
     * @param element 查询元素
     * @return
     */
    Integer countNoneCollectedAggCodeNumByCollectionCode(CollectionCodeEntity collectionCodeEntity);


    /**
     * 根据collectionCode集合ID查询该集合的汇总信息
     * @param element 查询元素
     * @return
     */
    @Deprecated
    void sumCollection(Map<CollectionConditionKeyEnum, Object> element);

    /**
     * 根据待集齐集合ID查询待集齐集合ID的待集齐情况
     * todo ======
     * @param collectionCodes
     * @return
     */
    List<CollectionCounter> sumCollectionByCollectionCode(List<CollectionCodeEntity> collectionCodes);

    /**
     * 根据待集齐集合
     * @param collectionCodeEntities
     * @param collectionStatusEnum
     * @param aggCodeTypeEnum
     * @return
     */
    List<CollectionAggCodeCounter> sumCollectionByCollectionCodeAndStatus(List<CollectionCodeEntity> collectionCodeEntities, CollectionStatusEnum collectionStatusEnum, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode);

    /**
     * 根据待集齐集合
     * @param collectionCodeEntities
     * @param collectionStatusEnum
     * @param aggCodeTypeEnum
     * @return
     */
    List<CollectionAggCodeCounter> sumCollectionByCollectionCodeAndStatusWithCollectedMark(List<CollectionCodeEntity> collectionCodeEntities, CollectionStatusEnum collectionStatusEnum, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode);

    /**
     * 根据集合ID和aggCode查询统计明细信息
     * @param collectionCodeEntities
     * @param aggCode
     * @param aggCodeTypeEnum
     * @param collectionStatusEnum
     * @return
     */
    List<CollectionScanCodeCounter> queryCollectionScanDetailByAggCode(List<CollectionCodeEntity> collectionCodeEntities, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, CollectionStatusEnum collectionStatusEnum);


}
