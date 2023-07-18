package com.jd.bluedragon.distribution.jy.dao.unload;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.jy.dto.unload.DimensionQueryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.GoodsCategoryDto;
import com.jd.bluedragon.distribution.jy.dto.unload.ScanStatisticsDto;
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
class JyUnloadAggsDaoBak extends BaseDao<JyUnloadAggsEntity> implements JyUnloadAggsDaoStrategy {

    private static final String DB_TABLE_NAME = "jy_unload_aggs";

    final static String NAMESPACE = JyUnloadAggsDaoBak.class.getName();

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

    public List<JyUnloadAggsEntity> queryShouldScanByBizIds(List<String> bizIds) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryShouldScanByBizIds", bizIds);
    }

    //查包裹维度任务统计信息
    public JyUnloadAggsEntity queryPackageStatistics(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryPackageStatistics", dto);
    }

    public JyUnloadAggsEntity queryToScanAndMoreScanStatistics(String bizId) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryToScanAndMoreScanStatistics", bizId);
    }

    //查运单维度任务统计信息
    public JyUnloadAggsEntity queryWaybillStatisticsUnderTask(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryWaybillStatisticsUnderTask", dto);
    }
    public JyUnloadAggsEntity queryWaybillStatisticsUnderBoard(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryWaybillStatisticsUnderBoard", dto);
    }

    public List<GoodsCategoryDto> queryGoodsCategoryStatistics(JyUnloadAggsEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".queryGoodsCategoryStatistics", entity);
    }

    public ScanStatisticsDto queryExcepScanStatistics(JyUnloadAggsEntity entity) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryExcepScanStatistics", entity);
    }

    public JyUnloadAggsEntity queryBoardStatistics(DimensionQueryDto dto) {
        return this.getSqlSession().selectOne(NAMESPACE + ".queryBoardStatistics", dto);
    }

    public int updateByBizProductBoard(JyUnloadAggsEntity entity){
        return this.getSqlSession().update(NAMESPACE + ".updateByBizProductBoard", entity);
    }

    public int insertSelective(JyUnloadAggsEntity entity){
        return this.getSqlSession().insert(NAMESPACE + ".insertSelective", entity);
    }

    public List<JyUnloadAggsEntity> getUnloadAggsBakData(JyUnloadAggsEntity entity) {
        return this.getSqlSession().selectList(NAMESPACE + ".getUnloadAggsBakData", entity);
    }
}
