package com.jd.bluedragon.distribution.busineCode.jqCode;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.distribution.base.dao.KvIndexDao;
import com.jd.bluedragon.distribution.base.domain.KvIndex;
import com.jd.bluedragon.distribution.busineCode.BusinessCodeManager;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeAttributeKey;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeNodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionBusinessTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionConditionKeyEnum;
import com.jd.bluedragon.distribution.jy.constants.JyPostEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.sn.SmartSNGen;
import com.jd.fastjson.JSON;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    public static final String JQ_CONDITION = "JQ_condition";

    public static final String CONDITION_SITE_ID = "SITE";//场地编码
    public static final String CONDITION_JY_BIZ_ID = "TASK:";//任务主键
    public static final String CONDITION_JY_POST = "POST";//岗位类型
    public static final String CONDITION_DATE_PARTITION = "DATE";//岗位类型

    public static final String ATTRIBUTE_SITE_ID = "site_code";//场地编码
    public static final String ATTRIBUTE_JY_BIZ_ID = "biz_id";//任务主键
    public static final String ATTRIBUTE_JY_POST = "jy_post";//岗位类型
    public static final String ATTRIBUTE_CONDITION = "condition";//岗位类型
    public static final String ATTRIBUTE_DATE_PARTITION = "date_partition";//创建时间


    @Autowired
    private SmartSNGen smartJQCodeSNGen;

    @Autowired
    private BusinessCodeManager businessCodeManager;

    @Autowired
    private KvIndexDao kvIndexDao;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;
    @Autowired
    private JimDbLock jimDbLock;

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

    /**
     * 注：todo 同一个condition生成collectionCode，每次调用返回值是否一致: smartJQCodeSNGen.gen
     * @param collectionCondition
     * @param attributeParam
     * @param fromSource
     * @param createUser
     * @return
     */
    private String createCollectionCode(String collectionCondition, Map<String,String> attributeParam, BusinessCodeFromSourceEnum fromSource, String createUser) {
        String methodDesc = "JQCodeServiceImpl：createCollectionCode集齐创建collectionCode信息：";
        if(Objects.isNull(fromSource)) {
            fromSource = BusinessCodeFromSourceEnum.DMS_WORKER_SYS;
        }
        if(StringUtils.isBlank(createUser)) {
            createUser = "none";
        }
        if(StringUtils.isBlank(collectionCondition)) {
            log.warn("拣运扫描获取collectionCondition为空,attributeParam={}", JsonHelper.toJson(attributeParam));
            throw new JyBizException("拣运扫描获取collectionCondition为空");
        }
        if(StringUtils.isBlank(attributeParam.get(JQCodeServiceImpl.JQ_CONDITION))) {
            attributeParam.put(JQCodeServiceImpl.JQ_CONDITION, collectionCondition);
        }

        String jqCodeKey = "collection_code_".concat(collectionCondition);
        String value = StringUtils.EMPTY;
        if (!jimDbLock.lock(jqCodeKey, value, 120, TimeUnit.SECONDS)) {
            log.error("{}未获取到锁,condition={},source={},attributeMap={}",
                    methodDesc, collectionCondition, fromSource.name(), JsonHelper.toJson(attributeParam));
            throw new JyBizException("集齐collectionCode创建未获取到锁" + collectionCondition);
        }

        try{
            //锁内二次查询
            String JQCode = kvIndexDao.queryRecentOneByKeyword(collectionCondition.toUpperCase());
            if(StringUtils.isNotBlank(JQCode)) {
                return JQCode;
            }
            /* 生成待集齐的集合号 */
            String collectionCode = smartJQCodeSNGen.gen(fromSource.name());
            //
            addKvIndex(collectionCode, collectionCode);
            //保存主副表数据，
            saveBusinessCodeAndAttribute(collectionCode, BusinessCodeNodeTypeEnum.collection_code, attributeParam, createUser, fromSource);
            return collectionCode;
        }catch (JyBizException e) {
            log.error("{}服务失败，errMsg={}, condition={},source={},attributeMap={}",
                    methodDesc, e.getMessage(), collectionCondition, fromSource.name(), JsonHelper.toJson(attributeParam));
            throw new RuntimeException(e.getMessage());
        }catch (Exception e) {
            log.error("{}服务异常condition={},source={},attributeMap={}",
                    methodDesc, collectionCondition, fromSource.name(), JsonHelper.toJson(attributeParam), e);
            throw new RuntimeException("创建集齐collectionCode服务失败" + collectionCondition);
        }finally {
            jimDbLock.releaseLock(jqCodeKey, value);
        }
    }

    private void saveBusinessCodeAndAttribute(String collectionCode,
                                              BusinessCodeNodeTypeEnum businessCodeNodeTypeEnum,
                                              Map<String,String> attributeParam,
                                              String createUser,
                                              BusinessCodeFromSourceEnum fromSource) {
        if (!businessCodeManager.saveBusinessCodeAndAttribute(collectionCode, businessCodeNodeTypeEnum, attributeParam, createUser, fromSource)) {
            log.error("saveBusinessCodeAndAttribute:插入业务单号主表副表失败:collectionCode={},source={},attributeMap={}",
                    collectionCode, fromSource.name(), JsonHelper.toJson(attributeParam));
            throw new JyBizException("插入业务单号到主副表失败" + collectionCode);
        }
    }

    private void addKvIndex(String keyword, String value) {
        KvIndex kvIndex = new KvIndex();
        kvIndex.setKeyword(keyword);
        kvIndex.setValue(value);
        if (kvIndexDao.add(kvIndex) <= 0) {
            log.error("key={},value={}添加失败", keyword, value);
            throw new JyBizException(String.format("添加kvIndex失败:%s:%s", keyword, value));
        }
    }


    /**
     * 任务维度 collectionCode 获取
     * @param jyPostEnum  岗位类型（必填）
     * @param siteId  操作场地（必填）
     * @param bizId  任务主键（必填）
     * @param userErp  操作人（选填）
     * @return
     */
    @Override
    public String getOrGenJyScanTaskCollectionCode(JyPostEnum jyPostEnum, Integer siteId, String bizId,  String userErp) {
        String methodDesc =  "JQCodeServiceImpl.getOrGenerateCollectionCodeByBusinessType:获取collectionCode:";
        String datePartition = DateUtil.format(new Date(), DateUtil.FORMAT_DATE);
        //
        // String condition = getJyScanCollectionConditionWithDataPartition(jyPostEnum, siteId, bizId, datePartition);
        String condition = getJyScanCollectionCondition(jyPostEnum, siteId, bizId);
        //
        String cacheKey = String.format("JQCondition:%s", condition);
        String collectionCode = jimdbCacheService.get(cacheKey);
        if(StringUtils.isBlank(collectionCode)) {
            collectionCode = kvIndexDao.queryRecentOneByKeyword(condition);
            if (StringUtils.isNotEmpty(collectionCode)) {
                jimdbCacheService.setEx(cacheKey, collectionCode, 30, TimeUnit.MINUTES);
            }
        }
        if(StringUtils.isNotBlank(collectionCode)) {
            if(log.isInfoEnabled()) {
                log.info("{}collectionCode={}，collection={}", methodDesc, collectionCode, condition);
            }
            return collectionCode;
        }
        if(log.isInfoEnabled()) {
            log.info("{}没有查到collectionCode,进行生成，collection={}", methodDesc, condition);
        }

        Map<String,String> attributeParam = getJyScanCollectionAttributeMap(jyPostEnum, siteId, bizId, datePartition, condition);
        BusinessCodeFromSourceEnum fromSource = BusinessCodeFromSourceEnum.DMS_WORKER_SYS;
        //当前没有符合的condition进行生成
        collectionCode = createCollectionCode(condition, attributeParam, fromSource, userErp);
        if(log.isInfoEnabled()) {
            log.info("{}生成collectionCode={}，collection={}", methodDesc, collectionCode, condition);
        }
        return collectionCode;
    }

    private Map<String, String> getJyScanCollectionAttributeMap(JyPostEnum jyPostEnum, Integer siteId, String bizId, String datePartition, String condition) {
        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(ATTRIBUTE_SITE_ID, siteId.toString());
        attributeMap.put(ATTRIBUTE_JY_BIZ_ID, bizId);
        attributeMap.put(ATTRIBUTE_JY_POST, jyPostEnum.getCode());
        attributeMap.put(ATTRIBUTE_DATE_PARTITION, datePartition);
        attributeMap.put(ATTRIBUTE_CONDITION, condition);
        return attributeMap;
    }

    /**
     * 获取集齐condition
     * 进来保证长度不要过长，底层对场地有限制，超过长度后会截取处理
     * 辨识度高的字段放前面： bizId + 岗位 + 场地
     * @param jyPostEnum
     * @param siteId
     * @param bizId
     * @return
     */
    private String getJyScanCollectionCondition(JyPostEnum jyPostEnum, Integer siteId, String bizId) {
        if(Objects.isNull(jyPostEnum) || Objects.isNull(siteId) || StringUtils.isBlank(bizId)) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(JQCodeServiceImpl.CONDITION_JY_BIZ_ID).append(bizId)
                .append(JQCodeServiceImpl.CONDITION_JY_POST).append(jyPostEnum.getCode())
                .append(JQCodeServiceImpl.CONDITION_SITE_ID).append(siteId);
        return sb.toString();
    }
    /**
     * 集齐collectionCode按时间分区 （当前可不考虑）
     * @param jyPostEnum
     * @param siteId
     * @param bizId
     * @param datePartition
     * @return
     */
//    private String getJyScanCollectionConditionWithDataPartition(JyPostEnum jyPostEnum, Integer siteId, String bizId, String datePartition) {
//        StringBuffer sb = new StringBuffer();
//        sb.append(this.getJyScanCollectionCondition(jyPostEnum, siteId, bizId));
//        sb.append(JQCodeServiceImpl.CONDITION_DATE_PARTITION).append(datePartition);
//        return sb.toString();
//    }


}
