package com.jd.bluedragon.distribution.collectNew.dao;


import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailCondition;
import com.jd.bluedragon.distribution.collectNew.entity.JyCollectRecordDetailPo;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Objects;

public class JyCollectRecordDetailDao extends BaseDao<JyCollectRecordDetailPo> {

    private static final String NAMESPACE = JyCollectRecordDetailDao.class.getName();

    private static final String TABLE_NAME = "jy_collect_record_detail";

    private SequenceGenAdaptor sequenceGenAdaptor;
    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
    private Long generateId(){
        return sequenceGenAdaptor.newId(TABLE_NAME);
    }


    /**
     * 插入
     * @param recordPo
     * @return
     */
    public int insertSelective(JyCollectRecordDetailPo recordPo){
        recordPo.setId(this.generateId());
        return super.getSqlSession().insert(NAMESPACE.concat(".insertSelective"), recordPo);
    }

    /**
     * 删除
     * @param condition
     * @return
     */
    public Integer deleteByCondition(JyCollectRecordDetailPo condition) {
        return super.getSqlSession().update(NAMESPACE.concat(".deleteByCondition"), condition);
    }

    /**
     * 根据collectionCode 和scanCode查询
     * @param condition
     * @return
     */
    public List<JyCollectRecordDetailPo> findAggCodeByCondition(JyCollectRecordDetailPo condition) {
        return super.getSqlSession().selectList(NAMESPACE.concat(".findAggCodeByCondition"), condition);
    }

    /**
     * 统计运单下扫描数量
     * @param detailPo
     * @return
     */
    public Integer countScanCodeNumNumByCollectedMarkAndAggCode(JyCollectRecordDetailPo detailPo) {
        if(StringUtils.isBlank(detailPo.getCollectionCode()) || Objects.isNull(detailPo.getSiteId())) {
            return null;
        }
        return super.getSqlSession().selectOne(NAMESPACE.concat(".countScanCodeNumNumByCollectedMarkAndAggCode"), detailPo);
    }

    /**
     * 按运单已扫包裹
     * @param condition
     * @return
     */
    public List<JyCollectRecordDetailPo> findByCollectionCodesAndAggCode(JyCollectRecordDetailCondition condition) {
        if(StringUtils.isBlank(condition.getCollectionCode()) || CollectionUtils.isEmpty(condition.getCollectionCodeList())) {
            return null;
        }
        if(Objects.isNull(condition.getSiteId())) {
            return null;
        }
        return super.getSqlSession().selectList(NAMESPACE.concat(".findPageByCondition"), condition);
    }
}