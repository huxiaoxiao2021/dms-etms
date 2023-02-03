package com.jd.bluedragon.distribution.jy.dto.send;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendPhotoRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 15:16
 **/
public class SendPhotoReq extends JyReqBaseDto implements Serializable {

    private static final long serialVersionUID = 8017084388836995983L;


    /**
     * send_vehicle业务主键
     */
    private String sendVehicleBizId;

    /**
     * 车辆未到、已到选项
     */
    private Integer vehicleArrived;

    /**
     * 拍照图片
     */
    private List<String> imgList;

    /**
     * 拍照类型：0-发货前拍照 1-封车前拍照
     */
    private Integer type;


    public String getSendVehicleBizId() {
        return sendVehicleBizId;
    }

    public void setSendVehicleBizId(String sendVehicleBizId) {
        this.sendVehicleBizId = sendVehicleBizId;
    }

    public Integer getVehicleArrived() {
        return vehicleArrived;
    }

    public void setVehicleArrived(Integer vehicleArrived) {
        this.vehicleArrived = vehicleArrived;
    }

    public List<String> getImgList() {
        return imgList;
    }

    public void setImgList(List<String> imgList) {
        this.imgList = imgList;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
