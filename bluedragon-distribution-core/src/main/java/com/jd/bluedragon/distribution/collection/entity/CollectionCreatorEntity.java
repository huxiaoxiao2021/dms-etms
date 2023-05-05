package com.jd.bluedragon.distribution.collection.entity;

import com.jd.dms.java.utils.sdk.base.Result;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.entity
 * @ClassName: CollectionCreatorEntity
 * @Description: 待集齐集合的初始化对象，包含业务类型对象，条件对象，聚合类型对象，以及待集齐单号和待集齐单号的聚合信息对象。
 * @Author： wuzuxiang
 * @CreateDate 2023/3/1 15:09
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Data
public class CollectionCreatorEntity {

    /**
     * 当前待集齐的业务类型对象，以及保存了当前待集齐业务类型的条件和aggCodeType的聚合类型对象。
     */
    private CollectionCodeEntity collectionCodeEntity;

    /**
     * 当前待集齐的业务中，所有的aggCode的aggMark值，用于排序或者分类，
     * 比如运单号对应的货区编码和流向，则key：JDVA0001， value：910
     */
    private Map<String, String> collectionAggMarks;

    /**
     * 当前待集齐集合的待集齐单号信息，以及包含了当前待集齐单号的聚合对象信息
     */
    private List<CollectionScanCodeEntity> collectionScanCodeEntities;

    /**
     * 此处使用了dms-java-utils中的result对象，此类重合太多
     * dms-java-utils是同事计划中希望去统一的公共工具包
     * @param result 是否成功对象
     * @return
     */
    public boolean checkEntity(Result<Boolean> result) {
        if (Objects.isNull(result)) {
            result = new Result<>(true);
        }
        if (Objects.isNull(this.getCollectionCodeEntity())) {
            result.toFail("没有检测到有效的待集齐集合信息");
            result.setData(false);
            return false;
        }
        if (StringUtils.isEmpty(this.getCollectionCodeEntity().getCollectionCode())) {
            result.toFail("没有检测到有效的集合ID信息");
            result.setData(false);
            return false;
        }
        if (Objects.isNull(this.getCollectionCodeEntity().getBusinessType())) {
            result.toFail("没有检测到有效的待集齐业务信息");
            result.setData(false);
            return false;
        }
        if (StringUtils.isEmpty(this.getCollectionCodeEntity().getCollectionCondition())
            || StringUtils.isEmpty(this.getCollectionCodeEntity().buildCollectionCondition().getCollectionCondition())) {
            result.toFail("没有检测到有效的待集齐业务条件信息");
            result.setData(false);
            return false;
        }
        if (CollectionUtils.isEmpty(this.getCollectionScanCodeEntities())) {
            result.toFail("没有检测到有效的待集齐集合信息");
            result.setData(false);
            return false;
        }

        return true;
    }

}
