package com.jd.bluedragon.distribution.jy.service.unload;

import com.jd.bluedragon.distribution.jy.dao.unload.JyUnloadCarAggsDao;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadCarAggsEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2022/10/11 16:49
 * @Description:
 */
@Service
public class JyUnloadCarAggsServiceImpl implements JyUnloadCarAggsService{

    private static final Logger log = LoggerFactory.getLogger(JyUnloadCarAggsServiceImpl.class);

    @Autowired
    @Qualifier("jyUnloadCarAggsDao")
    private JyUnloadCarAggsDao jyUnloadCarAggsDao;

    @Override
    public int insertOrUpdateJyUnloadCarAggs(JyUnloadCarAggsEntity entity) {
        return jyUnloadCarAggsDao.insertOrUpdate(entity);
    }
}
