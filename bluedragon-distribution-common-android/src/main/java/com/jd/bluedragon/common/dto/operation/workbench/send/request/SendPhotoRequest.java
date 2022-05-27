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
}
