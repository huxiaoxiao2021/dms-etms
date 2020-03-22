package com.jd.bluedragon.common.dto.material.warmbox.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WarmBoxOutboundReq
 * @Description
 * @Author wyh
 * @Date 2020/2/26 16:39
 **/
public class WarmBoxOutboundReq implements Serializable {

    private static final long serialVersionUID = 7972726093908770043L;

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

    /**
     * 出入库类型；按板号出：1；按箱号出：2
     */
    private Byte outboundType;

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

    public Byte getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(Byte outboundType) {
        this.outboundType = outboundType;
    }

}
