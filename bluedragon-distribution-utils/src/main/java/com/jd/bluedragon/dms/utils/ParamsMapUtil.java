package com.jd.bluedragon.dms.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数对象工具
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-07-15 14:25:15 周四
 */
public class ParamsMapUtil {

    private Map<String, String> paramsMap = new HashMap<String, String>();

    public Map<String, String> put(String key, String value){
        this.paramsMap.put(key, value);
        return this.paramsMap;
    }
}
