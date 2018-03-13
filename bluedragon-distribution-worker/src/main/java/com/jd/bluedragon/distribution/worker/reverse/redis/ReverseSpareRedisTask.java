package com.jd.bluedragon.distribution.worker.reverse.redis;

import java.util.ArrayList;
import java.util.List;

import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.message.MessageDestinationConstant;
import com.jd.bluedragon.distribution.qualityControl.domain.QualityControl;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.etms.waybill.api.WaybillTraceApi;
import com.jd.etms.waybill.dto.BdTraceDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.jd.bluedragon.distribution.api.request.ReverseSpareDto;
import com.jd.bluedragon.distribution.api.request.ReverseSpareRequest;
import com.jd.bluedragon.distribution.framework.RedisSingleScheduler;
import com.jd.bluedragon.distribution.reverse.domain.ReverseSpare;
import com.jd.bluedragon.distribution.reverse.service.ReverseSpareService;
import com.jd.bluedragon.distribution.sorting.domain.Sorting;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import org.springframework.beans.factory.annotation.Qualifier;

/**
 * @author zhaohc
 * @E-mail zhaohengchong@360buy.com
 * @createTime 2012-11-28 上午10:28:50
 * 
 *             逆向备件库按商品退货分拣处理
 */
public class ReverseSpareRedisTask extends RedisSingleScheduler {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private ReverseSpareService reverseSpareService;

	@Override
	public boolean executeSingleTask(Task task, String ownSign) throws Exception {
        return reverseSpareService.doReverseSpareTask(task);
	}

}
