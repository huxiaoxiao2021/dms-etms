package com.jd.bluedragon.distribution.send.manager.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.box.BoxReq;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.external.constants.BoxStatusEnum;
import com.jd.bluedragon.distribution.external.constants.OpBoxNodeEnum;
import com.jd.bluedragon.distribution.send.dao.SendMDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.manager.SendMManager;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;


/**
 * Created by hanjiaxing1 on 2018/10/16.
 */
@Component
public class SendMManagerImpl implements SendMManager {

    private static final String DB_TABLE_NAME = "send_m";

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    private SendMDao sendMDao;

    @Autowired
    private BoxService boxService;

    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.add", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public Integer add(String namespace, SendM sendM) {
        // 生成主键ID
        sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        Integer result = sendMDao.add(namespace, sendM);
        if (result > 0) {
            //关闭箱
            updateBoxStatus(sendM);
        }
        return result;
    }

    @Override
    @JProfiler(jKey = "DMSWEB.SendMManagerImpl.insertSendM", mState = JProEnum.TP, jAppName = Constants.UMP_APP_NAME_DMSWEB)
    public boolean insertSendM(SendM sendM) {
        // 生成主键ID
        sendM.setSendMId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        boolean result = sendMDao.insertSendM(sendM);
        if (result) {
            //关闭箱
            updateBoxStatus(sendM);
        }
        return result;
    }

    /**
     * 通过箱号和始发地获取发货数据
     * @param sendM
     * @return
     */
    @Override
    public List<SendM> findSendMByBoxCode(SendM sendM){
        return sendMDao.findSendMByBoxCode(sendM);
    }


    /**
     * 关闭箱
     * @param sendM
     */
    private void updateBoxStatus(SendM sendM){
        //构造参数
        BoxReq boxReq = new BoxReq();
        boxReq.setBoxCode(sendM.getBoxCode());
        boxReq.setBoxStatus(BoxStatusEnum.CLOSE.getStatus());
        boxReq.setOpNodeCode(OpBoxNodeEnum.SEND.getNodeCode());
        boxReq.setOpNodeName(OpBoxNodeEnum.SEND.getNodeName());
        boxReq.setOpSiteCode(sendM.getCreateSiteCode());
        boxReq.setOpSiteName("");
        boxReq.setOpErp(sendM.getCreateUser());
        boxReq.setOpTime(sendM.getOperateTime());
        boxReq.setOpDescription(String.format("%s操作发货，关闭此箱号%s的箱子", sendM.getCreateUser(),sendM.getBoxCode()));
        //更新逻辑
        boxService.updateBoxStatus(boxReq);
    }


}
