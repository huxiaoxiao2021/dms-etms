package com.jd.bluedragon.utils.mdc;

import com.jd.bluedragon.distribution.print.domain.TrackDto;
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
public class TrackUtil {
    
    private static final String MDC_LOG_KEY = "mdc_key";
    private static final HashMap<String, List<TrackDto>> MDC_LOG_MAP = new HashMap<>();
    
    public static void init() {
        if (!StringUtils.isEmpty(MDC.get(MDC_LOG_KEY))) {
            MDC.clear();
        }
        String key = UUID.randomUUID().toString().replace("-","");
        MDC.put(MDC_LOG_KEY, key);
        MDC_LOG_MAP.put(key, new ArrayList<>());
    }

    public static boolean check() {
        String key = MDC.get(MDC_LOG_KEY);
        return StringUtils.isEmpty(key);
    }
    public static void add(TrackDto logDto){
        String key = MDC.get(MDC_LOG_KEY);
        if (StringUtils.isEmpty(key)) {
            return;
        }
        List<TrackDto> logDtoList = MDC_LOG_MAP.get(key);
        logDtoList.add(logDto);
    }
    
    public static void clear() {
        String key = MDC.get(MDC_LOG_KEY);
        if (!StringUtils.isEmpty(key)) {
            MDC_LOG_MAP.remove(key);
        }
        MDC.clear();
    }

    public static List<TrackDto> getLogList() {
        String key = MDC.get(MDC_LOG_KEY);
        if (StringUtils.isEmpty(key)) {
            return new ArrayList<>();
        }
        return MDC_LOG_MAP.get(key);
    }
}
