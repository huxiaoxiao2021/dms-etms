package com.jd.bluedragon.distribution.collection.dao;

import com.jd.bluedragon.distribution.businessCode.dao.BusinessCodeDao;
import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.ibatis.session.SqlSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ProjectName：bluedragon-distribution
 * @Package： com.jd.bluedragon.distribution.collection.dao
 * @ClassName: CollectionRecordDao
 * @Description:
 * @Author： wuzuxiang
 * @CreateDate 2023/2/28 21:08
 * @Copyright: Copyright (c)2020 JDL.CN All Right Reserved
 * @Since: JDK 1.8
 * @Version： V1.0
 */
public class CollectionRecordDao {

    private static final String NAMESPACE = BusinessCodeDao.class.getName();

    private static final String TABLE_NAME_COLLECTION_RECORD = "COLLECTION_RECORD";
    private static final String TABLE_NAME_COLLECTION_RECORD_DETAIL = "COLLECTION_RECORD_DETAIL";

    private SqlSession sqlSession;

    private SequenceGenAdaptor sequenceGenAdaptor;

    public Integer insertCollectionRecord(CollectionRecordPo collectionRecordPo) {
        collectionRecordPo.setId(sequenceGenAdaptor.newId(TABLE_NAME_COLLECTION_RECORD));
        return this.sqlSession.insert(NAMESPACE.concat(".insertCollectionRecord"), collectionRecordPo);
    }

    public Integer batchInsertCollectionRecord(List<CollectionRecordPo> collectionRecordPos) {
        for (CollectionRecordPo collectionRecordPo : collectionRecordPos) {
            collectionRecordPo.setId(sequenceGenAdaptor.newId(TABLE_NAME_COLLECTION_RECORD));
        }
        return this.sqlSession.insert(NAMESPACE.concat(".batchInsertCollectionRecord"), collectionRecordPos);
    }

    public Integer batchInsertCollectionRecordDetail(List<CollectionRecordDetailPo> collectionRecordDetailPos) {
        for (CollectionRecordDetailPo collectionRecordDetailPo : collectionRecordDetailPos) {
            collectionRecordDetailPo.setId(sequenceGenAdaptor.newId(TABLE_NAME_COLLECTION_RECORD_DETAIL));
        }
        return this.sqlSession.insert(NAMESPACE.concat(".batchInsertCollectionRecordDetail"), collectionRecordDetailPos);
    }

    public List<CollectionRecordPo> findCollectionRecord(CollectionRecordPo collectionRecordPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findCollectionRecord"), collectionRecordPo);
    }

    public List<CollectionRecordDetailPo> findCollectionRecordDetail(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findCollectionRecordDetail"), collectionRecordDetailPo);
    }

    public Integer updateCollectionRecord(CollectionRecordPo collectionRecordPo) {
        return this.sqlSession.update(NAMESPACE.concat(".updateCollectionRecord"), collectionRecordPo);
    }

    public Integer updateCollectionRecordDetail(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.update(NAMESPACE.concat(".updateCollectionRecordDetail"), collectionRecordDetailPo);
    }

    public Integer countCollectionRecordByCollectionCode(String collectionCode) {
        return this.sqlSession.selectOne(NAMESPACE.concat(".countCollectionRecordByCollectionCode"), collectionCode);
    }

    public Integer countNoneCollectedAggCodeByCollectionCode(String collectionCode) {
        return this.sqlSession.selectOne(NAMESPACE.concat(".countNoneCollectedAggCodeByCollectionCode"), collectionCode);
    }

    public Integer countCollectionRecordDetailByCollectionCode(String collectionCode) {
        return this.sqlSession.selectOne(NAMESPACE.concat(".countCollectionRecordDetailByCollectionCode"), collectionCode);
    }

    public List<CollectionRecordDetailPo> findAggCodeByScanCode(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findAggCodeByScanCode"), collectionRecordDetailPo);
    }

    public CollectionAggCodeCounter countAggCollectedByAggCode(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectOne(NAMESPACE.concat(".countAggCollectedByAggCode"), collectionRecordDetailPo);
    }

    public List<CollectionScanMarkCounter> countAggCollectedByAggCodeWithMark(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".countAggCollectedByAggCodeWithMark"), collectionRecordDetailPo);
    }

    public List<CollectionCounter> sumCollectionRecordByCollectionCode(List<String> collectionCodes) {
        return this.sqlSession.selectList(NAMESPACE.concat(".sumCollectionRecordByCollectionCode"), collectionCodes);
    }

    public List<CollectionCollectedMarkCounter> sumCollectionAggCodeByCollectionCode(List<String> collectionCodes, Integer isCollected, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        Map<String, Object> param = new HashMap<>();
        param.put("list", collectionCodes);
        param.put("isCollected", isCollected);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        return this.sqlSession.selectList(NAMESPACE.concat(".sumCollectionAggCodeByCollectionCode"), param);
    }

    public List<CollectionRecordDetailPo> queryCollectedDetailByCollectionAndAggCode(List<String> collectionCodes, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum){
        Map<String, Object> param = new HashMap<>();
        param.put("list", collectionCodes);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        return this.sqlSession.selectList(NAMESPACE.concat(".queryCollectedDetailByCollectionAndAggCode"), param);
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
}
