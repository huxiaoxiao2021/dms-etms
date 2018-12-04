package com.jd.bluedragon.distribution.signAndReturn.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.signAndReturn.domain.MergedWaybill;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;

import java.util.List;

/**
 * @ClassName: MergedWaybillDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/29 18:15
 */
public class MergedWaybillDao extends BaseDao<MergedWaybill> {

    public static final String namespace = MergedWaybillDao.class.getName();

    /**
     * 批量增加
     * @param mergedWaybillList
     * @return
     */
    public int bathAdd(List<MergedWaybill> mergedWaybillList){
        return this.getSqlSession().insert(namespace + ".bathAdd",mergedWaybillList);
    }

    /**
     * 通过主键id获取旧运单号集合
     * @param id
     * @return
     */
    public List<MergedWaybill> getListBySignReturnPrintMId(Long id){

        return this.getSqlSession().selectList(namespace + ".getListBySignReturnPrintMId",id);
    }

    /**
     * 通过运单号获取旧运单号集合
     * */
    public List<MergedWaybill> getListByWaybillCode(SignReturnCondition condition) {
        return this.getSqlSession().selectList(namespace + ".getListByWaybillCode",condition);
    }
}
