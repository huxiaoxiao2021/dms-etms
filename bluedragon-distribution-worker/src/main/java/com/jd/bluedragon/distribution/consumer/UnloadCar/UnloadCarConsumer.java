package com.jd.bluedragon.distribution.consumer.UnloadCar;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.loadAndUnload.TmsSealCar;
import com.jd.bluedragon.distribution.loadAndUnload.UnloadCar;
import com.jd.bluedragon.distribution.loadAndUnload.domain.DistributeTaskRequest;
import com.jd.bluedragon.distribution.loadAndUnload.service.UnloadCarService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lijie
 * @Description 卸车任务消费逻辑
 * @date 2020/6/25 21:30
 */
@Service("unloadCarConsumer")
public class UnloadCarConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UnloadCarService unloadCarService;

    //操作状态：10-封车，20-解封，30-出围栏，40-进围栏
    private static final Integer SEAL_CAR_STATUS = 10;

    private static final Integer UNSEAL_CAR_STATUS = 20;

    @Override
    @JProfiler(jKey = "DmsWork.UnloadCarConsumer.consume",jAppName = Constants.UMP_APP_NAME_DMSWORKER,mState = {JProEnum.TP,JProEnum.Heartbeat})
    public void consume(Message message) throws Exception {
        if (!JsonHelper.isJsonString(message.getText())) {
            log.warn("封车解封车状态变化下发消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }
        try {
            TmsSealCar tmsSealCar = JsonHelper.fromJsonUseGson(message.getText(), TmsSealCar.class);
            if (StringUtils.isEmpty(tmsSealCar.getSealCarCode())){
                log.warn("封车解封车状态变化下发消息体缺少封车编码，内容为【{}】", message.getText());
                return;
            }
            // 消费解封车状态，自动分配卸车任务
            if (UNSEAL_CAR_STATUS.equals(tmsSealCar.getStatus())) {
                distributeUnloadCarTask(tmsSealCar);
                return;
            }

            if (StringUtils.isEmpty(tmsSealCar.getOperateSiteCode()) || CollectionUtils.isEmpty(tmsSealCar.getBatchCodes())){
                log.warn("封车解封车状态变化下发消息体缺少必要字段，内容为【{}】", message.getText());
                return;
            }

            if (!SEAL_CAR_STATUS.equals(tmsSealCar.getStatus())) {
                log.warn("封车解封车状态变化下发消息体为非封车，不需要消费，内容为【{}】", message.getText());
                return;
            }

            unloadCarService.insertUnloadCar(tmsSealCar);
        } catch (Exception e) {
            log.error("[卸车任务]消费封车解封车状态变化MQ时发生异常,内容为【{}】", message.getText(), e);
            throw new RuntimeException(e);
        }
    }

    private void distributeUnloadCarTask(TmsSealCar tmsSealCar) {
        // 根据封车编码查询卸车任务
        UnloadCar unloadCar = unloadCarService.selectBySealCarCode(tmsSealCar.getSealCarCode());
        if (unloadCar == null) {
            log.warn("消费解封车消息时，根据封车编码没有找到对应的卸车任务，tmsSealCar={}", JsonHelper.toJson(tmsSealCar));
            return;
        }
        // 卸车任务ID列表
        List<Integer> unloadCarIds = new ArrayList<>();
        unloadCarIds.add(unloadCar.getUnloadCarId().intValue());
        // 封车编码列表
        List<String> sealCarCodes = new ArrayList<>();
        sealCarCodes.add(tmsSealCar.getSealCarCode());

        // 组装分配卸车任务参数
        DistributeTaskRequest request = new DistributeTaskRequest();
        request.setUnloadCarIds(unloadCarIds);
        request.setUnloadUserErp(tmsSealCar.getOperateUserCode());
        request.setUnloadUserName(tmsSealCar.getOperateUserName());
        request.setRailWayPlatForm(null);
        request.setUpdateUserErp(tmsSealCar.getOperateUserCode());
        request.setUpdateUserName(tmsSealCar.getOperateUserName());
        request.setSealCarCodes(sealCarCodes);
        unloadCarService.distributeTask(request);
    }
}
