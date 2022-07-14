package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.unload.DimensionQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadAggDto;
import com.jd.bluedragon.distribution.jy.unload.JyUnloadAggsEntity;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * 卸车进度汇总表
 * 
 * @author liuduo8
 * @email liuduo3@jd.com
 * @date 2022-04-01 16:23:35
 */
public class JyUnloadAggsDao extends BaseDao<JyUnloadAggsEntity> {

    private static final String DB_TABLE_NAME = "jy_unload_aggs";

    final static String NAMESPACE = JyUnloadAggsDao.class.getName();

    @Autowired
    private SequenceGenAdaptor sequenceGenAdaptor;

    /**
     * 新增
     *
     * @param
     * @return
     */
    public int insert(JyUnloadAggsEntity entity) {
        entity.setId(sequenceGenAdaptor.newId(DB_TABLE_NAME));
        return this.getSqlSession().insert(NAMESPACE + ".insert", entity);
    }

    public List<JyUnloadAggsEntity> queryByBizId(JyUnloadAggsEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryByBizId", entity);
    }

    public JyUnloadAggsEntity queryStatisticsUnderPackage(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryStatisticsUnderPackage", dto);
    }
    public JyUnloadAggsEntity queryStatisticsUnderWaybill(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryStatisticsUnderWaybill", dto);
    }
    public JyUnloadAggsEntity queryUnloadStatistics(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryUnloadStatistics", dto);
    }
}
