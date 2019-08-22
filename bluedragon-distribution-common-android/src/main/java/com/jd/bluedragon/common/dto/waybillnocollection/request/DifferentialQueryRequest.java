package com.jd.bluedragon.common.dto.waybillnocollection.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * DifferentialQueryRequest
 * 一车一单发货、组板、建箱差异查询请求
 *
 * @author jiaowenqiang
 * @date 2019/7/5
 */
public class DifferentialQueryRequest implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * 查询编号
     */
    private String queryCode;

    /**
     * 查询类型
     * 1.按批次号查询
     * 2.按板号查询
     * 3.按箱号查询
     */
    private Integer queryType;


    /**
     * 目的地站点
     */
    private Integer receiveSiteCode;


    /**
     * 操作站点
     */
    private CurrentOperate currentOperate;

    /**
     * 用户
     */
    private User user;

    @Override
    public String toString() {
        return "DifferentialQueryRequest{" +
                "queryCode='" + queryCode + '\'' +
                ", queryType=" + queryType +
                ", receiveSiteCode=" + receiveSiteCode +
                ", currentOperate=" + currentOperate +
                ", user=" + user +
                '}';
    }

    public String getQueryCode() {
        return queryCode;
    }

    public void setQueryCode(String queryCode) {
        this.queryCode = queryCode;
    }

    public Integer getQueryType() {
        return queryType;
    }

    public void setQueryType(Integer queryType) {
        this.queryType = queryType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
