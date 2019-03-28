package com.jd.bluedragon.distribution.batch.service;

import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.batch.dao.BatchSendDao;
import com.jd.bluedragon.distribution.batch.domain.BatchInfo;
import com.jd.bluedragon.distribution.batch.domain.BatchSend;
import com.jd.bluedragon.distribution.batch.domain.BatchSendRequest;
import com.jd.bluedragon.distribution.batch.domain.BatchSendResponse;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.framework.utils.cache.annotation.Cache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by wangtingwei on 2014/10/20.
 */
@Service("BatchSendService")
public class BatchSendServiceImpl implements BatchSendService {

    private static final Log logger= LogFactory.getLog(BatchSendServiceImpl.class);

    @Autowired
    private RedisManager redisManager;

    @Autowired
    @Qualifier("batchSendDao")
    private BatchSendDao batchSendDao;

    @Autowired
    private BatchInfoService batchInfoService;
    /**
     * 生成波次对应的发货批次号
     * @param batchInfo 波次信息
     * @param siteCode  收货站点
     * @return
     */
    @Override
    public boolean generate(BatchInfo batchInfo, Integer siteCode) {
        BatchSend send=new BatchSend();
        send.setBatchCode(batchInfo.getBatchCode());
        send.setReceiveSiteCode(siteCode);
        send.setCreateSiteCode(batchInfo.getCreateSiteCode());
        send.setCreateUser(batchInfo.getCreateUser());
        send.setCreateUserCode(batchInfo.getCreateUserCode());
        Date current=new Date(System.currentTimeMillis());
        logger.info("时间格式为"+DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS);
        send.setSendCode(String.valueOf(batchInfo.getCreateSiteCode())+"-"+String.valueOf(siteCode)+"-"+ DateHelper.formatDate(current, DateHelper.DATE_FORMAT_YYYYMMDDHHmmssSSS));
        send.setYn(1);
        send.setSendStatus(0);
        boolean result=false;
        try{
			result = batchSendDao.insert(send) > 0;
		} catch (Exception ex) {
			logger.error("INSERTBATCHSEND", ex);
			result = false;
		}
		return result;
    }

    @Override
    public Integer add(BatchSend batchSend) {
        int result=0;
        try{
            result=batchSendDao.insert(batchSend);
        } catch (Exception ex){
            logger.error("INSERTLABLEPRINTBATCHSEND",ex);
        }
        return result;
    }

    /**
     * 获取当前发货批次
     * @param sortingCenterId   分拣中心ID
     * @param operateTime       分拣时间
     * @param siteCode          目的站点编号
     * @return
     */
    @Override
    public BatchSend readBySortinCenterIdAndOperateTimeAndSiteCode(Integer sortingCenterId, Date operateTime, Integer siteCode) {
        BatchInfo batchInfo=this.batchInfoService.findCurrentBatchInfo(sortingCenterId,operateTime);
        if(null==batchInfo){
            return null;
        }
        BatchSend batchSend=this.readFromCache(batchInfo.getBatchCode(),siteCode);
        return batchSend;
    }

    /**
     * 获取当前发货批次，如果不存在则即时生成并返回
     * @param sortingCenterId    分拣中心ID
     * @param operateTime        分拣时间
     * @param siteCode           目的站点编号
     * @return
     */
    @Override
    public BatchSend readAndGenerateIfNotExist(Integer sortingCenterId, Date operateTime, Integer siteCode) {
        BatchInfo batchInfo=this.batchInfoService.findCurrentBatchInfo(sortingCenterId,operateTime);
        if(null==batchInfo){
            logger.info("获取肖前波次号为空"+String.valueOf(sortingCenterId)+"--"+DateHelper.formatDateTime(operateTime));
            return null;
        }
        BatchSend batchSend=this.readFromCache(batchInfo.getBatchCode(),siteCode);
        if(null==batchSend){
            synchronized (BatchSendServiceImpl.class){
                batchSend=this.readFromCache(batchInfo.getBatchCode(),siteCode);
                if(null==batchSend){
                    this.generate(batchInfo,siteCode);
                    batchSend=this.readFromCache(batchInfo.getBatchCode(),siteCode);
                }
            }
        }

        return batchSend;
    }

    /**
     * 读波次发货批次信息
     * @param batchCode     波次号
     * @param siteCode      收货站点
     * @return
     */
    @Override
    public BatchSend read(String batchCode, Integer siteCode) {
        BatchSend send=this.batchSendDao.read(batchCode,siteCode);
        if(null==send){
            logger.info("读取波次发货批次信息为NULL"+batchCode+"----"+String.valueOf(siteCode));
        }
        return send;
    }

    /**
     * 【带缓存】读波次发货批次信息
     * @param batchCode     波次号
     * @param siteCode      收货站点
     * @return
     */
    @Override
    @Cache(key = "BatchSend.readSingle@args0-@args1", memoryEnable = true, memoryExpiredTime = 5 * 60 * 1000,
            redisEnable = true, redisExpiredTime = 10 * 60 * 1000)
    public BatchSend readFromCache(String batchCode, Integer siteCode) {
        return read(batchCode,siteCode);
    }


    /**
     * 发车
     * @param sendCode 发货批次号
     * @return
     */
    @Override
    public boolean sendCar(String sendCode,Date operateTime) {
        return alterSendCarState(sendCode,operateTime,1);
    }
    private boolean alterSendCarState(String sendCode,Date operateTime,Integer state){
        BatchSend last=readBySendCode(sendCode);
        if(null!=last&&(!state.equals(last.getSendCarState()))&&(null==last.getSendCarOperateTime()||operateTime.compareTo(last.getSendCarOperateTime())>0)){
            BatchSend send=new BatchSend();
            send.setSendCode(sendCode);
            send.setSendCarState(state);
            send.setSendCarOperateTime(operateTime);
            if(logger.isInfoEnabled()) {
                logger.info("发车批次状态修改参数" + JsonHelper.toJson(send));
            }
            batchSendDao.updateSendCarState(send);
            redisManager.del("BatchSend.readBySendCode"+sendCode);
        }
        return true;
    }
    /**
     * 取消发车
     * @param sendCode 发货批次号
     * @return
     */
    @Override
    public boolean cancelSendCar(String sendCode,Date operateTime) {
        return alterSendCarState(sendCode,operateTime,2);
    }

    /**
     * 读取发车状态
     * @param sendCode 发货批次号
     * @return 【true:已发车；false:已取消发车】
     */
    @Override
    @Cache(key = "BatchSend.readBySendCode@args0", memoryEnable = false,redisEnable = true, redisExpiredTime = 60 * 60 * 1000)
    public BatchSend readBySendCode(String sendCode) {
        return batchSendDao.readBySendCode(sendCode);
    }

    @Override
	public BatchSendResponse findBatchSend(BatchSendRequest request) {
		// TODO Auto-generated method stub
		List<BatchSend> lists = batchSendDao.findBatchSend(request);
		BatchSendResponse response = new BatchSendResponse();
		response.setCode(JdResponse.CODE_OK);
		response.setData(lists);
		response.setMessage(JdResponse.MESSAGE_OK);
		return response;
	}

	@Override
	public Integer batchUpdateStatus(BatchSend batchSend) {
		// TODO Auto-generated method stub
		return batchSendDao.batchUpdateStatus(batchSend);
	}
}
