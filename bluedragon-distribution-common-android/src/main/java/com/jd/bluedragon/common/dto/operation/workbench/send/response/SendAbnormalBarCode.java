package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendAbnormalBarCode
 * @Description
 * @Author wyh
 * @Date 2022/5/19 18:11
 **/
public class SendAbnormalBarCode implements Serializable {

    private static final long serialVersionUID = -6721934979581602081L;

    /**
     * 总数
     */
    private Long total;

    /**
     * 包裹列表
     */
    private List<SendScanBarCode> barCodeList;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<SendScanBarCode> getBarCodeList() {
        return barCodeList;
    }

    public void setBarCodeList(List<SendScanBarCode> barCodeList) {
        this.barCodeList = barCodeList;
    }
}
