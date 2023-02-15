package com.jd.bluedragon.distribution.jdq4.consume;

import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jdq4.JDQConfig;
import com.jd.bluedragon.distribution.jdq4.JDQConsumer;
import com.jd.bluedragon.distribution.jy.dto.comboard.JyAggsDto;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.laf.binding.annotation.Value;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class BoardChuteConsumer extends JDQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BoardChuteConsumer.class);

    @Value("${jdq.producer.boardchute.username}")
    private String userName;
    @Value("${jdq.dmsautomatic.boardchute.domain}")
    private String domain;
    @Value("${jdq.operatelog.boardchute.password}")
    private String password;
    @Value("${jdq.operatelog.boardchute.groupId}")
    private String groupId;
    @Value("${jdq.operatelog.boardchute.topic}")
    private String topic;
    @Autowired
    private JyComBoardSendService jyComBoardSendService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Override
    public void onMessage(ConsumerRecord<String, String> message) {
        System.out.println(JsonHelper.toJson(message));
        BoardChute boardChute = JsonHelper.fromJson(message.value(), BoardChute.class);
        if (boardChute == null) {
            logger.error("BoardChuteConsumer consume -->JSON转换后为空，内容为【{}】", message.value());
            return;
        }
        if (boardChute.getStatus()!=0){
            logger.error("BoardChuteConsumer consume -->状态，内容为【{}】", boardChute.getStatus());
            return;
        }
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo =
                initOperatorInfo(boardChute.getEndErp(), boardChute.getCreateSiteCode());
        BoardReq req = createFinishBoardReq(operatorInfo, boardChute.getBoardCode());
        logger.info("jdq消费"+JsonHelper.toJson(req));
//        jyComBoardSendService.finishBoard(req);
    }
    private com.jd.bluedragon.common.dto.base.request.OperatorInfo initOperatorInfo(String userErp, Integer siteCode){
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo = new com.jd.bluedragon.common.dto.base.request.OperatorInfo();
        BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(userErp);
        if(baseStaffSiteOrgDto != null){
            operatorInfo.setUserName(baseStaffSiteOrgDto.getStaffName());
            operatorInfo.setSiteName(baseStaffSiteOrgDto.getSiteName());
        }
        operatorInfo.setUserErp(userErp);
        operatorInfo.setSiteCode(siteCode);
        return operatorInfo;
    }
    private BoardReq createFinishBoardReq(com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo, String code) {
        BoardReq req = new BoardReq();
        req.setBizSource(BusinessCodeFromSourceEnum.DMS_AUTOMATIC_WORKER_SYS.name());
        req.setBoardCode(code);
        com.jd.bluedragon.common.dto.base.request.CurrentOperate currentOperate = new com.jd.bluedragon.common.dto.base.request.CurrentOperate();
        currentOperate.setOperateTime(new Date());
        currentOperate.setSiteCode(operatorInfo.getSiteCode());
        req.setCurrentOperate(currentOperate);

        com.jd.bluedragon.common.dto.base.request.User user = new com.jd.bluedragon.common.dto.base.request.User();
        user.setUserErp(operatorInfo.getUserErp());
        user.setUserName(operatorInfo.getUserName());
        req.setUser(user);
        return req;
    }


    @Override
    public JDQConfig createJDQConfig() {
        return new JDQConfig(userName,domain,password,groupId,topic);
    }
}
