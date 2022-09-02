package com.jd.bluedragon.common.service;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.ResultHandler;
import com.jd.bluedragon.distribution.lock.LockService;
import com.jd.ql.dms.common.cache.CacheService;

/**
 * jimdb 实现的分布式加锁
 * @author wuyoude
 *
 */
@Service("jimdbRemoteLockService")
public class JimdbRemoteLockServiceImpl implements LockService{
	
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;
	/**
	 * 默认过期时间为30秒
	 */
	private long exTime = 30*1000;
	
	@Override
	public void tryLock(String key, ResultHandler handler) {
		this.tryLock(key, exTime, handler);
	}
	@Override
	public void tryLock(String key,long timeOut, ResultHandler handler) {
		boolean lockSuc = false;
		if(handler == null) {
			throw new RuntimeException("tryLock参数ResultHandler不能为空！");
		}
		try {
			lockSuc = cacheService.setNx(key, Constants.FLAG_OPRATE_ON, timeOut,TimeUnit.MILLISECONDS);
			if(lockSuc) {
				handler.success();
			}else {
				handler.fail();
			}
		}catch (Exception e) {
			handler.error();
		}finally {
			if(lockSuc) {
				cacheService.del(key);
			}
		}
	}
}
