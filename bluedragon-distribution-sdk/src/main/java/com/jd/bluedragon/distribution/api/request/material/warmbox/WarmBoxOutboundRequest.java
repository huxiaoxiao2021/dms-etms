package com.jd.bluedragon.distribution.api.request.material.warmbox;

/**
 * @ClassName WarmBoxOutboundRequest
 * @Description
 * @Author wyh
 * @Date 2020/2/26 16:39
 **/
public class WarmBoxOutboundRequest extends WarmBoxInOutBaseRequest {

    private static final long serialVersionUID = 7972726093908770043L;

    /**
     * 出入库类型；按板号出：1；按箱号出：2
     */
    private Byte outboundType;

    public Byte getOutboundType() {
        return outboundType;
    }

    public void setOutboundType(Byte outboundType) {
        this.outboundType = outboundType;
    }

}
