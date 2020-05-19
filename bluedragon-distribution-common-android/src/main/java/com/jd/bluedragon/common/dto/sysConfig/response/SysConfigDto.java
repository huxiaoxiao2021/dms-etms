package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;

/**
 * SysConfigDto
 * 配置表信息
 * @author biyubo1
 * @date 2020/4/22
 */
public class SysConfigDto  implements Serializable {

    private static final long serialVersionUID = -1L;

    private Integer configType;
    private String configName;
    private String configContent;
    private Integer configOrder;

    public Integer getConfigType() {
        return configType;
    }

    public void setConfigType(Integer configType) {
        this.configType = configType;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigContent() {
        return configContent;
    }

    public void setConfigContent(String configContent) {
        this.configContent = configContent;
    }

    public Integer getConfigOrder() {
        return configOrder;
    }

    public void setConfigOrder(Integer configOrder) {
        this.configOrder = configOrder;
    }
}
