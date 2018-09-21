package com.jd.bluedragon.distribution.rma.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.rma.domain.RmaHandoverDetail;

import java.util.List;

/**
 * <p>
 * Created by lixin39 on 2018/9/20.
 */
public class RmaHandOverDetailDao extends BaseDao<RmaHandoverDetail> {

    public static final String namespace = RmaHandOverDetailDao.class.getName();

    /**
     * 新增
     *
     * @param rmaHandoverDetail
     * @return
     */
    public int add(RmaHandoverDetail rmaHandoverDetail) {
        return this.getSqlSession().insert(namespace + ".add", rmaHandoverDetail);
    }

    /**
     * 批量新增
     *
     * @param rmaHandoverDetails
     */
    public void batchAdd(List<RmaHandoverDetail> rmaHandoverDetails) {
        this.getSqlSession().insert(namespace + ".batchAdd", rmaHandoverDetails);
    }

    /**
     * 根据交接清单主表ID获取明细信息
     *
     * @param handoverWaybillId
     * @return
     */
    public List<RmaHandoverDetail> getListByHandoverWaybillId(Long handoverWaybillId) {
        return this.getSqlSession().selectList(namespace + ".getListByHandoverWaybillId", handoverWaybillId);
    }

}
