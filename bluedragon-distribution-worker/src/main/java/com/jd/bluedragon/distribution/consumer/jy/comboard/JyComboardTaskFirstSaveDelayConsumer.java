package com.jd.bluedragon.distribution.consumer.jy.comboard;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.lock.redis.JimDbLock;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.comboard.JyBizTaskComboardEntity;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.send.JyBizTaskComboardService;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.distribution.send.service.SendMService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.ObjectHelper;
import com.jd.jmq.common.message.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.jd.bluedragon.Constants.LOCK_EXPIRE;

/**
 * @author liwenji
 * 延时消息，删除2小时内没有操作组板的板号
 * @date 2023-09-13 15:57
 */
@Service("jyComboardTaskFirstSaveDelayConsumer")
@Slf4j
public class JyComboardTaskFirstSaveDelayConsumer extends MessageBaseConsumer {

    @Autowired
    private SendMService sendMService;

    @Autowired
    private JyBizTaskComboardService jyBizTaskComboardService;

    @Autowired
    private JimDbLock jimDbLock;
    
    @Override
    public void consume(Message message) throws Exception {
        if (ObjectHelper.isEmpty(message)){
            log.error("jyComboardTaskFirstSaveDelayConsumer 消息为空！");
            return;
        }

        JyBizTaskComboardEntity entity = JsonHelper.fromJson(message.getText(), JyBizTaskComboardEntity.class);
        if (entity == null) {
            log.error("jyComboardTaskFirstSaveDelayConsumer body体为空！");
            return;
        }
        
        if (StringUtils.isEmpty(entity.getSendCode()) || StringUtils.isEmpty(entity.getBoardCode())) {
            log.error("jyComboardTaskFirstSaveDelayConsumer 批次号或板号为空: {}", JsonHelper.toJson(entity));
            return;
        }

        //板加锁
        String boardLockKey = String.format(Constants.JY_COMBOARD_BOARD_LOCK_PREFIX, entity.getBoardCode());
        String uuid = UUID.randomUUID().toString();
        if (!jimDbLock.lock(boardLockKey, uuid, LOCK_EXPIRE, TimeUnit.SECONDS)) {
            throw new JyBizException("当前系统繁忙,请稍后再试！");
        }
        
        try {
            // 查询当前批次发货记录
            List<SendM> sendMList = sendMService
                    .selectBoxCodeBySiteAndSendCode(entity.getStartSiteId().intValue(), entity.getSendCode(), 1, 1);

            // 没有发货记录逻辑删除当前板号
            if (CollectionUtils.isEmpty(sendMList)) {
                // 查询板信息
                JyBizTaskComboardEntity task = jyBizTaskComboardService.queryBizTaskByBoardCode(entity.getStartSiteId().intValue(), entity.getBoardCode());
                if (task == null) {
                    log.info("未获取到板详情：{}", JsonHelper.toJson(task));
                    return;
                }
                
                JyBizTaskComboardEntity updateDetail = new JyBizTaskComboardEntity();
                updateDetail.setYn(Boolean.FALSE);
                updateDetail.setId(task.getId());
                if (jyBizTaskComboardService.updateBizTaskById(updateDetail) < 0) {
                    log.info("逻辑删除板号失败：{}", JsonHelper.toJson(updateDetail));
                }
            }
        }catch (Exception e) {
            log.error("延时删除板数据异常：{}", JsonHelper.toJson(entity),e);
        } finally {
            jimDbLock.releaseLock(boardLockKey, uuid);
        }
    }

}
