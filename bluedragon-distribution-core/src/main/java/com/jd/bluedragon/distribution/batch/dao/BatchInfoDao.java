package com.jd.bluedragon.distribution.batch.dao;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BatchInfoDao extends BaseDao<BatchInfo> {

    public static final String namespace = BatchInfoDao.class.getName();

    private static final Logger log = LoggerFactory.getLogger(BatchInfoDao.namespace);

    public BatchInfo findBatchInfoByCode(String code) {
        return (BatchInfo) super.getSqlSession().selectOne(BatchInfoDao.namespace + ".findBatchInfoByCode", code);
    }

    public BatchInfo findBatchInfoByBatchInfoCode(BatchInfo batchInfo) {
        return (BatchInfo) super.getSqlSession().selectOne(BatchInfoDao.namespace + ".findBatchInfoByBatchInfoCode", batchInfo);
    }

    @SuppressWarnings("unchecked")
    public List<BatchInfo> findBatchInfoesBySite(BatchInfo batchInfo) {
        return super.getSqlSession().selectList(BatchInfoDao.namespace + ".findBatchInfoesBySite", batchInfo);
    }

    @SuppressWarnings("unchecked")
    public List<BatchInfo> findBatchInfoes(BatchInfo batchInfo) {
        return super.getSqlSession().selectList(BatchInfoDao.namespace + ".findBatchInfoes", batchInfo);
    }

    @SuppressWarnings("unchecked")
    public Integer batchUpdateStatus(BatchInfo batchInfo) {
        return super.getSqlSession().update(BatchInfoDao.namespace + ".batchUpdateStatus", batchInfo);
    }

    @SuppressWarnings("unchecked")
    public List<BatchInfo> findBatchInfo(BatchInfo batchInfo) {
        return super.getSqlSession().selectList(BatchInfoDao.namespace + ".findBatchInfo", batchInfo);
    }
    @SuppressWarnings("unchecked")
    public  List<BatchInfo> findMaxCreateTimeBatchInfo(BatchInfo batchInfo){
        return super.getSqlSession().selectList(BatchInfoDao.namespace+".findMaxCreateTimeBatchInfo"
                ,batchInfo.getCreateSiteCode());
    }


    @SuppressWarnings("unchecked")
    public List<BatchInfo> findAllBatchInfo(BatchInfo batchInfo) {
        return super.getSqlSession().selectList(BatchInfoDao.namespace + ".findAllBatchInfo", batchInfo);
    }

    @SuppressWarnings("unchecked")
    public Integer updateBatchInfoTime(BatchInfo batchInfo){
        return update(BatchInfoDao.namespace,batchInfo);
    }

    /**
     * 获取当前波次
     * @param sortingCenterId   分拣中心ID
     * @param operateTime       当前操作时间
     * @return
     */
    public BatchInfo findCurrent(Integer sortingCenterId,Date operateTime){
        Map<String,Object> map=new HashMap<String,Object>(3);
        map.put("createSiteCode",sortingCenterId);
        map.put("operateTime",operateTime);
        map.put("minTime",new Date(0));
        log.info("查寻当前波次->查寻参数值为：createSitecode&operateTime");
        return (BatchInfo)super.getSqlSession().selectOne(BatchInfoDao.namespace+".findCurrent",map);
    }

}
