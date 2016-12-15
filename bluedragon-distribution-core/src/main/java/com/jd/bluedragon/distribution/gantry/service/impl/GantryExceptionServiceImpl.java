package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.distribution.gantry.dao.GantryExceptionDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hanjiaxing1 on 2016/12/12.
 */
@Service("gantryExceptionService")
public class GantryExceptionServiceImpl implements GantryExceptionService{

    private static final Log logger = LogFactory.getLog(GantryExceptionServiceImpl.class);

    @Autowired
    GantryExceptionDao gantryExceptionDao;

    @Override
    public List<GantryException> getGantryException(Map<String, Object> param) {
        return gantryExceptionDao.getGantryException(param);
    }

    @Override
    public Integer getGantryExceptionCount(Map<String, Object> param) {
        return gantryExceptionDao.getGantryExceptionCount(param);
    }

    @Override
    public List<GantryException> getGantryExceptionPage(Map<String, Object> param) {
        return gantryExceptionDao.getGantryExceptionPage(param);
    }

    @Override
    public int addGantryException(GantryException gantryException) {
        return gantryExceptionDao.addGantryException(gantryException);
    }

    @Override
    public Integer getGantryExceptionCount(Long machineId, Date beginTime, Date endTime) {
        HashMap<String, Object> param = new HashMap();
        param.put("machineId", machineId);
        param.put("beginTime", beginTime);
        param.put("endTime", endTime);

        return gantryExceptionDao.getGantryExceptionCount(param);
    }
}
