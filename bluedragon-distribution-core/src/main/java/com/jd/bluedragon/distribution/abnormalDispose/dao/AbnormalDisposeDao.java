package com.jd.bluedragon.distribution.abnormalDispose.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalQc;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by hujiping on 2018/6/26.
 */
public class AbnormalDisposeDao extends BaseDao<AbnormalQc> {
    public static final String namespace = AbnormalDisposeDao.class.getName();

    public Integer saveInspection(AbnormalQc abnormalQc) {
        return super.getSqlSession().insert(namespace + ".saveInspection", abnormalQc);
    }

    public Integer updateInspection(AbnormalQc abnormalQc){
        return super.getSqlSession().update(namespace + ".updateInspection", abnormalQc);
    }

    public AbnormalQc findInspection(AbnormalQc abnormalQc) {
        return super.getSqlSession().selectOne(namespace + ".findInspection", abnormalQc);
    }

    public List<AbnormalQc> queryQcCodes(ArrayList<String> waybillCodeList) {
        return super.getSqlSession().selectList(namespace+ ".queryQcCodes",waybillCodeList);
    }
}
