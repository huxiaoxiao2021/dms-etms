package com.jd.bluedragon.distribution.newseal.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.newseal.entity.PreSealBatch;
import com.jd.bluedragon.distribution.newseal.dao.PreSealBatchDao;
import com.jd.bluedragon.distribution.newseal.service.PreSealBatchService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

/**
 * @ClassName: PreSealBatchServiceImpl
 * @Description: 预封车批次数据表--Service接口实现
 * @author wuyoude
 * @date 2020年12月31日 16:45:40
 *
 */
@Service("preSealBatchService")
public class PreSealBatchServiceImpl implements PreSealBatchService {

	private static final Logger logger = LoggerFactory.getLogger(PreSealBatchServiceImpl.class);

	@Autowired
	@Qualifier("preSealBatchDao")
	private PreSealBatchDao preSealBatchDao;

	@Override
	public boolean batchInsert(List<PreSealBatch> preSealBatchs) {
		return preSealBatchDao.batchInsert(preSealBatchs) > 0;
	}

	@Override
	public boolean batchLogicalDeleteByUuid(String preSealUuid) {
		return preSealBatchDao.batchLogicalDeleteByUuid(preSealUuid) > 0;
	}

	@Override
	public List<String> queryByUuid(String preSealUuid) {
		return preSealBatchDao.queryByUuid(preSealUuid);
	}

}
