package com.jd.bluedragon.distribution.reassignWaybill.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.api.request.ReassignWaybillApprovalRecordQuery;
import com.jd.bluedragon.distribution.jy.dto.strand.JyStrandTaskPageCondition;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybillApprovalRecord;

import java.util.List;
import java.util.Map;

public class ReassignWaybillApprovalRecordDao extends BaseDao<ReassignWaybillApprovalRecord> {

    public static final String namespace = ReassignWaybillApprovalRecordDao.class.getName();


    public Boolean add(ReassignWaybillApprovalRecord record){
        return this.getSqlSession().insert(namespace + ".add", record) > 0;
    }

    public int updateByBarCodeApprovalNoPass(ReassignWaybillApprovalRecord record) {
        return this.getSqlSession().update(namespace + ".updateByBarCodeApprovalNoPass", record);
    }

    public int updateReassignWaybilApprovalNotPassByBarCode(Map<String,Object> params){
        return this.getSqlSession().update(namespace + ".updateReassignWaybilApprovalNotPassByBarCode", params);
    }

    public ReassignWaybillApprovalRecord selectByBarCodeApprovalNoPass(String barCode){
        return this.getSqlSession().selectOne(namespace + ".selectByBarCodeApprovalNoPass", barCode);
    }

    public Integer queryTotalByCondition(ReassignWaybillApprovalRecordQuery req) {
        return this.getSqlSession().selectOne(namespace + ".queryTotalByCondition", req);
    }

    public List<ReassignWaybillApprovalRecord> getApprovalRecordPageListByCondition(ReassignWaybillApprovalRecordQuery req) {
        return this.getSqlSession().selectList(namespace + ".getApprovalRecordPageListByCondition", req);
    }


}