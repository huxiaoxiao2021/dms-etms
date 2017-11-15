package com.jd.bluedragon.distribution.express.domain;

import com.jd.bluedragon.distribution.api.JdResponse;

import java.util.List;

/**
 * 快运到齐箱明细实体
 *
 * @author zhangleqi
 * @date 2017/11/14
 */
public class ExpressBoxDetailsResponse extends JdResponse {

    private static final long serialVersionUID = 9015640463647589701L;

    public ExpressBoxDetailsResponse(Integer code, String message) {
        super(code, message);
    }

    public ExpressBoxDetailsResponse() {
    }

    private List<ExpressBoxDetail> expressBoxDetails;

    public List<ExpressBoxDetail> getExpressBoxDetails() {
        return expressBoxDetails;
    }

    public void setExpressBoxDetails(List<ExpressBoxDetail> expressBoxDetails) {
        this.expressBoxDetails = expressBoxDetails;
    }
}

