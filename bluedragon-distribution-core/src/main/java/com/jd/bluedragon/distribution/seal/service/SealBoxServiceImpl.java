package com.jd.bluedragon.distribution.seal.service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.seal.dao.SealBoxDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service("sealBoxService")
public class SealBoxServiceImpl implements SealBoxService {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private SealBoxDao sealBoxDao;

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer add(SealBox sealBox) {
		Assert.notNull(sealBox, "sealBox must not be null");
		return this.sealBoxDao.add(SealBoxDao.namespace, sealBox);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Integer update(SealBox sealBox) {
		Assert.notNull(sealBox, "sealBox must not be null");
		return this.sealBoxDao.update(SealBoxDao.namespace, sealBox);
	}

	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public void saveOrUpdate(SealBox sealBox) {
		if (Constants.NO_MATCH_DATA == this.update(sealBox)) {
			this.add(sealBox);
		}
	}

	public SealBox findBySealCode(String sealCode) {
		Assert.notNull(sealCode, "sealCode must not be null");
		return this.sealBoxDao.findBySealCode(sealCode);
	}
	
	public SealBox findByBoxCode(String boxCode) {
		Assert.notNull(boxCode, "boxCode must not be null");
		return this.sealBoxDao.findByBoxCode(boxCode);
	}

	@Override
	public List<SealBox> findListByBoxCodes(List<String> boxCodeList) {
		Assert.notNull(boxCodeList, "boxCodeList must not be null");
		if (boxCodeList.size() == 0) {
			return Collections.EMPTY_LIST;
		}
		return this.sealBoxDao.findListByBoxCodes(boxCodeList);
	}

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
			if(request.getBoxCode().length() > Constants.BOX_CODE_DB_COLUMN_LENGTH_LIMIT){
				log.warn("封箱箱号字段超长，消息体：{}" , JsonHelper.toJson(request));
				continue;
			}
			SealBox sealBox = SealBox.toSealBox(request);
			this.add(sealBox);
		}
	}

	  
	@Override
	public int addSealBox(SealBox sealBox) {
		SealBox sealBoxOne = this.sealBoxDao.findBySealCode(sealBox.getCode());
		if (sealBoxOne == null) {
			this.sealBoxDao.addSealBox(sealBox);
			this.log.info("封箱服务-->增加封箱信息，执行新增操作成功，封箱号【{}】",sealBox.getCode());
		} else {
			this.sealBoxDao.updateSealBox(sealBox);
			this.log.info("封箱服务-->更新封箱信息，执行更新成功，封箱号【{}】",sealBox.getCode());
		}
		return 1;
	}
}
