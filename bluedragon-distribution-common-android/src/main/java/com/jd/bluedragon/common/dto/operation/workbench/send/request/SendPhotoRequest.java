package com.jd.bluedragon.common.dto.operation.workbench.send.request;

import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.request.User;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName SendPhotoRequest
 * @Description
 * @Author wyh
 * @Date 2022/5/19 15:16
 **/
public class SendPhotoRequest implements Serializable {

    private static final long serialVersionUID = 8017084388836995983L;

    private User user;

    private CurrentOperate currentOperate;

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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public CurrentOperate getCurrentOperate() {
        return currentOperate;
    }

    public void setCurrentOperate(CurrentOperate currentOperate) {
        this.currentOperate = currentOperate;
    }

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
