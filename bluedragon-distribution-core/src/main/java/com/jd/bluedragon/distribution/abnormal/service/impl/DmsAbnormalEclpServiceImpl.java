package com.jd.bluedragon.distribution.abnormal.service.impl;

import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclpCondition;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.common.web.LoginContext;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.bluedragon.distribution.abnormal.dao.DmsAbnormalEclpDao;
import com.jd.bluedragon.distribution.abnormal.service.DmsAbnormalEclpService;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @ClassName: DmsAbnormalEclpServiceImpl
 * @Description: ECLP外呼申请表--Service接口实现
 * @author wuyoude
 * @date 2018年03月14日 16:31:20
 *
 */
@Service("dmsAbnormalEclpService")
public class DmsAbnormalEclpServiceImpl extends BaseService<DmsAbnormalEclp> implements DmsAbnormalEclpService {

	@Autowired
	@Qualifier("dmsAbnormalEclpDao")
	private DmsAbnormalEclpDao dmsAbnormalEclpDao;

	@Autowired
	private BaseMajorManager baseMajorManager;

	@Override
	public Dao<DmsAbnormalEclp> getDao() {
		return this.dmsAbnormalEclpDao;
	}

	@Autowired
	@Qualifier("abnormalEclpSendProducer")
	private DefaultJMQProducer abnormalEclpSendProducer;

	@Override
	public JdResponse<Boolean> save(DmsAbnormalEclp dmsAbnormalEclp) {
		JdResponse<Boolean> rest = new JdResponse<Boolean>();
		//1.2个月内该运单只能发起一次库房拒收外呼申请
		DmsAbnormalEclpCondition condition = new DmsAbnormalEclpCondition();
		condition.setWaybillCode(dmsAbnormalEclp.getWaybillCode());
		// 不限制该运单是否在进行外呼中，即，只能发起一次
		condition.setStartTime(DateHelper.add(new Date(), Calendar.MONTH, -2));
		PagerResult result = queryByPagerCondition(condition);
		//判断当前运单是否有未进行完毕的外呼
		if(result.getTotal() > 0){
			rest.toFail("运单已发起过库房拒收的外呼申请：" + dmsAbnormalEclp.getWaybillCode());
			return rest;
		}
		LoginContext loginContext = LoginContext.getLoginContext();
		BaseStaffSiteOrgDto dto = baseMajorManager.getBaseStaffByErpNoCache(loginContext.getPin());
		dmsAbnormalEclp.setCreateUser(loginContext.getPin());
		dmsAbnormalEclp.setCreateUserCode(dto.getStaffNo());
		dmsAbnormalEclp.setCreateUserName(loginContext.getNick());
		dmsAbnormalEclp.setDmsSiteCode(dto.getSiteCode());
		dmsAbnormalEclp.setDmsSiteName(dto.getSiteName());
		if (!saveOrUpdate(dmsAbnormalEclp)){
			rest.toFail("保存外呼申请失败，请重试。" );
			logger.error("保存外呼申请失败："+ JsonHelper.toJson(dmsAbnormalEclp));
			return rest;
		}

//		abnormalEclpSendProducer.sendOnFailPersistent(dmsAbnormalEclp.getId(),);

		return null;
	}
}
