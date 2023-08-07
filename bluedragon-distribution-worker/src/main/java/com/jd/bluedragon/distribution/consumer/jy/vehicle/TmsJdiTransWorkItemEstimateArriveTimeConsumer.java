package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.jy.dto.task.TransWorkItemEstimateArriveTimeMqDTO;
import com.jd.bluedragon.distribution.jy.enums.JyBizTaskUnloadStatusEnum;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: ql-dms-distribution
 * @description: 运输封车任务预计到达时间 Consumer
 * @author: ext.tiyong1
 * @create: 2023/8/3 周四 15:20
 **/
@Service("tmsJdiTransWorkItemEstimateArriveTimeConsumer")
public class TmsJdiTransWorkItemEstimateArriveTimeConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(TmsJdiTransWorkItemEstimateArriveTimeConsumer.class);


    @Autowired
    JyBizTaskUnloadVehicleService jyBizTaskUnloadVehicleService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.tmsJdiTransWorkItemEstimateArriveTimeConsumer.consume", jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP, JProEnum.FunctionError})
    public void consume(Message message) throws Exception {

        if (!checkMessage(message)) return;

        TransWorkItemEstimateArriveTimeMqDTO transWorkItemEstimateArriveTimeMqDTO = JsonHelper.fromJson(message.getText(), TransWorkItemEstimateArriveTimeMqDTO.class);

        if (!paramCheck(transWorkItemEstimateArriveTimeMqDTO)) {
            logger.warn("TmsJdiTransWorkItemEstimateArriveTimeConsumer consume -->JSON转换失败或缺少必要参数，内容为【{}】", message.getText());
            return;
        }

        JyBizTaskUnloadVehicleEntity fromDatabase = jyBizTaskUnloadVehicleService.findByBizIdIgnoreYn(transWorkItemEstimateArriveTimeMqDTO.getSealCarCode());

        /**
         * 查询数据若没有记录则新增
         * 若该条数据失效（YN值为0）则不做操作
         * 若数据有效且任务状态（vehicleStatus）为初始或在途时，同时更新sortTime和estimateArriveTime，若为其他状态，只更新estimateArriveTime
         */
        if (fromDatabase != null && fromDatabase.getYn().equals(Constants.YN_NO)) {
            return;
        }

        JyBizTaskUnloadVehicleEntity forUpdate = new JyBizTaskUnloadVehicleEntity();
        forUpdate.setBizId(transWorkItemEstimateArriveTimeMqDTO.getSealCarCode());
        forUpdate.setPredictionArriveTime(transWorkItemEstimateArriveTimeMqDTO.getEstimateArriveTime());


        if (fromDatabase == null || JyBizTaskUnloadStatusEnum.INIT.getCode().equals(fromDatabase.getVehicleStatus()) || JyBizTaskUnloadStatusEnum.ON_WAY.getCode().equals(fromDatabase.getVehicleStatus())) {
            forUpdate.setSortTime(transWorkItemEstimateArriveTimeMqDTO.getEstimateArriveTime());
        }

        boolean success = jyBizTaskUnloadVehicleService.saveOrUpdateOfBusinessInfo(forUpdate);

        if (!success) {
            logger.warn("TmsJdiTransWorkItemEstimateArriveTimeConsumer consume -->消费失败：jyBizTaskUnloadVehicleService saveOrUpdateOfBusinessInfo 更新数据失败，消息内容为【{}】", message.getText());
            throw new JyBizException("消费处理tms_jdi_trans_work_item_estimate_arrive_time失败");
        } else {
            logger.info("TmsJdiTransWorkItemEstimateArriveTimeConsumer consume -->消费成功，消息内容为【{}】", message.getText());
        }
    }

    private boolean paramCheck(TransWorkItemEstimateArriveTimeMqDTO transWorkItemEstimateArriveTimeMqDTO) {
        return transWorkItemEstimateArriveTimeMqDTO != null && !StringUtils.isBlank(transWorkItemEstimateArriveTimeMqDTO.getSealCarCode()) && transWorkItemEstimateArriveTimeMqDTO.getEstimateArriveTime() != null;
    }
}
