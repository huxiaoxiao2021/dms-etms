package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

/**
 * @ClassName WarmBoxBoardRelationReq
 * @Description
 * @Author wyh
 * @Date 2020/2/26 15:27
 **/
public class WarmBoxBoardRelationReqVO extends JdRequest {

    private static final long serialVersionUID = 8103048971766699396L;

    /**
     * 板号
     */
    private String boardCode;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }
}
