package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.comboard.request.ComboardScanReq;
import com.jd.bluedragon.core.jsf.dms.GroupBoardManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.jy.service.send.JyComBoardSendService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
import com.jd.transboard.api.dto.Board;
import com.jd.transboard.api.dto.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



/**
 * @author weixiaofeng12
 * @date 2022-12-10 11:45
 */
@Service("boxComboardConsumer")
@Slf4j
public class BoxComboardConsumer extends MessageBaseConsumer  {
    @Autowired
    GroupBoardManager groupBoardManager;
    @Autowired
    JyComBoardSendService jyComBoardSendService;
    @Autowired
    JyBizTaskComboardService jyBizTaskComboardService;
    
    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message)){
            log.error("boxComboardConsumer 获取消息为空！");
            return;
        }
        ComboardScanReq req = JsonHelper.fromJson(message.getText(),ComboardScanReq.class);
        if (ObjectHelper.isEmpty(req)){
            log.error("boxComboardConsumer body体为空！");
            return;
        }

        if (ObjectHelper.isEmpty(req.getBarCode()) || !BusinessUtil.isBoxcode(req.getBarCode())){
            log.error("非箱号类型的拆分组板任务，不支持！");
            return;
        }

        log.info("boxComboardConsumer body:{}",message.getText());

        Response<Board> rs =groupBoardManager.getBoardByBoxCode(req.getBarCode(),req.getCurrentOperate().getSiteCode());
        if (ObjectHelper.isNotNull(rs) && JdCResponse.CODE_SUCCESS.equals(rs.getCode())
                && ObjectHelper.isNotNull(rs.getData()) && req.getBoardCode().equals(rs.getData().getCode())){
            log.error("该箱：{}已经在该板:{}中，不允许重复组板！",req.getBarCode(),req.getBoardCode());
            return;
        }


        JyBizTaskComboardEntity condition = new JyBizTaskComboardEntity();
        condition.setStartSiteId(Long.valueOf(req.getCurrentOperate().getSiteCode()));
        condition.setBoardCode(req.getBoardCode());
        JyBizTaskComboardEntity entity = jyBizTaskComboardService.queryBizTaskByBoardCode(condition);
        if (ObjectHelper.isEmpty(entity)) {
            throw new JyBizException("该板以被清理，请重新扫描！");
        }

        jyComBoardSendService.execComboardOnce(req, entity, req.getOperateTime(),false);
    }
}
