package com.jd.bluedragon.distribution.residentPrint.service.impl;

import com.jd.bluedragon.distribution.residentPrint.service.ResidentService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: ResidentServiceImpl
 * @Description: 驻场打印实现类
 * @author: hujiping
 * @date: 2019/3/27 17:15
 */
@Service
public class ResidentServiceImpl implements ResidentService {

    @Autowired
    private SendDatailDao sendDatailDao;

    /**
     * 获得箱号中所有运单号
     * @param boxCode
     * @return
     */
    @Override
    public List<String> getAllWaybillCodeByBoxCode(String boxCode) {
        //存放箱号中的运单号
        List<String> waybillCodes = new ArrayList<>();
        SendDetail sendDetail = new SendDetail();
        sendDetail.setBoxCode(boxCode);
        List<SendDetail> sendDetails = sendDatailDao.querySendDatailsByBoxCode(sendDetail);
        if(sendDetails != null && sendDetails.size() > 0){
            for(SendDetail send : sendDetails){
                waybillCodes.add(send.getWaybillCode());
            }
        }
        return waybillCodes;
    }
}
