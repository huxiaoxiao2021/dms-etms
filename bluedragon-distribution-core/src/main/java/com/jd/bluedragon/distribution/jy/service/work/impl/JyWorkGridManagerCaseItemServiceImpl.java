package com.jd.bluedragon.distribution.jy.service.work.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.jy.dao.work.JyWorkGridManagerCaseItemDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;
import com.jd.bluedragon.distribution.jy.service.work.JyWorkGridManagerCaseItemService;

/**
 * @ClassName: JyWorkGridManagerCaseItemServiceImpl
 * @Description: 巡检任务表-检查项明细--Service接口实现
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
@Service("jyWorkGridManagerCaseItemService")
public class JyWorkGridManagerCaseItemServiceImpl implements JyWorkGridManagerCaseItemService {

	private static final Logger logger = LoggerFactory.getLogger(JyWorkGridManagerCaseItemServiceImpl.class);

	@Autowired
	@Qualifier("jyWorkGridManagerCaseItemDao")
	private JyWorkGridManagerCaseItemDao jyWorkGridManagerCaseItemDao;

	@Override
	public List<JyWorkGridManagerCaseItem> queryItemListByBizId(String bizId) {
		return jyWorkGridManagerCaseItemDao.queryItemListByBizId(bizId);
	}


}
