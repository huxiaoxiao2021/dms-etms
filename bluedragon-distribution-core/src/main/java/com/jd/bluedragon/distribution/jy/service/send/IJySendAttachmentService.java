package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;

/**
 * @ClassName IJySendAttachmentService
 * @Description
 * @Author wyh
 * @Date 2022/6/3 20:46
 **/
public interface IJySendAttachmentService {

    /**
     * 发货任务是否拍照
     * @param entity
     * @return true：拍照了
     */
    Boolean sendVehicleTakePhoto(JySendAttachmentEntity entity);

    /**
     * 发货任务是否选择车辆状态
     * @param entity 请求参数
     * @return true：已选择车辆状态
     * @author fanggang7
     * @time 2022-08-02 19:42:44 周二
     */
    Boolean sendVehicleHasSelectStatus(JySendAttachmentEntity entity);

    /**
     * 根据bizId查找拍照记录
     * @param entity
     * @return
     */
    JySendAttachmentEntity selectBySendVehicleBizId(JySendAttachmentEntity entity);

    /**
     * 记录拍照
     * @param entity
     * @return
     */
    Integer saveAttachment(JySendAttachmentEntity entity);
}
