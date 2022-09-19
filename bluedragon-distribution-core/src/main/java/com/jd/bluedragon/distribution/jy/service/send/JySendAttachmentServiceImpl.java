package com.jd.bluedragon.distribution.jy.service.send;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.jy.dao.send.JySendAttachmentDao;
import com.jd.bluedragon.distribution.jy.send.JySendAttachmentEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(JySendAttachmentServiceImpl.class);


    @Autowired
    private JySendAttachmentDao sendAttachmentDao;

    @Override
    public Boolean sendVehicleTakePhoto(JySendAttachmentEntity entity) {
        return sendAttachmentDao.hasPhoto(entity) != null;
    }

    @Override
    public Boolean sendVehicleHasSelectStatus(JySendAttachmentEntity entity) {
        return sendAttachmentDao.selectBySendVehicleBizId(entity) != null;
    }

    @Override
    public Integer saveAttachment(JySendAttachmentEntity entity) {
        log.info("测试上传发货照片=entity={}", JSON.toJSONString(entity));
        int rows = sendAttachmentDao.updateByBiz(entity);
        if (rows == Constants.NO_MATCH_DATA) {
            return sendAttachmentDao.insert(entity);
        }
        return rows;
    }

}
