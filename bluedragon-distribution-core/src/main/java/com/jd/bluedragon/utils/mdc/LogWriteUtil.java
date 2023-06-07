package com.jd.bluedragon.utils.mdc;

import com.jd.bluedragon.distribution.print.domain.LogDto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author liwenji
 * @description 
 * @date 2023-06-06 15:31
 */
public class LogWriteUtil {
    
    private static final String MDC_LOG_KEY = "mdc_key";
    private static final HashMap<String, List<LogDto>> MDC_LOG_MAP = new HashMap<>();
    
    public static void init() {
        if (!StringUtils.isEmpty(MDC.get(MDC_LOG_KEY))) {
            MDC.clear();
        }
        String key = UUID.randomUUID().toString().replace("-","");
        MDC.put(MDC_LOG_KEY, key);
        MDC_LOG_MAP.put(key, new ArrayList<>());
    }
    
    public static void addLog(LogDto logDto){
        String key = MDC.get(MDC_LOG_KEY);
        if (StringUtils.isEmpty(key)) {
            return;
        }
        List<LogDto> logDtoList = MDC_LOG_MAP.get(key);
        logDtoList.add(logDto);
    }
    
    public static void clear() {
        String key = MDC.get(MDC_LOG_KEY);
        if (!StringUtils.isEmpty(key)) {
            MDC_LOG_MAP.remove(key);
        }
        MDC.clear();
    }
    
    public static Boolean isNeedWriteLog() {
        return !StringUtils.isEmpty(MDC.get(MDC_LOG_KEY));
    }

    public static List<LogDto> getLogList() {
        String key = MDC.get(MDC_LOG_KEY);
        if (StringUtils.isEmpty(key)) {
            return new ArrayList<>();
        }
        return MDC_LOG_MAP.get(key);
    }
}
