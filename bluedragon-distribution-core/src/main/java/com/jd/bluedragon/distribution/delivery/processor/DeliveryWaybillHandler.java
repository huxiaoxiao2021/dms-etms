package com.jd.bluedragon.distribution.delivery.processor;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wyh
 * @className DeliveryWaybillHandler
 * @description
 * @date 2021/8/6 17:40
 **/
@Service("deliveryWaybillHandler")
public class DeliveryWaybillHandler extends DeliveryBaseHandler {

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Override
    public DeliveryResponse initDeliveryTask(SendMWrapper wrapper) {

        for (String waybillCode : wrapper.getBarCodeList()) {

            // 获取运单包裹数
            Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
            if (waybill == null || waybill.getGoodNumber() == null) {
                log.error("[发货任务]获取运单包裹数失败! code:{}, sendM:{}", waybillCode, JsonHelper.toJson(wrapper.getSendM()));
                continue;
            }

            int totalNum = waybill.getGoodNumber();
            int onePageSize = dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize() == 0 ? SEND_SPLIT_NUM : dmsConfigManager.getPropertyConfig().getWaybillSplitPageSize();
            int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;

            // 生成本次发货的唯一标识
            String batchUniqKey = wrapper.getBatchUniqKey();

            // 设置本次发货的批处理锁
            lockPageDelivery(batchUniqKey, pageTotal);

            // 插入分页任务
            for (int i = 0; i < pageTotal; i++) {

                SendM sendM = wrapper.getSendM();

                SendMWrapper copyWrapper = new SendMWrapper();
                copyWrapper.setSendM(wrapper.getSendM());
                copyWrapper.setWaybillCode(waybillCode);
                copyWrapper.setKeyType(wrapper.getKeyType());
                copyWrapper.setBatchUniqKey(batchUniqKey);
                copyWrapper.setPageNo(i + 1);
                copyWrapper.setPageSize(onePageSize);
                copyWrapper.setTotalPage(pageTotal);

                Task task = new Task();

                task.setCreateSiteCode(sendM.getCreateSiteCode());
                task.setReceiveSiteCode(sendM.getReceiveSiteCode());

                task.setSequenceName(Task.getSequenceName(task.getTableName()));
                task.setType(Task.TASK_TYPE_DELIVERY_ASYNC_V2);
                task.setTableName(Task.getTableName(task.getType()));
                task.setKeyword1(wrapper.getKeyType().name());
                task.setKeyword2(i + 1 + Constants.UNDER_LINE + pageTotal + Constants.UNDER_LINE + waybillCode);
                task.setOwnSign(BusinessHelper.getOwnSign());
                task.setBody(JsonHelper.toJson(copyWrapper));

                String fingerprint = batchUniqKey +
                        Constants.UNDER_LINE + System.currentTimeMillis();
                task.setFingerprint(Md5Helper.encode(fingerprint));

                taskService.doAddTask(task,false);
            }
        }

        return DeliveryResponse.oK();
    }
    /**
     * 处理发货逻辑
     *
     * @param wrapper
     * @return
     */
    @Override
    public boolean dealCoreDelivery(SendMWrapper wrapper) {
        int pageSize = wrapper.getPageSize();
        final int pageNo = wrapper.getPageNo();

        // 分页获取运单包裹数据
        final String waybillCode = wrapper.getWaybillCode();
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.warn("[发货]运单拆分任务分页获取包裹数量为空! waybillCode={}", waybillCode);
            return false;
        }

        List<SendM> waybillSendMList = Lists.newArrayListWithCapacity(pageSize);
        final SendM waybillSendM = wrapper.getSendM();
        for (DeliveryPackageD deliveryPackageD : baseEntity.getData()) {
            SendM domain = new SendM();
            BeanUtils.copyProperties(waybillSendM, domain);
            domain.setBoxCode(deliveryPackageD.getPackageBarcode());
            waybillSendMList.add(domain);
        }

        final String waybillBatchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(waybillSendMList.get(0).getBizSource(), waybillSendMList);

        // 判断是否推送全程跟踪任务
        SendM taskSendM = waybillSendMList.get(0);
        return judgePushSendTracking(pageNo, waybillCode, waybillSendM, waybillBatchUniqKey, taskSendM);
    }

    @Override
    public boolean dealCoreDeliveryV2(SendMWrapper wrapper) {
        int pageSize = wrapper.getPageSize();
        final int pageNo = wrapper.getPageNo();

        // 分页获取运单包裹数据
        final String waybillCode = wrapper.getWaybillCode();
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCodeOfPage(waybillCode, pageNo, pageSize);
        if (baseEntity == null || CollectionUtils.isEmpty(baseEntity.getData())) {
            log.warn("[发货]运单拆分任务分页获取包裹数量为空! waybillCode={}", waybillCode);
            return false;
        }

        List<SendM> waybillSendMList = Lists.newArrayListWithCapacity(pageSize);
        final SendM waybillSendM = wrapper.getSendM();
        for (DeliveryPackageD deliveryPackageD : baseEntity.getData()) {
            SendM domain = new SendM();
            BeanUtils.copyProperties(waybillSendM, domain);
            domain.setBoxCode(deliveryPackageD.getPackageBarcode());
            waybillSendMList.add(domain);
        }

        final String waybillBatchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(waybillSendMList.get(0).getBizSource(), waybillSendMList);

        return competeTaskIncrCount(waybillBatchUniqKey);
    }

    private boolean judgePushSendTracking(int pageNo, String waybillCode, SendM waybillSendM, String waybillBatchUniqKey, SendM taskSendM) {
        String redisKey = String.format(CacheKeyConstants.WAYBILL_SEND_BATCH_KEY, waybillBatchUniqKey);
        String redisVal = redisClientCache.get(redisKey);
        if (StringUtils.isEmpty(redisVal)) {
            log.warn("[运单]获取批次任务缓存数据为空. key:{}", waybillBatchUniqKey);
            return true;
        }

        String countRedisKey = String.format(CacheKeyConstants.WAYBILL_SEND_COUNT_KEY, waybillBatchUniqKey);
        // 设置单页处理完成标志位
        redisClientCache.setBit(countRedisKey, pageNo, true);
        redisClientCache.expire(countRedisKey, EXPIRE_TIME_SECOND, TimeUnit.SECONDS);

        // 全部分页任务处理完成，生成发货任务
        if (Integer.parseInt(redisVal) == redisClientCache.bitCount(countRedisKey).intValue()) {

            redisClientCache.del(redisKey);
            redisClientCache.del(countRedisKey);

            // 发货任务
            deliveryService.addTaskSend(taskSendM);

            if (log.isInfoEnabled()) {
                log.info("[运单]当前批次任务全部处理完毕! waybillCode:{}, sendM:{}", waybillCode, JsonHelper.toJson(waybillSendM));
            }
        }
        return false;
    }
}
