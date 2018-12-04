package com.jd.bluedragon.distribution.signAndReturn.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.signAndReturn.domain.SignReturnPrintM;
import com.jd.bluedragon.distribution.signReturn.SignReturnCondition;

import java.util.List;

/**
 * @ClassName: SignReturnDao
 * @Description: 类描述信息
 * @author: hujiping
 * @date: 2018/11/28 13:43
 */
public class SignReturnDao extends BaseDao<SignReturnPrintM> {

    public static final String namespace = SignReturnDao.class.getName();

    /**
     * 新增
     * @param signReturnPrintM
     * @return
     */
    public int add(SignReturnPrintM signReturnPrintM){
        return this.getSqlSession().insert(namespace + ".add",signReturnPrintM);
    }

    /**
     * 根据运单号获得签单信息
     * @param condition
     * @return
     */
    public List<SignReturnPrintM> getListByWaybillCode(SignReturnCondition condition){

        return this.getSqlSession().selectList(namespace + ".getListByWaybillCode",condition);
    }

    /**
     * 根据id获得签单信息
     * @param id
     * @return
     */
    public List<SignReturnPrintM> getSignReturnById(Long id) {

        return this.getSqlSession().selectList(namespace + ".getSignReturnById",id);
    }
}
