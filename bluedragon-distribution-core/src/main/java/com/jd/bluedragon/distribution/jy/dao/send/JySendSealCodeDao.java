package com.jd.bluedragon.distribution.jy.dao.send;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.send.JySendSealCodeEntity;

import java.util.List;

/**
 * 发货封签明细表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-05-16 17:50:07
 */
public class JySendSealCodeDao extends BaseDao<JySendSealCodeEntity> {

    private final static String NAMESPACE = JySendSealCodeDao.class.getName();

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JySendSealCodeEntity entity) {
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public int insertBatch(List<JySendSealCodeEntity> list) {
        return this.getSqlSession().insert(NAMESPACE + ".insertBatch", list);
    }

    public List<String> selectSealCodeByBizId(String bizId) {
        return this.getSqlSession().selectList(NAMESPACE + ".selectSealCodeByBizId", bizId);
    }

    public int countByBiz(String sendVehicleBiz) {
        return this.getSqlSession().selectOne(NAMESPACE + ".countByBiz", sendVehicleBiz);
    }
}
