package com.jd.bluedragon.common.dto.inspection.response;

/**
 * waybillCancel拦截结果
 *
 * @author hujiping
 * @date 2024/3/22 2:23 PM
 */
public class WaybillCancelResultDto {

    /**
     * 是否拦截标识
     */
    private boolean interceptFlag;

    /**
     * 拦截编码
     */
    private Integer interceptCode;

    /**
     * 拦截提示语
     */
    private String interceptMsg;

    public boolean getInterceptFlag() {
        return interceptFlag;
    }

    public void setInterceptFlag(boolean interceptFlag) {
        this.interceptFlag = interceptFlag;
    }

    public Integer getInterceptCode() {
        return interceptCode;
    }

    public void setInterceptCode(Integer interceptCode) {
        this.interceptCode = interceptCode;
    }

    public String getInterceptMsg() {
        return interceptMsg;
    }

    public void setInterceptMsg(String interceptMsg) {
        this.interceptMsg = interceptMsg;
    }
}
