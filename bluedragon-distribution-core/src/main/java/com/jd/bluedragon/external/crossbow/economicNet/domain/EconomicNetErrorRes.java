package com.jd.bluedragon.external.crossbow.economicNet.domain;

/**
 * <p>
 *     经济网接口返回失败时的错误列表
 *
 * @author wuzuxiang
 * @since 2020/1/17
 **/
public class EconomicNetErrorRes {

    /**
     * 调用记录编号
     */
    private String id;

    /**
     * 消息内容
     */
    private String msg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
