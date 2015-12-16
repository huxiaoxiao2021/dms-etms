package com.jd.bluedragon.distribution.fBarCode.service;

import com.google.common.collect.Lists;
import com.jd.bluedragon.core.base.BaseMinorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.fBarCode.dao.FBarCodeDao;
import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;
import com.jd.bluedragon.utils.BeanHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.dbs.objectId.IGenerateObjectId;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String separator = "$";

    private static final String siteType = "1024";

    private static final int timeout = 172800;

    @Autowired
    private FBarCodeDao fBarCodeDao;

    @Autowired
    private BaseService baseService;

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
                this.logger.error("打印F条码写入缓存失败",e);
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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Integer updateStatusByCodes(FBarCode fBarCode) {
        return this.fBarCodeDao.updateStatusByCodes(fBarCode);
    }

    @JProfiler(jKey= "DMSWEB.FBarCodeService.findFBarCodeByCode", mState = {JProEnum.TP})
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public FBarCode findFBarCodeByCode(String code) {
        Assert.notNull(code, "code must not be null");

        try {
            // 取出缓存
            // keyF条码
            String fBarCodeJson = redisManager.getCache(code);
            FBarCode fBarCode = null;

            if (fBarCodeJson != null && !fBarCodeJson.isEmpty()) {
                fBarCode = JsonHelper.fromJson(fBarCodeJson, FBarCode.class);
                if (fBarCode != null) {
                    this.logger.info("findFBarCodeByCode缓存命中F条码为" + code);
                    //如果F条码 目的地 始发地不为空的时候
                    if (fBarCode.getCode() != null && fBarCode.getCreateSiteCode() != null) {
                        return fBarCode;
                    }
                } else {
                    this.logger.info("findFBarCodeByCode没有缓存命中F条码为" + code);
                }
            } else {
                this.logger.info("findFBarCodeByCode缓存命中,但是消息为null,F条码为" + code);
            }


        } catch (Exception e) {
            this.logger.error("findFBarCodeByCode获取缓存F条码失败，F条码为" + code, e);
        }

        return this.fBarCodeDao.findFBarCodeByCode(code);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public FBarCode findFBarCodeByFBarCodeCode(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
        return this.fBarCodeDao.findFBarCodeByFBarCodeCode(fBarCode);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<FBarCode> findFBarCodeesBySite(FBarCode fBarCode) {
        Assert.notNull(fBarCode, "fBarCode must not be null");
        return this.fBarCodeDao.findFBarCodeesBySite(fBarCode);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
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
                    this.logger.info("findFBarCodeByCode缓存命中F条码为" + fBarCodeCode);
                    if (fBarCode.getCode() != null && fBarCode.getCreateSiteCode() != null) {
                        return fBarCode;
                    }
                }
            }

        } catch (Exception e) {
            this.logger.error("findFBarCodeByCode获取缓存F条码失败，F条码为" + fBarCodeCode, e);
        }

        return null;

    }

    public Long delfBarCodeCodeCache(String fBarCodeCode) {
        Long resulte = 0L;
        try {
            resulte = redisManager.del(fBarCodeCode);
        } catch (Exception e) {
            this.logger.error("delfBarCodeCodeCache删除缓存失败，F条码为" + fBarCodeCode, e);
        }
        return resulte;
    }
}