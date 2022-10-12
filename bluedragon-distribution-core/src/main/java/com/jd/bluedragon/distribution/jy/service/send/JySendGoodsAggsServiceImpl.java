package com.jd.bluedragon.distribution.jy.service.send;

import com.jd.bluedragon.distribution.jy.dao.send.JySendGoodsAggsDao;
import com.jd.bluedragon.distribution.jy.send.JySendGoodsAggsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 17:37
 * @Description:
 */
@Service
public class JySendGoodsAggsServiceImpl implements JySendGoodsAggsService{

    @Autowired
    @Qualifier("jySendGoodsAggsDao")
    private JySendGoodsAggsDao jySendGoodsAggsDao;

    @Override
    public int insertOrUpdateJySendGoodsAggs(JySendGoodsAggsEntity entity) {
        return jySendGoodsAggsDao.insertOrUpdate(entity);
    }
}
