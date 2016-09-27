package com.jd.bluedragon.distribution.dbs.service;

import com.jd.bluedragon.distribution.dbs.dao.ObjectIdDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by dudong on 2016/9/19.
 */
@Service("objectIdService")
public class ObjectIdServiceImpl implements ObjectIdService{

    @Autowired
    private ObjectIdDao objectIdDao;

    @Override
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Long getFirstId(String objectName, Integer count) {
        Integer rowCount = objectIdDao.updateFirstIdByName(objectName, count);
        if (rowCount == 0) {
            objectIdDao.insertObjectId(objectName, count);
            return 1L;
        } else {
            return Long.valueOf(objectIdDao.selectFirstIdByName(objectName));
        }
    }
}
