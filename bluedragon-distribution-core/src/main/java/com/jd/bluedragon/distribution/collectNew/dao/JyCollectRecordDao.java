package com.jd.bluedragon.distribution.collectNew.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordPo;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordStatistics;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;

import java.util.List;
import java.util.Objects;

public class JyCollectRecordDao extends BaseDao<JyCollectRecordPo> {

    private static final String NAMESPACE = JyCollectRecordDao.class.getName();

    private static final String TABLE_NAME = "jy_collect_record";
    private SequenceGenAdaptor sequenceGenAdaptor;
    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
    private Long generateId(){
        return sequenceGenAdaptor.newId(TABLE_NAME);
    }

    public int insertSelective(JyCollectRecordPo recordPo){
        recordPo.setId(this.generateId());
        return super.getSqlSession().insert(NAMESPACE.concat(".insertSelective"), recordPo);
    }

    /**
     * 根据collectionCode 和 aggCode 查
     * @param collectionRecordPo
     * @return
     */
    public JyCollectRecordPo findJyCollectRecordByAggCode(JyCollectRecordPo collectionRecordPo) {
        return super.getSqlSession().selectOne(NAMESPACE.concat(".findJyCollectRecordByAggCode"), collectionRecordPo);
    }

    /**
     * 修改
     * @param condition
     * @return
     */
    public Integer updateByCondition(JyCollectRecordPo condition) {
        return super.getSqlSession().update(NAMESPACE.concat(".updateByCondition"), condition);
    }

    /**
     * 查询不齐运单数
     * @param condition
     * @return
     */
    public List<JyCollectRecordStatistics> findBuQiByCollectionCodes(JyCollectRecordCondition condition) {
        return super.getSqlSession().selectList(NAMESPACE.concat(".findPageBuQiByCollectionCodes"), condition);
    }

    public List<JyCollectRecordPo> findPageByCondition(JyCollectRecordCondition condition) {
        if(Objects.isNull(condition.getOffset()) || condition.getOffset() < 0) {
            condition.setOffset(0);
        }
        if(Objects.isNull(condition.getPageSize()) || condition.getPageSize() <= 0) {
            condition.setPageSize(1000);
        }

        return super.getSqlSession().selectList(NAMESPACE.concat(".findPageByCondition"), condition);

    }
}