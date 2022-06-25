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
     * 记录拍照
     * @param entity
     * @return
     */
    Integer saveAttachment(JySendAttachmentEntity entity);
}
