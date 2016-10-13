package com.jd.bluedragon.distribution.send.dao;

import java.util.Collections;
import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.lang.StringUtils;

/**
 * @author dudong
 * @date 2015/5/25
 */
public class SendMReadDao extends BaseDao<SendM>{
    private static final String namespace = SendMReadDao.class.getName();

    public List<SendM> findSendMByBoxCode(SendM sendM) {
        if(null!=sendM&& StringUtils.isNotBlank(sendM.getSendCode())&&null==sendM.getCreateSiteCode()){
            sendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendM.getSendCode()));
        }
        return getSqlSessionRead().selectList(SendMReadDao.namespace + ".findSendMByBoxCode", sendM);
    }
    
    public List<String> selectBoxCodeBySendCode(String sendCode) {
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null == createSiteCode) return Collections.emptyList();
    	SendM sendM = new SendM();
		sendM.setSendCode(sendCode);
        sendM.setCreateSiteCode(createSiteCode);
        return getSqlSessionRead().selectList(SendMReadDao.namespace + ".selectBoxCodeBySendCode", sendM);
    }
}
