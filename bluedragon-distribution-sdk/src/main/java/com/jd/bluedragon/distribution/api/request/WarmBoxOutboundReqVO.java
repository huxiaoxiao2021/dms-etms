package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName WarmBoxOutboundReq
 * @Description
 * @Author wyh
 * @Date 2020/2/26 16:39
 **/
public class WarmBoxOutboundReqVO extends JdRequest {

    private static final long serialVersionUID = 7972726093908770043L;

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
    
    private String userErp;

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

    public Byte getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(Byte outboundType) {
        this.outboundType = outboundType;
    }

    public String getUserErp() {
        return userErp;
    }

    public void setUserErp(String userErp) {
        this.userErp = userErp;
    }
}
