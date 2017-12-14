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
    /**
     * 更新Id尝试次数
     */
    private static int TRY_MAX_TIMES = 5;
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

	@Override
	public Long getNextId(String objectName) {
		int tryTimes = 0;
		int updateRows = 0;
		Integer currId = null;
		while(updateRows != 1 && tryTimes < TRY_MAX_TIMES){
			currId = objectIdDao.selectFirstIdByName(objectName);
	    	if(currId == null){
	    		updateRows = objectIdDao.insertObjectId(objectName, 1);
	    		currId = 0;
	    	}else{
	    		updateRows = objectIdDao.updateFirstIdByNameAndCurrId(objectName, currId, 1);
	    	}
			++ tryTimes;
		}
		if(updateRows != 1){
			throw new RuntimeException("Fail to getNextId,objectName="+objectName);
		}
    	return Long.valueOf(currId + 1);
	}
}
