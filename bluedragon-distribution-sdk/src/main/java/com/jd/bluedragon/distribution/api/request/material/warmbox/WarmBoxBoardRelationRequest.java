package com.jd.bluedragon.distribution.api.request.material.warmbox;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @ClassName WarmBoxBoardRelationRequest
 * @Description
 * @Author wyh
 * @Date 2020/2/26 15:27
 **/
public class WarmBoxBoardRelationRequest extends JdRequest {

    private static final long serialVersionUID = 8103048971766699396L;

    /**
     * 板号
     */
    private String boardCode;

    private String userErp;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
