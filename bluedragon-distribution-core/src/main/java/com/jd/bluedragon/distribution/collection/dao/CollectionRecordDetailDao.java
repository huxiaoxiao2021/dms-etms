package com.jd.bluedragon.distribution.collection.dao;

import com.jd.bluedragon.distribution.collection.entity.CollectionCollectedMarkCounter;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordDetailPo;
import com.jd.bluedragon.distribution.collection.entity.CollectionRecordPo;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 集齐明细
 */
public class CollectionRecordDetailDao {

    private static final String NAMESPACE = CollectionRecordDetailDao.class.getName();

    private static final String TABLE_NAME_COLLECTION_RECORD_DETAIL = "COLLECTION_RECORD_DETAIL";

    private SqlSession sqlSession;

    private SequenceGenAdaptor sequenceGenAdaptor;

    public Integer batchInsertCollectionRecordDetail(List<CollectionRecordDetailPo> collectionRecordDetailPos) {
        for (CollectionRecordDetailPo collectionRecordDetailPo : collectionRecordDetailPos) {
            if(collectionRecordDetailPo.getCollectedMark() == null) {
                collectionRecordDetailPo.setCollectedMark("");
            }
            collectionRecordDetailPo.setId(sequenceGenAdaptor.newId(TABLE_NAME_COLLECTION_RECORD_DETAIL));
        }
        return this.sqlSession.insert(NAMESPACE.concat(".batchInsertCollectionRecordDetail"), collectionRecordDetailPos);
    }

    public List<CollectionRecordDetailPo> findCollectionRecordDetail(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findCollectionRecordDetail"), collectionRecordDetailPo);
    }

    public List<CollectionRecordDetailPo> findExistDetails(String collectionCode, List<String> scanCodes,
        String aggCode, CollectionAggCodeTypeEnum aggCodeType){

        Map<String,Object> param = new HashMap<>();
        param.put("collectionCode", collectionCode);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeType.name());
        param.put("scanCodes", scanCodes);
        return this.sqlSession.selectList(NAMESPACE.concat(".findExistDetails"), param);

    }


    public Integer updateCollectionRecordDetail(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.update(NAMESPACE.concat(".updateCollectionRecordDetail"), collectionRecordDetailPo);
    }

    public Integer updateDetailInfoByScanCodes(String collectionCode, List<String> scanCodes,
        String aggCode, CollectionAggCodeTypeEnum aggCodeType, CollectionStatusEnum statusEnum, String collectedMark) {

        Map<String, Object> param = new HashMap<>();
        param.put("collectionCode", collectionCode);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeType.name());
        param.put("scanCodes", scanCodes);
        param.put("collectedStatus", statusEnum.getStatus());
        param.put("collectedMark", collectedMark);
        return this.sqlSession.update(NAMESPACE.concat(".updateDetailInfoByScanCodes"), param);
    }

    public Timestamp getMaxTimeStampByCollectionCodesAndCollectedMark(List<String> collectionCodes,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String collectedMark) {

        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("collectedMark", collectedMark);
        return this.sqlSession.selectOne(NAMESPACE.concat(".getMaxTimeStampByCollectionCodesAndCollectedMark"), param);
    }

    public Timestamp getMaxTimeStampByCollectionCodesAndAggCode(List<String> collectionCodes,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode) {

        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("aggCode", aggCode);
        return this.sqlSession.selectOne(NAMESPACE.concat(".getMaxTimeStampByCollectionCodesAndAggCode"), param);
    }

    public List<CollectionRecordDetailPo> findAggCodeByScanCode(CollectionRecordDetailPo collectionRecordDetailPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findAggCodeByScanCode"), collectionRecordDetailPo);
    }

    public List<CollectionCollectedMarkCounter> sumAggCollectionByCollectionCode(List<String> collectionCodes, List<String> aggCodes,
        CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCodes", aggCodes);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        return this.sqlSession.selectList(NAMESPACE.concat(".sumAggCollectionByCollectionCode"), param);
    }

    public List<CollectionRecordDetailPo> queryCollectedDetailByCollectionAndAggCode(List<String> collectionCodes, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum, Integer limit, Integer offset){
        Map<String, Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("limit", limit);
        param.put("offset", offset);
        return this.sqlSession.selectList(NAMESPACE.concat(".queryCollectedDetailByCollectionAndAggCode"), param);
    }

    public List<CollectionCollectedMarkCounter> countCollectionByAggCodeAndCollectionCodes(List<String> collectionCodes, String aggCode, CollectionAggCodeTypeEnum aggCodeTypeEnum) {
        Map<String, Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCode", aggCode);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        return this.sqlSession.selectList(NAMESPACE.concat(".countCollectionByAggCodeAndCollectionCodes"), param);
    }

    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }

    public List<String> getAggCodesByCollectedMark(List<String> collectionCodes, String collectedMark, String aggCodeTypeEnumName) {
        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCodeType", aggCodeTypeEnumName);
        param.put("collectedMark", collectedMark);
        return this.sqlSession.selectOne(NAMESPACE.concat(".getAggCodesByCollectedMark"), param);
    }

    public List<CollectionRecordDetailPo> findByAggCode(List<String> collectionCodes, String collectedMark, CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, Integer isCollected, Integer isExtraCollected, Integer isMoreCollectedMark) {
        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("collectedMark", collectedMark);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("aggCode", aggCode);
        return this.sqlSession.selectList(NAMESPACE.concat(".findByAggCode"), param);
    }

    public List<CollectionRecordDetailPo> findAggCodeByCollectedMark(List<String> collectionCodes,
                                                               String collectedMark,
                                                               CollectionAggCodeTypeEnum aggCodeTypeEnum,
                                                               Integer limit,
                                                               Integer offset) {
        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("collectedMark", collectedMark);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("limit", limit);
        param.put("offset", offset);
        return this.sqlSession.selectList(NAMESPACE.concat(".findAggCodeByCollectedMark"), param);
    }
}
