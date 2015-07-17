package com.jd.bluedragon.distribution.seal.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.seal.domain.SealBox;

public class SealBoxDao extends BaseDao<SealBox> {

    public static final String namespace = SealBoxDao.class.getName();
    public SealBox findBySealCode(String sealCode) {
        return (SealBox) super.getSqlSession().selectOne(SealBoxDao.namespace + ".findBySealCode",
                sealCode);
    }

    public SealBox findByBoxCode(String boxCode) {
        return (SealBox) super.getSqlSession().selectOne(SealBoxDao.namespace + ".findByBoxCode",
        		boxCode);
    }
    
    public int addSealBox(SealBox sealBox) {
    	return this.getSqlSession().insert(SealBoxDao.namespace + ".addSealBox",
    			sealBox);
    }
    
    public int updateSealBox(SealBox sealBox) {
    	return this.getSqlSession().update(SealBoxDao.namespace + ".updateSealBox",
    			sealBox);
    }
}
