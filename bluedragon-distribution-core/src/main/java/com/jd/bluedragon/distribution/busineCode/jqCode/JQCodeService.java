package com.jd.bluedragon.distribution.busineCode.jqCode;

import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;

import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.busineCode.jqCode
 * @ClassName: JQCodeService
 * @Description: 拣运集齐能力域-JQCode集合ID的创建类
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 16:26
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public interface JQCodeService {

    /**
     * 创建批次号 （采用实时时间生成）生成不重复的批次号
     * @param fromSource 创建来源
     * @param collectionConditionKeyMap 待集齐集合的必要条件，也是待集齐集合属性collection_condition
     * @param collectionBusinessTypeEnum 待集齐集合的业务类型
     * @param createUser 创建人
     * @see BusinessCodeAttributeKey.JQCodeAttributeKeyEnum 待集齐集合的属性
     * @see CollectionConditionKeyEnum 待集齐集合的条件属性
     * @return 集合唯一属性ID
     */
    String createJQCode(Map<CollectionConditionKeyEnum, Object> collectionConditionKeyMap,
        CollectionBusinessTypeEnum collectionBusinessTypeEnum, BusinessCodeFromSourceEnum fromSource, String createUser);


    /**
     * 拣运app岗位处理集齐获取 collectionCode  （任务维度）
     * @param jyPostEnum  岗位类型（必填）
     * @param siteId  操作场地（必填）
     * @param bizId  任务主键（必填）
     * @param userErp  操作人（选填）
     * @return
     */
    String getOrGenJyScanTaskCollectionCode(JyPostEnum jyPostEnum, Integer siteId, String bizId, String userErp);

    /**
     * 获取condition
     * @param jyPostEnum
     * @param siteId
     * @param bizId
     * @return
     */
    String getJyScanCollectionCondition(JyPostEnum jyPostEnum, Integer siteId, String bizId);
}
