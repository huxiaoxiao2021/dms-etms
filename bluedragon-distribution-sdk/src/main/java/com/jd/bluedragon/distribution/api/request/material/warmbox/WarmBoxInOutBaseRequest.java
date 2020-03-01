package com.jd.bluedragon.distribution.api.request.material.warmbox;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.util.List;

/**
 * @ClassName WarmBoxInOutBaseRequest
 * @Description
 * @Author wyh
 * @Date 2020/2/26 16:59
 **/
public class WarmBoxInOutBaseRequest extends JdRequest {

    private static final long serialVersionUID = -2995739994967405432L;

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
}
