package com.jd.bluedragon.common.dto.material.warmbox.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;

/**
 * @ClassName WarmBoxBoardRelationReq
 * @Description
 * @Author wyh
 * @Date 2020/2/26 15:27
 **/
public class WarmBoxBoardRelationReq implements Serializable {

    private static final long serialVersionUID = 8103048971766699396L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 板号
     */
    private String boardCode;

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
}
