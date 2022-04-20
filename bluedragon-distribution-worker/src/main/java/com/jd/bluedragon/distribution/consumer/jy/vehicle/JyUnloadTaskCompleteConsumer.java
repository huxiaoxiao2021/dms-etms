package com.jd.bluedragon.distribution.consumer.jy.vehicle;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.api.response.base.Result;
import com.jd.bluedragon.distribution.jy.dto.unload.UnloadTaskCompleteDto;
import com.jd.bluedragon.distribution.jy.exception.JyBizException;
import com.jd.bluedragon.distribution.jy.group.JyTaskGroupMemberEntity;
import com.jd.bluedragon.distribution.jy.service.group.JyTaskGroupMemberService;
import com.jd.bluedragon.distribution.jy.service.task.JyBizTaskUnloadVehicleService;
import com.jd.bluedragon.distribution.jy.task.JyBizTaskUnloadVehicleEntity;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.proxy.Profiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @ClassName JyUnloadTaskCompleteConsumer
 * @Description
 * @Author wyh
 * @Date 2022/4/9 19:49
 **/
@Service("jyUnloadTaskCompleteConsumer")
public class JyUnloadTaskCompleteConsumer extends MessageBaseConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JyUnloadTaskCompleteConsumer.class);

    @Autowired
    private JyBizTaskUnloadVehicleService taskUnloadVehicleService;

    @Autowired
    @Qualifier("jyTaskGroupMemberService")
    private JyTaskGroupMemberService taskGroupMemberService;

    @Override
    @JProfiler(jKey = "DMS.WORKER.jyUnloadTaskCompleteConsumer.consume",
            jAppName = Constants.UMP_APP_NAME_DMSWORKER, mState = {JProEnum.TP,JProEnum.FunctionError})
    public void consume(Message message) throws Exception {
        if (StringHelper.isEmpty(message.getText())) {
            logger.warn("jyUnloadTaskCompleteConsumer consume --> 消息为空");
            return;
        }
        if (!JsonHelper.isJsonString(message.getText())) {
            logger.warn("jyUnloadTaskCompleteConsumer consume -->消息体非JSON格式，内容为【{}】", message.getText());
            return;
        }

        UnloadTaskCompleteDto completeDto = JsonHelper.fromJson(message.getText(), UnloadTaskCompleteDto.class);
        if (completeDto == null) {
            logger.error("jyUnloadTaskCompleteConsumer consume -->JSON转换后为空，内容为【{}】", message.getText());
            return;
        }

        boolean saveData = saveUnloadCompleteData(message, completeDto);

        if (saveData) {

            finishUnloadTaskGroup(completeDto);
        }
    }

    private void finishUnloadTaskGroup(UnloadTaskCompleteDto completeDto) {
        JyTaskGroupMemberEntity endData = new JyTaskGroupMemberEntity();
        endData.setRefTaskId(completeDto.getTaskId());
        endData.setUpdateUser(completeDto.getOperateUserErp());
        endData.setUpdateUserName(completeDto.getOperateUserName());
        endData.setUpdateTime(completeDto.getOperateTime());
        Result<Boolean> result = taskGroupMemberService.endTask(endData);

        if (logger.isInfoEnabled()) {
            logger.info("卸车完成关闭小组任务. data:{}, result:{}", JsonHelper.toJson(completeDto), JsonHelper.toJson(result));
        }
    }

    private boolean saveUnloadCompleteData(Message message, UnloadTaskCompleteDto completeDto) {
        boolean saveData;
        try {
            JyBizTaskUnloadVehicleEntity updateData = assembleCompleteData(completeDto);
            saveData = taskUnloadVehicleService.saveOrUpdateOfBusinessInfo(updateData);
        }
        catch (JyBizException bizException) {
            logger.warn("保存卸车任务完成数据发生业务异常，将重试! {}", message.getText(), bizException);
            throw bizException;
        }
        catch (Exception e) {
            Profiler.businessAlarm("dms.web.jyUnloadTaskCompleteConsumer.consume", "拣运保存卸车任务完成数据");
            logger.error("保存卸车任务完成数据失败. {}", message.getText(), e);
            throw new RuntimeException("保存卸车任务完成数据失败");
        }

        return saveData;
    }

    private JyBizTaskUnloadVehicleEntity assembleCompleteData(UnloadTaskCompleteDto completeDto) {
        JyBizTaskUnloadVehicleEntity updateData = new JyBizTaskUnloadVehicleEntity();
        updateData.setBizId(completeDto.getBizId());
        updateData.setUnloadFinishTime(completeDto.getOperateTime());
        updateData.setAbnormalFlag(completeDto.getAbnormalFlag().intValue());
        updateData.setUpdateUserErp(completeDto.getOperateUserErp());
        updateData.setUpdateUserName(completeDto.getOperateUserName());
        updateData.setUpdateTime(completeDto.getOperateTime());
        updateData.setMoreCount(completeDto.getMoreScanCount());
        updateData.setLessCount(completeDto.getToScanCount());
        return updateData;
    }
}
