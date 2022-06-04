package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendDao;
import com.jd.bluedragon.distribution.jy.send.JySendEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName JySendServiceImpl
 * @Description
 * @Author wyh
 * @Date 2022/6/4 17:26
 **/
@Service
public class JySendServiceImpl implements IJySendService{

    @Autowired
    private JySendDao sendDao;

    @Override
    public JySendEntity findSendRecordExistAbnormal(JySendEntity entity) {
        return sendDao.findSendRecordExistAbnormal(entity);
    }
}
