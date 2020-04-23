package com.jd.bluedragon.common.dto.material.warmbox.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WarmBoxInboundReq
 * @Description
 * @Author wyh
 * @Date 2020/2/26 16:39
 **/
public class WarmBoxInboundReq implements Serializable {

    private static final long serialVersionUID = -3012240394195681092L;

    private User user;

    private CurrentOperate currentOperate;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 保温箱号集合
     */
    private List<String> warmBoxCodes;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public List<String> getWarmBoxCodes() {
        return warmBoxCodes;
    }

    public void setWarmBoxCodes(List<String> warmBoxCodes) {
        this.warmBoxCodes = warmBoxCodes;
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
}
