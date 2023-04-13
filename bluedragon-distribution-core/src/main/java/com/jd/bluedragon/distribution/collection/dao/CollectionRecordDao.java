package com.jd.bluedragon.distribution.collection.dao;

import com.jd.bluedragon.distribution.collection.entity.*;
import com.jd.bluedragon.distribution.collection.enums.CollectionAggCodeTypeEnum;
import com.jd.bluedragon.distribution.collection.enums.CollectionStatusEnum;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import org.apache.ibatis.session.SqlSession;

import java.sql.Timestamp;
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

    private static final String NAMESPACE = CollectionRecordDao.class.getName();

    private static final String TABLE_NAME_COLLECTION_RECORD = "COLLECTION_RECORD";

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

    public List<CollectionRecordPo> findCollectionRecord(CollectionRecordPo collectionRecordPo) {
        return this.sqlSession.selectList(NAMESPACE.concat(".findCollectionRecord"), collectionRecordPo);
    }

    public Integer updateCollectionRecord(CollectionRecordPo collectionRecordPo) {
        return this.sqlSession.update(NAMESPACE.concat(".updateCollectionRecord"), collectionRecordPo);
    }

    public Integer countAggCodeByCollectionCodesAndStatus(List<String> collectionCodes,
        List<String> aggCodeList, String aggCodeTypeEnumName, Integer isCollected, Integer isMoreCollectedMark) {

        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("aggCodeType", aggCodeTypeEnumName);
        param.put("isCollected", isCollected);
        param.put("isMoreCollectedMark", isMoreCollectedMark);
        param.put("aggCodeList", aggCodeList);
        return this.sqlSession.selectOne(NAMESPACE.concat(".countAggCodeByCollectionCodesAndStatus"), param);
    }

    public List<CollectionRecordPo> findAggCodeByCollectedMark(List<String> collectionCodes, String collectedMark,
        CollectionAggCodeTypeEnum aggCodeTypeEnum, String aggCode, Integer isCollected, Integer isExtraCollected,
        Integer isMoreCollectedMark, Integer limit, Integer offset) {
        Map<String,Object> param = new HashMap<>();
        param.put("collectionCodes", collectionCodes);
        param.put("collectedMark", collectedMark);
        param.put("aggCodeType", aggCodeTypeEnum.name());
        param.put("aggCode", aggCode);
        param.put("isCollected", isCollected);
        param.put("isExtraCollected", isExtraCollected);
        param.put("isMoreCollectedMark", isMoreCollectedMark);
        param.put("limit", limit);
        param.put("offset", offset);
        return this.sqlSession.selectList(NAMESPACE.concat(".findAggCodeByCollectedMark"), param);
    }


    public void setSqlSession(SqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public void setSequenceGenAdaptor(SequenceGenAdaptor sequenceGenAdaptor) {
        this.sequenceGenAdaptor = sequenceGenAdaptor;
    }
}
