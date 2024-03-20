package com.jd.bluedragon.distribution.jy.dao.work;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.work.JyWorkGridManagerResponsibleInfo;

public class JyWorkGridManagerResponsibleInfoDao extends BaseDao<JyWorkGridManagerResponsibleInfo> {
    final static String NAMESPACE = JyWorkGridManagerResponsibleInfoDao.class.getName();
    public int add(JyWorkGridManagerResponsibleInfo responsibleInfo) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", responsibleInfo);
        
    }
    public JyWorkGridManagerResponsibleInfo queryByBizId(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryByBizId", bizId);
    }
    
}
