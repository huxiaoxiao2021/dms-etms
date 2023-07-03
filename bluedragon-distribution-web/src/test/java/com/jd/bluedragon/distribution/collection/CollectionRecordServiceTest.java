package com.jd.bluedragon.distribution.collection;

import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.*;
import com.jd.bluedragon.distribution.collection.service.CollectionRecordService;
import com.jd.bluedragon.distribution.test.AbstractTestCase;
import com.jd.bluedragon.distribution.waybill.service.WaybillCacheService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.java.utils.sdk.base.Result;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;

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
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.date_time,"2023-03-21");
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.site_code,"10186");
        collectionCodeEntityUnload.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23032100029183");
    }
    private static final CollectionCodeEntity collectionCodeEntitySite1 = new CollectionCodeEntity(CollectionBusinessTypeEnum.all_site_collection);
    static {
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.date_time,"2023-03-15");
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.site_code,"910");
        collectionCodeEntitySite1.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23040600030436");
    }

    private static final CollectionCodeEntity collectionCodeEntitySite2 = new CollectionCodeEntity(CollectionBusinessTypeEnum.all_site_collection);
    static {
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.date_time,"2023-03-14");
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.site_code,"10186");
        collectionCodeEntitySite2.addKey(CollectionConditionKeyEnum.seal_car_code,"XCZJ23040400000023");
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
        System.out.println(collectionRecordService.getOrGenJQCodeByBusinessType(collectionCodeEntityUnload, "wzx"));
        System.out.println(collectionRecordService.getOrGenJQCodeByBusinessType(collectionCodeEntitySite1, "wzx"));
        System.out.println(collectionRecordService.getOrGenJQCodeByBusinessType(collectionCodeEntitySite2, "wzx"));
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
     * @return 返回是否创建成功
     */
    @Test
    public void initPartCollection() {
        CollectionCreatorEntity collectionCreatorEntity = new CollectionCreatorEntity();
        collectionCreatorEntity.setCollectionCodeEntity(collectionCodeEntityUnload);
        collectionCreatorEntity.setCollectionAggMarks(Collections.singletonMap("JD0093356842901", "39"));

        CollectionScanCodeEntity collectionScanCodeEntity = new CollectionScanCodeEntity();
        collectionScanCodeEntity.setScanCode("JD0093356832910-1-3-");
        collectionScanCodeEntity.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842901"));

        CollectionScanCodeEntity collectionScanCodeEntity1 = new CollectionScanCodeEntity();
        collectionScanCodeEntity1.setScanCode("JD0093356842902-2-3-");
        collectionScanCodeEntity1.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity1.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity1.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842902"));

        CollectionScanCodeEntity collectionScanCodeEntity2 = new CollectionScanCodeEntity();
        collectionScanCodeEntity2.setScanCode("JD0093356842902-3-3-");
        collectionScanCodeEntity2.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity2.setCollectedMark("SC2412341231231");
        collectionScanCodeEntity2.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JD0093356842902"));

        collectionCreatorEntity.setCollectionScanCodeEntities(Arrays.asList(collectionScanCodeEntity, collectionScanCodeEntity1, collectionScanCodeEntity2));

        collectionCodeEntityUnload.setCollectionCode(collectionRecordService.getOrGenJQCodeByBusinessType(collectionCodeEntityUnload, ""));
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

        collectionCodeEntityUnload.setCollectionCode(collectionRecordService.getOrGenJQCodeByBusinessType(collectionCodeEntityUnload, ""));
        System.out.println(collectionRecordService.initAndCollectedPartCollection(collectionCreatorEntity, new com.jd.dms.java.utils.sdk.base.Result<>()));
    }

    /**
     * 根据操作的单号去消除在待集齐集合中的单号状态
     */
    @Test
    public void collectTheScanCode() {
        CollectionCollectorEntity collectionCollectorEntity = new CollectionCollectorEntity();
        collectionCollectorEntity.setCollectElements(collectionCodeEntityUnload.getCollectElements());

        CollectionScanCodeEntity collectionScanCodeEntity2 = new CollectionScanCodeEntity();
        collectionScanCodeEntity2.setScanCode("JDVA00257390332-1-5-");
        collectionScanCodeEntity2.setScanCodeType(CollectionScanCodeTypeEnum.package_code);
        collectionScanCodeEntity2.setCollectedMark("SC23040300030204");
        collectionScanCodeEntity2.setCollectionAggCodeMaps(Collections.singletonMap(CollectionAggCodeTypeEnum.waybill_code,"JDVA00257390332"));

        collectionCollectorEntity.setCollectionScanCodeEntity(collectionScanCodeEntity2);
        collectionRecordService.collectTheScanCode(collectionCollectorEntity, new Result<Boolean>());
    }

    /**
     * 查询某一个待集齐集合下的aggCode的集齐情况，以及collectedMark相同的数量
     * @return
     */
    @Test
    public void countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark() {
//        System.out.println(JsonHelper.toJson(collectionRecordService.countCollectionStatusByAggCodeAndCollectionCodeWithCollectedMark(
//            collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), null),
//            null, CollectionAggCodeTypeEnum.waybill_code, "SC23032100029221"
//        )));

//        System.out.println(JsonHelper.toJson(collectionRecordService.sumCollectionByAggCodeAndCollectionCode(
//            collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), null), null,
//            "JD0003419509821", CollectionAggCodeTypeEnum.waybill_code, "SC23032100029183"
//        )));

        collectionCodeEntitySite2.setCollectionCode("JQ23033122360300032");
        System.out.println(JsonHelper.toJson(collectionRecordService.sumCollectionByAggCodeAndCollectionCode(
            collectionCodeEntitySite2,
            "JDVA00257543655", CollectionAggCodeTypeEnum.waybill_code, "XCZJ23040400000023"
        )));
    }

    /**
     * 通过查询元素定位到所有的满足该元素下的所有的集合的未集齐aggCode数量
     * @return 返回不齐数量
     */
    @Test
    public void countNoneCollectedAggCodeNumByCollectionCode() {
        System.out.println(
            JsonHelper.toJson(collectionRecordService.countNoneCollectedAggCodeNumByCollectionCode(
                collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(), null),
                CollectionAggCodeTypeEnum.waybill_code, "", String.valueOf(collectionCodeEntityUnload.getCollectElements().get(CollectionConditionKeyEnum.seal_car_code))
                )
            )
        );
    }

    /**
     * 根据待集齐集合ID查询待集齐集合ID的待集齐情况
     * @return
     */
    @Test
    public void sumCollectionByCollectionCode() {
        System.out.println(JsonHelper.toJson(
            collectionRecordService.countCollectionAggCodeNumByCollectionCodeInnerMark(
                collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(),null)
                ,CollectionAggCodeTypeEnum.waybill_code, "", ""
        )));
        System.out.println(JsonHelper.toJson(
            collectionRecordService.countCollectionAggCodeNumByCollectionCodeOutMark(
                collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(),null)
                ,CollectionAggCodeTypeEnum.waybill_code, "", ""
            )));
    }

    /**
     * 根据集合ID和aggCode查询统计明细信息
     * @return
     */
    @Test
    public void queryCollectionScanDetailByAggCode() {
        System.out.println(collectionRecordService.queryCollectionScanDetailByAggCode(
            collectionRecordService.queryAllCollectionCodesByElement(collectionCodeEntityUnload.getCollectElements(),null),
            "JD0093356842901", CollectionAggCodeTypeEnum.waybill_code, "SC23010143333786", 10, 0
        ));
    }

    @Test
    public void some() {

        CollectionCodeEntity collectionCodeEntityUnload1 = new CollectionCodeEntity(CollectionBusinessTypeEnum.unload_collection);
        collectionCodeEntityUnload1.addKey(CollectionConditionKeyEnum.site_code,"10186");
        collectionCodeEntityUnload1.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23040600030409");
        collectionCodeEntityUnload1.setCollectionCode("JQ23033122360300032");

        CollectionCodeEntity collectionCodeEntityUnload2 = new CollectionCodeEntity(CollectionBusinessTypeEnum.unload_collection);
        collectionCodeEntityUnload2.addKey(CollectionConditionKeyEnum.site_code,"10186");
        collectionCodeEntityUnload2.addKey(CollectionConditionKeyEnum.seal_car_code,"SC23040600030409");
        collectionCodeEntityUnload2.setCollectionCode("JQ23040610514700032");


        System.out.println(
            collectionRecordService.sumNoneCollectedAggCodeByCollectionCode(
                Arrays.asList(collectionCodeEntityUnload1,collectionCodeEntityUnload2), CollectionAggCodeTypeEnum.waybill_code, null, "SC23040600030409", 10, 0
            )
        );

        System.out.println(
            collectionRecordService.sumCollectedAggCodeByCollectionCodeOutMark(
                Arrays.asList(collectionCodeEntityUnload1,collectionCodeEntityUnload2), CollectionAggCodeTypeEnum.waybill_code, null, "SC23040600030409", 10, 0
            )
        );
    }

    @Autowired
    private WaybillCacheService waybillCacheService;

    @Test
    public void testRouter() {
        System.out.println(JsonHelper.toJson(waybillCacheService.getRouterByWaybillCode("JD0003419520694")));
    }
}
