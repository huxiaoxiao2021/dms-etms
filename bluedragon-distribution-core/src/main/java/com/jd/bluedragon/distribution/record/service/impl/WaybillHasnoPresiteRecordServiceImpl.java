package com.jd.bluedragon.distribution.record.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.api.utils.JsonHelper;
import com.jd.bluedragon.distribution.record.dao.WaybillHasnoPresiteRecordDao;
import com.jd.bluedragon.distribution.record.dto.WaybillHasnoPresiteRecordQo;
import com.jd.bluedragon.distribution.record.entity.BdBlockerInitMq;
import com.jd.bluedragon.distribution.record.entity.DmsHasnoPresiteWaybillMq;
import com.jd.bluedragon.distribution.record.enums.DmsHasnoPresiteWaybillMqOperateEnum;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordCallStatusEnum;
import com.jd.bluedragon.distribution.record.enums.WaybillHasnoPresiteRecordStatusEnum;
import com.jd.bluedragon.distribution.record.model.WaybillHasnoPresiteRecord;
import com.jd.bluedragon.distribution.record.service.WaybillHasnoPresiteRecordService;
import com.jd.bluedragon.distribution.record.vo.WaybillHasnoPresiteRecordVo;
import com.jd.bluedragon.distribution.waybill.domain.CancelWaybill;
import com.jd.bluedragon.distribution.waybill.domain.WaybillCancelInterceptTypeEnum;
import com.jd.bluedragon.distribution.waybill.service.WaybillCancelService;
import com.jd.bluedragon.dms.utils.DmsConstants;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.domain.PackageState;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.cache.CacheService;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import com.jd.ql.dms.print.utils.StringHelper;

/**
 * 无滑道查询
 *
 * @copyright jd.com 京东物流JDL
 */
