package com.jd.bluedragon.common.dto.board.request;


import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * CombinationBoardRequest
 * 组板请求信息
 *
 * @author jiaowenqiang
 * @date 2019/7/8
 */
public class CombinationBoardRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户
     */
    private User user;

    /**
     * 站点
     */
    private CurrentOperate currentOperate;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 目的地编码
     */
    private Integer receiveSiteCode;

    /**
     * 目的地名称
     */
    private String receiveSiteName;

    /**
     * 箱号或包裹号
     */
    private String boxOrPackageCode;

    /**
     * 是否强制组板
     */
    private boolean forceCombination;

    @Override
    public String toString() {
        return "CombinationBoardRequest{" +
                "user=" + user +
                ", currentOperate=" + currentOperate +
                ", boardCode='" + boardCode + '\'' +
                ", receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", boxOrPackageCode='" + boxOrPackageCode + '\'' +
                ", forceCombination=" + forceCombination +
                '}';
    }

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

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getBoxOrPackageCode() {
        return boxOrPackageCode;
    }

    public void setBoxOrPackageCode(String boxOrPackageCode) {
        this.boxOrPackageCode = boxOrPackageCode;
    }

    public boolean isForceCombination() {
        return forceCombination;
    }

    public void setForceCombination(boolean forceCombination) {
        this.forceCombination = forceCombination;
    }
}
