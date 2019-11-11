package com.jd.bluedragon.distribution.receiveInspectionExc.service.impl;

import com.jd.bluedragon.distribution.api.request.ShieldsBoxErrorRequest;
import com.jd.bluedragon.distribution.api.request.ShieldsCarErrorRequest;
import com.jd.bluedragon.distribution.receiveInspectionExc.dao.ShieldsErrorDao;
import com.jd.bluedragon.distribution.receiveInspectionExc.domain.ShieldsError;
import com.jd.bluedragon.distribution.receiveInspectionExc.service.ShieldsErrorService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service("shieldsErrorService")
public class ShieldsErrorServiceImpl implements ShieldsErrorService {
	private Logger logger = LoggerFactory.getLogger(ShieldsErrorServiceImpl.class);
	@Autowired
	private ShieldsErrorDao shieldsErrorDao;

	/**
	 * 记录封签异常
	 * 
	 * @param shieldsErrors
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public boolean doAddShieldsError(ShieldsError shieldsError) {
		return shieldsErrorDao.add(ShieldsErrorDao.namespace, shieldsError) > 0;
	}

	/**
	 * 解析json数据
	 */
	public ShieldsError doParseShieldsCar(Task task) {
		String jsonShieldsError = task.getBody();
		logger.info("封签异常json数据：{}" , jsonShieldsError);
		List<ShieldsCarErrorRequest> shieldsErrorRequest = Arrays
				.asList(JsonHelper.jsonToArray(jsonShieldsError,
						ShieldsCarErrorRequest[].class));
		logger.info("封签异常json数据转化后：{}" , shieldsErrorRequest);
		ShieldsError shieldsError = new ShieldsError();
		ShieldsCarErrorRequest sh = shieldsErrorRequest.get(0);
		shieldsError.setCarCode(sh.getCarCode());
		shieldsError.setShieldsCode(sh.getShieldsCode());
		shieldsError.setCreateSiteCode(sh.getSiteCode());
		shieldsError
				.setCreateTime(DateHelper.getSeverTime(sh.getOperateTime()));
		shieldsError
				.setUpdateTime(DateHelper.getSeverTime(sh.getOperateTime()));
		shieldsError.setCreateUser(sh.getUserName());
		shieldsError.setCreateUserCode(sh.getUserCode());
		shieldsError.setShieldsError(sh.getShieldsError());
		shieldsError.setYn(1);
		shieldsError.setBusinessType(sh.getBusinessType());
		return shieldsError;
	}

	/**
	 * 解析json数据
	 * 
	 * */
	public ShieldsError doParseShieldsBox(Task task) {
		String jsonShieldsError = task.getBody();
		logger.info("封签异常json数据：{}" , jsonShieldsError);
		List<ShieldsBoxErrorRequest> shieldsErrorRequest = Arrays
				.asList(JsonHelper.jsonToArray(jsonShieldsError,
						ShieldsBoxErrorRequest[].class));
		logger.info("封签异常json数据转化后：{}" , shieldsErrorRequest);
		ShieldsError shieldsError = new ShieldsError();
		ShieldsBoxErrorRequest sh = shieldsErrorRequest.get(0);
		shieldsError.setBoxCode(sh.getBoxCode());
		shieldsError.setShieldsCode(sh.getShieldsCode());
		shieldsError.setCreateSiteCode(sh.getSiteCode());
		shieldsError
				.setCreateTime(DateHelper.getSeverTime(sh.getOperateTime()));
		shieldsError
				.setUpdateTime(DateHelper.getSeverTime(sh.getOperateTime()));
		shieldsError.setCreateUser(sh.getUserName());
		shieldsError.setCreateUserCode(sh.getUserCode());
		shieldsError.setShieldsError(sh.getShieldsError());
		shieldsError.setYn(1);
		shieldsError.setBusinessType(sh.getBusinessType());
		return shieldsError;
	}
}
