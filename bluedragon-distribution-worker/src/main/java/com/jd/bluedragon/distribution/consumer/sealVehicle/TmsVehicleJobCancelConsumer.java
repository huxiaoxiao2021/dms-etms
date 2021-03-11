package com.jd.bluedragon.distribution.consumer.sealVehicle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jsf.tms.VosVehicleJobQueryWSManager;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleJobCancelMq;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dbs.util.CollectionUtils;
import com.jd.etms.vos.dto.SealVehicleJobDto;
import com.jd.etms.vos.dto.SealVehicleRouteDto;
import com.jd.jmq.common.message.Message;

@Service("tmsVehicleJobCancelConsumer")
public class TmsVehicleJobCancelConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(TmsVehicleJobCancelConsumer.class);
    /**
     * 取消标识
     */
    private static final Integer STATUS_CANCEL = 200;

    @Autowired
    @Qualifier("tmsVehicleRouteService")
    private TmsVehicleRouteService tmsVehicleRouteService;
    @Autowired
    @Qualifier("vosVehicleJobQueryWSManager")
    private VosVehicleJobQueryWSManager vosVehicleJobQueryWSManager;
    @Override
    public void consume(Message message) throws Exception {
        //处理消息体
        log.info("TmsVehicleJobCancelConsumer consume --> 消息Body为【{}】", message.getText());
        //反序列化
        TmsVehicleJobCancelMq mqData = JsonHelper.fromJson(message.getText(), TmsVehicleJobCancelMq.class);
        if (mqData == null) {
            log.warn("TmsVehicleJobCancelConsumer consume --> 消息转换对象失败：{}", message.getText());
            return;
        }
        List<TmsVehicleRoute> tmsVehicleRoutes = convertBean(mqData);
        if(CollectionUtils.isNotEmpty(tmsVehicleRoutes)) {
        	for(TmsVehicleRoute tmsVehicleRoute : tmsVehicleRoutes) {
        		tmsVehicleRouteService.syncToDb(tmsVehicleRoute,null,null);
        	}
        }
    }
    /**
     * 转换成List<TmsVehicleRoute>对象
     * @param mqData
     * @return
     */
	private List<TmsVehicleRoute> convertBean(TmsVehicleJobCancelMq mqData) {
		List<TmsVehicleRoute> tmsVehicleRoutes = Collections.EMPTY_LIST;
		//只处理取消状态的消息
		if(STATUS_CANCEL.equals(mqData.getStatus())) {
			//查询任务编码对应的线路明细
			JdResult<SealVehicleJobDto> jobData = vosVehicleJobQueryWSManager.getSealVehicleJobByVehicleJobCode(mqData.getVehicleJobCode());
			if(!jobData.isSucceed()
					|| jobData.getData() == null
					|| CollectionUtils.isEmpty(jobData.getData().getRouteList())) {
				log.warn("TmsVehicleJobCancelConsumer consume --> 任务编码[{}]线路信息为空！", mqData.getVehicleJobCode());
				return tmsVehicleRoutes;
			}
			tmsVehicleRoutes = new ArrayList<TmsVehicleRoute>();
			//组装取消线路信息
			for(SealVehicleRouteDto  routeData: jobData.getData().getRouteList()) {
				TmsVehicleRoute tmsVehicleRoute = new TmsVehicleRoute();
				tmsVehicleRoute.setVehicleJobCode(mqData.getVehicleJobCode());
				tmsVehicleRoute.setVehicleRouteCode(routeData.getVehicleRouteCode());
				tmsVehicleRoute.setCancelTime(mqData.getOperateTime());
				tmsVehicleRoute.setYn(Constants.YN_NO);
				tmsVehicleRoutes.add(tmsVehicleRoute);
			}
		}
		return tmsVehicleRoutes;
	}
}
