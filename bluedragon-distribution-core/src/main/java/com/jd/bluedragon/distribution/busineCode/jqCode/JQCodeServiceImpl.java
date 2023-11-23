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
import com.jd.bluedragon.distribution.jy.enums.JyFuncCodeEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.coo.sa.sn.SmartSNGen;
import com.alibaba.fastjson.JSON;
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

    public static final String LOCK_DEFAULT_VALUE = "1";

    /**
     * 组装collectionCode附属属性attribute的字段
     */
//    public static final String ATTRIBUTE_SITE_ID = "site_code";//场地编码
//    public static final String ATTRIBUTE_JY_BIZ_ID = "biz_id";//任务主键
    public static final String ATTRIBUTE_BATCH_CODE = "batch_code";//批次号
    public static final String ATTRIBUTE_JY_POST = "jy_post";//岗位枚举
    public static final String ATTRIBUTE_CONDITION = "condition";//
    public static final String ATTRIBUTE_DATE_PARTITION = "date_partition";//创建时间
    public static final String ATTRIBUTE_POST_TYPE = "post_type";//岗位类型
    public static final String  ATTRIBUTE_COLLECTION_CODE = "collection_code";//关联的collectionCode
    public static final String ATTRIBUTE_POST_TYPE_SEND = "send";//发货岗


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

    /**
     * 创建collectionCode时，如果遇到锁，直接返回改collectionCode使用，认为有效，实际锁内会异常，该collectionCode可能是无效
     * @param collectionConditionKeyMap 待集齐集合的必要条件，也是待集齐集合属性collection_condition
     * @param collectionBusinessTypeEnum 待集齐集合的业务类型
     * @param fromSource 创建来源
     * @param createUser 创建人
     * @return
     */
    @Override
    @Deprecated
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
        if(StringUtils.isBlank(attributeParam.get(JQCodeServiceImpl.ATTRIBUTE_CONDITION))) {
            attributeParam.put(JQCodeServiceImpl.ATTRIBUTE_CONDITION, collectionCondition);
        }

        String jqCodeKey = "collection_code_".concat(collectionCondition);
        String value = JQCodeServiceImpl.LOCK_DEFAULT_VALUE;
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
            addKvIndex(collectionCondition, collectionCode);
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
     * @param jyFuncCodeEnum  岗位类型（必填）
     * @param sendCode  批次号
     * @param userErp  操作人（选填）
     * @return
     */
    @Override
    public String getOrGenJyScanTaskSendCodeCollectionCode(JyFuncCodeEnum jyFuncCodeEnum, String sendCode,  String userErp) {
        String methodDesc =  "JQCodeServiceImpl.getOrGenerateCollectionCodeByBusinessType:获取collectionCode:";
        String datePartition = DateUtil.format(new Date(), DateUtil.FORMAT_DATE);
        //
        String condition = this.getJyScanSendCodeCollectionCondition(jyFuncCodeEnum, sendCode);
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

        Map<String,String> attributeParam = this.getJyScanCollectionAttributeMap(jyFuncCodeEnum, sendCode, datePartition, condition);
        BusinessCodeFromSourceEnum fromSource = BusinessCodeFromSourceEnum.DMS_WORKER_SYS;
        //当前没有符合的condition进行生成
        collectionCode = this.createCollectionCode(condition, attributeParam, fromSource, userErp);
        if(log.isInfoEnabled()) {
            log.info("{}生成collectionCode={}，collection={}", methodDesc, collectionCode, condition);
        }
        return collectionCode;
    }

    private Map<String, String> getJyScanCollectionAttributeMap(JyFuncCodeEnum jyFuncCodeEnum, String sendCode, String datePartition, String condition) {
        Map<String, String> attributeMap = new HashMap<>();
        attributeMap.put(ATTRIBUTE_BATCH_CODE, sendCode);
        attributeMap.put(ATTRIBUTE_JY_POST, jyFuncCodeEnum.getCode());
        attributeMap.put(ATTRIBUTE_DATE_PARTITION, datePartition);
        attributeMap.put(ATTRIBUTE_CONDITION, condition);
        if(JyFuncCodeEnum.isSendPost(jyFuncCodeEnum.getCode())) {
            //记录下是不是发货岗， 取消发货时要找发货collectionCode
            attributeMap.put(ATTRIBUTE_POST_TYPE, JQCodeServiceImpl.ATTRIBUTE_POST_TYPE_SEND);
        }
        return attributeMap;
    }

    /**
     * 获取集齐condition （批次维度）
     * 进来保证长度不要过长，底层对场地有限制，超过长度后会截取处理
     * 辨识度高的字段放前面：  岗位 + 批次
     * @param jyFuncCodeEnum
     * @param sendCode
     * @return
     */
    @Override
    public String getJyScanSendCodeCollectionCondition(JyFuncCodeEnum jyFuncCodeEnum, String sendCode) {
        if(Objects.isNull(jyFuncCodeEnum) || StringUtils.isBlank(sendCode)) {
            return null;
        }
        return String.format("%s:%s", convertPost(jyFuncCodeEnum), sendCode);
    }

    /**
     * 岗位类型转换
     * kv_index 表定义keyword长度有限，原岗位码场地过长， 批次号拼岗位码 长度超长
     * 目前接货仓发货岗使用，其他岗位根据业务需要单独处理转换
     * @param jyFuncCodeEnum
     * @return
     */
    String convertPost(JyFuncCodeEnum jyFuncCodeEnum) {
        String res = null;
        switch(jyFuncCodeEnum.getCode()) {
            case "WAREHOUSE_SEND_POSITION":
                res = "101";
                break;
        }
        if(StringUtils.isBlank(res)) {
         throw new JyBizException("集齐转换collectionCode不支持的岗位类型" + jyFuncCodeEnum.getName());
        }
        return res;
    }


}
