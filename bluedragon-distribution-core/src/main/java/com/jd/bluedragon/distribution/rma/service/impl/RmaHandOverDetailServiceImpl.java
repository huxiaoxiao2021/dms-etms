package com.jd.bluedragon.distribution.rma.service.impl;

import com.jd.bluedragon.distribution.rma.dao.RmaHandOverDetailDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import com.jd.bluedragon.distribution.rma.service.RmaHandOverDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
@Service
public class RmaHandOverDetailServiceImpl implements RmaHandOverDetailService {

    @Autowired
    private RmaHandOverDetailDao rmaHandOverDetailDao;

    @Override
    public boolean add(RmaHandoverDetail detail) {
        if (rmaHandOverDetailDao.add(detail) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void batchAdd(List<RmaHandoverDetail> details) {
        rmaHandOverDetailDao.batchAdd(details);
    }

    @Override
    public List<RmaHandoverDetail> getListByHandoverWaybillId(Long waybillId) {
        if (waybillId != null & waybillId > 0) {
            return rmaHandOverDetailDao.getListByHandoverWaybillId(waybillId);
        }
        return null;
    }
}
