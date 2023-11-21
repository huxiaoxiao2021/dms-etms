package com.jd.bluedragon.distribution.consumer.boardChute;

import com.jd.binlog.client.EntryMessage;
import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jdq4.consume.BoardChute;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.dms.binlake.BinLakeUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.client.consumer.MessageListener;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("boardChuteConsumer")
public class BoardChuteConsumer  implements MessageListener {

    private static final Logger logger = LoggerFactory.getLogger(BoardChuteConsumer.class);

    @Autowired
    private JyComBoardSendService jyComBoardSendService;
    @Autowired
    private BaseMajorManager baseMajorManager;

    private com.jd.bluedragon.common.dto.base.request.OperatorInfo initOperatorInfo(BoardChute boardChute){
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo = new com.jd.bluedragon.common.dto.base.request.OperatorInfo();
        if (StringUtils.isNotEmpty(boardChute.getEndErp())){
            BaseStaffSiteOrgDto baseStaffSiteOrgDto = baseMajorManager.getBaseStaffByErpNoCache(boardChute.getEndErp());
            if(baseStaffSiteOrgDto != null){
                operatorInfo.setUserName(baseStaffSiteOrgDto.getStaffName());
            }else{
                operatorInfo.setUserName(boardChute.getMachineCode());
            }
            operatorInfo.setUserErp(boardChute.getEndErp());

        }else{
            operatorInfo.setUserErp(boardChute.getMachineCode());
            operatorInfo.setUserName(boardChute.getMachineCode());
        }
        operatorInfo.setSiteName(boardChute.getCreateSiteName());
        operatorInfo.setSiteCode(boardChute.getCreateSiteCode());
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
    public void onMessage(List<Message> messages) throws Exception {

        List<EntryMessage> entryMessages = BinLakeUtils.deserialize(messages);
        for (EntryMessage entryMessage : entryMessages) {
            //获取数据行消息，分为INSERT、DELETE、UPDATE和其他
            BoardChute boardChute = BinLakeUtils.copyByList(entryMessage, BoardChute.class);
            if (boardChute == null) {
                logger.error("BoardChuteConsumer consume -->JSON转换后为空，内容为【{}】", JsonHelper.toJson(entryMessage));
                continue;
            }
            if (boardChute.getStatus()!=0){
                logger.error("BoardChuteConsumer consume -->状态，内容为【{}】", boardChute.getStatus());
                continue;
            }
            if (StringUtils.isEmpty(boardChute.getSendCode())){
                logger.error("BoardChuteConsumer consume -->非组板发货数据【{}】", JsonHelper.toJson(boardChute));
                continue;
            }
            com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo =
                    initOperatorInfo(boardChute);
            BoardReq req = createFinishBoardReq(operatorInfo, boardChute.getBoardCode());
            logger.info("jmq4消费"+JsonHelper.toJson(req));
            jyComBoardSendService.finishBoard(req);
        }
    }


}
