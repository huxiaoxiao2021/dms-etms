package com.jd.bluedragon.distribution.jy.dto.send;

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
     * 运单列表
     */
    private List<SendScanWaybill> waybillList;


    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<SendScanWaybill> getWaybillList() {
        return waybillList;
    }

    public void setWaybillList(List<SendScanWaybill> waybillList) {
        this.waybillList = waybillList;
    }
}
