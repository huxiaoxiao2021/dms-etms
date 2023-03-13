package com.jd.bluedragon.distribution.collection;

import com.jd.bluedragon.distribution.collection.entity.CollectionCodeEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionCollectorEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionCreatorEntity;
import com.jd.bluedragon.distribution.collection.entity.CollectionScanCodeEntity;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionScanCodeTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import org.junit.Test;

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
public class CollectionRecordServiceTest {

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

    }
}
