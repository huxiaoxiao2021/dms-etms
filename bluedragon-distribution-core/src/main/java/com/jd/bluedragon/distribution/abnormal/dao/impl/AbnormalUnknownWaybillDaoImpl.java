package com.jd.bluedragon.distribution.abnormal.dao.impl;

import com.jd.bluedragon.distribution.abnormal.dao.AbnormalUnknownWaybillDao;
import com.jd.bluedragon.distribution.abnormal.domain.AbnormalUnknownWaybill;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wuyoude
 * @ClassName: AbnormalUnknownWaybillDaoImpl
 * @Description: 三无订单申请--Dao接口实现
 * @date 2018年05月08日 15:16:15
 */
@Repository("abnormalUnknownWaybillDao")
public class AbnormalUnknownWaybillDaoImpl extends BaseDao<AbnormalUnknownWaybill> implements AbnormalUnknownWaybillDao {


    @Override
    public List<String> queryHasDetailWaybillCode(List<String> waybillCodes) {
        return this.getSqlSession().selectList(this.getNameSpace() + ".queryHasDetailWaybillCode", waybillCodes);
    }

    @Override
    public AbnormalUnknownWaybill findLastReportByWaybillCode(String waybillCode) {
        return this.getSqlSession().selectOne(this.getNameSpace() + ".findLastReportByWaybillCode", waybillCode);
    }

    @Override
    public int updateReceive(AbnormalUnknownWaybill abnormalUnknownWaybill) {
        return this.getSqlSession().update(this.getNameSpace() + ".updateReceive", abnormalUnknownWaybill);
    }

    @Override
    public List<AbnormalUnknownWaybill> queryByWaybillCode(String waybillCode) {
        return this.getSqlSession().selectList(this.getNameSpace() + ".queryByWaybillCode", waybillCode);
    }
}
