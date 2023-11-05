package com.jd.bluedragon.distribution.reassignWaybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecord;

import java.util.List;

public class ReassignWaybillApprovalRecordDao extends BaseDao<ReassignWaybillApprovalRecord> {

    public static final String namespace = ReassignWaybillApprovalRecordDao.class.getName();


    public Boolean add(ReassignWaybillApprovalRecord record){
        return this.getSqlSession().insert(namespace + ".add", record) > 0;
    }

    public int update(ReassignWaybillApprovalRecord record) {
        return this.getSqlSession().update(namespace + ".updateByBarCode", record);
    }

    public ReassignWaybillApprovalRecord selectByBarCode(String barCode){
        return this.getSqlSession().selectOne(namespace + ".selectByBarCode", barCode);
    }

    public List<ReassignWaybillApprovalRecord> getApprovalRecordList(ReassignWaybillApprovalRecordQuery req) {
        return this.getSqlSession().selectList(namespace + ".getApprovalRecordList", req);
    }

    public ReassignWaybillApprovalRecord getApprovalNotPassByBarCode(String barCode){
        return this.getSqlSession().selectOne(namespace + ".getApprovalNotPassByBarCode", barCode);
    }

}