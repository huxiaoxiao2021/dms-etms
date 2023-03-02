package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;
import java.util.List;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/8/16
 * @Description: 发货不齐处理提交请求对象
 */
public class IncompleteSendReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = -1L;

    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 包裹号列表
     */
    private List<String> packList;

    /**
     * 是否全选
     */
    private Boolean checkAllFlag;

    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public List<String> getPackList() {
        return packList;
    }

    public void setPackList(List<String> packList) {
        this.packList = packList;
    }

    public Boolean getCheckAllFlag() {
        return checkAllFlag;
    }

    public void setCheckAllFlag(Boolean checkAllFlag) {
        this.checkAllFlag = checkAllFlag;
    }
}
