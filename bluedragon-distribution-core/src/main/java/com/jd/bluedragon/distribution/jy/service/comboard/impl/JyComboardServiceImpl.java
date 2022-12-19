package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.common.dto.base.request.User;
import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyComboardDao;
import com.jd.bluedragon.distribution.jy.dto.comboard.BatchUpdateCancelReq;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.jd.bluedragon.dms.utils.BusinessUtil.encryptIdCard;

/**
 * @author liwenji
 * @date 2022-11-23 21:17
 */
@Service
public class JyComboardServiceImpl implements JyComboardService {

    @Autowired
    private JyComboardDao jyComboardDao;

    @Override
    public List<User> queryUserByStartSiteCode(JyComboardEntity entity) {
        List<User> users = jyComboardDao.queryUserByStartSiteCode(entity);
        hideInfo(users);
        return users;
    }

    private void hideInfo(List<User> userList) {
        for (User user : userList) {
            user.setUserErp(encryptIdCard(user.getUserErp()));
        }
    }
    
    @Override
    public int save(JyComboardEntity entity) {
        return jyComboardDao.insertSelective(entity);
    }

    @Override
    public JyComboardEntity queryIfScaned(JyComboardEntity condition) {
        condition.setForceSendFlag(false);
        condition.setInterceptFlag(false);
        condition.setCancelFlag(false);
        return jyComboardDao.queryByBarCode(condition);
    }

    @Override
    public boolean batchUpdateCancelFlag(BatchUpdateCancelReq req) {
        return jyComboardDao.batchUpdateCancelFlag(req) > 0;
    }

    @Override
    public String queryWayBillCodeByBoardCode(JyComboardEntity entity) {
        return jyComboardDao.queryWayBillCodeByBoardCode(entity);
    }
}
