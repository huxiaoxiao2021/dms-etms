package com.jd.bluedragon.distribution.rma.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverWaybill;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public class RmaHandOverWaybillDao extends BaseDao<RmaHandoverWaybill> {

    public static final String namespace = RmaHandOverWaybillDao.class.getName();

    /**
     * 新增
     *
     * @param rmaHandoverWaybill
     * @return
     */
    public int add(RmaHandoverWaybill rmaHandoverWaybill) {
        return this.getSqlSession().insert(namespace + ".add", rmaHandoverWaybill);
    }

    /**
     * 修改
     *
     * @param rmaHandoverWaybill
     * @return
     */
    public int update(RmaHandoverWaybill rmaHandoverWaybill) {
        return this.getSqlSession().update(namespace + ".update", rmaHandoverWaybill);
    }

    /**
     * 更新打印信息
     *
     * @param parameter
     * @return
     */
    public int updatePrintInfo(Map<String, Object> parameter) {
        return this.getSqlSession().update(namespace + ".updatePrintInfo", parameter);
    }

    /**
     * 根据id查询
     *
     * @param id
     * @return
     */
    public RmaHandoverWaybill getById(Long id) {
        return this.get(namespace + ".getById", id);
    }

    /**
     * 根据id列表分组
     *
     * @param ids
     * @return
     */
    public List<RmaHandoverWaybill> getListByIds(List<Long> ids) {
        return this.getSqlSession().selectList(namespace + ".getListByIds", ids);
    }

    /**
     * 根据条件查询
     *
     * @param parameter
     * @return
     */
    public List<RmaHandoverWaybill> getListByParams(Map<String, Object> parameter) {
        return this.getSqlSession().selectList(namespace + ".getListByParams", parameter);
    }

    /**
     * 查询数量
     *
     * @param parameter
     * @return
     */
    public Integer getCountByParams(Map<String, Object> parameter){
        return this.getSqlSession().selectOne(namespace + ".getCountByParams", parameter);
    }

}

