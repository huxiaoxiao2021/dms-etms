package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;
import com.jd.bluedragon.distribution.abnormal.domain.ReportTypeEnum;
import com.jd.bluedragon.distribution.jy.annotation.JyAggsType;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardAggsEntity;
import com.jd.bluedragon.distribution.jy.constants.JyAggsTypeEnum;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyComboardAggsDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyComboardAggsDto;
import com.jd.bluedragon.distribution.jy.enums.UnloadProductTypeEnum;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsCondition;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsConditionBuilder;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardAggsService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.jim.cli.Cluster;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import com.jdl.jy.realtime.enums.comboard.ComboardBarCodeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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

    @Value("${local.cache.expire.days:7}")
    private Integer LOCAL_CACHE_EXPIRE_DAYS = null;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    public static final String PREFIXOFLOCK = "DMS_COMBOARD_AGGS_LOCK_";
    public static final String PREFIXOFDATA = "DMS_COMBOARD_AGGS_DATA_";
    public static final Integer LOCK_TTL = 2;
    /**
     * 根缓存信息
     */
    public static LoadingCache<JyComboardAggsCondition, JyComboardAggsEntity> COMBOARD_AGGS_CACHE = null;

    @Override
    public boolean saveAggs(Message message) {
        JyComboardAggsDto dto = JsonHelper.fromJson(message.getText(), JyComboardAggsDto.class);
        try {
            if (!lock(dto)){
                // 获取锁当前数据丢弃
                return false;
            }
            saveOrUpdate(dto);
            //冗余包裹、箱号数量
            redundancyPackageAndBox(dto);
            return true;
        }catch (Exception e){
            log.error("saveAggs error :param json 【{}】",JsonHelper.toJson(dto),e);
            return false;
        }finally {
            unLock(dto);
        }
    }

    private String getLockCashKey(String pre,JyComboardAggsDto dto){
        return  pre + dto.getOperateSiteId() + "-" +
                dto.getReceiveSiteId() + "-" +
                dto.getBizId() + "-" +
                dto.getBoardCode() + "-" +
                dto.getProductType() + "-" +
                dto.getScanType();
    }

    private boolean lock(JyComboardAggsDto dto) {
        String lockKey = getLockCashKey(PREFIXOFLOCK,dto);
        log.info("开始获取锁lockKey={}", lockKey);
        try {
            for(int i =0;i < 4;i++){
                if (jimdbCacheService.setNx(lockKey, org.apache.commons.lang.StringUtils.EMPTY, LOCK_TTL, TimeUnit.SECONDS)){
                    return Boolean.TRUE;
                }else{
                    Thread.sleep(100);
                }
            }
        } catch (Exception e) {
            log.error("组板统计数据Lock异常:JyComboardAggsDto={},e=", JsonHelper.toJson(dto) , e);
            jimdbCacheService.del(lockKey);
        }
        return Boolean.FALSE;
    }
    private void unLock(JyComboardAggsDto dto) {
        try {
            String lockKey = getLockCashKey(PREFIXOFLOCK,dto);
            jimdbCacheService.del(lockKey);
        } catch (Exception e) {
            log.error("组板统计数据unLock异常:JyComboardAggsDto={},e=", JsonHelper.toJson(dto) , e);
        }
    }
    private boolean saveCache(JyComboardAggsDto dto,JyComboardAggsEntity jyComboardAggsEntity){
        if (jyComboardAggsEntity == null){
            return false;
        }
        String dataKey = getLockCashKey(PREFIXOFDATA,dto);
        return jimdbCacheService.setEx(dataKey,JsonHelper.toJson(jyComboardAggsEntity),LOCAL_CACHE_EXPIRE_DAYS,TimeUnit.DAYS);
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
            if (Objects.equals(dto.getScanType(), ComboardBarCodeTypeEnum.PACKAGE.getCode())){
                jyComboardAggsEntity.setPackageScannedCount(dto.getScannedCount());
            }else if (Objects.equals(dto.getScanType(),ComboardBarCodeTypeEnum.BOX.getCode())){
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
            if (Objects.equals(dto.getScanType(),ComboardBarCodeTypeEnum.PACKAGE.getCode())){
                jyComboardAggsEntity.setPackageScannedCount(dto.getScannedCount());
            }else if (Objects.equals(dto.getScanType(),ComboardBarCodeTypeEnum.BOX.getCode())){
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
            saveCache(dto,jyComboardAggsEntity);
        }else{
            if (null == dto.getWaitScanCount()&&
                    null == dto.getScannedCount()&&
                    null == dto.getBoardCount()&&
                    null == dto.getInterceptCount()&&
                    null == dto.getMoreScannedCount()){
                //全为空的数据丢弃
                return;
            }
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
            saveCache(dto,jyComboardAggsEntity);
        }
    }

    private JyComboardAggsEntity queryComboardAggsCache(JyComboardAggsCondition comboardAggsCondition) throws Exception {
        List<JyComboardAggsEntity> jyComboardAggsEntities = jyComboardAggsDao.queryComboardAggs(comboardAggsCondition);
        if (CollectionUtils.isNotEmpty(jyComboardAggsEntities)){
            return jyComboardAggsEntities.get(0);
        }
        return null;
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
