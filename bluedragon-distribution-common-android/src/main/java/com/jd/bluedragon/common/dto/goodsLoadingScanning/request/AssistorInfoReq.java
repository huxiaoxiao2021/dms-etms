package com.jd.bluedragon.common.dto.goodsLoadingScanning.request;

import java.io.Serializable;

/**
 * @program: bluedragon-distribution
 * @description: 协助人信息
 * @author: wuming
 * @create: 2020-10-14 16:09
 */
public class AssistorInfoReq implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 协助人erp
     */
    private String operateUserErp;

    /**
     * 协助人姓名
     */
    private String operateUserName;

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }
}
