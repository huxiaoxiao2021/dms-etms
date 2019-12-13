package com.jd.bluedragon.distribution.send.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public  class SendMDao extends BaseDao<SendM>  {
	
	public static final String namespace = SendMDao.class.getName();
	private final Logger log = LoggerFactory.getLogger(this.getClass());

	public SendM selectOneBySiteAndSendCode(Integer createSiteCode, String sendCode) {
		SendM querySendM = new SendM();
        querySendM.setCreateSiteCode(createSiteCode);
        if(null == createSiteCode) {
            querySendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode));
        }

		querySendM.setSendCode(sendCode);

		//FIXME 如果sendCode不合法则无法通过sendCode获取createSiteCode
		//如果传进来的参数createSiteCode不为空则还会进行一次查询

		if(null == querySendM.getCreateSiteCode()){
			log.info("selectOneBySiteAndSendCode-->参数createSiteCode：{};sendCode:{}",createSiteCode,sendCode);
			log.info("createSiteCode = null");
			return null;
		}

		return (SendM) getSqlSession().selectOne(SendMDao.namespace + ".selectOneBySiteAndSendCode", querySendM);
	}

	@SuppressWarnings("unchecked")
	public List<SendM> selectOneBySendCode(String sendCode) {
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null == createSiteCode) return Collections.emptyList();
		SendM querySendM = new SendM();
		querySendM.setSendCode(sendCode);
        querySendM.setCreateSiteCode(createSiteCode);
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
        Integer createSiteCode = SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null == createSiteCode) return null;
        SendM querySendM = new SendM();
        querySendM.setCreateSiteCode(createSiteCode);
        querySendM.setSendCode(sendCode);
		return (SendM) getSqlSession().selectOne(SendMDao.namespace + ".selectBySendCode", querySendM);
	}
	
	@SuppressWarnings("unchecked")
	public List<SendM> selectBySendSiteCode(SendM sendM) {
		if(log.isInfoEnabled()){
			log.info("selectBySendSiteCode-->参数sendM：{}" , JsonHelper.toJson(sendM));
		}
		if(sendM == null){
            return Collections.emptyList();
        }
		if(null != sendM.getSendCode() && null == sendM.getCreateSiteCode()) {
			sendM.setCreateSiteCode(SerialRuleUtil.getCreateSiteCodeFromSendCode(sendM.getSendCode()));
		}
		if(null == sendM.getCreateSiteCode()){
			log.info("createSiteCode = null");
			return Collections.emptyList();
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
        //TODO
        return getSqlSession().selectList(SendMDao.namespace + ".findSendMByBoxCode", sendM);
    }

	public List<SendM> findSendMByBoxCode2(SendM sendM) {
        //TODO
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

    /**
     * Update By wangtingwei@jd.com，提取创建站点作为查询条件，以便消除全节点数据库查询
     * @param sendCode 发货批次号
     * @return
     */
	public List<String> selectBoxBySendCode(String sendCode) {
		SendM sendM = new SendM();
		sendM.setSendCode(sendCode);
        Integer createSiteCode=SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null==createSiteCode){
            return new ArrayList<String>(0);
        }
        sendM.setCreateSiteCode(createSiteCode);
		return	this.getSqlSession().selectList(
				SendMDao.namespace + ".selectBoxBySendCode", sendM);

	}


    public List<String> selectBoxCodeBySendCodeAndCreateSiteCode(String sendCode){
        SendM sendM = new SendM();
        sendM.setSendCode(sendCode);
        Integer createSiteCode=SerialRuleUtil.getCreateSiteCodeFromSendCode(sendCode);
        if(null==createSiteCode){
            return new ArrayList<String>(0);
        }
        sendM.setCreateSiteCode(createSiteCode);
        return	this.getSqlSession().selectList(
                SendMDao.namespace + ".selectBoxCodeBySendCodeAndCreateSiteCode", sendM);
    }
    /**
     * 按条件查询sendM明细数据
     * @param sendM
     * @return
     */
	public List<SendM> queryListByCondition(SendM sendM) {
		return getSqlSession().selectList(SendMDao.namespace + ".queryListByCondition", sendM);
	}

	/**
	 * 根据板号、批次号和始发分拣中心Code查询sendM中的发货记录
	 * @param sendM
	 * @return
     */
	public List<String> selectBoxCodeByBoardCodeAndSendCode(SendM sendM){
		return	this.getSqlSession().selectList(SendMDao.namespace + ".selectBoxCodeByBoardCodeAndSendCode", sendM);
	}

    /**
	 * 判断板号是否已经操作过发货
	 * @param sendM
	 * @return
     */
	public boolean checkSendByBoard(SendM sendM) {
		Integer count = (Integer)this.getSqlSession().selectOne(namespace+".checkSendByBoard", sendM);
		return count>0;
	}

    /**
	 * 通过板号查询发货记录，只取最新的一条
	 * @param sendM
	 * @return
     */
	public SendM findSendMByBoardCode(SendM sendM){
		return	this.getSqlSession().selectOne(SendMDao.namespace + ".findSendMByBoardCode", sendM);
	}

	/**
	 * 根据始发分拣中心、目的分拣中心、箱号确定send_m的一条发货记录
	 * @param sendM
	 * @return
	 */
	public SendM selectOneByBoxCode(SendM sendM){
		if(sendM == null || sendM.getCreateSiteCode() == null || sendM.getReceiveSiteCode() == null || StringUtils.isBlank(sendM.getBoxCode())){
			throw new IllegalArgumentException("始发分拣中心、目的分拣中心、箱号不能为空.");
		}
		return this.getSqlSession().selectOne(SendMDao.namespace + ".selectOneByBoxCode",sendM);
	}

    /**
     * 根据始发场地和起始时间查询发货记录
     * @param createSiteCode
     * @param startDate
     * @return
     */
    public List<SendM> findAllSendCodesWithStartTime(Integer createSiteCode, Integer receiveSiteCode, Date startDate) {
        if(createSiteCode == null || startDate == null){
            throw new IllegalArgumentException("始发分拣中心、起始时间不能为空.");
        }
        SendM sendM = new SendM();
        sendM.setCreateSiteCode(createSiteCode);
        sendM.setReceiveSiteCode(receiveSiteCode);
        sendM.setOperateTime(startDate);
        return this.getSqlSession().selectList(SendMDao.namespace + ".findAllSendCodesWithStartTime",sendM);
    }
}
