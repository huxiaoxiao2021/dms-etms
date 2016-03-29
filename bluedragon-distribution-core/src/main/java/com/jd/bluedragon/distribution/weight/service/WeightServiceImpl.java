package com.jd.bluedragon.distribution.weight.service;

import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.client.WeightClient;
import com.jd.bluedragon.distribution.task.domain.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

@Service("weightService")
public class WeightServiceImpl implements WeightService {

	private final Log logger = LogFactory.getLog(this.getClass());

	public boolean doWeightTrack(Task task) {
		this.logger.info("向运单系统回传包裹称重信息: ");
		WeightResponse response = null;
		try {
			String body = task.getBody();
			if (!StringUtils.isNotBlank(body)) {
				return false;
			}
			response = WeightClient.weightTrack(body.substring(1, body.length() - 1));
			if (WeightResponse.WEIGHT_TRACK_OK.equals(response.getCode())) {
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
