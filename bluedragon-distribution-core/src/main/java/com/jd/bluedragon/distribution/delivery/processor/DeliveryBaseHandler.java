package com.jd.bluedragon.distribution.delivery.processor;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.configuration.ucc.UccPropertyConfiguration;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.distribution.api.response.DeliveryResponse;
import com.jd.bluedragon.distribution.delivery.entity.SendMWrapper;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.DeliveryService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.CollectionHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.Md5Helper;
import com.jd.coo.sa.mybatis.plugins.id.SequenceGenAdaptor;
import com.jd.jim.cli.Cluster;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wyh
 * @className DeliveryBaseHandler
 * @description
 * @date 2021/8/6 17:36
 **/
public abstract class DeliveryBaseHandler implements IDeliveryBaseHandler {

    protected final Logger log = LoggerFactory.getLogger(this.getClass());

    static int SEND_SPLIT_NUM = 30;

    static int EXPIRE_TIME_SECOND = 2 * 60 * 60;

    @Autowired
    @Qualifier("redisClientCache")
    protected Cluster redisClientCache;

    @Autowired
    protected SequenceGenAdaptor sequenceGenAdaptor;

    @Autowired
    protected UccPropertyConfiguration uccConfig;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected DeliveryService deliveryService;

    @Autowired
    protected WaybillPackageManager waybillPackageManager;

    /**
     * 生成该次发货操作的唯一标识
     *
     * @param wrapper
     * @return
     */
    @Override
    public String genBatchTaskUniqKey(SendMWrapper wrapper) {
        String sequence;
        try {
            sequence = String.valueOf(sequenceGenAdaptor.newId(wrapper.getKeyType().toString()));
        }
        catch (Throwable ex) {
            log.error("[发货包裹任务]生成唯一标识失败.", ex);

            try {
                sequence = String.valueOf(sequenceGenAdaptor.newId(wrapper.getKeyType().toString()));
            }
            catch (Throwable ex1) {
                log.error("[发货包裹任务]再次生成唯一标识失败.");
                throw new RuntimeException("[发货]服务器异常！请重试！如果仍失败，请咚咚联系[分拣小秘]！");
            }
        }

        return sequence + Constants.UNDER_LINE + wrapper.getSendM().getSendCode();
    }

    @Override
    public DeliveryResponse initDeliveryTask(SendMWrapper wrapper) {

        int onePageSize = uccConfig.getOldSendSplitPageSize() <= 0 ? SEND_SPLIT_NUM : uccConfig.getOldSendSplitPageSize();
        int totalNum = wrapper.getBarCodeList().size();
        int pageTotal = (totalNum % onePageSize) == 0 ? (totalNum / onePageSize) : (totalNum / onePageSize) + 1;

        SendM sendM = wrapper.getSendM();

        // 生成本次发货的唯一标识
        String batchUniqKey = wrapper.getBatchUniqKey();

        // 设置本次发货的批处理锁，值为本次任务的总页数
        lockPageDelivery(batchUniqKey, pageTotal);

        // 锁定本次发货的包裹/箱号
        batchLockBox(wrapper.getBarCodeList(), sendM.getCreateSiteCode());

        List<List<String>> pageBarCode = CollectionHelper.splitList(wrapper.getBarCodeList(), onePageSize);

        for (int i = 0; i < pageTotal; i++) {

            SendMWrapper copyWrapper = new SendMWrapper();
            copyWrapper.setSendM(sendM);
            copyWrapper.setBatchUniqKey(batchUniqKey);
            copyWrapper.setKeyType(wrapper.getKeyType());
            // 设置本次需要执行的包裹或箱号
            copyWrapper.setBarCodeList(pageBarCode.get(i));
            copyWrapper.setPageNo(i + 1);
            copyWrapper.setPageSize(onePageSize);
            copyWrapper.setTotalPage(pageTotal);

            Task task = new Task();

            task.setCreateSiteCode(sendM.getCreateSiteCode());
            task.setReceiveSiteCode(sendM.getReceiveSiteCode());

            task.setType(Task.TASK_TYPE_DELIVERY_ASYNC_V2);
            task.setTableName(Task.getTableName(task.getType()));
            task.setSequenceName(Task.getSequenceName(task.getTableName()));
            task.setKeyword1(wrapper.getKeyType().name());
            task.setKeyword2(i + 1 + Constants.UNDER_LINE + pageTotal + Constants.UNDER_LINE + sendM.getSendCode());
            task.setOwnSign(BusinessHelper.getOwnSign());
            task.setBody(JsonHelper.toJson(copyWrapper));

            String fingerprint = batchUniqKey +
                    Constants.UNDER_LINE + System.currentTimeMillis();
            task.setFingerprint(Md5Helper.encode(fingerprint));

            taskService.doAddTask(task,false);
        }

        return DeliveryResponse.oK();
    }

