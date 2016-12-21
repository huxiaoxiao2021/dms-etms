package com.jd.bluedragon.distribution.gantry.service.impl;

import com.jd.bluedragon.distribution.gantry.dao.GantryExceptionDao;
import com.jd.bluedragon.distribution.gantry.domain.GantryException;
import com.jd.bluedragon.distribution.gantry.service.GantryExceptionService;
import com.jd.bluedragon.utils.StringHelper;
import com.sun.tools.corba.se.idl.toJavaPortable.Helper;
import org.apache.commons.beanutils.converters.DoubleConverter;
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

    static final Double VOLUME_DEFAULT = 0.0;

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
     * @param gantryException machineId、barCode、waybillCode、createSiteCode、createSiteName、operateTime和type不能为空
     *
     */
    @Override
    public int addGantryException(GantryException gantryException) {
        if (gantryException.getMachineId() == null ||
                StringHelper.isEmpty(gantryException.getBarCode()) ||
                StringHelper.isEmpty(gantryException.getWaybillCode()) ||
                gantryException.getCreateSiteCode() == null ||
                StringHelper.isEmpty(gantryException.getCreateSiteName()) ||
                gantryException.getOperateTime() == null) {
            return 0;
        }

        if (gantryException.getVolume() == null) {
            gantryException.setVolume(VOLUME_DEFAULT);
        }

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


    /**
     * 插入异常
     *
     * @param barCode 条码编号 不能为空
     *
     */
    @Override
    public int updateSendStatus(String barCode) {
        if (StringHelper.isEmpty(barCode)) {
            return 0;
        }
        HashMap<String, Object> param = new HashMap();
        param.put("barCode", barCode);
        return gantryExceptionDao.updateSendStatus(param);
    }
}
