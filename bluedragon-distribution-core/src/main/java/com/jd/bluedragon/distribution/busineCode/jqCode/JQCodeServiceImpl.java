package com.jd.bluedragon.distribution.busineCode.jqCode;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.busineCode.BusinessCodeManager;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.fastjson.JSON;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.busineCode.jqCode
 * @ClassName: JQCodeServiceImpl
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/2/27 16:26
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
@Service
@Slf4j
public class JQCodeServiceImpl implements JQCodeService {

    @Autowired
    private SmartSNGen smartJQCodeSNGen;

    @Autowired
    private BusinessCodeManager businessCodeManager;

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Override
    @JProfiler(jKey = "DMS.CORE.JQCodeService.createJQCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {
        JProEnum.TP,JProEnum.FunctionError})
    public String createJQCode(Map<CollectionConditionKeyEnum, Object> collectionConditionKeyMap,
        CollectionBusinessTypeEnum collectionBusinessTypeEnum, BusinessCodeFromSourceEnum fromSource, String createUser) {

        if (Objects.isNull(collectionBusinessTypeEnum) || MapUtils.isEmpty(collectionConditionKeyMap)) {
            log.error("创建待集齐集合号的参数不正确：{}，业务类型{}，数据来源：{}",
                JSON.toJSONString(collectionConditionKeyMap), collectionBusinessTypeEnum.name(), fromSource.name());
            return StringUtils.EMPTY;
        }
        /* 生成待集齐的集合号 */
        String jqCode = smartJQCodeSNGen.gen(fromSource.name());

        /* 将所有的CollectionCondition的key落入明细表 */
        Map<String,String> attributeParam = new HashMap<>();
        for (Map.Entry<CollectionConditionKeyEnum, Object> attributeKeyEntity : collectionConditionKeyMap.entrySet()) {
            attributeParam.put(attributeKeyEntity.getKey().name(), String.valueOf(attributeKeyEntity.getValue()));
        }
        /* 将businessType添加到attributeParam中写入属性表 */
        attributeParam.put(BusinessCodeAttributeKey.JQCodeAttributeKeyEnum.collection_business_type.name(),
            collectionBusinessTypeEnum.name());
        /* 根据businessType创建待集齐集合的条件，并添加到attributeParam中写入属性表 */
        String condition = CollectionConditionKeyEnum.getCondition(collectionConditionKeyMap,collectionBusinessTypeEnum);
        attributeParam.put(BusinessCodeAttributeKey.JQCodeAttributeKeyEnum.collection_condition.name(), condition);

        String jqCodeKey = "collection_code_".concat(condition);

        if (jimdbCacheService.setNx(jqCodeKey, jqCode, 5, TimeUnit.MINUTES)) {
            KvIndex kvIndex = new KvIndex();
            kvIndex.setKeyword(condition);
            kvIndex.setValue(jqCode);
            if (kvIndexDao.add(kvIndex) <= 0) {
                log.error("创建待集齐集合号失败：{}，业务类型{}，数据来源：{}",
                    JSON.toJSONString(collectionConditionKeyMap), collectionBusinessTypeEnum.name(), fromSource.name());
                return "";
            }

            boolean isSuccess = businessCodeManager.saveBusinessCodeAndAttribute(jqCode,
                BusinessCodeNodeTypeEnum.collection_code, attributeParam, createUser, fromSource);

            if (!isSuccess) {
                log.error("插入业务单号主表副表失败，创建集齐集合号失败，集合号:{},集合属性:{}", jqCode, JsonHelper.toJson(attributeParam));
                return StringUtils.EMPTY;
            }
            return jqCode;

        } else {
            return jimdbCacheService.get(jqCodeKey);
        }

    }
}
