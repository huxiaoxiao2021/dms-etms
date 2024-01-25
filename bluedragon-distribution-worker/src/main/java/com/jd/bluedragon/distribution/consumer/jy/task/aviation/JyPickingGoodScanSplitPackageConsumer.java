package com.jd.bluedragon.distribution.consumer.jy.task.aviation;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.pickinggood.JyPickingGoodScanDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.picking.JyPickingSendRecordService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:36
 * @Description
 */
@Service("jyPickingGoodScanSplitPackageConsumer")
public class JyPickingGoodScanSplitPackageConsumer extends MessageBaseConsumer {

    private Logger log = LoggerFactory.getLogger(JyPickingGoodScanSplitPackageConsumer.class);
    private static final String DEFAULT_VALUE_1 = "1";

    @Autowired
    private JyPickingSendRecordService jyPickingSendRecordService;
    @Autowired
    private JimDbLock jimDbLock;
    @Override
    @JProfiler(jKey = "DMSWORKER.jy.JyPickingGoodScanSplitPackageConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            log.warn("JyPickingGoodScanSplitPackageConsumer consume --> 消息为空");
            return;
        }
        if (! JsonHelper.isJsonString(message.getText())) {
            log.warn("JyPickingGoodScanSplitPackageConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        JyPickingGoodScanDto mqBody = JsonHelper.fromJson(message.getText(), JyPickingGoodScanDto.class);
        if(Objects.isNull(mqBody)){
            log.error("JyPickingGoodScanSplitPackageConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }
        //无效数据过滤
        if(!invalidDataFilter(mqBody)) {
            log.error("提货扫描拆分为包裹维度数据异步：消息丢弃，msg={}", JsonHelper.toJson(mqBody));
            return;
        }
        if(log.isInfoEnabled()){
            log.info("提货扫描拆分为包裹维度数据异步开始，businessId={}, 内容{}", message.getBusinessId(), message.getText());
        }
        mqBody.setBusinessId(message.getBusinessId());
        this.deal(mqBody);
        if(log.isInfoEnabled()){
            log.info("提货扫描拆分为包裹维度数据异步消费结束，businessId={}", message.getBusinessId());
        }
    }

    private void deal(JyPickingGoodScanDto mqBody) {

        if(!this.lock(mqBody.getBizId(), mqBody.getPackageCode())) {
            log.error("提货扫描record明细消费获取锁失败，mqBody={}", JsonHelper.toJson(mqBody));
            throw new JyBizException(String.format("提货扫描record明细消费获取锁失败，businessId=%s", mqBody.getBusinessId()));
        }
        try{
            jyPickingSendRecordService.pickingRecordSave(mqBody);
        }catch (Exception ex) {
            log.error("提货扫描拆分为包裹维度数据异步消费异常，errMsg={}, mqBody={}", ex.getMessage(), JsonHelper.toJson(mqBody), ex);
            throw new JyBizException(String.format("提货扫描拆分为包裹维度数据异步消费异常,businessId：%s", mqBody.getBusinessId()));
        } finally {
            this.unlock(mqBody.getBizId(), mqBody.getPackageCode());
        }
    }


    public boolean lock(String bizId, String packageCode) {
        String lockKey = this.getLockKey(bizId, packageCode);
        return jimDbLock.lock(lockKey, DEFAULT_VALUE_1, 2, TimeUnit.MINUTES);
    }
    public void unlock(String bizId, String packageCode) {
        String lockKey = this.getLockKey(bizId, packageCode);
        jimDbLock.releaseLock(lockKey, DEFAULT_VALUE_1);
    }
    private String getLockKey(String bizId, String packageCode) {
        return String.format("lock:picking:scan:record:%s:%s", bizId, packageCode);
    }




    /**
     * 过滤无效数据  返回true 放行，false拦截
     * @param mqBody
     * @return
     */
    private boolean invalidDataFilter(JyPickingGoodScanDto mqBody) {
        return true;
    }

}
