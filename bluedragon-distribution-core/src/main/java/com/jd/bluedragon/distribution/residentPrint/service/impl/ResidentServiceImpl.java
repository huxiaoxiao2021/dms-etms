package com.jd.bluedragon.distribution.residentPrint.service.impl;

import com.jd.bluedragon.distribution.residentPrint.service.ResidentService;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
     * 判断运单是否存在箱号中
     * @param boxCode
     * @param waybillCode
     * @return
     */
    @Override
    public Boolean isExist(String boxCode, String waybillCode) {
        //存放箱号中的运单号
        Set<String> waybillCodeSet = new HashSet<>();
        SendDetail sendDetail = new SendDetail();
        sendDetail.setBoxCode(boxCode);
        List<SendDetail> sendDetails = sendDatailDao.querySendDatailsByBoxCode(sendDetail);
        if(sendDetails != null && sendDetails.size() > 0){
            for(SendDetail send : sendDetails){
                waybillCodeSet.add(send.getWaybillCode());
            }
        }
        if(waybillCodeSet.contains(waybillCode)){
            return true;
        }
        return false;
    }
}
