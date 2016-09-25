package com.jd.bluedragon.distribution.send.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.jboss.marshalling.serial.Serial;

public class SendMDao extends BaseDao<SendM>  {
	
	public static final String namespace = SendMDao.class.getName();
	
	public SendM selectOneBySiteAndSendCode(Integer createSiteCode, String sendCode) {
		SendM querySendM = new SendM();
		querySendM.setCreateSiteCode(createSiteCode);
		querySendM.setSendCode(sendCode);
		return (SendM) getSqlSession().selectOne(SendMDao.namespace + ".selectOneBySiteAndSendCode", querySendM);
	}

	@SuppressWarnings("unchecked")
	public List<SendM> selectOneBySendCode(String sendCode) {
		SendM querySendM = new SendM();
		querySendM.setSendCode(sendCode);
        querySendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
		return getSqlSession().selectList(SendMDao.namespace + ".selectOneBySendCode", querySendM);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendM> selectBySiteAndSendCode(Integer createSiteCode, String sendCode) {
		SendM querySendM = new SendM();
		querySendM.setCreateSiteCode(createSiteCode);
		querySendM.setSendCode(sendCode);
		return getSqlSession().selectList(SendMDao.namespace + ".selectBySiteAndSendCode", querySendM);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendM> selectBySiteAndSendCodeBYtime(Integer createSiteCode, String sendCode) {
		SendM querySendM = new SendM();
		querySendM.setCreateSiteCode(createSiteCode);
		querySendM.setSendCode(sendCode);
		return getSqlSession().selectList(SendMDao.namespace + ".selectBySiteAndSendCodeBYtime", querySendM);
	}

	public SendM selectBySendCode(String sendCode) {
        SendM querySendM = new SendM();
        querySendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
        querySendM.setSendCode(sendCode);
		return (SendM) getSqlSession().selectOne(SendMDao.namespace + ".selectBySendCode", querySendM);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendM> selectBySendSiteCode(SendM sendM) {
        if(null !=sendM && null != sendM.getSendCode() && null == sendM.getCreateSiteCode()) {
            sendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendM.getSendCode()));
        }
		return getSqlSession().selectList(SendMDao.namespace + ".selectBySendSiteCode", sendM);
	}

	public int updateBySendCodeSelective(SendM record){
		return getSqlSession().update(SendMDao.namespace + ".updateBySendCodeSelective", record);
	}

    public boolean insertSendM(SendM dSendM) {
    	return this.getSqlSession().insert(SendMDao.namespace + ".insertSendM",
    	        dSendM)>0;
    }
    
    
    @SuppressWarnings("unchecked")
    public List<SendM> findSendMByBoxCode(SendM sendM) {
        return getSqlSession().selectList(SendMDao.namespace + ".findSendMByBoxCode", sendM);
    }

	public List<SendM> findSendMByBoxCode2(SendM sendM) {
		return getSqlSession().selectList(SendMDao.namespace + ".findSendMByBoxCode2", sendM);
	}
    
    public boolean cancelSendM(SendM tSendM) {
        return this
                .getSqlSession()
                .update(SendMDao.namespace + ".cancelSendM",tSendM) > 0;
    }
    
	public boolean checkSendByBox(SendM sendM) {
		Integer count = (Integer)this.getSqlSession().selectOne(namespace+".checkSendByBox", sendM);
		return count>0;
	}
	
	@SuppressWarnings("unchecked")
	public List<SendM> querySendCodesByDepartue(Long shieldsCarId) {
		SendM sendM = new SendM();
		sendM.setShieldsCarId(shieldsCarId);
		return this.getSqlSession().selectList(
				SendMDao.namespace + ".querySendCodesByDepartue", sendM);
	}
	
    public Integer addBatch(List<SendM> param) {
        return this.getSqlSession().insert(namespace + ".addBatch", param);
    }
    
    @SuppressWarnings("unchecked")
	public List<String> batchQuerySendMList(SendM sendM) {
		return getSqlSession().selectList(SendMDao.namespace + ".batchQuerySendMList", sendM);
	}

    /**
     * 已取消发货的箱号
     * @param sendM
     * @return
     */
    @SuppressWarnings("unchecked")
	public List<String> batchQueryCancelSendMList(SendM sendM) {
		return getSqlSession().selectList(SendMDao.namespace + ".batchQueryCancelSendMList", sendM);
	}

	public List<SendM> selectBoxBySendCode(String sendCode) {
		SendM sendM = new SendM();
		sendM.setSendCode(sendCode);
		return	this.getSqlSession().selectList(
				SendMDao.namespace + ".selectBoxBySendCode", sendM);

	}
}
