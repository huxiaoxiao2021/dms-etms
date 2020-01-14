package com.jd.bluedragon.distribution.weight.service;

import com.google.gson.reflect.TypeToken;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.api.response.WeightResponse;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.systemLog.domain.Goddess;
import com.jd.bluedragon.distribution.systemLog.service.GoddessService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.weight.domain.OpeEntity;
import com.jd.bluedragon.distribution.weight.domain.OpeObject;
import com.jd.bluedragon.distribution.weight.domain.OpeSendObject;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.jmq.common.exception.JMQException;
import com.jd.ql.dms.common.cache.CacheKeyGenerator;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.type.JavaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("weightService")
public class WeightServiceImpl implements WeightService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    /**
     * 存放待上传重量的json数据
     */
    private static final String KEY_JSON_FOR_UPLOAD_WEIGHT = "weightJsonData";
    /**
     * 存保存的数据对象
     */
    private static final String KEY_LIST_WEIGHT_OBJECT_FOR_SAVE = "weightList";
    @Autowired
    @Qualifier("dmsWeightSendMQ")
    private DefaultJMQProducer dmsWeightSendMQ;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Resource(name = "goddessService")
    private GoddessService goddessService;
    
	@Autowired
	@Qualifier("cacheKeyGenerator")
	private CacheKeyGenerator cacheKeyGenerator;
	
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService jimdbCacheService;

    private static final Type WAYBILL_WEIGHT=new TypeToken<List<OpeEntity>>(){}.getType();

    public boolean doWeightTrack(Task task) {
        String taskBody = null;
        Goddess goddess=builderGoddess(task.getBody());
        try {
             taskBody = task.getBody();
            if (!StringUtils.isNotBlank(taskBody)) {
                log.warn("向运单回传包裹称重信息失败，称重信息为空");
                return false;
            }
            JdResult<Map<String,Object>> weightInfo = dealMinusVolume(taskBody);
            String weightJsonData = null;
            List<WeightOperFlow> weightFlowList = null;
            if(weightInfo.isSucceed()){
            	weightJsonData = (String)weightInfo.getData().get(KEY_JSON_FOR_UPLOAD_WEIGHT);
            	//无效的称重数据，不需要重跑worker，直接返回true
                if(weightJsonData == null){
                	log.warn("doWeightTrack-warn:无效的称重数据，消息体：{}", taskBody);
                	return true;
                }
                weightFlowList = (List<WeightOperFlow>)weightInfo.getData().get(KEY_LIST_WEIGHT_OBJECT_FOR_SAVE);
                try {
					batchSaveDmsWeight(weightFlowList);
				} catch (Exception e) {
					log.error("doWeightTrack-fail:保存运单称重记录到redis失败:{}消息体：{}",weightInfo.getMessage(),taskBody,e);
				}
            }else{
            	log.warn("doWeightTrack-fail:{}消息体：{}",weightInfo.getMessage(),taskBody);
            	return false;
            }
            Map<String, Object> map = waybillPackageManager.uploadOpe(weightJsonData.substring(1, weightJsonData.length() - 1));
            goddess.setHead(MessageFormat.format("提交称重至运单:结果{0}", JsonHelper.toJson(map)));
            this.sendMQ(weightJsonData);

            if (map != null && map.containsKey("code") && WeightResponse.WEIGHT_TRACK_OK == Integer.parseInt(map.get("code").toString())) {
                if(log.isInfoEnabled()){
                    this.log.info("向运单系统回传包裹称重信息：{}", taskBody);
                    this.log.info("向运单系统回传包裹称重信息成功：{}" , JsonHelper.toJson(map));
                }
                return true;
            } else {
                this.log.info("向运单系统回传包裹称重信息：{}", taskBody);
                this.log.warn("向运单系统回传包裹称重信息失败： {}", JsonHelper.toJson(map));
                return false;
            }
        } catch (Exception e) {
            this.log.info("向运单系统回传包裹称重信息：{}" , taskBody);
            this.log.error("处理称重回传任务发生异常，异常信息为：", e);
            goddess.setHead(MessageFormat.format("提交称重至运单:异常为{0}",e.getMessage()));
        }finally {
            goddessService.save(goddess);
        }
        return false;
    }

    /**
     * 发送包裹称重、体积信息MQ
     *
     * @param body 数据内容
     * @throws JMQException
     */
    private void sendMQ(String body) {
        try {
            List<OpeObject> opeDetails = JsonHelper.jsonToArray(body.substring(1, body.length() - 1), OpeEntity.class).getOpeDetails();
            if (opeDetails != null && opeDetails.size() > 0) {
                for (OpeObject ope : opeDetails) {
                    OpeSendObject opeSend = new OpeSendObject();
                    opeSend.setPackage_code(ope.getPackageCode());
                    opeSend.setDms_site_id(ope.getOpeSiteId());
                    opeSend.setThisUpdateTime(this.getDateLong(ope.getOpeTime()));
                    opeSend.setWeight(ope.getpWeight());
                    opeSend.setLength(ope.getpLength());
                    opeSend.setWidth(ope.getpWidth());
                    opeSend.setHigh(ope.getpHigh());
                    opeSend.setOpeUserId(ope.getOpeUserId());
                    opeSend.setOpeUserName(ope.getOpeUserName());
                    if (ope.getpHigh() != null && ope.getpLength() != null && ope.getpWidth() != null) {
                        //计算体积
                        opeSend.setVolume(ope.getpHigh() * ope.getpLength() * ope.getpWidth());
                    } else {
                        opeSend.setVolume(null);
                    }
                    this.dmsWeightSendMQ.send(ope.getPackageCode(), JsonHelper.toJson(opeSend));
                }
            }
        } catch (Exception e) {
            Profiler.businessAlarm("DmsWorker.send_weight_mq_error",(new Date()).getTime(),"向分拣中心监控报表系统发送称重MQ消息失败，异常信息为：" + e.getMessage());
                this.log.error("向分拣中心监控报表系统发送称重MQ消息失败，异常信息为：", e);
        }
    }
    /**
     * 处理称重数据
     * 1、重量值<=0则记为无效，上传的重量值设置为空
     * 2、长宽高有一个值<=0则记为无效，上传的长宽高值设置为空
     * 3、重量或体积有一个有效值，则上传称重
     * @param body 原始上传数据内容
     * @return 组装待上传json数据和称重数据
     */
    private JdResult<Map<String,Object>> dealMinusVolume(String body){
    	JdResult<Map<String,Object>> result = new JdResult<Map<String,Object>>();
        try{
        	Map<String,Object> mapData = new HashMap<String,Object>();
        	result.setData(mapData);
        	result.toSuccess();
            JavaType javaType = JsonHelper.getCollectionType(ArrayList.class, OpeEntity.class);
            List<OpeEntity> opeEntities = JsonHelper.getMapper().readValue(body, javaType);
            OpeEntity opeEntity = opeEntities.get(0);

            List<OpeObject> opeDetails = opeEntity.getOpeDetails();
            if (opeDetails != null && opeDetails.size() > 0) {
            	List<OpeObject> newOpeDetails = new ArrayList<OpeObject>();
            	List<WeightOperFlow> weightFlowList = new ArrayList<WeightOperFlow>();
                for (OpeObject ope : opeDetails) {
                	boolean weightIsEffect = false;
                	boolean volumeIsEffect = false;
                	if(NumberHelper.gt0(ope.getpWeight())){
                		weightIsEffect = true;
                	}else{
                		//重量值<=0则记为无效，对象设置为null
                		ope.setpWeight(null);
                		log.warn("doWeightTrack-weight:称重上传重量值无效，设置为null，packageCode={}", ope.getPackageCode());
                	}
                	if(NumberHelper.gt0(ope.getpLength())
                		&&NumberHelper.gt0(ope.getpWidth())
                		&&NumberHelper.gt0(ope.getpHigh())){
                		volumeIsEffect = true;
                	}else{
                		//长宽高有一个<=0则记为无效，对象设置为null
                		ope.setpLength(null);
                		ope.setpWidth(null);
                		ope.setpHigh(null);
                		log.warn("doWeightTrack-volume:称重上传体积无效，设置为null，packageCode={}", ope.getPackageCode());
                	}
                	//重量或体积有一个有效,加入到newOpeDetails中
                	if(weightIsEffect || volumeIsEffect){
                		newOpeDetails.add(ope);
                		WeightOperFlow dmsWeightFlow = new WeightOperFlow();
                		dmsWeightFlow.setBarcode(ope.getPackageCode());
                		if(weightIsEffect){
                			dmsWeightFlow.setWeight(ope.getpWeight().doubleValue());
                		}
                		if(volumeIsEffect){
                			dmsWeightFlow.setLength(ope.getpLength().doubleValue());
                			dmsWeightFlow.setWidth(ope.getpWidth().doubleValue());
                			dmsWeightFlow.setHigh(ope.getpHigh().doubleValue());
                			dmsWeightFlow.setVolume(
                					dmsWeightFlow.getLength()
                					*dmsWeightFlow.getWidth()
                					*dmsWeightFlow.getHigh());
                		}
                		weightFlowList.add(dmsWeightFlow);
                	}
                }
                //存在有效数据则设置称重数据
                if(newOpeDetails.size()>0){
                	opeEntity.setOpeDetails(newOpeDetails);
                	mapData.put(KEY_JSON_FOR_UPLOAD_WEIGHT, JsonHelper.toJson(opeEntities));
                	mapData.put(KEY_LIST_WEIGHT_OBJECT_FOR_SAVE, weightFlowList);
                }
            }
        }catch (Exception e){
            this.log.error("处理称重数据异常", e);
            result.toError("处理称重数据异常"+e.getMessage());
        }
        return result;
    }
    /**
     * 根据字符串类型日期转换为时间戳
     * @param dateStr
     * @return
     */
    private Long getDateLong(String dateStr) {
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = format.parse(dateStr);
                return date.getTime();
            } catch (ParseException e) {
                this.log.error("无效的日期格式：{}" , dateStr, e);
                return null;
            }
        }
        return null;
    }

    private Goddess builderGoddess(String body){
        Goddess domain = new Goddess();
        try {
            List<OpeEntity> waybillWeights = JsonHelper.fromJsonUseGson(body, WAYBILL_WEIGHT);
            if (null != waybillWeights) {
                for (OpeEntity entity : waybillWeights) {
                    domain.setKey(entity.getWaybillCode());
                    domain.setBody(body);
                }
            }
        }catch (Throwable throwable){
            log.error("称重JSON解析失败",throwable);
        }
        return domain;
    }
	/**
	 * 保存分拣称重信息，存入redis
	 * @param weightOperFlow
	 * @return
	 */
    public boolean saveDmsWeight(WeightOperFlow weightOperFlow){
		String[] hashKey = BusinessHelper.getHashKeysByPackageCode(weightOperFlow.getBarcode());
		if(hashKey != null){
			String key = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_DMS_WEIGHT_INFO, hashKey[0]);
			String keyField = hashKey[1];
			boolean rest = jimdbCacheService.hSetEx(key, keyField,weightOperFlow,Constants.TIME_SECONDS_ONE_MONTH);
			return rest;
		}
		return false;
	}
	public WeightOperFlow getDmsWeightByPackageCode(String packageCode) {
		String[] hashKey = BusinessHelper.getHashKeysByPackageCode(packageCode);
		if(hashKey != null){
			String key = this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_DMS_WEIGHT_INFO, hashKey[0]);
			String keyField = hashKey[1];
			return this.jimdbCacheService.hGet(key, keyField, WeightOperFlow.class);
		}
		return null;
	}
	public Map<String,WeightOperFlow> getDmsWeightsByWaybillCode(String waybillCode) {
		int pageIndex = 1;
		String hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
		if(hashKey != null){
			Map<String,WeightOperFlow> result = new HashMap<String,WeightOperFlow>();
			Map<String,WeightOperFlow> values = this.jimdbCacheService.hGetAll(
					this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_DMS_WEIGHT_INFO, hashKey),
					WeightOperFlow.class
					);
			//循环所有分页获取数据
			while(values!=null && !values.isEmpty()){
					result.putAll(values);
				++ pageIndex;
				hashKey = BusinessHelper.getHashKey(waybillCode,pageIndex);
				values = this.jimdbCacheService.hGetAll(
						this.cacheKeyGenerator.getCacheKey(CacheKeyConstants.CACHE_KEY_DMS_WEIGHT_INFO, hashKey),
						WeightOperFlow.class
						);
			}
			return result;
		}
		return null;
	}

	@Override
	public boolean batchSaveDmsWeight(List<WeightOperFlow> weightFlowList) {
		if(weightFlowList!=null){
			for(WeightOperFlow weightOperFlow:weightFlowList){
				this.saveDmsWeight(weightOperFlow);
			}
		}
		return true;
	}
}
