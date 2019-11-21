package com.jd.bluedragon.distribution.consumer.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalDetail;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopAbnormalReceiveVO;
import com.jd.bluedragon.distribution.popAbnormal.domain.PopReceiveAbnormal;
import com.jd.bluedragon.distribution.popAbnormal.service.PopReceiveAbnormalService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("popAbnormalNewConsumer")
public class PopAbnormalNewConsumer extends MessageBaseConsumer {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private PopReceiveAbnormalService popReceiveAbnormalService;

    @JProfiler(jKey = "popAbnormalNewMessageConsumer.popMqFromPop", mState = {JProEnum.TP})
	public void consume(Message message) {
		String popAbnormalReceiveJson = message.getText();
		Integer resultCode = Constants.YN_YES;
		
		log.debug("popAbnormalReceiveJson:{}" , popAbnormalReceiveJson);

		PopAbnormalReceiveVO popAbnormalReceiveVO = null;
		try {
			popAbnormalReceiveVO = JsonHelper.fromJson(popAbnormalReceiveJson,
					PopAbnormalReceiveVO.class);
			if (popAbnormalReceiveVO.getSerialNumber() == null
					|| popAbnormalReceiveVO.getSerialNumber() <= 0
					|| popAbnormalReceiveVO.getOrderCode() == null
					|| popAbnormalReceiveVO.getMainType() == null
					|| popAbnormalReceiveVO.getMainType() <= 0
					|| StringUtils.isBlank(popAbnormalReceiveVO
							.getOperateTime())) {
				log.warn("popAbnormalReceiveVO -- 参数有误！Id [{}]， message [{}]",message.getBusinessId(),popAbnormalReceiveJson);
				resultCode = Constants.YN_NO;
			}

			if (popAbnormalReceiveVO.getMainType().equals(2)
					|| popAbnormalReceiveVO.getMainType().equals(3)
					|| popAbnormalReceiveVO.getMainType().equals(4)) {
				if (popAbnormalReceiveVO.getSubType() == null) {
					resultCode = Constants.YN_NO;
				} else if (popAbnormalReceiveVO.getSubType().equals(402)
						&& BusinessHelper.checkIntNumNotInRange(Integer
								.valueOf(popAbnormalReceiveVO.getAttr1()))) {
					resultCode = Constants.YN_NO;
				}
			} else if (popAbnormalReceiveVO.getMainType().equals(5)) {
				if (BusinessHelper.checkIntNumNotInRange(Integer
						.valueOf(popAbnormalReceiveVO.getAttr1()))) {
					resultCode = Constants.YN_NO;
				}
			}

		} catch (Exception e) {
			log.error("popAbnormalNewMessageConsumer.popMqFromPop 类型转换或字段异常：{}",popAbnormalReceiveJson, e);
			resultCode = Constants.YN_NO;
		}

		if (Constants.YN_YES.equals(resultCode)) {
			PopReceiveAbnormal popReceiveAbnormal = new PopReceiveAbnormal();
			popReceiveAbnormal.setAbnormalId(popAbnormalReceiveVO
					.getSerialNumber());
			popReceiveAbnormal.setWaybillCode(String
					.valueOf(popAbnormalReceiveVO.getOrderCode()));
			popReceiveAbnormal.setMainType(popAbnormalReceiveVO.getMainType());
			popReceiveAbnormal.setSubType(popAbnormalReceiveVO.getSubType());
			popReceiveAbnormal.setAttr3(popAbnormalReceiveVO.getAttr1());
			if (PopAbnormalReceiveVO.NOT_END.equals(popAbnormalReceiveVO
					.getIsEnd())) {
				popReceiveAbnormal
						.setAbnormalStatus(PopReceiveAbnormal.IS_BACK);
			} else {
				popReceiveAbnormal
						.setAbnormalStatus(PopReceiveAbnormal.IS_FINISH);
			}
			popReceiveAbnormal.setUpdateTime(DateHelper.parseDate(
					popAbnormalReceiveVO.getOperateTime(),
					Constants.DATE_TIME_FORMAT));

			PopAbnormalDetail popAbnormalDetail = new PopAbnormalDetail();
			popAbnormalDetail.setAbnormalId(popAbnormalReceiveVO
					.getSerialNumber());
			popAbnormalDetail.setOperatorName("商家");
			popAbnormalDetail.setOperatorTime(DateHelper.parseDate(
					popAbnormalReceiveVO.getOperateTime(),
					Constants.DATE_TIME_FORMAT));
			if (popAbnormalReceiveVO.getMainType().equals(5)
					|| (popAbnormalReceiveVO.getMainType().equals(4) && popAbnormalReceiveVO
							.getSubType().equals(402))) {
				
				if(popAbnormalReceiveVO.getComment()!=null && popAbnormalReceiveVO.getComment().startsWith("处理方式")
						&& popAbnormalReceiveVO.getSubType()!=null && popAbnormalReceiveVO.getSubType().equals(402))
					popAbnormalDetail.setRemark("实际确认数量："
							+ popAbnormalReceiveVO.getAttr1()+";\n"+popAbnormalReceiveVO.getComment());
				else
					popAbnormalDetail.setRemark("实际确认数量："
							+ popAbnormalReceiveVO.getAttr1());
					
			} else {
				popAbnormalDetail.setRemark(popAbnormalReceiveVO.getComment());
			}

			resultCode = this.popReceiveAbnormalService.update(
					popReceiveAbnormal, popAbnormalDetail, Boolean.FALSE);
		}

		log.info("Id [{}] , 处理结果：{}" ,message.getBusinessId(), resultCode);
	}
	
	public static void main(String args[]){
		PopAbnormalNewConsumer consumer = new PopAbnormalNewConsumer();
		Message message = new Message();
		message.setText("{\"serialNumber\":79026,\"orderCode\":8339149425,\"mainType\":5,\"subType\":null,\"comment\":\"处理方式:\",\"operateTime\":\"2015-02-06 18:02:29\",\"attr1\":3,\"isEnd\":1}");
		consumer.consume(message);
	}
}
