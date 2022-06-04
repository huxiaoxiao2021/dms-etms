package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendAttachmentDao;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName JySendAttachmentServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/3 21:06
 **/
@Service
public class JySendAttachmentServiceImpl implements IJySendAttachmentService{

    @Autowired
    private JySendAttachmentDao sendAttachmentDao;

    @Override
    public Boolean sendVehicleTakePhoto(JySendAttachmentEntity entity) {
        return null;
    }
}
