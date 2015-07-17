package com.jd.bluedragon.distribution.batch.dao;

import java.util.List;

import com.jd.bluedragon.common.dao.BaseDao;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;


public class BatchSendDao extends BaseDao<BatchSend> {

    public static final String namespace = BatchSendDao.class.getName();

    /**
     * 读取波次发货批次信息
     * @param batchCode     波次号
     * @param siteCode      收货站点
     * @return
     */
    public BatchSend read(String batchCode,Integer siteCode){
        BatchSend send=new BatchSend();
        send.setReceiveSiteCode(siteCode);
        send.setBatchCode(batchCode);
        return (BatchSend) super.getSqlSession().selectOne(BatchSendDao.namespace + ".getBatchSend",send);
    }

    /**
     * 插入单波次发货信息
     * @param send 波次发货批次信息
     * @return
     */
    public int insert(BatchSend send){
        return super.getSqlSession().insert(BatchSendDao.namespace + ".insertOne",send);
    }
	
    /**
     * 发货读取波次发货批次信息
     * @param batchSend
     * @return
     */
	@SuppressWarnings("unchecked")
    public List<BatchSend> findBatchSend(BatchSendRequest request) {
        return super.getSqlSession().selectList(BatchSendDao.namespace + ".findBatchSend", request);
    }
	
	/**
     * 发货更新状态
     * @param batchSend
     * @return
     */
    public Integer batchUpdateStatus(BatchSend batchSend) {
        return super.getSqlSession().update(BatchSendDao.namespace + ".batchUpdateStatus", batchSend);
    }

    /**
     * 更新发车状态
     * @param batchSend 批次对象
     * @return
     */
    public Integer updateSendCarState(BatchSend batchSend){
        return super.getSqlSession().update(BatchSendDao.namespace+".updateSendCarState",batchSend);
    }

    /**
     *
     * @param sendCode
     * @return
     */
    public BatchSend readBySendCode(String sendCode){
        return (BatchSend)super.getSqlSession().selectOne(BatchSendDao.namespace+".readBySendCode",sendCode);
    }
}
