package com.jd.bluedragon.common.service;

/**
 * 导出并发限制
 */
public interface ExportConcurrencyLimitService {

   /**
    * 校验导出并发限制接口
    * @param redisKey
    * @return true 可以导出  false:超过并发数 限制导出
    */
   boolean checkConcurrencyLimit(String redisKey);

   /**
    * 获取导出数量限制
    * @return
    */
   Integer uccSpotCheckMaxSize();

   /**
    * 导出单次查询数据库条数限制
    * @return
    */
   Integer getOneQuerySizeLimit();

   /**
    * 进行中并发数+1
    * @param key
    */
   void incrKey(String key);

   /**
    * 进行中并发数-1
    * @param key
    */
   void decrKey(String key);

   /**
    * 导出增加通用操作日志
    * @param requestParam
    * @param methodName
    * @param time
    * @param count
    */
   void addBusinessLog(String requestParam,String methodName,long time,Integer count);
}
