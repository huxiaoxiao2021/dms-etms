package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.Constants;
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
        return sendAttachmentDao.hasPhoto(entity) != null;
    }

    @Override
    public Integer saveAttachment(JySendAttachmentEntity entity) {
        int rows = sendAttachmentDao.updateByBiz(entity);
        if (rows == Constants.NO_MATCH_DATA) {
            return sendAttachmentDao.insert(entity);
        }
        return rows;
    }

}
