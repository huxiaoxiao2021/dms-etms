package com.jd.bluedragon.distribution.weight.service;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.client.WeightClient;
import com.jd.bluedragon.distribution.task.domain.Task;

@Service("weightService")
public class WeightServiceImpl implements WeightService {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Profiled(tag = "WeightServiceImpl.doWeightTrack")
	public boolean doWeightTrack(Task task) {
		this.logger.info("向运单系统回传包裹称重信息: ");
		WeightResponse response = null;
		try {
			String body = task.getBody();
			if (!StringUtils.isNotBlank(body)) {
				return false;
			}
			response = WeightClient.weightTrack(body.substring(1, body.length() - 1));
			if (WeightResponse.WEIGHT_TRACK_OK == response.getCode()) {
				this.logger.info("向运单系统回传包裹称重信息成功");
				return true;
			} else {
				this.logger.error("向运单系统回传包裹称重信息失败 : " + response.getMessage());
				return false;
			}
		} catch (Exception e) {
			this.logger.error("处理称重回传任务发生异常，异常信息为：", e);
		}
		return false;
	}

}
