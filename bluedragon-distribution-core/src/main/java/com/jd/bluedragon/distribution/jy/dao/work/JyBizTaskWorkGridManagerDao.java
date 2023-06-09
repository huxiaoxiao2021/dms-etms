package com.jd.bluedragon.distribution.jy.dao.work;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.common.dto.work.JyWorkGridManagerQueryRequest;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManager;
import com.jd.bluedragon.distribution.jy.dto.work.JyBizTaskWorkGridManagerCount;

/**
 * @ClassName: JyBizTaskWorkGridManagerDao
 * @Description: 巡检任务表--Dao接口
 * @author wuyoude
 * @date 2023年06月02日 10:54:36
 *
 */
public class JyBizTaskWorkGridManagerDao extends BaseDao<JyBizTaskWorkGridManager> {
	
    final static String NAMESPACE = JyBizTaskWorkGridManagerDao.class.getName();
    
	public JyBizTaskWorkGridManager queryByBizId(String bizId) {
		return this.getSqlSession().selectOne(NAMESPACE + ".queryByBizId", bizId);
	}

	public List<JyBizTaskWorkGridManagerCount> queryDataCountListForPda(JyWorkGridManagerQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataCountListForPda", query);
	}

	public List<JyBizTaskWorkGridManager> queryDataListForPda(JyWorkGridManagerQueryRequest query) {
		return this.getSqlSession().selectList(NAMESPACE + ".queryDataListForPda", query);
	}

	
}
