package com.jd.bluedragon.distribution.jdq4.consume;

import com.jd.bluedragon.common.dto.comboard.request.BoardReq;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.businessCode.BusinessCodeFromSourceEnum;
import com.jd.bluedragon.distribution.jdq4.binlake.BinLakeDto;
import com.jd.bluedragon.distribution.jdq4.binlake.BinLakeUtils;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
@Service("boardChuteConsumer")
@Slf4j
public class BoardChuteConsumer extends MessageBaseConsumer {

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
    public void consume(Message message) throws Exception {
        String content = message.getText();
        if (StringUtils.isEmpty(content)) {
            return;
        }

        BinLakeDto dto = null;
        try {
            dto = com.jdl.basic.common.utils.JsonHelper.toObject(content, BinLakeDto.class);
        } catch (Exception e) {
            log.error("滑道笼车配置消息解析异常！{}{}", content, e);
            return;
        }
        if (CollectionUtils.isEmpty(dto.getAfterChangeOfColumns())) {
            return;
        }
        BoardChute boardChute = BinLakeUtils.copyByList(dto.getAfterChangeOfColumns(), BoardChute.class);
        if (boardChute == null) {
            logger.error("BoardChuteConsumer consume -->JSON转换后为空，内容为【{}】", content);
            return;
        }
        if (boardChute.getStatus()!=0){
//            logger.error("BoardChuteConsumer consume -->状态，内容为【{}】", boardChute.getStatus());
            return;
        }
        if (StringUtils.isEmpty(boardChute.getSendCode())){
//            logger.error("BoardChuteConsumer consume -->非组板发货数据【{}】", JsonHelper.toJson(boardChute));
            return;
        }
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo =
                initOperatorInfo(boardChute);
        BoardReq req = createFinishBoardReq(operatorInfo, boardChute.getBoardCode());
        logger.info("jmq4消费"+JsonHelper.toJson(req));
//        jyComBoardSendService.finishBoard(req);
    }
}
