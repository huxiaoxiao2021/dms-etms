package com.jd.bluedragon.common.dto.base.request;

import java.io.Serializable;

/**
 * 基础请求参数
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2021-11-15 20:54:46 周一
 */
public class BaseRequest implements Serializable {

    private static final long serialVersionUID = 8554023317645676388L;

    /**
     * 操作用户信息
     */
    private OperateUser operateUser;

    /**
     * 操作时间
     */
    private Long operateTime;

    /**
     * 业务来源
     */
    private Integer bizSource;

    public BaseRequest() {
    }

    public OperateUser getOperateUser() {
        return operateUser;
    }

    public BaseRequest setOperateUser(OperateUser operateUser) {
        this.operateUser = operateUser;
        return this;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public BaseRequest setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
        return this;
    }

    public Integer getBizSource() {
        return bizSource;
    }

    public BaseRequest setBizSource(Integer bizSource) {
        this.bizSource = bizSource;
        return this;
    }
}
