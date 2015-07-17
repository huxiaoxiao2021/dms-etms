package com.jd.bluedragon.utils;

import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.distribution.systemLog.service.SystemLogService;

public class SystemLogUtil {
	
    /**
     * 使用日志对象进行记录
     * @param systemLog
     */
    public static int log(SystemLog systemLog) {
       SystemLogService logService = (SystemLogService) SpringHelper.getBean("SystemLogService");
       return logService.add(systemLog);
    }
    
    /**
     * 使用详细信息进行记录
     * @param keyword1
     * @param keyword2
     * @param keyword3
     * @param keyword4
     * @param content
     * @param type
     */
    public static int log(String keyword1, String keyword2, String keyword3, Long keyword4, String content, Long type) {
        SystemLogService logService = (SystemLogService) SpringHelper.getBean("SystemLogService");
        return logService.add(keyword1, keyword2, keyword3, keyword4, content, type);
     }
    
    /**
     * 使用详细信息进行记录
     * @param keyword1
     * @param keyword2
     * @param keyword3
     * @param keyword4
     * @param content
     * @param type
     */
    public static int log(String keyword1, String keyword2, String keyword3, long keyword4, String content, long type) {
        SystemLogService logService = (SystemLogService) SpringHelper.getBean("SystemLogService");
        return logService.add(keyword1, keyword2, keyword3, Long.valueOf(keyword4), content, Long.valueOf(type));
    }
}
