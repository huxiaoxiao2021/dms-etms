package com.jd.bluedragon.core.message.consumer.pop;

import java.util.HashMap;
import java.util.Map;

import com.jd.jmq.common.message.Message;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalTransferConverter;
import com.jd.bluedragon.distribution.popAbnormal.service.PopAbnormalService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.XmlHelper;

@Service("popAbnormalConsumer")
public class PopAbnormalConsumer extends MessageBaseConsumer {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private PopAbnormalService popAbnormalService;

	public void consume(Message message) {

		String popAbnormalXml = message.getText();

		Boolean isCommit = Boolean.FALSE;
		PopAbnormal popAbnormal = null;
		PopAbnormal order = null;

		try {
			// 2 处理消息体
			this.logger
					.info("PopAbnormalMessageConsumer updatePopPackNum --> 消息Body为【"
							+ popAbnormalXml + "】");
			if (StringUtils.isBlank(popAbnormalXml)) {
				this.logger
						.info("PopAbnormalMessageConsumer updatePopPackNum --> 获取MQ数据内容为空，直接commit MQ");
				isCommit = Boolean.TRUE;
			} else {
				popAbnormal = XmlHelper.xmlToObject(popAbnormalXml,
						PopAbnormalTransferConverter.ALIAS_NAME,
						PopAbnormal.class, new PopAbnormalTransferConverter());
				if (popAbnormal == null
						|| StringUtils.isBlank(popAbnormal.getSerialNumber())
						|| StringUtils.isBlank(popAbnormal.getOrderCode())
						|| !BusinessHelper.checkIntNumRange(popAbnormal
								.getConfirmNum())) {
					if (popAbnormal != null) {
						this.logger
								.info("PopAbnormalMessageConsumer updatePopPackNum --> 商家确认运单号-包裹数量为【"
										+ popAbnormal.getOrderCode()
										+ "-"
										+ popAbnormal.getConfirmNum()
										+ "】，不合要求，直接commit MQ");
					} else {
						this.logger
								.info("PopAbnormalMessageConsumer updatePopPackNum --> popAbnormal为空，不合要求，直接commit MQ");
					}
					isCommit = Boolean.TRUE;
				}
			}

		} catch (Exception e) {
			this.logger.error(
					"PopAbnormalMessageConsumer updatePopPackNum --> 获取MQ数据内容转换对象，内容【"
							+ popAbnormalXml + "】，直接提交MQ， 异常：", e);
			isCommit = Boolean.TRUE;
		}

		if (!isCommit) {
			// 处理数据
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("serialNumber", popAbnormal.getSerialNumber());
			paramMap.put("orderCode", popAbnormal.getOrderCode());
			this.logger
					.info("PopAbnormalMessageConsumer updatePopPackNum --> 验证数据，参数："
							+ paramMap);
			order = this.popAbnormalService.checkByMap(paramMap);
			if (order == null) {
				this.logger
						.info("PopAbnormalMessageConsumer updatePopPackNum --> 验证本地数据不存在，直接commit MQ，参数："
								+ paramMap);
				isCommit = Boolean.TRUE;
			} else {
				// 组装需要提交的参数
				order.setConfirmNum(popAbnormal.getConfirmNum());
				order.setConfirmTime(popAbnormal.getConfirmTime());

				this.logger
						.info("PopAbnormalMessageConsumer updatePopPackNum --> 验证数据成功，更新数据，订单号【"
								+ order.getOrderCode()
								+ "】，数量【"
								+ order.getConfirmNum() + "】");

				int uptCount = this.popAbnormalService.updatePopPackNum(order);
				if (uptCount == 3) {
					// 3 提交消息
					isCommit = Boolean.TRUE;
				} else {
					this.logger.info("更新ID为“" + order.getId() + "”的数据失败,返回状态为:"
							+ uptCount);
				}
			}
		}

		this.logger.info("Id:" + message.getBusinessId() + ", 处理结果：" + isCommit);
	}
}
