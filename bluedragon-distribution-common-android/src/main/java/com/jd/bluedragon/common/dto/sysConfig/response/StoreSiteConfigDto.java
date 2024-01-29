package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StoreSiteConfigDto implements Serializable {

    private static final long serialVersionUID = 5558432835220167498L;

    /**
     * 配置列表
     */
    private HashMap<Integer, List<Integer>> configMap;


    public HashMap<Integer, List<Integer>> getConfigMap() {
        return configMap;
    }

    public void setConfigMap(HashMap<Integer, List<Integer>> configMap) {
        this.configMap = configMap;
    }
}
