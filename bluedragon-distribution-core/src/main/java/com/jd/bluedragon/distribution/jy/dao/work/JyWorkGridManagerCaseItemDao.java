package com.jd.bluedragon.distribution.jy.dao.work;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCaseItem;

/**
 * @ClassName: JyWorkGridManagerCaseItemDao
 * @Description: 巡检任务表-检查项明细--Dao接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public class JyWorkGridManagerCaseItemDao extends BaseDao<JyWorkGridManagerCaseItem> {
	final static String NAMESPACE = JyWorkGridManagerCaseDao.class.getName();
    
	public List<JyWorkGridManagerCaseItem> queryItemListByBizId(String bizId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryItemListByBizId", bizId);
	}

}
