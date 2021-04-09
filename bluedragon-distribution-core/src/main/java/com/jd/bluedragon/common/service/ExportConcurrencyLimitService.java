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
}
