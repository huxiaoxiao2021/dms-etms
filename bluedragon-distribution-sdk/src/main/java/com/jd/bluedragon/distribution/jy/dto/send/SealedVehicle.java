package com.jd.bluedragon.distribution.jy.dto.send;

import java.util.List;

/**
 * @ClassName SealedVehicle
 * @Description
 * @Author wyh
 * @Date 2022/5/18 19:49
 **/
public class SealedVehicle extends BaseSendVehicle {

    private static final long serialVersionUID = 8760156973684779457L;

    /**
     * 封签数
     */
    private Integer sealCodeCount;

    /**
     * 发货流向
     */
    private List<SendVehicleDetail> sendDestList;

    public Integer getSealCodeCount() {
        return sealCodeCount;
    }

    public void setSealCodeCount(Integer sealCodeCount) {
        this.sealCodeCount = sealCodeCount;
    }

    public List<SendVehicleDetail> getSendDestList() {
        return sendDestList;
    }

    public void setSendDestList(List<SendVehicleDetail> sendDestList) {
        this.sendDestList = sendDestList;
    }
}
