package com.jd.bluedragon.distribution.consumer.storage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.exception.StorageException;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.send.domain.SendDetailMessage;
import com.jd.bluedragon.distribution.storage.domain.PutawayDTO;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageD;
import com.jd.bluedragon.distribution.storage.domain.StoragePackageM;
import com.jd.bluedragon.distribution.storage.domain.StorageSourceEnum;
import com.jd.bluedragon.distribution.storage.service.StoragePackageDService;
import com.jd.bluedragon.distribution.storage.service.StoragePackageMService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 快运暂存发货消费
 * <p>
 *   1、下架全程跟踪时间点比发货时间早3-5秒
 *   2、更新全部下架时间
 * </p>
 *
 * @author: hujiping
 * @date: 2020/12/9 15:16
 */
@Service("kyStorageSendConsumer")
public class KyStorageSendConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KyStorageSendConsumer.class);

    /**
     * 暂存下架缓存前缀
     * */
    public static final String STORAGE_DOWN_AWAY_LOCK = "STORAGE_DOWN_AWAY_LOCK_";

    /**
     * 推前时间：3s
     * */
    private static final Long PUSH_FORWARD_TIME = 3000L;

    @Autowired
    private StoragePackageMService storagePackageMService;

    @Autowired
    private StoragePackageDService storagePackageDService;

    @Autowired
    private WaybillCommonService waybillCommonService;

    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;

    @Override
    public void consume(Message message) throws Exception {
        logger.info("KyStorageSendConsumer consume --> 消息Body为【{}】", message.getText());
        SendDetailMessage sendDetail;
        try {
            //反序列化
            sendDetail = JsonHelper.fromJson(message.getText(), SendDetailMessage.class);
            if(sendDetail == null || sendDetail.getPackageBarcode() == null
                    || sendDetail.getCreateSiteCode() == null){
                logger.error("快运暂存发货消息参数错误,入参：{}",message.getText());
                return;
            }
        }catch (Exception e){
            logger.error("【{}】反序列化异常",message.getText(),e);
            return;
        }

        String packageCode = sendDetail.getPackageBarcode();
        String waybillCode = WaybillUtil.getWaybillCode(sendDetail.getPackageBarcode());
        Integer createSiteCode = sendDetail.getCreateSiteCode();
        // 缓存key
        String lockKey = STORAGE_DOWN_AWAY_LOCK + waybillCode + Constants.UNDERLINE_FILL + createSiteCode;
        try {
            StoragePackageM storagePackageM = storagePackageMService.getStoragePackageM(waybillCode);
            // 非暂存站点直接返回
            if(!isStorageSend(storagePackageM,createSiteCode)){
                logger.warn("非暂存站点的发货数据无需处理,入参：{}",message.getText());
                return;
            }
            Waybill waybill = waybillCommonService.findByWaybillCode(waybillCode);
            // 判断是否是企配仓订单，企配仓订单发送8410全流程跟踪
            boolean qpcWaybill = BusinessUtil.isEdn(waybill == null ? null : waybill.getSendPay(),
                    waybill == null ? null : waybill.getWaybillSign());
            if(waybillCommonService.isStorageWaybill(waybillCode)
                    || qpcWaybill){
                if(!isLock(lockKey)){
                    // 抛出异常让jmq重试
                    throw new StorageException();
                }
                // 更新运单暂存状态
                updateWaybillStatusOfKYZC(sendDetail, qpcWaybill);
                // 更新下架时间
                storagePackageMService.updateDownAwayTimeByWaybillCode(waybillCode);

                if(isAllPutAwayAll(storagePackageM) && isLastSendPackage(packageCode)){
                    if(logger.isInfoEnabled()){
                        logger.info("快运暂存/企配仓订单【{}】下最后一个包裹【{}】已发货",waybillCode,packageCode);
                    }
                    // 更新全部下架时间、状态
                    storagePackageMService.updateDownAwayCompleteTimeAndStatusByWaybillCode(waybillCode);
                }
                // 更新明细表包裹发货时间
                storagePackageDService.updateSendTimeByPackageCode(packageCode);
            }
        }catch (StorageException ex){
            throw new StorageException();
        }catch (Exception e){
            logger.error("快运暂存发货消费异常!", e);
        }finally {
            // 删除缓存锁
            deleteLock(lockKey);
        }
    }

    /**
     * 删除缓存锁
     * @param lockKey
     */
    private void deleteLock(String lockKey) {
        try {
            cacheService.del(lockKey);
        }catch (Exception e){
            logger.error("删除缓存锁异常!");
        }
    }

    /**
     * 判断是否有锁
     * @param lockKey
     * @return
     */
    private boolean isLock(String lockKey) {
        try {
            if(!cacheService.setNx(lockKey, StringUtils.EMPTY,Constants.TIME_SECONDS_ONE_MINUTE, TimeUnit.SECONDS)){
                Thread.sleep(100);
                return cacheService.setNx(lockKey, StringUtils.EMPTY,Constants.TIME_SECONDS_ONE_MINUTE, TimeUnit.SECONDS);
            }
        }catch (Exception e){
            logger.error("设置缓存异常!");
            throw new StorageException();
        }
        return true;
    }

    /**
     * 判断是否最后一个发货包裹
     * @param packageCode
     * @return
     */
    private boolean isLastSendPackage(String packageCode) {
        List<StoragePackageD> storagePackageDS
                = storagePackageDService.queryUnSendByWaybill(WaybillUtil.getWaybillCode(packageCode));
        if(!CollectionUtils.isEmpty(storagePackageDS) && storagePackageDS.size() == 1
                && storagePackageDS.get(0) != null && packageCode.equals(storagePackageDS.get(0).getPackageCode())){
            return true;
        }
        return false;
    }

    /**
     * 判断是否暂存站点发货
     * @param storagePackageM
     * @param createSiteCode
     * @return
     */
    private boolean isStorageSend(StoragePackageM storagePackageM, Integer createSiteCode) {
        if(createSiteCode == null){
            return false;
        }
        if(storagePackageM == null || storagePackageM.getCreateSiteCode() == null){
            return false;
        }
        return createSiteCode == storagePackageM.getCreateSiteCode().intValue();
    }

    /**
     * 是否全部上架
     *
     * @param storagePackageM
     * @return
     */
    private boolean isAllPutAwayAll(StoragePackageM storagePackageM) {
        return storagePackageM != null && storagePackageM.getPackageSum() != null
                && storagePackageM.getPackageSum().equals(storagePackageM.getPutawayPackageSum());
    }

    /**
     * 更新运单暂存状态
     * @param sendDetail
     * @param qpcWaybill
     */
    private void updateWaybillStatusOfKYZC(SendDetailMessage sendDetail, boolean qpcWaybill) {
        PutawayDTO putawayDTO = new PutawayDTO();
        putawayDTO.setOperateTime(sendDetail.getOperateTime() - PUSH_FORWARD_TIME);
        putawayDTO.setCreateSiteCode(sendDetail.getCreateSiteCode());
        putawayDTO.setBarCode(sendDetail.getPackageBarcode());
        putawayDTO.setOperatorErp(sendDetail.getCreateUser());
        if (qpcWaybill) {
            putawayDTO.setStorageSource(StorageSourceEnum.QPC_STORAGE.getCode());
        }
        storagePackageMService.updateWaybillStatusOfKYZC(putawayDTO,false);
    }
}
