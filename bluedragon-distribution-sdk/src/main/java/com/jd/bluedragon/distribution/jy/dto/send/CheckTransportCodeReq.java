package com.jd.bluedragon.distribution.jy.dto.send;


import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;

public class CheckTransportCodeReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = 5809455684081224009L;
    /**
     * 任务编码
     */
    private String transWorkItemCode;

    /**
     * 运力编码
     */
    private String transportCode;


    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
