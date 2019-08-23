package com.jd.bluedragon.distribution.reprint.service.impl;

import com.jd.bluedragon.KeyConstants;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.reprint.dao.ReprintRecordDao;
import com.jd.bluedragon.distribution.reprint.domain.ReprintRecord;
import com.jd.bluedragon.distribution.reprint.service.ReprintRecordService;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class ReprintRecordServiceImpl implements ReprintRecordService {

    private final Logger logger = LoggerFactory.getLogger(ReprintRecordServiceImpl.class);

    @Autowired
    private ReprintRecordDao rePrintRecordDao;

    @Autowired
    private RedisManager redisManager;

    @Override
    public boolean isBarCodeRePrinted(String barCode) {
        //读取redis的缓存记录
        String cachedKey = KeyConstants.genConstantsKey(KeyConstants.REDIS_PREFIX_KEY_PACK_REPRINT, barCode);
        String barCodeCached = redisManager.getCache(cachedKey);
        //如果缓存有记录
        if (StringHelper.isNotEmpty(barCodeCached)) {
            return true;
        }
        //查询运单是否有补打记录
        int count = rePrintRecordDao.getCountByCondition(barCode);
        if (count > 0) {
            //1小时缓存
            redisManager.setex(cachedKey, 3600, barCode);
            return true;
        }

        return false;
    }

    @Override
    public void insertRePrintRecord(ReprintRecord rePrintRecord) {
        try {
            Date date = new Date();
            rePrintRecord.setOperateTime(date);
            rePrintRecord.setCreateTime(date);
            rePrintRecordDao.add(rePrintRecord);
        } catch (Exception e) {
            logger.error("插入包裹补打记录表异常", e);
        }
    }
}
