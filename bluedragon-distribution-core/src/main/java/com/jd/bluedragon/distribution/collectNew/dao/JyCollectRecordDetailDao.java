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
    public Integer deleteByAggCode(JyCollectRecordDetailCondition condition) {
        return super.getSqlSession().update(NAMESPACE.concat(".deleteByAggCode"), condition);
    }
    /**
     * 删除
     * @param condition
     * @return
     */
    public Integer deleteByScanCode(JyCollectRecordDetailCondition condition) {
        return super.getSqlSession().update(NAMESPACE.concat(".deleteByScanCode"), condition);
    }



    /**
     * 根据collectionCode 和scanCode查询
     * @param condition
     * @return
     */
    public List<JyCollectRecordDetailPo> findAggCodeByScanCode(JyCollectRecordDetailPo condition) {
        return super.getSqlSession().selectList(NAMESPACE.concat(".findAggCodeByScanCode"), condition);
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
        if(StringUtils.isBlank(condition.getCollectionCode()) && CollectionUtils.isEmpty(condition.getCollectionCodeList())) {
            return null;
        }
        if(Objects.isNull(condition.getSiteId())) {
            return null;
        }
        return this.findPageByCondition(condition);
    }

    public List<JyCollectRecordDetailPo> findPageByCondition(JyCollectRecordDetailCondition condition) {
        if(Objects.isNull(condition.getOffset()) || condition.getOffset() < 0) {
            condition.setOffset(0);
        }
        if(Objects.isNull(condition.getPageSize()) || condition.getPageSize() <= 0) {
            condition.setPageSize(1000);
        }
        return super.getSqlSession().selectList(NAMESPACE.concat(".findPageByCondition"), condition);
    }

}