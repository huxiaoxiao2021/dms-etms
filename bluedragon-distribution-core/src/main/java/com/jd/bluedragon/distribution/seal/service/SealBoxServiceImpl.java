package com.jd.bluedragon.distribution.seal.service;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.seal.dao.SealBoxDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;

@Service("sealBoxService")
public class SealBoxServiceImpl implements SealBoxService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private SealBoxDao sealBoxDao;

	@Profiled(tag = "SealBoxService.addSealBox")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(SealBox sealBox) {
		Assert.notNull(sealBox, "sealBox must not be null");
		return this.sealBoxDao.add(SealBoxDao.namespace, sealBox);
	}

	@Profiled(tag = "SealBoxService.updateSealBox")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(SealBox sealBox) {
		Assert.notNull(sealBox, "sealBox must not be null");
		return this.sealBoxDao.update(SealBoxDao.namespace, sealBox);
	}

	@Profiled(tag = "SealBoxService.saveOrUpdate")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveOrUpdate(SealBox sealBox) {
		if (Constants.NO_MATCH_DATA == this.update(sealBox)) {
			this.add(sealBox);
		}
	}

	@Profiled(tag = "SealBoxService.findBySealCode")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SealBox findBySealCode(String sealCode) {
		Assert.notNull(sealCode, "sealCode must not be null");
		return this.sealBoxDao.findBySealCode(sealCode);
	}
	
	@Profiled(tag = "SealBoxService.findByBoxCode")
	@Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
	public SealBox findByBoxCode(String boxCode) {
		Assert.notNull(boxCode, "boxCode must not be null");
		return this.sealBoxDao.findByBoxCode(boxCode);
	}

	@Profiled(tag = "SealBoxService.doSealBox")
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void doSealBox(Task task) {
		this.taskToSealBox(task);
	}

	private void taskToSealBox(Task task) {
		String json = task.getBody();
		if (StringHelper.isEmpty(json)) {
			return;
		}

		SealBoxRequest[] array = JsonHelper.jsonToArray(json,
				SealBoxRequest[].class);
		Set<SealBoxRequest> sealBoxes = new CollectionHelper<SealBoxRequest>()
				.toSet(array);
		for (SealBoxRequest request : sealBoxes) {
			SealBox sealBox = SealBox.toSealBox(request);
			this.add(sealBox);
		}
	}

	  
	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public int addSealBox(SealBox sealBox) {
		this.logger.info("封箱服务-->增加封箱信息，先执行更新操作，封箱号【" + sealBox.getCode() + "】");
		int tempUpdateCount = this.sealBoxDao.updateSealBox(sealBox);
		if (tempUpdateCount <= 0) {
			this.sealBoxDao.addSealBox(sealBox);
			this.logger.info("封箱服务-->增加封箱信息，更新无数据，执行新增操作成功，封箱号【" + sealBox.getCode() + "】");
		} else {
			this.logger.info("封箱服务-->增加封箱信息，执行更新成功，封箱号【" + sealBox.getCode() + "】");
		}
		return 1;
	}
}
