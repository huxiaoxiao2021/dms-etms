package com.jd.bluedragon.distribution.log.service;

import java.util.List;

import com.jd.bluedragon.distribution.log.BusinessLogDto;

/**
 * 日志管理
 * 
 * @author wuyoude
 *
 */
public interface BusinessLogManager {
	/**
	 * 写入日志
	 * @param log
	 * @return
	 */
    boolean addLog(BusinessLogDto log);
    /**
     * 查询日志
     * @param businessKey
     * @return
     */
    List<BusinessLogDto> queryLogs(String businessKey);
    /**
     * 查询日志
     * @param businessKey
     * @param operateType
     * @return
     */
    List<BusinessLogDto> queryLogs(String businessKey,Integer operateType);
}