@Service("waybillHasnoPresiteRecordService")
public class WaybillHasnoPresiteRecordServiceImpl implements WaybillHasnoPresiteRecordService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillHasnoPresiteRecordDao waybillHasnoPresiteRecordDao;
    
    @Autowired
    @Qualifier("jimdbCacheService")
    private CacheService cacheService;
    
    @Autowired
    @Qualifier("dmsHasnoPresiteWaybillMqProducer")
    private DefaultJMQProducer dmsHasnoPresiteWaybillMqProducer;
    @Autowired
    @Qualifier("bdBlockerInitMqProducer")
    private DefaultJMQProducer bdBlockerInitMqProducer;
    
    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired
    private WaybillCancelService waybillCancelService;
    @Autowired
    private WaybillTraceManager waybillTraceManager;
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    /**
     * 数据缓存天数，默认7（单位：天）
     */
    @Value("${beans.WaybillHasnoPresiteRecordServiceImpl.cacheDays:7}")
	private int cacheDays;
    /**
     * 默认外呼失败天数，默认2（单位：天）
     */
    @Value("${beans.WaybillHasnoPresiteRecordServiceImpl.defaultFailDays:2}")
	private int defaultFailDays;
    /**
     * 扫描最近7天的数据，默认7（单位：天）
     */
    @Value("${beans.WaybillHasnoPresiteRecordServiceImpl.scanLastDays:7}")
	private int scanLastDays;
    /**
     * 每次扫描最大次数，默认1000
     */
    @Value("${beans.WaybillHasnoPresiteRecordServiceImpl.maxScanTimes:1000}")
	private int maxScanTimes;
    /**
     * 每次扫描数量，默认200
     */
    @Value("${beans.WaybillHasnoPresiteRecordServiceImpl.perScanNum:200}")
	private int perScanNum;
    
    
    /**
     * 标识扫描状态
     */
    private static int scanStatus = 0;
    
    private static int SCAN_STATUS_WATING_SCANING = 0;
    private static int SCAN_STATUS_SCANING = 1;
    /**
     * 获取总数
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<Long> selectCount(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectCount param {}", JsonHelper.toJson(query));
        Response<Long> result = new Response<>();
        result.toSucceed();
        try {
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = waybillHasnoPresiteRecordDao.selectCount(query);
            result.setData(total);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectCount exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    /**
     * 获取列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<List<WaybillHasnoPresiteRecord>> selectList(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectList param {}", JsonHelper.toJson(query));
        Response<List<WaybillHasnoPresiteRecord>> result = new Response<>();
        result.toSucceed();
        try {
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            List<WaybillHasnoPresiteRecord> dataList = waybillHasnoPresiteRecordDao.selectList(query);
            result.setData(dataList);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        return result;
    }

    private Response<Void> checkAndSetPram4SelectParam(WaybillHasnoPresiteRecordQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        Integer hourRange = query.getHourRange();
        if(hourRange == null || hourRange <= 0) {
        	hourRange = DateHelper.ONE_DAY_HOURS;
        }
        Date endTime = new Date();
        Date startTime = DateHelper.newTimeRangeHoursAgo(endTime, hourRange);
        query.setStartTimeTs(startTime);
        query.setEndTimeTs(endTime);
        return result;
    }

    /**
     * 获取分页列表
     * @param query 请求参数
     * @return Result
     */
    @Override
    public Response<PageDto<WaybillHasnoPresiteRecordVo>> selectPageList(WaybillHasnoPresiteRecordQo query) {
        log.info("WaybillHasnoPresiteRecordServiceImpl.selectPageList param {}", JsonHelper.toJson(query));
        Response<PageDto<WaybillHasnoPresiteRecordVo>> result = new Response<>();
        result.toSucceed();
        PageDto<WaybillHasnoPresiteRecordVo> pageDto = new PageDto<>(query.getPageNumber(), query.getPageSize());
        List<WaybillHasnoPresiteRecordVo> dataList = new ArrayList<>();
        try {
            Response<Void> checkResult = this.checkPram4SelectPageList(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            Response<Void> checkAndSetResult = this.checkAndSetPram4SelectParam(query);
            if(!checkAndSetResult.isSucceed()){
                result.toError(checkAndSetResult.getMessage());
                return result;
            }
            long total = waybillHasnoPresiteRecordDao.selectCount(query);
            pageDto.setTotalRow(new Long(total).intValue());
            if (total > 0) {
                List<WaybillHasnoPresiteRecord> rawDataList = waybillHasnoPresiteRecordDao.selectList(query);
                for (WaybillHasnoPresiteRecord waybillHasnoPresiteRecord : rawDataList) {
                    dataList.add(toWaybillHasnoPresiteRecordVo(waybillHasnoPresiteRecord));
                }
            }
            pageDto.setResult(dataList);
        } catch (Exception e) {
            log.error("WaybillHasnoPresiteRecordServiceImpl.selectPageList exception ", e);
            result.toError("系统发生异常，请联系分拣小秘");
        }
        result.setData(pageDto);
        return result;
    }
    /**
     * 对象转换
     * @param dbData
     * @return
     */
    private WaybillHasnoPresiteRecordVo toWaybillHasnoPresiteRecordVo(WaybillHasnoPresiteRecord dbData) {
    	if(dbData == null) {
    		return null;
    	}
    	WaybillHasnoPresiteRecordVo vo = new WaybillHasnoPresiteRecordVo();
        BeanUtils.copyProperties(dbData, vo);
        vo.setStatusDesc(WaybillHasnoPresiteRecordStatusEnum.getNameByCode(vo.getStatus()));
        vo.setCallStatusDesc(WaybillHasnoPresiteRecordCallStatusEnum.getNameByCode(vo.getCallStatus()));
        return vo;
    }
    private Response<Void> checkPram4SelectPageList(WaybillHasnoPresiteRecordQo query){
        Response<Void> result = new Response<>();
        result.toSucceed();
        if(query.getPageNumber() <= 0){
            result.toError("参数错误，pageNumber必须大于0");
            return result;
        }
        if(query.getPageSize() == null){
            result.toError("参数错误，pageSize不能为空");
            return result;
        }
        return result;

    }
	@Override
	public WaybillHasnoPresiteRecordVo selectByWaybillCode(String waybillCode) {
		return toWaybillHasnoPresiteRecordVo(waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillCode));
	}
    private String getCacheKey(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
    	return String.format(CacheKeyConstants.CACHE_KEY_FORMAT_WAYBILL_HASNO_PRESITE_RECORD_FLG,
    			waybillHasnoPresiteRecord.getWaybillCode());
    }
    private String getCacheKey(DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq) {
    	return String.format(CacheKeyConstants.CACHE_KEY_FORMAT_WAYBILL_HASNO_PRESITE_RECORD_FLG,
    			dmsHasnoPresiteWaybillMq.getWaybillCode());
    }
	@Override
	public boolean syncMqDataToDb(DmsHasnoPresiteWaybillMq mqObj) {
		if(mqObj==null 
				|| StringHelper.isEmpty(mqObj.getWaybillCode())) {
			log.warn("WaybillHasnoPresiteRecordServiceImpl.syncMqDataToDb,数据无效！");
			return false;
		}
		Integer operateCode = mqObj.getOperateCode();
		WaybillHasnoPresiteRecord waybillHasnoPresiteRecord = new WaybillHasnoPresiteRecord();
		waybillHasnoPresiteRecord.setWaybillCode(mqObj.getWaybillCode());
		/**
		 * 数据初始化处理
		 */
		if(DmsHasnoPresiteWaybillMqOperateEnum.INIT.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.INIT.getCode());
	    	waybillHasnoPresiteRecord.setEndDmsId(mqObj.getEndDmsId());
	    	waybillHasnoPresiteRecord.setPresiteCode(mqObj.getPresiteCode());
	    	waybillHasnoPresiteRecord.setCreateTime(new Date());
			return doInit(waybillHasnoPresiteRecord);
		}
		//验货处理
		if(DmsHasnoPresiteWaybillMqOperateEnum.CHECK.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setSiteCode(mqObj.getOperateSiteCode());
			waybillHasnoPresiteRecord.setCheckTime(mqObj.getOperateTime());
			fillOtherInfo(waybillHasnoPresiteRecord);
			return doCheck(waybillHasnoPresiteRecord);
		}
		//外呼成功处理
		if(DmsHasnoPresiteWaybillMqOperateEnum.CALL_SUC.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_EXCHANGE.getCode());
			waybillHasnoPresiteRecord.setCallStatus(WaybillHasnoPresiteRecordCallStatusEnum.SUC.getCode());
			waybillHasnoPresiteRecord.setCallTime(mqObj.getOperateTime());
			return doCallSuc(waybillHasnoPresiteRecord);
		}
		//外呼失败处理
		if(DmsHasnoPresiteWaybillMqOperateEnum.CALL_FAIL.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_REVERSE.getCode());
			waybillHasnoPresiteRecord.setCallStatus(WaybillHasnoPresiteRecordCallStatusEnum.FAIL.getCode());
			waybillHasnoPresiteRecord.setCallTime(mqObj.getOperateTime());
			return doCallFail(waybillHasnoPresiteRecord);
		}
		//补打操作
		if(DmsHasnoPresiteWaybillMqOperateEnum.REPRINT.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setSiteCode(mqObj.getOperateSiteCode());
			waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.REPRINT_FINISH.getCode());
			waybillHasnoPresiteRecord.setFinishTime(mqObj.getOperateTime());
			fillOtherInfo(waybillHasnoPresiteRecord);
			return doSucFinish(waybillHasnoPresiteRecord);
		}
		//弃件/换单操作
		if(DmsHasnoPresiteWaybillMqOperateEnum.EXCHANGE.getCode().equals(operateCode)
				|| DmsHasnoPresiteWaybillMqOperateEnum.WASTE.getCode().equals(operateCode)) {
			waybillHasnoPresiteRecord.setSiteCode(mqObj.getOperateSiteCode());
			if(DmsHasnoPresiteWaybillMqOperateEnum.EXCHANGE.getCode().equals(operateCode)){
				waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.EXCHANGE_FINISH.getCode());
			}else {
				waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.WASTE_FINISH.getCode());
			}
			waybillHasnoPresiteRecord.setFinishTime(mqObj.getOperateTime());
			fillOtherInfo(waybillHasnoPresiteRecord);
			return doFailFinish(waybillHasnoPresiteRecord);
		}
		return false;
	}
	private boolean doSucFinish(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		if(waybillHasnoPresiteRecordDao.updateSucFinishInfo(waybillHasnoPresiteRecord)>0) {
			cacheService.del(getCacheKey(waybillHasnoPresiteRecord));
		}
		return true;
	}
	/**
	 * 更新完成时间和状态
	 * @param waybillHasnoPresiteRecord
	 * @return
	 */
	private boolean doFailFinish(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		if(waybillHasnoPresiteRecordDao.updateFailFinishInfo(waybillHasnoPresiteRecord)>0) {
			cacheService.del(getCacheKey(waybillHasnoPresiteRecord));
		}
		return true;
	}
	private boolean doCallFail(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		waybillHasnoPresiteRecordDao.updateCallInfo(waybillHasnoPresiteRecord);
		return true;
	}
	/**
	 * 系统自动外呼失败
	 * @param waybillHasnoPresiteRecord
	 * @return
	 */
	private boolean doSystemAutoCallFail(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		String waybillCode = waybillHasnoPresiteRecord.getWaybillCode();
		Waybill waybill = waybillQueryManager.getOnlyWaybillByWaybillCode(waybillCode);
		if(waybill != null
				&& waybill.getOldSiteId()!= null
				&& waybill.getOldSiteId() > 0) {
			log.warn("系统自动外呼扫描-不处理，{}已存在预分拣站点",waybillCode);
			return false;
		}
		//不存在拦截
		List<CancelWaybill> waybillCancelList = waybillCancelService.getByWaybillCode(waybillCode);
		if(CollectionUtils.isNotEmpty(waybillCancelList)) {
			log.warn("系统自动外呼扫描-未处理，{}存在拦截",waybillCode);
			return false;
		}
		//全程跟踪不存在站点特殊节点
		List<PackageState> siteOperations= waybillTraceManager.getAllOperationsByOpeCodeAndState(waybillCode, DmsConstants.SITE_OPERAT_STATES);
		if(CollectionUtils.isNotEmpty(siteOperations)) {
			log.warn("系统自动外呼扫描-未处理，{}已到达站点",waybillCode);
			return false;
		}
		Date callTime = new Date();
		waybillHasnoPresiteRecord.setStatus(WaybillHasnoPresiteRecordStatusEnum.FOR_REVERSE.getCode());
		waybillHasnoPresiteRecord.setCallStatus(WaybillHasnoPresiteRecordCallStatusEnum.FAIL.getCode());
		waybillHasnoPresiteRecord.setCallTime(callTime);
		boolean updateCallInfo = waybillHasnoPresiteRecordDao.updateCallInfo(waybillHasnoPresiteRecord) > 0;
		//更新数据状态成功
		if(updateCallInfo) {
			BdBlockerInitMq bdBlockerInitMq = new BdBlockerInitMq();
			bdBlockerInitMq.setWaybillCode(waybillCode);
			bdBlockerInitMq.setCreateTime(DateHelper.formatDateTime(callTime));
			bdBlockerInitMq.setUpdateTime(bdBlockerInitMq.getCreateTime());
			bdBlockerInitMq.setInterceptType(WaybillCancelInterceptTypeEnum.FULL_ORDER_FAIL.getCode());
			bdBlockerInitMq.setRemark("验货无下文，分拣自动发起全量接单拦截");
			log.info("系统自动外呼扫描-发起拦截，{}",waybillCode);
			bdBlockerInitMqProducer.sendOnFailPersistent(waybillCode, JsonHelper.toJson(bdBlockerInitMq));
		}
		//发送外呼失败消息
		return true;
	}
	private boolean doCallSuc(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		waybillHasnoPresiteRecordDao.updateCallInfo(waybillHasnoPresiteRecord);
		return true;
	}

	/**
	 * 更新验货时间和场地编码
	 * @param waybillHasnoPresiteRecord
	 * @return
	 */
	private boolean doCheck(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		waybillHasnoPresiteRecordDao.updateCheckInfo(waybillHasnoPresiteRecord);
		return true;
	}
	public boolean doInit(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord) {
		WaybillHasnoPresiteRecord oldData = waybillHasnoPresiteRecordDao.queryByWaybillCode(waybillHasnoPresiteRecord.getWaybillCode());
		if(oldData == null) {
			waybillHasnoPresiteRecordDao.insert(waybillHasnoPresiteRecord);
			cacheService.setNx(getCacheKey(waybillHasnoPresiteRecord), 
					waybillHasnoPresiteRecord.getId(),
					cacheDays * DateHelper.ONE_DAY_SECONDS);
			return true;
		}else {
			log.warn("WaybillHasnoPresiteRecordServiceImpl.syncDataToDb,运单数据已存在！");
		}
		return false;
	}

	@Override
	public boolean sendDataChangeMq(DmsHasnoPresiteWaybillMq dmsHasnoPresiteWaybillMq) {
		if(dmsHasnoPresiteWaybillMq == null
				|| StringHelper.isEmpty(dmsHasnoPresiteWaybillMq.getWaybillCode())) {
			return false;
		}
		//存在全量接单标识，发送mq
		if(cacheService.exists(getCacheKey(dmsHasnoPresiteWaybillMq))) {
			dmsHasnoPresiteWaybillMqProducer.sendOnFailPersistent(dmsHasnoPresiteWaybillMq.getWaybillCode(), JsonHelper.toJson(dmsHasnoPresiteWaybillMq));
		}
		return true;
	}
	//分页扫描-验货超过指定天数的数据
	@Override
	public boolean doScan() {
		if(!startScan()) {
			return true;
		}
		scanStatus = SCAN_STATUS_SCANING;
		WaybillHasnoPresiteRecordQo query = new WaybillHasnoPresiteRecordQo();
		Long scanStartId = 0L;
		Date endCheckTime = DateHelper.addDate(new Date(), -defaultFailDays);
		Date startCreateTime = DateHelper.addDate(new Date(), -scanLastDays);
		query.setStartId(scanStartId);
		query.setEndCheckTime(endCheckTime);
		query.setStartCreateTime(startCreateTime);
		query.setPageSize(perScanNum);
		int scanTimes = 0;
		List<WaybillHasnoPresiteRecord> scanList = null;
		while(scanTimes < this.maxScanTimes){
			scanTimes++;
			scanList = waybillHasnoPresiteRecordDao.selectScanList(query);
			if(scanList == null
					|| scanList.size()==0) {
				log.info("WaybillHasnoPresiteRecordServiceImpl.doScan:第{}次扫描数据为0，结束扫描！",scanTimes);
				break;
			}
			if(log.isInfoEnabled()) {
				log.info("WaybillHasnoPresiteRecordServiceImpl.doScan:第{}次扫描{}条数据",scanTimes,scanList.size());
			}
			if(log.isDebugEnabled()) {
				log.debug("WaybillHasnoPresiteRecordServiceImpl.doScan:第{}次扫描数据:{}",scanTimes,JsonHelper.toJson(scanList));
			}
			for(WaybillHasnoPresiteRecord waybillHasnoPresiteRecord : scanList) {
				doSystemAutoCallFail(waybillHasnoPresiteRecord);
			}
			scanStartId = scanList.get(scanList.size()-1).getId();
			query.setStartId(scanStartId);
		}
		endScan();
		return true;
	}
	private void fillOtherInfo(WaybillHasnoPresiteRecord record) {
	    BaseStaffSiteOrgDto siteData =baseMajorManager.getBaseSiteBySiteId(record.getSiteCode());
		if(siteData != null) {
	        record.setSiteName(siteData.getSiteName());
		}
	}
	private synchronized boolean startScan() {
		if(scanStatus == SCAN_STATUS_WATING_SCANING) {
			scanStatus = SCAN_STATUS_SCANING;
			return true;
		}
		return false;
	}
	private synchronized boolean endScan() {
		if(scanStatus == SCAN_STATUS_SCANING) {
			scanStatus = SCAN_STATUS_WATING_SCANING;
			return true;
		}
		return false;
	}
}
