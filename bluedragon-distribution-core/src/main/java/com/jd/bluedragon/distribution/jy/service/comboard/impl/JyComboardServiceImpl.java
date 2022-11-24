package com.jd.bluedragon.distribution.jy.service.comboard.impl;

import com.jd.bluedragon.distribution.jy.comboard.JyComboardEntity;
import com.jd.bluedragon.distribution.jy.dao.comboard.JyComboardDao;
import com.jd.bluedragon.distribution.jy.service.comboard.JyComboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author liwenji
 * @date 2022-11-23 21:17
 */
@Service
public class JyComboardServiceImpl implements JyComboardService {

    @Autowired
    private JyComboardDao jyComboardDao;

    @Override
    public int queryUserCountByStartSiteCode(Long startSiteId) {
        return jyComboardDao.queryUserCountByStartSiteCode(startSiteId);
    }

    @Override
    public int save(JyComboardEntity entity) {
        return jyComboardDao.insertSelective(entity);
    }
}
