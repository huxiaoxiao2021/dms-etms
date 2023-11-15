package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/11/7 14:43
 * @Description: 返调度省区审核配置
 */
public class ReassignWaybillProvinceAreaApprovalConfigDto implements Serializable {
    private static final long serialVersionUID = 1616353040652677983L;

    /**
     * 配置列表
     */
    private Map<String,List<String>> configList;

    public Map<String, List<String>> getConfigList() {
        return configList;
    }

    public void setConfigList(Map<String, List<String>> configList) {
        this.configList = configList;
    }
}
