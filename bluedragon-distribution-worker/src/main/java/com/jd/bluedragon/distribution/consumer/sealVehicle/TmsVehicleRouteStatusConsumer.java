package com.jd.bluedragon.distribution.consumer.sealVehicle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.message.base.MessageBaseConsumer;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRoute;
import com.jd.bluedragon.distribution.newseal.entity.TmsVehicleRouteMq;
import com.jd.bluedragon.distribution.newseal.service.TmsVehicleRouteService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jmq.common.message.Message;
import com.jd.ql.dms.common.cache.CacheService;

@Service("tmsVehicleRouteStatusConsumer")
public class TmsVehicleRouteStatusConsumer extends MessageBaseConsumer {

    private final Logger log = LoggerFactory.getLogger(TmsVehicleRouteStatusConsumer.class);
    /**
     * 取消标识
     */
    private static final Integer OPERATE_CANCEL = 20;
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    @Autowired
    @Qualifier("tmsVehicleRouteService")
    private TmsVehicleRouteService tmsVehicleRouteService;

    @Override
    public void consume(Message message) throws Exception {
        //处理消息体
        log.info("TmsVehicleRouteStatusConsumer consume --> 消息Body为【{}】", message.getText());
        //反序列化
        TmsVehicleRouteMq mqData = JsonHelper.fromJson(message.getText(), TmsVehicleRouteMq.class);
        if (mqData == null) {
            log.warn("TmsVehicleRouteStatusConsumer consume --> 消息转换对象失败：{}", message.getText());
            return;
        }
        TmsVehicleRoute tmsVehicleRoute = convertBean(mqData);
        if(tmsVehicleRoute == null) {
            log.warn("TmsVehicleRouteStatusConsumer consume --> 无效消息：{}", message.getText());
            return;
        }
        tmsVehicleRouteService.syncToDb(tmsVehicleRoute);
    }
    /**
     * 转换成TmsVehicleRoute对象，并扩展其他相应的字段信息
     * @param mqData
     * @return
     */
	private TmsVehicleRoute convertBean(TmsVehicleRouteMq mqData) {
		TmsVehicleRoute tmsVehicleRoute = new TmsVehicleRoute();
		tmsVehicleRoute.setTransportCode(mqData.getTransportCode());
		tmsVehicleRoute.setVehicleJobCode(mqData.getVehicleJobCode());
		tmsVehicleRoute.setVehicleJobCode(mqData.getVehicleJobCode());
		tmsVehicleRoute.setCreateTime(mqData.getCreateTime());
		tmsVehicleRoute.setCancelTime(mqData.getCancelTime());
		tmsVehicleRoute.setCarrierTeamCode(mqData.getCarrierTeamCode());
		tmsVehicleRoute.setCarrierTeamName(mqData.getCarrierTeamName());
		tmsVehicleRoute.setVehicleNumber(mqData.getVehicleNumber());
		tmsVehicleRoute.setJobCreateTime(mqData.getJobCreateTime());
		//取消状态
		if(OPERATE_CANCEL.equals(mqData.getOperateType())) {
			tmsVehicleRoute.setYn(Constants.YN_NO);
		}else {
			tmsVehicleRoute.setYn(Constants.YN_YES);
			//根据任务查询创建时间
			
			//根据运力编码查询始发和目的信息，计算发车时间
		}
		return tmsVehicleRoute;
	}
}
