package com.jd.bluedragon.common.dto.sysConfig.response;

import java.io.Serializable;
import java.util.List;

/**
 * 安卓功能是否可使用结果
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-03-22 23:45:31 周三
 */
public class GlobalFuncUsageControlDto implements Serializable {

    private static final long serialVersionUID = 5558432835220167498L;

    /**
     * 需要管控的功能编码列表
     */
    private List<String> funcCodes;

    public List<String> getFuncCodes() {
        return funcCodes;
    }

    public void setFuncCodes(List<String> funcCodes) {
        this.funcCodes = funcCodes;
    }
}
