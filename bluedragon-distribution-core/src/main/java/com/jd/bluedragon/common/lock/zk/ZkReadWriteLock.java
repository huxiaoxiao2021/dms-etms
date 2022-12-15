package com.jd.bluedragon.common.lock.zk;

import com.jd.ql.dms.common.lock.AbstractLock;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.locks.InterProcessLock;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;
import org.apache.curator.retry.RetryNTimes;


/**
 * 读写互斥-写写互斥-读读不互斥
 */
@Slf4j
public class ZkReadWriteLock extends AbstractLock {

  /**
   * 1.Connect to zk
   */
  private CuratorFramework client;

  private InterProcessReadWriteLock lock;


  public ZkReadWriteLock(String zkAddress, String lockPath) {
    // 1.Connect to zk
    client = CuratorFrameworkFactory.newClient(
        zkAddress,
        new RetryNTimes(5, 5000)
    );
    client.start();
    if (client.getState() == CuratorFrameworkState.STARTED) {
      log.info("zk client start successfully!");
      log.info("zkAddress:{},lockPath:{}", zkAddress, lockPath);
    } else {
      throw new RuntimeException("客户端启动失败。。。");
    }
    this.lock = defaultLock(lockPath);
  }

  private InterProcessReadWriteLock defaultLock(String lockPath) {
    return new InterProcessReadWriteLock(client, lockPath);
  }

}
