package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @author pengchong28
 * @description 司机违规举报新增req
 * @date 2024/4/12
 */
public class DriverViolationReportingAddRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 登录用户
     */
    private User user;

    /**
     * 操作场地
     */
    private CurrentOperate currentOperate;

    /**
     * 发车任务ID，业务主键
     */
    private String bizId;

    /**
     * 图片ulr集合
     */
    private List<String> imgUrlList;

    /**
     * 视频url
     */
    private String videoUlr;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public List<String> getImgUrlList() {
        return imgUrlList;
    }

    public void setImgUrlList(List<String> imgUrlList) {
        this.imgUrlList = imgUrlList;
    }

    public String getVideoUlr() {
        return videoUlr;
    }

    public void setVideoUlr(String videoUlr) {
        this.videoUlr = videoUlr;
    }
}
