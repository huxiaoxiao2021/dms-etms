package com.jd.bluedragon.distribution.send.service.impl;

import com.jd.bluedragon.distribution.send.dao.SendDatailReadDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.service.SendDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("sendDetailService")
public class SendDetailServiceImpl implements SendDetailService {

    @Autowired
    private SendDatailReadDao sendDatailReadDao;

    /**
     * 根据包裹号查询当前分拣中心的send_d
     * @param waybillCode
     * @param createSiteCode
     * @return
     */
    public List<SendDetail> findSendByPackageCodeFromReadDao(String waybillCode, Integer createSiteCode){
        return sendDatailReadDao.findSendByPackageCode(waybillCode, createSiteCode);
    }
}
