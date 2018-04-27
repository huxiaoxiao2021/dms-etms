package com.jd.bluedragon.distribution.abnormal.dao.impl;

import com.jd.bluedragon.distribution.abnormal.dao.DmsAbnormalEclpDao;
import com.jd.bluedragon.distribution.abnormal.domain.DmsAbnormalEclp;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;
import org.springframework.stereotype.Repository;

/**
 * @author wuyoude
 * @ClassName: DmsAbnormalEclpDaoImpl
 * @Description: ECLP外呼申请表--Dao接口实现
 * @date 2018年03月14日 16:31:20
 */
@Repository("dmsAbnormalEclpDao")
public class DmsAbnormalEclpDaoImpl extends BaseDao<DmsAbnormalEclp> implements DmsAbnormalEclpDao {

    @Override
    public int updateResult(DmsAbnormalEclp dmsAbnormalEclp) {
        return super.getSqlSession().update(this.getNameSpace()+".updateResult", dmsAbnormalEclp);
    }
}
