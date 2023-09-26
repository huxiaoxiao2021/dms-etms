//package com.jd.bluedragon.distribution.jy.service.task;
//
//import com.jd.bluedragon.common.lock.redis.JimDbLock;
//import com.jd.jim.cli.Cluster;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.stereotype.Service;
//
///**
// * @Author zhengchengfa
// * @Date 2023/8/3 14:58
// * @Description  摆渡干支任务绑定缓存服务
// */
//@Service
//public class JyBizTaskBindCacheService {
//
//    private static final Logger log = LoggerFactory.getLogger(JyBizTaskBindCacheService.class);
//
//    public static final String DEFAULT_VALUE_1 = "1";
//
//
//
//
//    /**
//     * K:bizId V：erp
//     */
////    public static final String LOCK_TASK_BIND_BIZID = "lock:task:bind:bizId:%s";
////    public static final int LOCK_TASK_BIND_BIZID_TIMEOUT_SECONDS = 30;
//
//
//    @Autowired
//    private JimDbLock jimDbLock;
//    @Qualifier("redisClientOfJy")
//    private Cluster redisClientOfJy;
//
////
////    /**
////     * 并发锁
////     * 场景：摆渡任务绑定传摆任务
////     * @param bizId  任务bizId
////     */
////    public boolean lockTaskBindBizId(String bizId) {
////        try {
////            String lockKey = this.getLockKeyTaskBindBizId(bizId);
////            return jimDbLock.lock(lockKey,
////                    DEFAULT_VALUE_1,
////                    JyBizTaskBindCacheService.LOCK_TASK_BIND_BIZID_TIMEOUT_SECONDS,
////                    TimeUnit.SECONDS);
////        }catch (Exception e) {
////            log.error("lockTaskBindBizId:任务绑定加锁失败:bizId={},errMsg={}", bizId, e.getMessage(), e);
////            throw new JyBizException("任务绑定加锁失败");
////        }
////    }
////    public void unlockTaskBindBizId(String bizId) {
////        String lockKey = this.getLockKeyTaskBindBizId(bizId);
////        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
////    }
////    private String getLockKeyTaskBindBizId(String bizId) {
////        return String.format(JyBizTaskBindCacheService.LOCK_TASK_BIND_BIZID, bizId);
////    }
//
//
//
//
//}
