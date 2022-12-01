package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.jy.annotation.JyAggsType;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.constants.JyAggsTypeEnum;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyComboardAggsDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyAggsDto;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyComboardAggsDto;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsCondition;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsConditionBuilder;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@JyAggsType(JyAggsTypeEnum.JY_COMBOARD_AGGS)
@Slf4j
public class JyComboardAggsServiceImpl implements JyComboardAggsService {

    @Autowired
    private JyComboardAggsDao jyComboardAggsDao;

    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;

    @Value("${local.cache.expire.minutes:30}")
    private Integer LOCAL_CACHE_EXPIRE_MINUTES = null;

    /**
     * 根缓存信息
     */
    public static LoadingCache<JyComboardAggsCondition, JyComboardAggsEntity> COMBOARD_AGGS_CACHE = null;

    @PostConstruct
    public void init() {
        COMBOARD_AGGS_CACHE = CacheBuilder
                .newBuilder()
                .initialCapacity(2000)
                .concurrencyLevel(10)
                .expireAfterWrite(LOCAL_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES)
                .maximumSize(200_000L)
                .softValues()
                .build(new CacheLoader<JyComboardAggsCondition, JyComboardAggsEntity>() {
                    @Override
                    public JyComboardAggsEntity load(JyComboardAggsCondition comboardAggsCondition) throws Exception {
                        String redisKey = comboardAggsCondition.redisKey();
                        String resultStr = redisClientCache.get(redisKey);
                        if (StringUtils.isNotBlank(resultStr)) {
                            return JsonHelper.fromJsonUseGson(resultStr, new TypeToken<List<JyComboardAggsEntity>>(){}.getType());
                        }
                        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(comboardAggsCondition);
                        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)) {
                            redisClientCache.setEx(redisKey,JsonHelper.toJson(jyComboardAggsEntities.get(0)), LOCAL_CACHE_EXPIRE_MINUTES, TimeUnit.MINUTES);
                        }
                        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)) {
                            return jyComboardAggsEntities.get(0);
                        }
                        return null;
                    }
                });
    }
    @Override
    public boolean saveAggs(Message message) {
        JyComboardAggsDto dto = JsonHelper.fromJson(message.getText(), JyComboardAggsDto.class);
        try {
            saveOrUpdate(dto);
            //冗余包裹、箱号数量
            redundancyPackageAndBox(dto);
            return true;
        }catch (Exception e){
            log.error("saveAggs error :param json 【{}】",JsonHelper.toJson(dto),e);
            return false;
        }
    }
    //冗余包裹、箱号数量
    private void redundancyPackageAndBox(JyComboardAggsDto dto) {
        if (Objects.equals(dto.getScanType(),JyComboardAggsConditionBuilder.DEFAULT_CONDITON_INTEGER_VALUE)){
            return;
        }
        JyComboardAggsCondition conditon = new JyComboardAggsConditionBuilder().operateSiteId(dto.getOperateSiteId())
                .receiveSiteId(dto.getReceiveSiteId())
                .bizId(dto.getBizId()).boardCode(dto.getBoardCode()).productType(dto.getProductType()).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(conditon);

        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)){
            JyComboardAggsEntity jyComboardAggsEntity = jyComboardAggsEntities.get(0);
            if (Objects.equals(dto.getScanType(),ReportTypeEnum.PACKAGE_CODE.getCode())){
                jyComboardAggsEntity.setPackageScannedCount(dto.getScannedCount());
            }else if (Objects.equals(dto.getScanType(),ReportTypeEnum.BOX_CODE.getCode())){
                jyComboardAggsEntity.setBoxScannedCount(dto.getScannedCount());
            }
            jyComboardAggsDao.updateByPrimaryKeyAndTs(jyComboardAggsEntity);
        }else{
            // 初始化
            JyComboardAggsEntity jyComboardAggsEntity = new JyComboardAggsEntity();
            jyComboardAggsEntity.setOperateSiteId(dto.getOperateSiteId());
            jyComboardAggsEntity.setReceiveSiteId(dto.getReceiveSiteId());
            jyComboardAggsEntity.setCreateTime(dto.getCreateTime());
            jyComboardAggsEntity.setBizId(dto.getBizId());
            jyComboardAggsEntity.setBoardCode(dto.getBoardCode());
            jyComboardAggsEntity.setProductType(dto.getProductType());
            jyComboardAggsEntity.setScanType(JyComboardAggsConditionBuilder.DEFAULT_CONDITON_INTEGER_VALUE);
            if (Objects.equals(dto.getScanType(),ReportTypeEnum.PACKAGE_CODE.getCode())){
                jyComboardAggsEntity.setPackageScannedCount(dto.getScannedCount());
            }else if (Objects.equals(dto.getScanType(),ReportTypeEnum.BOX_CODE.getCode())){
                jyComboardAggsEntity.setBoxScannedCount(dto.getScannedCount());
            }
            jyComboardAggsDao.insertSelective(jyComboardAggsEntity);
        }
    }

    private void saveOrUpdate(JyComboardAggsDto dto) {
        JyComboardAggsCondition conditon = new JyComboardAggsConditionBuilder().operateSiteId(dto.getOperateSiteId())
                .receiveSiteId(dto.getReceiveSiteId())
                .bizId(dto.getBizId()).boardCode(dto.getBoardCode()).productType(dto.getProductType()).scanType(dto.getScanType())
                .build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(conditon);
        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)){
            JyComboardAggsEntity jyComboardAggsEntity = jyComboardAggsEntities.get(0);
            if (Objects.equals(jyComboardAggsEntity.getWaitScanCount(),dto.getWaitScanCount())&&
                    Objects.equals(jyComboardAggsEntity.getScannedCount(),dto.getScannedCount())&&
                    Objects.equals(jyComboardAggsEntity.getBoardCount(),dto.getBoardCount())&&
                    Objects.equals(jyComboardAggsEntity.getInterceptCount(),dto.getInterceptCount())&&
                    Objects.equals(jyComboardAggsEntity.getMoreScannedCount(),dto.getMoreScannedCount())){
                return;
            }
            jyComboardAggsEntity.setWaitScanCount(dto.getWaitScanCount());
            jyComboardAggsEntity.setScannedCount(dto.getScannedCount());
            jyComboardAggsEntity.setBoardCount(dto.getBoardCount());
            jyComboardAggsEntity.setInterceptCount(dto.getInterceptCount());
            jyComboardAggsEntity.setMoreScannedCount(dto.getMoreScannedCount());
            jyComboardAggsEntity.setTs(dto.getCreateTime());
            jyComboardAggsDao.updateByPrimaryKeyAndTs(jyComboardAggsEntity);
        }else{
            JyComboardAggsEntity jyComboardAggsEntity = new JyComboardAggsEntity();
            jyComboardAggsEntity.setWaitScanCount(dto.getWaitScanCount());
            jyComboardAggsEntity.setScannedCount(dto.getScannedCount());
            jyComboardAggsEntity.setBoardCount(dto.getBoardCount());
            jyComboardAggsEntity.setInterceptCount(dto.getInterceptCount());
            jyComboardAggsEntity.setMoreScannedCount(dto.getMoreScannedCount());
            jyComboardAggsEntity.setOperateSiteId(Integer.valueOf(dto.getOperateSiteId()));
            jyComboardAggsEntity.setReceiveSiteId(Integer.valueOf(dto.getReceiveSiteId()));
            jyComboardAggsEntity.setCreateTime(dto.getCreateTime());
            jyComboardAggsEntity.setBizId(dto.getBizId());
            jyComboardAggsEntity.setBoardCode(dto.getBoardCode());
            jyComboardAggsEntity.setProductType(dto.getProductType());
            jyComboardAggsEntity.setScanType(Integer.valueOf(dto.getScanType()));
            jyComboardAggsDao.insertSelective(jyComboardAggsEntity);
        }
    }

    private JyComboardAggsEntity queryComboardAggsCache(JyComboardAggsCondition comboardAggsCondition) throws Exception {
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(comboardAggsCondition);
        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)){
            return jyComboardAggsEntities.get(0);
        }
        return null;
