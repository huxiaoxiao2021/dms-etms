package com.jd.bluedragon.distribution.rma.service;

import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
@Service("rmaHandOverDetailService")
public class RmaHandOverDetailServiceImpl implements RmaHandOverDetailService{


    @Override
    public void add(RmaHandoverDetail detail) {

    }

    @Override
    public void batchAdd(List<RmaHandoverDetail> details) {

    }

    @Override
    public List<RmaHandoverDetail> getListByWaybillId(Long waybillId) {
        return null;
    }
}
