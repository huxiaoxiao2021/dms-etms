package com.jd.bluedragon.distribution.api.response;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * @author jinjingcheng
 * @Description
 * @date 2018/6/20.
 */
public class BatchGenerateSendCodeReponse extends JdResponse{

    private static final long serialVersionUID = -8653359411662723973L;
    private List<String> sendCodes;

    public List<String> getSendCodes() {
        return sendCodes;
    }

    public void setSendCodes(List<String> sendCodes) {
        this.sendCodes = sendCodes;
    }
}
