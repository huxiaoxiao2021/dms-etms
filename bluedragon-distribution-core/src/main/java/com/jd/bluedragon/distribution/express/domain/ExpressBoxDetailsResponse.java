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

    /**
     * 箱明细
     */
    private List<ExpressBoxDetail> boxDetails;

    /**
     * 箱包数量
     */
    private int boxSize;

    public int getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(int boxSize) {
        this.boxSize = boxSize;
    }

    public List<ExpressBoxDetail> getBoxDetails() {
        return boxDetails;
    }

    public void setBoxDetails(List<ExpressBoxDetail> boxDetails) {
        this.boxDetails = boxDetails;
    }
}

