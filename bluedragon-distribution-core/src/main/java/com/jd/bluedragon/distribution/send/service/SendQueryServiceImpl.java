package com.jd.bluedragon.distribution.send.service;

import com.jd.bluedragon.core.base.MonitorManager;
import com.jd.bluedragon.distribution.send.dao.SendQueryDao;
import com.jd.bluedragon.distribution.send.domain.SendDifference;
import com.jd.bluedragon.distribution.send.domain.SendQuery;
import com.jd.etms.monitor.dto.vos.DifferenceType;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import com.jd.etms.monitor.jsf.VosScanCodeDifferenceQuery;
import com.jd.etms.monitor.dto.vos.ScanDto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangtingwei on 2014/12/5.
 */
@Service("SendQueryService")
public class SendQueryServiceImpl implements SendQueryService {

    private final Logger logger = Logger.getLogger(SendQueryServiceImpl.class);



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
        logger.info(sendDifference.toString());

       // sendDifference.setPackageDifferences(scanDto.getScanCodes());

        return sendDifference;
    }

}
