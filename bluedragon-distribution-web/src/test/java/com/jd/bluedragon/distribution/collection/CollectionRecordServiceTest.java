package com.jd.bluedragon.distribution.collection;

import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.*;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.test.AbstractTestCase;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection
 * @ClassName: CollectionRecordServiceTest
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/3/13 13:52
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CollectionRecordServiceTest extends AbstractTestCase {

    @Autowired
    private CollectionRecordService collectionRecordService;

    private static final CollectionCodeEntity collectionCodeEntityUnload = new CollectionCodeEntity(CollectionBusinessTypeEnum.unload_collection);
    static {
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.date_time,"2023-03-15");
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.site_code,"910");
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23010143333786");
    }
    private static final CollectionCodeEntity collectionCodeEntitySite1 = new CollectionCodeEntity(CollectionBusinessTypeEnum.all_site_collection);
    static {
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.date_time,"2023-03-15");
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.site_code,"910");
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23010143333786");
    }

    private static final CollectionCodeEntity collectionCodeEntitySite2 = new CollectionCodeEntity(CollectionBusinessTypeEnum.all_site_collection);
    static {
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.date_time,"2023-03-14");
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.site_code,"910");
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23010143333786");
    }

    @Test
    public void argTest(){
        CollectionCodeEntity collectionCodeEntity = new CollectionCodeEntity(CollectionBusinessTypeEnum.unload_collection);
        collectionCodeEntity.addKey(CollectionConditionKeyEnum.date_time,"2023-03-13");
        collectionCodeEntity.addKey(CollectionConditionKeyEnum.site_code,"910");
        collectionCodeEntity.addKey(CollectionConditionKeyEnum.seal_car_code,"SC2412341231231");

        CollectionCreatorEntity collectionCreatorEntity = new CollectionCreatorEntity();
        collectionCreatorEntity.setCollectionCodeEntity(collectionCodeEntity);
        collectionCreatorEntity.setCollectionAggMarks(Collections.singletonMap("JD0093356842901","39"));

        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
        collectionScanCodeEntity.setScanCode("JD0093356842901-2-3-");
        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        collectionCreatorEntity.setCollectionScanCodeEntities(Collections.singletonList(collectionScanCodeEntity));
        System.out.println(JsonHelper.toJson(collectionCreatorEntity));

        CollectionCollectorEntity collectionCollectorEntity = new CollectionCollectorEntity();
        collectionCollectorEntity.setCollectElements(collectionCodeEntity.getCollectElements());
        collectionCollectorEntity.setCollectionScanCodeEntity(collectionScanCodeEntity);
        System.out.println(JsonHelper.toJson(collectionCollectorEntity));

        System.out.println(JsonHelper.toJson(Collections.singletonList(collectionCodeEntity)));
    }

    /**
     * 查询或者创建一个待集齐集合ID
     * @return
     */
    @Test
    public void getJQCodeByBusinessType() {
        System.out.println(collectionRecordService.getJQCodeByBusinessType(collectionCodeEntityUnload, "wzx"));
        System.out.println(collectionRecordService.getJQCodeByBusinessType(collectionCodeEntitySite1, "wzx"));
        System.out.println(collectionRecordService.getJQCodeByBusinessType(collectionCodeEntitySite2, "wzx"));
    }

    /**
     * 根据当前的扫描要素查询所有可能得待集齐集合ID
     */
    @Test
    public void queryAllCollectionCodesByElement() {
        System.out.println(JsonHelper.toJson(collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), null)));
        System.out.println(JsonHelper.toJson(collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), CollectionBusinessTypeEnum.all_site_collection)));
        System.out.println(JsonHelper.toJson(collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), CollectionBusinessTypeEnum.unload_collection)));
    }

    /**
     * 初始化待集齐集合对象，包括collection_record统计主表和collection_record_detail统计明细表
     * 增量模式，按照待集齐集合下某个aggCode进行增量初始化
     * 重要约束，为了保证增量模式下的操作效率，一次增量初始化时，只能保证一个aggCodeType下，最多只有一个aggCode。
     * 简而言之，如果按照运单(aggCodeType==运单号)，那么最多只能包含一个运单(aggCode==&{waybillCode0})
     * 不满足重要约束的直接返回失败
     * @param collectionCreatorEntity 待集齐集合的创建对象
     * @return 返回是否创建成功
     */
    @Test
    public void initPartCollection() {
        CollectionCreatorEntity collectionCreatorEntity = new CollectionCreatorEntity();
        collectionCreatorEntity.setCollectionCodeEntity(collectionCodeEntityUnload);
        collectionCreatorEntity.setCollectionAggMarks(Collections.singletonMap("JD0093356842901", "39"));

        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
        collectionScanCodeEntity.setScanCode("JD0093356842901-1-3-");
        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        CollectionScanCodeEntity collectionScanCodeEntity1 = new CollectionScanCodeEntity();
        collectionScanCodeEntity1.setScanCode("JD0093356842901-2-3-");
        collectionScanCodeEntity1.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity1.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity1.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        CollectionScanCodeEntity collectionScanCodeEntity2 = new CollectionScanCodeEntity();
        collectionScanCodeEntity2.setScanCode("JD0093356842901-3-3-");
        collectionScanCodeEntity2.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity2.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity2.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        collectionCreatorEntity.setCollectionScanCodeEntities(Arrays.asList(collectionScanCodeEntity, collectionScanCodeEntity1, collectionScanCodeEntity2));

        collectionCodeEntityUnload.setCollectionCode(collectionRecordService.getJQCodeByBusinessType(collectionCodeEntityUnload, ""));
        System.out.println(collectionRecordService.initPartCollection(collectionCreatorEntity, new com.jd.dms.java.utils.sdk.base.Result<>()));

    }


    @Test
    public void initAndCollectedPartCollection() {
        CollectionCreatorEntity collectionCreatorEntity = new CollectionCreatorEntity();
        collectionCreatorEntity.setCollectionCodeEntity(collectionCodeEntityUnload);
        collectionCreatorEntity.setCollectionAggMarks(Collections.singletonMap("JD0093356842901", "39"));

        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
        collectionScanCodeEntity.setScanCode("JD0093356842901-1-3-");
        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        CollectionScanCodeEntity collectionScanCodeEntity1 = new CollectionScanCodeEntity();
        collectionScanCodeEntity1.setScanCode("JD0093356842901-2-3-");
        collectionScanCodeEntity1.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity1.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity1.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        CollectionScanCodeEntity collectionScanCodeEntity2 = new CollectionScanCodeEntity();
        collectionScanCodeEntity2.setScanCode("JD0093356842901-3-3-");
        collectionScanCodeEntity2.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity2.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity2.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        collectionCreatorEntity.setCollectionScanCodeEntities(Arrays.asList(collectionScanCodeEntity, collectionScanCodeEntity1, collectionScanCodeEntity2));

        collectionCodeEntityUnload.setCollectionCode(collectionRecordService.getJQCodeByBusinessType(collectionCodeEntityUnload, ""));
        System.out.println(collectionRecordService.initAndCollectedPartCollection(collectionCreatorEntity, new com.jd.dms.java.utils.sdk.base.Result<>()));
    }

    /**
     * 根据操作的单号去消除在待集齐集合中的单号状态
     * @param collectionCollectorEntity 待集齐单号对象
     * @param result 处理结果
     * @return 返回成功或者失败
     */
    boolean collectTheScanCode(CollectionCollectorEntity collectionCollectorEntity, Result<Boolean> result) {
        return true;
    }

    /**
     * 查询某一个待集齐集合下的aggCode的集齐情况，以及collectedMark相同的数量
     * @param collectionCodeEntities 带有集齐ID的查询条件，
     * @param aggCode 聚合统计号，例如JDVA0000000100101
     * @param aggCodeTypeEnum 聚合统计号类型，例如：CollectionAggCodeTypeEnum#waybill_code 表示按运单号聚合
     * @param collectedMark 集齐时的标示，用于统计或排序
     * @return
     */
    CollectionAggCodeCounter countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(List<CollectionCodeEntity> collectionCodeEntities, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {
        return null;
    }

    /**
     * 通过查询元素定位到所有的满足该元素下的所有的集合的未集齐aggCode数量
     * @param collectionCodeEntities 查询元素 内包含待集齐集合ID
     * @return 返回不齐数量
     */
    Integer countNoneCollectedAggCodeNumByCollectionCode(List<CollectionCodeEntity> collectionCodeEntities) {
        return null;
    }

    /**
     * 根据待集齐集合ID查询待集齐集合ID的待集齐情况
     * @param collectionCodeEntities
     * @return
     */
    List<CollectionCounter> sumCollectionByCollectionCode(List<CollectionCodeEntity> collectionCodeEntities) {
        return null;
    }

    /**
     * 根据待集齐集合和状态查询aggCode统计情况
     * @param collectionCodeEntities
     * @param collectionStatusEnum
     * @param aggCodeTypeEnum
     * @return
     */
    List<CollectionAggCodeCounter> sumCollectionByCollectionCodeAndStatus(List<CollectionCodeEntity> collectionCodeEntities, CollectionStatusEnum
    collectionStatusEnum, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, String collectedMark, Integer limit, Integer offset) {
        return null;
    }

    /**
     * 根据集合ID和aggCode查询统计明细信息
     * @param collectionCodeEntities
     * @param aggCode
     * @param aggCodeTypeEnum
     * @return
     */
    List<CollectionScanCodeDetail> queryCollectionScanDetailByAggCode(List<CollectionCodeEntity> collectionCodeEntities, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark, Integer limit, Integer offset) {
        return null;
    }
}
