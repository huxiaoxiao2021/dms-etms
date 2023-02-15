package com.jd.bluedragon.distribution.jdq4.consume;

import com.alibaba.fastjson.JSONObject;
import com.jd.bdp.jdw.avro.JdwData;
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
import lombok.Setter;
import org.apache.commons.lang.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class BoardChuteConsumer extends JDQConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BoardChuteConsumer.class);


    @Setter
    private String username;
    @Setter
    private String domain;
    @Setter
    private String password;
    @Setter
    String topic;
    @Setter
    String groupId;

    @Autowired
    private JyComBoardSendService jyComBoardSendService;
    @Autowired
    private BaseMajorManager baseMajorManager;
    @Override
    public void onMessage(ConsumerRecord<String, String> message) {
        logger.info("BoardChuteConsumer:"+ JsonHelper.toJson(message));
        JdwData jdwData = JsonHelper.fromJson(message.value(), JdwData.class);
        BoardChute boardChute = (BoardChute)toPojo(jdwData, BoardChute.class);
        if (boardChute == null) {
            logger.error("BoardChuteConsumer consume -->JSON转换后为空，内容为【{}】", message.value());
            return;
        }
        if (boardChute.getStatus()!=0){
            logger.error("BoardChuteConsumer consume -->状态，内容为【{}】", boardChute.getStatus());
            return;
        }
        if (StringUtils.isNotEmpty(boardChute.getSendCode())){
            logger.error("BoardChuteConsumer consume -->非组板发货数据【{}】", JsonHelper.toJson(boardChute));
            return;
        }
        com.jd.bluedragon.common.dto.base.request.OperatorInfo operatorInfo =
                initOperatorInfo(boardChute.getEndErp(), boardChute.getCreateSiteCode());
        BoardReq req = createFinishBoardReq(operatorInfo, boardChute.getBoardCode());
        logger.info("jdq消费"+JsonHelper.toJson(req));
//        jyComBoardSendService.finishBoard(req);
    }
    public static Object toPojo(JdwData jdwData, Class clazz) {
        Map<CharSequence, CharSequence> srcMap = jdwData.getSrc();
        Map<CharSequence, CharSequence> curMap = jdwData.getCur();

        srcMap.putAll(curMap);
        HashMap<String, Object> map = new HashMap<>();

        srcMap.keySet().forEach(charSequence -> {
            map.put(charSequence.toString(), srcMap.get(charSequence) == null ? "" : srcMap.get(charSequence).toString());
        });
        Object o = null;
        JSONObject jsonObject = new JSONObject(map);
        try {
            o = jsonObject.toJavaObject(clazz);
        } catch (Exception e) {
            logger.error("反序列化失败.map:{}", jsonObject.toJSONString());
            throw e;
        }
        return o;
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
        return new JDQConfig(username,domain,password,groupId,topic);
    }
}
