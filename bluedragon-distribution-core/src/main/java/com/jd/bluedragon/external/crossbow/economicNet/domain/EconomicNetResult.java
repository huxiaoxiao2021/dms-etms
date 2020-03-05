package com.jd.bluedragon.external.crossbow.economicNet.domain;

import java.util.List;

/**
 * <p>
 *
 * @author wuzuxiang
 * @since 2020/1/16
 **/
public class EconomicNetResult<T> {

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 0000	上传成功
     * 1001	签名验证失败
     * 1002	请求参数有空
     * 1003	合作者ID不存在
     * 1004	违返唯一约束
     * 1005	部份成功
     * 1006	其他
     * 9999	服务器异常
     */
    private String code;

    /**
     * 消息
     */
    private String msg;

    /**
     * 数据对象
     */
    private List<T> data;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }
}
