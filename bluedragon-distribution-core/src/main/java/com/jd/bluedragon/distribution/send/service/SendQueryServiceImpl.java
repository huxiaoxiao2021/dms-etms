package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.core.base.MonitorManager;
import com.jd.bluedragon.distribution.send.dao.SendQueryDao;
import com.jd.bluedragon.distribution.send.domain.SendDifference;
import com.jd.bluedragon.distribution.send.domain.SendQuery;
import com.jd.etms.monitor.dto.vos.ScanDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangtingwei on 2014/12/5.
 */
@Service("SendQueryService")
public class SendQueryServiceImpl implements SendQueryService {

    private final Logger log = LoggerFactory.getLogger(SendQueryServiceImpl.class);



    @Autowired
    private SendQueryDao sendQueryDao;

    @Autowired
    private MonitorManager monitorManager;

    @Override
    public boolean insert(SendQuery domain) {
        return sendQueryDao.add(domain);
    }

    @Override
    public List<SendQuery> queryBySendCode(String sendCode) {
        return sendQueryDao.queryBySendCode(sendCode);
    }
    @Override
    public SendDifference querySendDifference(String sendCode){
        SendDifference sendDifference=new SendDifference(200,"ok");
     //  ScanDto scanDto=  monitorJsfService.query(DifferenceType.SortingSendVosNotReceive,sendCode,0,50);
        ScanDto scanDto=  monitorManager.query(sendCode,0,50);

        sendDifference.setDmsSendNums(scanDto.getUpstreamNum());
        sendDifference.setTmsReceiveNums((scanDto.getReceiveNum()));
        sendDifference.setSendCode(sendCode);
        sendDifference.setPackageDifferences(scanDto.getScanCodes());
        log.info(sendDifference.toString());

       // sendDifference.setPackageDifferences(scanDto.getScanCodes());

        return sendDifference;
    }

}