    /**
     * 锁定本次批处理任务
     * @param batchUniqKey
     * @param pageTotal
     * @return
     */
    @Override
    public boolean lockPageDelivery(String batchUniqKey, int pageTotal) {
        String redisKey = String.format(CacheKeyConstants.INITIAL_SEND_COUNT_KEY, batchUniqKey);
        try {
            redisClientCache.incrBy(redisKey,pageTotal);
            log.info("批次任务初始化 {}，计数成功：{}",batchUniqKey,pageTotal);
        } catch (Exception e) {
            log.error("lockPageDelivery初始化批次计数异常",e);
            return false;
        }
        return true;
    }

    /**
     * 批量锁定包裹/箱号发货数据
     * @param barCodeList
     * @param siteCode
     * @return
     */
    private boolean batchLockBox(List<String> barCodeList, Integer siteCode) {
        for (String barCode : barCodeList) {
            String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_LOCK_KEY, barCode, siteCode);
            redisClientCache.set(redisKey, "lock", EXPIRE_TIME_SECOND, TimeUnit.SECONDS, false);
        }

        return true;
    }

    /**
     * 处理发货逻辑
     *
     * @param wrapper
     * @return
     */
    @Override
    public boolean dealCoreDelivery(final SendMWrapper wrapper) {
        List<SendM> sendMList = Lists.newArrayListWithCapacity(wrapper.getBarCodeList().size());
        final SendM sendM = wrapper.getSendM();
        for (String barCode : wrapper.getBarCodeList()) {
            SendM domain = new SendM();
            BeanUtils.copyProperties(sendM, domain);
            domain.setBoxCode(barCode);
            sendMList.add(domain);
        }

        final int pageNo = wrapper.getPageNo();
        final int pageTotal = wrapper.getTotalPage();
        final String batchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(sendMList.get(0).getBizSource(), sendMList);

        // 判断是否推送全程跟踪任务
        SendM taskSendM = sendMList.get(0);
        return judgePushSendTracking(sendM, pageNo, pageTotal, batchUniqKey, taskSendM);
    }

    @Override
    public boolean dealCoreDeliveryV2(final SendMWrapper wrapper) {
        List<SendM> sendMList = Lists.newArrayListWithCapacity(wrapper.getBarCodeList().size());
        final SendM sendM = wrapper.getSendM();
        for (String barCode : wrapper.getBarCodeList()) {
            SendM domain = new SendM();
            BeanUtils.copyProperties(sendM, domain);
            domain.setBoxCode(barCode);
            sendMList.add(domain);
        }

        final String batchUniqKey = wrapper.getBatchUniqKey();

        deliveryService.deliveryCoreLogic(sendMList.get(0).getBizSource(), sendMList);

        return competeTaskIncrCount(batchUniqKey);
    }

    @Override
    public boolean competeTaskIncrCount(String batchUniqKey) {
        String compeletedCountKey = String.format(CacheKeyConstants.COMPELETE_SEND_COUNT_KEY, batchUniqKey);
        try {
            redisClientCache.incr(compeletedCountKey);
            log.info("发货批次 {} competeTaskIncrCount加一",batchUniqKey);
        } catch (Exception e) {
            log.error("任务完成计数异常",e);
        }
        return true;
    }

    private boolean judgePushSendTracking(SendM sendM, int pageNo, int pageTotal, String batchUniqKey, SendM taskSendM) {
        String redisKey = String.format(CacheKeyConstants.PACKAGE_SEND_BATCH_KEY, batchUniqKey);
        String redisVal = redisClientCache.get(redisKey);
        if (StringUtils.isEmpty(redisVal)) {
            log.warn("[包裹/箱号]获取批次任务缓存数据为空. key:{}", batchUniqKey);
            return true;
        }

        String countRedisKey = String.format(CacheKeyConstants.PACKAGE_SEND_COUNT_KEY, batchUniqKey);
        // 设置单页处理完成标志位
        redisClientCache.setBit(countRedisKey, pageNo, true);
        redisClientCache.expire(countRedisKey, EXPIRE_TIME_SECOND, TimeUnit.SECONDS);

        // 全部分页任务处理完成，生成发货任务
        if (Integer.parseInt(redisVal) == redisClientCache.bitCount(countRedisKey).intValue()) {

            // 删除批任务处理锁
            redisClientCache.del(redisKey);

            redisClientCache.del(countRedisKey);

            // 插入发货任务
            deliveryService.addTaskSend(taskSendM);

            if (log.isInfoEnabled()) {
                log.info("[包裹/箱号]当前批次任务全部处理完毕! total:{}, sendM:{}", pageTotal, JsonHelper.toJson(sendM));
            }
        }
        return false;
    }
}
