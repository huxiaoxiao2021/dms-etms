package com.jd.bluedragon.distribution.jy.dao.work;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerCase;

/**
 * @ClassName: JyWorkGridManagerCaseDao
 * @Description: 巡检任务表-检查项--Dao接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public class JyWorkGridManagerCaseDao extends BaseDao<JyWorkGridManagerCase> {
    
	final static String NAMESPACE = JyWorkGridManagerCaseDao.class.getName();
    
	public List<JyWorkGridManagerCase> queryCaseListByBizId(String bizId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryCaseListByBizId", bizId);
	}
	
}
