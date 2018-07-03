package com.jd.bluedragon.distribution.abnormalDispose.dao;

import com.google.common.collect.Maps;
import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.abnormalDispose.domain.AbnormalQc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by hujiping on 2018/6/26.
 */
public class AbnormalQcDao extends BaseDao<AbnormalQc> {
    public static final String namespace = AbnormalQcDao.class.getName();

    public Integer insertAbnormalQc(AbnormalQc abnormalQc) {
        return super.getSqlSession().insert(namespace + ".insertAbnormalQc", abnormalQc);
    }

    public Integer updateAbnormalQc(AbnormalQc abnormalQc) {
        return super.getSqlSession().update(namespace + ".updateAbnormalQc", abnormalQc);
    }

    /**
     * 通过班次id和 多运单，查明细
     * @param waveBusinessId
     * @param waybillCodeList
     * @return
     */
    public List<AbnormalQc> queryQcByWaveIdAndWaybillCodes(String waveBusinessId, ArrayList<String> waybillCodeList) {
        Map<String, Object> param = Maps.newHashMap();
        param.put("waveBusinessId", waveBusinessId);
        param.put("waybillCodes", waybillCodeList);
        return super.getSqlSession().selectList(namespace + ".queryQcByWaveIdAndWaybillCodes", param);
    }

    /**
     * 按班次号 统计
     *
     * @param waveIds
     * @return
     */
    public List<AbnormalQc> getByWaveIds(List<String> waveIds) {
        return super.getSqlSession().selectList(namespace + ".getByWaveIds", waveIds);
    }
}
