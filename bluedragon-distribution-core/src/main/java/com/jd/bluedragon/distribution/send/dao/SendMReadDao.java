package com.jd.bluedragon.distribution.send.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.SerialRuleUtil;

/**
 * @author dudong
 * @date 2015/5/25
 */
public class SendMReadDao extends BaseDao<SendM>{
    private static final String namespace = SendMReadDao.class.getName();

    public List<SendM> findSendMByBoxCode(SendM sendM) {
        return getSqlSessionRead().selectList(SendMReadDao.namespace + ".findSendMByBoxCode", sendM);
    }
    
    public List<String> selectBoxCodeBySendCode(String sendCode) {
    	SendM sendM = new SendM();
		sendM.setSendCode(sendCode);
        sendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
        return getSqlSessionRead().selectList(SendMReadDao.namespace + ".selectBoxCodeBySendCode", sendM);
    }
}
