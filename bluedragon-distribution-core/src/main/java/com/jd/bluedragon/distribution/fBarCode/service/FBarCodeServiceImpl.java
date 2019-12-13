package com.jd.bluedragon.distribution.fBarCode.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.objectid.IGenerateObjectId;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.fBarCode.dao.FBarCodeDao;
import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.List;

/**
 * 获取返单条码
 */
@Service("fBarCodeService")
class FBarCodeServiceImpl implements FBarCodeService  {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final int timeout = 172800;

    @Autowired
    private FBarCodeDao fBarCodeDao;

    @Autowired
    private IGenerateObjectId genObjectId;

    @Autowired
    RedisManager redisManager;

    @Autowired
    BaseMinorManager baseMinorManager;

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer add(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
       // return 1;
        return this.fBarCodeDao.add(FBarCodeDao.namespace, fBarCode);
    }

    @JProfiler(jKey= "DMSWEB.FBarCodeService.batchAdd",mState = {JProEnum.TP})
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public List<FBarCode> batchAdd(FBarCode param) {
        List<FBarCode> fBarCodees = Lists.newArrayList();
        String fBarCodeCodePrefix = this.generateFBarCodeCodePrefix(param);

        for (Integer loop = 0; loop < param.getQuantity(); loop++) {
            String fBarCodeCodeSuffix = StringHelper.padZero(this.genObjectId.getObjectId(this
                    .generateKey(param)),11);
            FBarCode fBarCode = new FBarCode();
            BeanHelper.copyProperties(fBarCode, param);
            fBarCode.setCode(fBarCodeCodePrefix + fBarCodeCodeSuffix);
            fBarCodees.add(fBarCode);

            this.add(fBarCode);
            try {
                //写入F条码之后添加缓存
                //keyF条码
                fBarCode.setStatus(1);
                redisManager.setex(fBarCode.getCode(), timeout,
                        JsonHelper.toJson(fBarCode));
            } catch (Exception e) {
                this.log.error("打印F条码写入缓存失败",e);
            }
        }

        return fBarCodees;
    }

    private String generateFBarCodeCodePrefix(FBarCode fBarCode) {
        return "F";
    }

    private String generateKey(FBarCode fBarCode) {
        return FBarCode.class.getName();
    }

    @JProfiler(jKey= "DMSWEB.FBarCodeService.findFBarCodeByCode", mState = {JProEnum.TP})
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public FBarCode findFBarCodeByCode(String code) {
        Assert.notNull(code, "code must not be null");
        CallerInfo info = Profiler.registerInfo("DMSWEB.FBarCodeService.findFBarCodeByCode.fromRedis", Constants.UMP_APP_NAME_DMSWEB,false, true);
        try {
            // 取出缓存
            // keyF条码
            String fBarCodeJson = redisManager.getCache(code);
            FBarCode fBarCode = null;

            if (fBarCodeJson != null && !fBarCodeJson.isEmpty()) {
                fBarCode = JsonHelper.fromJson(fBarCodeJson, FBarCode.class);
                if (fBarCode != null) {
                    this.log.info("findFBarCodeByCode缓存命中F条码为:{}" , code);
                    //如果F条码 目的地 始发地不为空的时候
                    if (fBarCode.getCode() != null && fBarCode.getCreateSiteCode() != null) {
                        return fBarCode;
                    }
                } else {
                    this.log.info("findFBarCodeByCode没有缓存命中F条码为:{}" , code);
                }
            } else {
                this.log.info("findFBarCodeByCode缓存命中,但是消息为null,F条码为:{}" , code);
            }


        } catch (Exception e) {
            Profiler.functionError(info);
            this.log.error("findFBarCodeByCode获取缓存F条码失败，F条码为:{}" , code, e);
        }finally {
            Profiler.registerInfoEnd(info);
        }

        return this.fBarCodeDao.findFBarCodeByCode(code);
    }

    public FBarCode findFBarCodeByFBarCodeCode(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
        return this.fBarCodeDao.findFBarCodeByFBarCodeCode(fBarCode);
    }

    public List<FBarCode> findFBarCodeesBySite(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
        return this.fBarCodeDao.findFBarCodeesBySite(fBarCode);
    }

    public List<FBarCode> findFBarCodees(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
        return this.fBarCodeDao.findFBarCodees(fBarCode);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer print(FBarCode fBarCode) {
        Assert.notNull(fBarCode.getUpdateUserCode(), "fBarCode updateUsercode must not be null");
        Assert.notNull(fBarCode.getUpdateUser(), "fBarCode updateUser must not be null");
        Assert.notNull(fBarCode.getCode(), "fBarCode code must not be null");
        return this.fBarCodeDao.print(fBarCode);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer reprint(FBarCode fBarCode) {
        Assert.notNull(fBarCode.getUpdateUserCode(), "fBarCode updateUsercode must not be null");
        Assert.notNull(fBarCode.getUpdateUser(), "fBarCode updateUser must not be null");
        Assert.notNull(fBarCode.getCode(), "fBarCode code must not be null");
        return this.fBarCodeDao.reprint(fBarCode);
    }

    @Override
    public FBarCode findFBarCodeCacheByCode(String fBarCodeCode) {

        Assert.notNull(fBarCodeCode, "fBarCodeCode must not be null");

        try {
            // 取出缓存
            // keyF条码
            String fBarCodeJson = redisManager.getCache(fBarCodeCode);
            FBarCode fBarCode = null;

            if (fBarCodeJson != null && !fBarCodeJson.isEmpty()) {
                fBarCode = JsonHelper.fromJson(fBarCodeJson, FBarCode.class);
                if (fBarCode != null) {
                    this.log.info("findFBarCodeByCode缓存命中F条码为:{}" , fBarCodeCode);
                    if (fBarCode.getCode() != null && fBarCode.getCreateSiteCode() != null) {
                        return fBarCode;
                    }
                }
            }

        } catch (Exception e) {
            this.log.error("findFBarCodeByCode获取缓存F条码失败，F条码为:{}" , fBarCodeCode, e);
        }

        return null;

    }

    public Long delfBarCodeCodeCache(String fBarCodeCode) {
        Long resulte = 0L;
        try {
            resulte = redisManager.del(fBarCodeCode);
        } catch (Exception e) {
            this.log.error("delfBarCodeCodeCache删除缓存失败，F条码为:{}" , fBarCodeCode, e);
        }
        return resulte;
    }
}