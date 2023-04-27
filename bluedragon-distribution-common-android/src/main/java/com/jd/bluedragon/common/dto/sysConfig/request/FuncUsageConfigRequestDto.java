package com.jd.bluedragon.common.dto.sysConfig.request;

import com.jd.bluedragon.common.dto.base.request.OperateUser;

import java.io.Serializable;

/**
 * 安卓功能是否可使用条件配置 请求参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2022-04-11 17:38:02 周一
 */
public class FuncUsageConfigRequestDto implements Serializable {
    private static final long serialVersionUID = 7929667345809176046L;

    /**
     * 系统编码
     */
    private String systemCode;

    /**
     * 功能编码
     */
    private String funcCode;

    private OperateUser operateUser;

    public String getSystemCode() {
        return systemCode;
    }

    public void setSystemCode(String systemCode) {
        this.systemCode = systemCode;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
    }
}
