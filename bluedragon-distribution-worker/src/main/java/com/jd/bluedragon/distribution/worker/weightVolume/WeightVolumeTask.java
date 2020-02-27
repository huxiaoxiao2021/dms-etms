package com.jd.bluedragon.distribution.worker.weightVolume;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.framework.DBSingleScheduler;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weightVolume.domain.WeightVolumeEntity;
import com.jd.bluedragon.distribution.weightVolume.service.DMSWeightVolumeService;
import com.jd.bluedragon.utils.JsonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class WeightVolumeTask extends DBSingleScheduler {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DMSWeightVolumeService weightVolumeService;

	@Override
	protected boolean executeSingleTask(Task task, String ownSign) throws Exception {
		try {
			this.log.info("task id is {}" , task.getId());
			WeightVolumeEntity weightVolumeEntity = JsonHelper.fromJson(task.getBody(),WeightVolumeEntity.class);
			if (null == weightVolumeEntity) {
				this.log.warn("称重量方消息反序列化失败{}" , task.getBody());
				return Boolean.FALSE;
			}
			InvokeResult<Boolean> invokeResult = weightVolumeService.dealWeightAndVolume(weightVolumeEntity);
			if (invokeResult != null &&
					InvokeResult.RESULT_SUCCESS_CODE == invokeResult.getCode() && Boolean.TRUE.equals(invokeResult.getData())) {
				return Boolean.TRUE;
			} else {
				log.warn("称重量方任务处理失败，处理单号为：{}，处理结果：{}", weightVolumeEntity.getBarCode(), JsonHelper.toJson(invokeResult));
				return Boolean.FALSE;
			}
		} catch (Exception e) {
			this.log.error("处理称重回传任务发生异常：{}" ,task.getId(), e);
			return Boolean.FALSE;
		}
	}

}
