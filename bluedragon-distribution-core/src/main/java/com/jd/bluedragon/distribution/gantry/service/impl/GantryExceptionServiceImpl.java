package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.distribution.gantry.dao.GantryExceptionDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.utils.StringHelper;
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


    /**
     * 插入异常
     *
     * @param gantryException machineId、barCode、operateTime和type不能为空
     *
     */
    @Override
    public int addGantryException(GantryException gantryException) {
//        if (gantryException.getMachineId() == null ||
//                StringHelper.isEmpty(gantryException.getBarCode()) ||
//                gantryException.getOperateTime() == null) {
//            return 0;
//        }

        return gantryExceptionDao.addGantryException(gantryException);
    }

    @Override
    public Integer getGantryExceptionCount(String machineId, Date startTime, Date endTime) {
        HashMap<String, Object> param = new HashMap();
        param.put("machineId", machineId);
        param.put("startTime", startTime);
        param.put("endTime", endTime);

        return gantryExceptionDao.getGantryExceptionCount(param);
    }

    @Override
    public Integer getGantryExceptionCountForUpdate(String barCode, Long createSiteCode) {
        if (StringHelper.isEmpty(barCode) || createSiteCode == null) {
            return 0;
        }
        HashMap<String, Object> param = new HashMap();
        param.put("barCode", barCode);
        param.put("createSiteCode", createSiteCode);
        return gantryExceptionDao.getGantryExceptionCountForUpdate(param);
    }

    /**
     * 插入异常
     *
     * @param barCode 条码编号 不能为空
     * @param createSiteCode 始发分拣中心编号 不能为空
     *
     */
    @Override
    public int updateSendStatus(String barCode, Long createSiteCode) {
        if (StringHelper.isEmpty(barCode) || createSiteCode == null) {
            return 0;
        }
        HashMap<String, Object> param = new HashMap();
        param.put("barCode", barCode);
        param.put("createSiteCode", createSiteCode);
        return gantryExceptionDao.updateSendStatus(param);
    }
}