//        return COMBOARD_AGGS_CACHE.get(comboardAggsCondition);
    }

    @Override
    public JyComboardAggsEntity queryComboardAggs(Integer operateSiteId, Integer receiveSiteId) throws Exception {
        return queryComboardAggsCache(new JyComboardAggsConditionBuilder().operateSiteId(operateSiteId).receiveSiteId(receiveSiteId).build());
    }

    @Override
    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId, List<Integer> receiveSiteIds) throws Exception {
        if (CollectionUtils.isEmpty(receiveSiteIds)) {
            return null;
        }

        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().operateSiteId(operateSiteId).receiveSiteIds(receiveSiteIds).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(condition);
        return jyComboardAggsEntities;
    }

    public JyComboardAggsEntity queryComboardAggs(String boardCode) throws Exception{
        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().boardCode(boardCode).build();
        return queryComboardAggsCache(condition);
    }

    @Override
    public List<JyComboardAggsEntity> queryComboardAggs(List<String> boardCodes) throws Exception {
        if (CollectionUtils.isEmpty(boardCodes)) {
            return null;
        }

        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().boardCodes(boardCodes).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(condition);
        return jyComboardAggsEntities;
    }

    @Override
    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId, Integer receiveSiteId, ReportTypeEnum ... reportTypeEnums) throws Exception {
        if (reportTypeEnums == null || reportTypeEnums.length == 0) {
            return null;
        }
        List<Integer> reprotTypes = Lists.newArrayList();
        for (ReportTypeEnum reportTypeEnum : reportTypeEnums) {
            reprotTypes.add(reportTypeEnum.getCode());
        }
        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().operateSiteId(operateSiteId).receiveSiteId(receiveSiteId).scanTypes(reprotTypes).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(condition);
        return jyComboardAggsEntities;
    }

    @Override
    public List<JyComboardAggsEntity> queryComboardAggs(Integer operateSiteId, Integer receiveSiteId, UnloadProductTypeEnum... productTypeEnums) throws Exception {
        if (productTypeEnums == null || productTypeEnums.length == 0) {
            return null;
        }
        List<String> productTypes = Lists.newArrayList();
        for (UnloadProductTypeEnum productTypeEnum : productTypeEnums) {
            productTypes.add(productTypeEnum.getCode());
        }
        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().operateSiteId(operateSiteId).receiveSiteId(receiveSiteId).productTypes(productTypes).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(condition);
        return jyComboardAggsEntities;
    }

    @Override
    public List<JyComboardAggsEntity> queryComboardAggs(String boardCode, UnloadProductTypeEnum... productTypeEnums) throws Exception {
        if (productTypeEnums == null || productTypeEnums.length == 0) {
            return null;
        }
        List<String> productTypes = Lists.newArrayList();
        for (UnloadProductTypeEnum productTypeEnum : productTypeEnums) {
            productTypes.add(productTypeEnum.getCode());
        }
        JyComboardAggsCondition condition = new JyComboardAggsConditionBuilder().boardCode(boardCode).productTypes(productTypes).build();
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(condition);
        return jyComboardAggsEntities;
    }

}
