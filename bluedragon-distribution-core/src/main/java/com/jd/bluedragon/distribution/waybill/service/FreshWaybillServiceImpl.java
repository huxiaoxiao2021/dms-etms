package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.waybill.dao.FreshWaybillDao;
import com.jd.bluedragon.distribution.waybill.domain.FreshWaybill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author dudong
 * @version 1.0
 * @date 2016/4/28
 */
@Service("freshWaybillService")
public class FreshWaybillServiceImpl implements FreshWaybillService{

    @Autowired
    private FreshWaybillDao freshWaybillDao;

    @Override
    @Transactional
    public void updateFreshWaybill(FreshWaybill freshWaybill) {
        List<FreshWaybill> existedWaybill = freshWaybillDao.getFreshWaybillByID(freshWaybill);
        if(null != existedWaybill && existedWaybill.size() > 0) {
            freshWaybillDao.delFreshWaybillByID(freshWaybill);
            freshWaybillDao.addFreshWaybill(freshWaybill);
        }
    }
}
