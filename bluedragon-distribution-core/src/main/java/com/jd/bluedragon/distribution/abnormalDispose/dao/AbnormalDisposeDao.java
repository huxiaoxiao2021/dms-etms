package com.jd.bluedragon.distribution.abnormalDispose.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeCondition;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeInspection;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalDisposeRecord;

import java.util.List;

/**
 * Created by hujiping on 2018/6/26.
 */
public class AbnormalDisposeDao extends BaseDao<AbnormalDisposeCondition> {
    public static final String namespace = AbnormalDisposeDao.class.getName();

    public List<AbnormalDisposeInspection> queryInspection(AbnormalDisposeCondition abnormalDisposeCondition){
        return super.getSqlSession().selectList(namespace + ".queryInspection",abnormalDisposeCondition);
    }

    public Integer saveInspection(AbnormalDisposeRecord abnormalDisposeRecord) {
        return super.getSqlSession().insert(namespace + ".saveInspection",abnormalDisposeRecord);
    }

    public Integer updateInspection(AbnormalDisposeRecord abnormalDisposeRecord){
        return super.getSqlSession().update(namespace + ".updateInspection",abnormalDisposeRecord);
    }

    public AbnormalDisposeRecord findInspection(AbnormalDisposeRecord abnormalDisposeRecord) {
        return super.getSqlSession().selectOne(namespace + ".findInspection",abnormalDisposeRecord);
    }
}
