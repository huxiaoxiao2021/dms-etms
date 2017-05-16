package com.jd.bluedragon.distribution.urban.service.impl;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.jd.bluedragon.distribution.urban.dao.TransbillMDao;
import com.jd.bluedragon.distribution.urban.domain.TransbillM;
import com.jd.bluedragon.distribution.urban.service.TransbillMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

/**
 * 城配运单M表--Service接口实现
 *
 * @author wuyoude
 * @ClassName: TransbillMServiceImpl
 * @Description: TODO
 * @date 2017年04月28日 13:30:01
 */
@Service("transbillMService")
@SuppressWarnings("all")
public class TransbillMServiceImpl implements TransbillMService {

    private static final Log logger = LogFactory.getLog(TransbillMServiceImpl.class);

    @Autowired
    private TransbillMDao transbillMDao;

    @Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public boolean saveOrUpdate(TransbillM transbillM) {
        Integer rs = 0;
        if (transbillM != null && transbillM.getMId() != null) {
            TransbillM oldData = transbillMDao.findById(transbillM.getMId());
            if (oldData != null) {
                if (transbillM.getTsM() >= oldData.getTsM()) {
                    rs = transbillMDao.updateBySelective(transbillM);
                } else {
                    logger.warn("本次数据ts_m小于数据库当前ts_m，抛弃本次数据！transbillM:" + JsonHelper.toJson(transbillM));
                }
            } else {
                rs = transbillMDao.insert(transbillM);
            }
            return rs == 1;
        } else {
            logger.warn("城配运单transbillM保存失败！transbillM:" + (transbillM == null ? "对象为空" : JsonHelper.toJson(transbillM)));
        }
        return false;
    }

    @Override
    public TransbillM getByWaybillCode(String waybillCode) {
        if (StringHelper.isNotEmpty(waybillCode)) {
            return transbillMDao.findByWaybillCode(waybillCode);
        }
        return null;
    }

    @Override
    public List<String> getEffectWaybillCodesByScheduleBillCode(String scheduleBillCode) {
        if (StringHelper.isNotEmpty(scheduleBillCode)) {
            return transbillMDao.findEffectWaybillCodesByScheduleBillCode(scheduleBillCode);
        }
        return null;
    }
}
