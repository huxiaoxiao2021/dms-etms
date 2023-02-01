package com.jd.bluedragon.common.lock.redis;

import com.jd.common.util.StringUtils;
import com.jd.jim.cli.Cluster;
import com.jd.tp.common.utils.Objects;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class JimDbLock {

  private static final Logger log = LoggerFactory.getLogger(JimDbLock.class);
  @Autowired
  @Qualifier("redisClientOfJy")
  private Cluster redisClient;
  private final long BIZ_TIMEOUT =3000;

  public boolean tryLock(String key, String value, long expire, TimeUnit timeUnit) {
    if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value) || expire <= 0 || Objects.isNull(timeUnit)) {
      log.info("参数错误，获取锁失败 key:{}", key);
      return false;
    }

    try {
      boolean result = redisClient.set(key, value, expire, timeUnit, false);
      return result;
    } catch (Exception e) {
      if (redisClient.exists(key) && value.equals(redisClient.get(key))) {
        return true;
      }
      log.error("tryLock error and key is {}, value is {}, timeUnit is {}, expireTime is {}", new Object[]{key, value, timeUnit, expire, e});
      return false;
    }
  }

  public boolean lock(String key, String value, long expire, TimeUnit timeUnit) {
    Long startMillis = System.currentTimeMillis();
    boolean isLock;
    int tryCount = 0;

    do {
      if (tryCount++ > 0) {
        try {
          Thread.sleep(100);
        } catch (InterruptedException e) {
        }
      }
      isLock = tryLock(key, value, expire, timeUnit);
    } while (!isLock && startMillis + BIZ_TIMEOUT > System.currentTimeMillis());
    return isLock;
  }

  /**
   * 释放锁操作
   */
  public void releaseLock(String key, String value) {
    if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
      log.info("参数问题，无法释放锁 key:{} value:{}", key, value);
    }
    if (redisClient.exists(key) && value.equals(redisClient.get(key))) {
      redisClient.del(key);
    }
    log.info("释放分布式锁参数：key:{} value:{}", key, value);
  }


}
